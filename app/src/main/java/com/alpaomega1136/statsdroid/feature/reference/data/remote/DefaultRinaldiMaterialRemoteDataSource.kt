package com.alpaomega1136.statsdroid.feature.reference.data.remote

import android.content.Context
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceFileType
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterialOrigin
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.json.JSONArray
import org.json.JSONObject

class DefaultRinaldiMaterialRemoteDataSource @Inject constructor(
    @ApplicationContext context: Context,
) : RinaldiMaterialRemoteDataSource {

    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun loadLatestMaterials(
        forceRefresh: Boolean,
    ): RinaldiScrapeResult {
        if (!forceRefresh) {
            readCache()?.let { cachedResult ->
                return cachedResult
            }
        }

        return scrapeLatestMaterials().also(::writeCache)
    }

    private fun scrapeLatestMaterials(): RinaldiScrapeResult {
        val rootDocument = connect(PROBSTAT_ROOT_URL)
        val newestCoursePage = findNewestAcademicYearPage(
            rootDocument,
        )

        val academicYear = newestCoursePage?.academicYear
        val pageUrl = newestCoursePage?.url
            ?: LEGACY_COURSE_PAGE_URL

        val primaryDocument = connect(pageUrl)
        val materials = extractMaterials(
            document = primaryDocument,
            academicYear = academicYear,
        ).ifEmpty {
            extractFromLinkedHtmlPages(
                document = primaryDocument,
                academicYear = academicYear,
            )
        }

        return RinaldiScrapeResult(
            materials = materials
                .distinctBy(ReferenceMaterial::remoteUrl)
                .sortedWith(
                    compareBy<ReferenceMaterial> { it.fileType != ReferenceFileType.PDF }
                        .thenBy(ReferenceMaterial::title),
                ),
            latestAcademicYear = academicYear,
        )
    }

    private fun connect(url: String): Document {
        return Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .timeout(NETWORK_TIMEOUT_MILLIS)
            .followRedirects(true)
            .get()
    }

    private fun findNewestAcademicYearPage(
        rootDocument: Document,
    ): AcademicYearPage? {
        return rootDocument
            .select("a[href]")
            .mapNotNull { link ->
                val absoluteUrl = link.absUrl("href")
                if (!isAllowedOfficialUrl(absoluteUrl)) {
                    return@mapNotNull null
                }
                val isCoursePage =
                    absoluteUrl.endsWith("/", ignoreCase = true) ||
                        absoluteUrl.endsWith(".htm", ignoreCase = true) ||
                        absoluteUrl.endsWith(".html", ignoreCase = true)
                if (!isCoursePage) {
                    return@mapNotNull null
                }
                val match = ACADEMIC_YEAR_PATTERN
                    .find(absoluteUrl)
                    ?: return@mapNotNull null

                val startYear = match.groupValues[1].toInt()
                val endYear = match.groupValues[2].toInt()

                AcademicYearPage(
                    academicYear = "$startYear-$endYear",
                    startYear = startYear,
                    endYear = endYear,
                    url = absoluteUrl,
                )
            }
            .maxWithOrNull(
                compareBy<AcademicYearPage> { it.endYear }
                    .thenBy { it.startYear },
            )
    }

    private fun extractFromLinkedHtmlPages(
        document: Document,
        academicYear: String?,
    ): List<ReferenceMaterial> {
        return document
            .select("a[href]")
            .mapNotNull { link ->
                link.absUrl("href")
                    .takeIf { url ->
                        isAllowedOfficialUrl(url) &&
                            (
                                url.endsWith(".htm", ignoreCase = true) ||
                                    url.endsWith(".html", ignoreCase = true)
                                )
                    }
            }
            .distinct()
            .take(MAX_LINKED_HTML_PAGES)
            .flatMap { linkedPageUrl ->
                runCatching {
                    extractMaterials(
                        document = connect(linkedPageUrl),
                        academicYear = academicYear,
                    )
                }.getOrDefault(emptyList())
            }
    }

    private fun extractMaterials(
        document: Document,
        academicYear: String?,
    ): List<ReferenceMaterial> {
        return document
            .select("a[href]")
            .mapNotNull { link ->
                val url = link.absUrl("href")
                if (!isAllowedOfficialUrl(url)) {
                    return@mapNotNull null
                }
                val fileType = fileTypeFromUrl(url)
                    ?: return@mapNotNull null

                val title = link.text()
                    .trim()
                    .takeIf { text ->
                        text.isNotBlank() &&
                            !text.equals("pdf", ignoreCase = true) &&
                            !text.equals("ppt", ignoreCase = true) &&
                            !text.equals("pptx", ignoreCase = true)
                    }
                    ?: titleFromUrl(url)

                if (!shouldIncludeMaterial(title, url)) {
                    return@mapNotNull null
                }

                ReferenceMaterial(
                    id = "rinaldi-${sha256(url).take(16)}",
                    title = title,
                    description = when (fileType) {
                        ReferenceFileType.PDF ->
                            "PDF slide from the official Rinaldi Munir course website."

                        ReferenceFileType.PPT,
                        ReferenceFileType.PPTX,
                        -> "PowerPoint slide discovered from the official Rinaldi Munir course website. Open the source to download it."
                    },
                    sourceName = "Rinaldi Munir",
                    academicYear = academicYear,
                    fileType = fileType,
                    origin = ReferenceMaterialOrigin.RINALDI_MUNIR,
                    remoteUrl = url,
                )
            }
    }


    private fun shouldIncludeMaterial(
        title: String,
        url: String,
    ): Boolean {
        val searchableText = "$title $url".lowercase()
        return EXCLUDED_RESOURCE_KEYWORDS.none(searchableText::contains)
    }

    private fun isAllowedOfficialUrl(url: String): Boolean {
        return runCatching {
            URI(url).host.equals(OFFICIAL_HOST, ignoreCase = true)
        }.getOrDefault(false)
    }

    private fun fileTypeFromUrl(
        url: String,
    ): ReferenceFileType? {
        val cleanUrl = url.substringBefore('?').substringBefore('#')
        return when {
            cleanUrl.endsWith(".pdf", ignoreCase = true) ->
                ReferenceFileType.PDF

            cleanUrl.endsWith(".pptx", ignoreCase = true) ->
                ReferenceFileType.PPTX

            cleanUrl.endsWith(".ppt", ignoreCase = true) ->
                ReferenceFileType.PPT

            else -> null
        }
    }

    private fun titleFromUrl(url: String): String {
        val fileName = url
            .substringBefore('?')
            .substringAfterLast('/')
            .substringBeforeLast('.')

        return URLDecoder.decode(
            fileName,
            StandardCharsets.UTF_8.name(),
        )
            .replace('_', ' ')
            .replace('-', ' ')
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun readCache(): RinaldiScrapeResult? {
        val cachedJson = preferences.getString(
            KEY_CACHED_MATERIALS,
            null,
        ) ?: return null

        val savedAt = preferences.getLong(
            KEY_CACHE_TIME,
            0L,
        )

        val cacheIsFresh =
            System.currentTimeMillis() - savedAt < CACHE_DURATION_MILLIS

        if (!cacheIsFresh) {
            return null
        }

        return runCatching {
            val root = JSONObject(cachedJson)
            val items = root.getJSONArray("materials")
            val materials = buildList {
                for (index in 0 until items.length()) {
                    val item = items.getJSONObject(index)
                    add(
                        ReferenceMaterial(
                            id = item.getString("id"),
                            title = item.getString("title"),
                            description = item.getString("description"),
                            sourceName = "Rinaldi Munir",
                            academicYear = item
                                .optString("academicYear")
                                .takeIf(String::isNotBlank),
                            fileType = ReferenceFileType.valueOf(
                                item.getString("fileType"),
                            ),
                            origin = ReferenceMaterialOrigin.RINALDI_MUNIR,
                            remoteUrl = item.getString("remoteUrl"),
                        ),
                    )
                }
            }

            RinaldiScrapeResult(
                materials = materials,
                latestAcademicYear = root
                    .optString("latestAcademicYear")
                    .takeIf(String::isNotBlank),
            )
        }.getOrNull()
    }

    private fun writeCache(result: RinaldiScrapeResult) {
        val materialsJson = JSONArray()
        result.materials.forEach { material ->
            materialsJson.put(
                JSONObject()
                    .put("id", material.id)
                    .put("title", material.title)
                    .put("description", material.description)
                    .put("academicYear", material.academicYear.orEmpty())
                    .put("fileType", material.fileType.name)
                    .put("remoteUrl", material.remoteUrl),
            )
        }

        val root = JSONObject()
            .put("latestAcademicYear", result.latestAcademicYear.orEmpty())
            .put("materials", materialsJson)

        preferences.edit()
            .putString(KEY_CACHED_MATERIALS, root.toString())
            .putLong(KEY_CACHE_TIME, System.currentTimeMillis())
            .apply()
    }

    private fun sha256(value: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString(separator = "") { byte ->
                "%02x".format(byte)
            }
    }

    private data class AcademicYearPage(
        val academicYear: String,
        val startYear: Int,
        val endYear: Int,
        val url: String,
    )

    companion object {
        private const val OFFICIAL_HOST =
            "informatika.stei.itb.ac.id"

        private const val PROBSTAT_ROOT_URL =
            "https://informatika.stei.itb.ac.id/~rinaldi.munir/Probstat/"

        private const val LEGACY_COURSE_PAGE_URL =
            "https://informatika.stei.itb.ac.id/~rinaldi.munir/Probstat/2010-2011/probstat10-11.htm"

        private const val USER_AGENT =
            "StatsDroid/1.0 educational material scraper"

        private const val NETWORK_TIMEOUT_MILLIS = 20_000
        private const val MAX_LINKED_HTML_PAGES = 8
        private const val CACHE_DURATION_MILLIS = 24L * 60L * 60L * 1000L

        private const val PREFERENCES_NAME =
            "reference_library_scraper"
        private const val KEY_CACHED_MATERIALS =
            "cached_materials"
        private const val KEY_CACHE_TIME =
            "cache_time"

        private val ACADEMIC_YEAR_PATTERN =
            Regex("(20\\d{2})-(20\\d{2})")

        private val EXCLUDED_RESOURCE_KEYWORDS = listOf(
            "nilai",
            "makalah",
            "foto",
            "kuis",
            "ujian",
            "uts",
            "uas",
            "pekerjaan rumah",
            "informasi perkuliahan",
        )
    }
}
