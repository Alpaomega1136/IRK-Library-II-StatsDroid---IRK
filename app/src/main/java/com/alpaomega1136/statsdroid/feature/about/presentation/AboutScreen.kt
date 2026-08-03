package com.alpaomega1136.statsdroid.feature.about.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.about.presentation.components.ExternalLinkButton
import com.alpaomega1136.statsdroid.ui.components.StatsExpandableInfoCard
import com.alpaomega1136.statsdroid.ui.components.StatsHeroCard
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.components.StatsSegmentedControl
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

private enum class AboutFocus(
    val label: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
) {
    DEVELOPER(
        label = "Pembuat",
        title = "Pembuat aplikasi",
        subtitle = "Informasi tentang developer StatsDroid",
        icon = Icons.Default.Person,
    ),
    MOTIVATION(
        label = "Motivasi",
        title = "Motivasi",
        subtitle = "Alasan pembuatan aplikasi",
        icon = Icons.Default.Lightbulb,
    ),
    IRK_VISION(
        label = "Harapan IRK",
        title = "Harapan sebagai asisten IRK",
        subtitle = "Visi dan misi sebagai asisten",
        icon = Icons.Default.School,
    ),
    CONTACT(
        label = "Kontak",
        title = "Contact information",
        subtitle = "Email atau media sosial developer",
        icon = Icons.Default.ContactPage,
    ),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(
    profile: AboutProfile,
    onOpenGitHub: () -> Unit,
    onOpenRepository: () -> Unit,
    onOpenLinkedIn: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var selectedFocus by rememberSaveable {
        mutableStateOf(AboutFocus.DEVELOPER)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = StatsSpacing.Medium,
            vertical = StatsSpacing.Large,
        ),
        verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
    ) {
        item {
            StatsHeroCard(
                eyebrow = "About Me • Seleksi IRK 2026",
                title = profile.name,
                description = "${profile.studentId} • ${profile.role}",
                icon = Icons.Default.Person,
                badgeText = "StatsDroid 1.0",
            )
        }

        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
            ) {
                ProjectTag(text = "4 halaman aplikasi")
                ProjectTag(text = "3 distribusi probabilitas")
                ProjectTag(text = "2 pengujian hipotesis")
                ProjectTag(text = "3 populasi CLT")
            }
        }

        item {
            StatsSectionCard(
                title = "About Me",
                subtitle =
                    "Pilih salah satu bagian untuk melihat informasi yang diminta pada tugas.",
            ) {
                StatsSegmentedControl(
                    items = AboutFocus.entries,
                    selectedItem = selectedFocus,
                    onItemSelected = { selectedFocus = it },
                    itemLabel = { focus -> focus.label },
                )

                AnimatedContent(
                    targetState = selectedFocus,
                    transitionSpec = {
                        (fadeIn() + slideInHorizontally { it / 8 }) togetherWith
                            (fadeOut() + slideOutHorizontally { -it / 8 })
                    },
                    label = "about_focus_content",
                ) { focus ->
                    AboutFocusContent(
                        focus = focus,
                        profile = profile,
                        onOpenGitHub = onOpenGitHub,
                        onOpenRepository = onOpenRepository,
                        onOpenLinkedIn = onOpenLinkedIn,
                    )
                }
            }
        }

        item {
            StatsExpandableInfoCard(
                title = stringResource(R.string.about_statsdroid),
                summary =
                    "Tujuan proyek, fitur utama, teknologi, dan arsitektur aplikasi.",
            ) {
                Text(
                    text =
                        "StatsDroid menghubungkan rumus statistika dengan visualisasi interaktif. Pengguna dapat menghitung probabilitas kumulatif, membandingkan keputusan pengujian hipotesis, dan mengamati Central Limit Theorem melalui simulasi sampling berulang.",
                    style = MaterialTheme.typography.bodyMedium,
                )

                SectionHeading(
                    icon = Icons.Default.CheckCircle,
                    text = stringResource(R.string.about_features),
                )
                FeatureItem(
                    "Lookup probabilitas untuk distribusi Binomial, Poisson, dan Standard Normal.",
                )
                FeatureItem(
                    "Visualisasi Z-Test dan t-Test dengan p-value serta rejection region.",
                )
                FeatureItem(
                    "Simulasi CLT interaktif dengan populasi Uniform, Exponential, dan Bimodal.",
                )

                SectionHeading(
                    icon = Icons.Default.Code,
                    text = "Arsitektur dan teknologi",
                )
                Text(
                    text =
                        "Kotlin • Jetpack Compose • Material 3 • MVVM • StateFlow • Hilt • Coroutines • Navigation Compose • Apache Commons Statistics • JUnit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text =
                        "Compose UI → ViewModel → Repository → Local Data Source → Statistical Calculator",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text =
                        "Kalkulator statistik tetap berupa pure Kotlin, sedangkan state dan presentation logic Android dipisahkan dari domain layer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Text(
                text = "Dibuat untuk tugas seleksi Laboratorium IRK.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AboutFocusContent(
    focus: AboutFocus,
    profile: AboutProfile,
    onOpenGitHub: () -> Unit,
    onOpenRepository: () -> Unit,
    onOpenLinkedIn: (() -> Unit)?,
) {
    Column(
        modifier = Modifier.padding(top = StatsSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = focus.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column {
                Text(
                    text = focus.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = focus.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        when (focus) {
            AboutFocus.DEVELOPER -> {
                Text(
                    text = profile.description,
                    style = MaterialTheme.typography.bodyLarge,
                )
                DeveloperDetail(
                    label = "Nama",
                    value = profile.name,
                )
                DeveloperDetail(
                    label = "NIM",
                    value = profile.studentId,
                )
                DeveloperDetail(
                    label = "Peran",
                    value = profile.role,
                )
            }

            AboutFocus.MOTIVATION -> {
                Text(
                    text = profile.motivation,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            AboutFocus.IRK_VISION -> {
                Text(
                    text = profile.assistantVision,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Misi sebagai asisten",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                profile.assistantMissions.forEach { mission ->
                    FeatureItem(mission)
                }
            }

            AboutFocus.CONTACT -> {
                Text(
                    text = profile.contactSummary,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                ) {
                    ExternalLinkButton(
                        label = stringResource(R.string.open_github_profile),
                        onClick = onOpenGitHub,
                    )
                    ExternalLinkButton(
                        label = stringResource(R.string.open_repository),
                        onClick = onOpenRepository,
                    )
                    if (profile.linkedInUrl != null && onOpenLinkedIn != null) {
                        ExternalLinkButton(
                            label = stringResource(R.string.open_linkedin_profile),
                            onClick = onOpenLinkedIn,
                        )
                    }
                }

                Text(
                    text =
                        "GitHub: ${profile.githubUrl}\nRepository: ${profile.repositoryUrl}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DeveloperDetail(
    label: String,
    value: String,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(StatsSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(StatsSpacing.ExtraSmall),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun SectionHeading(
    icon: ImageVector,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun FeatureItem(
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ProjectTag(
    text: String,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(
                horizontal = StatsSpacing.Medium,
                vertical = StatsSpacing.Small,
            ),
        )
    }
}
