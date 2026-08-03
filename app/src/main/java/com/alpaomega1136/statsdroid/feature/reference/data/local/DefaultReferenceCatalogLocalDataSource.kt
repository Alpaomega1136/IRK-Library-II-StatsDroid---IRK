package com.alpaomega1136.statsdroid.feature.reference.data.local

import android.content.Context
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceFileType
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterialOrigin
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.json.JSONObject

class DefaultReferenceCatalogLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ReferenceCatalogLocalDataSource {

    override fun loadBundledMaterials(): List<ReferenceMaterial> {
        val json = context.assets
            .open(CATALOG_ASSET_PATH)
            .bufferedReader()
            .use { reader -> reader.readText() }

        val materialsArray = JSONObject(json)
            .getJSONArray("materials")

        return buildList {
            for (index in 0 until materialsArray.length()) {
                val item = materialsArray.getJSONObject(index)
                val fileType = ReferenceFileType.valueOf(
                    item.optString("fileType", "PDF")
                        .uppercase(),
                )

                add(
                    ReferenceMaterial(
                        id = item.getString("id"),
                        title = item.getString("title"),
                        description = item.optString("description"),
                        sourceName = item.optString(
                            "sourceName",
                            "Bundled Probstat material",
                        ),
                        academicYear = item
                            .optString("academicYear")
                            .takeIf(String::isNotBlank),
                        fileType = fileType,
                        origin = ReferenceMaterialOrigin.BUNDLED,
                        assetPath = item.getString("assetPath"),
                    ),
                )
            }
        }
    }

    companion object {
        private const val CATALOG_ASSET_PATH =
            "reference/library.json"
    }
}
