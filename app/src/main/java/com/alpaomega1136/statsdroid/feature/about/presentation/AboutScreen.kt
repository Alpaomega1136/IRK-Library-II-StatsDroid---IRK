package com.alpaomega1136.statsdroid.feature.about.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.about.presentation.components.AboutHeaderCard
import com.alpaomega1136.statsdroid.feature.about.presentation.components.AboutSectionCard
import com.alpaomega1136.statsdroid.feature.about.presentation.components.ExternalLinkButton
import com.alpaomega1136.statsdroid.ui.theme.SmallControlShape
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(
    profile: AboutProfile,
    onOpenGitHub: () -> Unit,
    onOpenRepository: () -> Unit,
    onOpenLinkedIn: (() -> Unit)?,
    onOpenInstagram: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = StatsSpacing.Medium,
            vertical = StatsSpacing.Large,
        ),
        verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
    ) {
        item {
            Text(
                text = "About Me",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        item {
            AboutHeaderCard(profile = profile)
        }

        item {
            AboutSectionCard(
                title = "Informasi Diri",
                icon = Icons.Default.Person,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium)) {
                    Text(
                        text = "Mahasiswa Teknik Informatika Institut Teknologi Bandung (ITB) angkatan 2024. Saya berfokus untuk membangun fondasi yang kuat dalam rekayasa perangkat lunak, sains data, dan kecerdasan buatan.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = "Saya senang mengubah ide menjadi perangkat lunak praktis, mempelajari cara kerja sistem, serta mengeksplorasi hubungan antara kode, gim, dan animasi visual.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small)) {
                        Text(
                            text = "Fokus",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                            verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                        ) {
                            profile.focusAreas.forEach { area ->
                                AssistChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            text = area,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                    border = null,
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            AboutSectionCard(
                title = "Motivasi",
                icon = Icons.Default.Lightbulb,
            ) {
                Surface(
                    shape = SmallControlShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = profile.motivation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(StatsSpacing.Medium),
                    )
                }
            }
        }

        item {
            AboutSectionCard(
                title = "Harapan sebagai Asisten IRK",
                icon = Icons.Default.School,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium)) {
                    Surface(
                        shape = SmallControlShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(StatsSpacing.Medium),
                            verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                        ) {
                            Text(
                                text = "Visi",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = profile.visionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small)) {
                        Text(
                            text = "Misi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                        )

                        profile.missionPoints.forEachIndexed { index, point ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp),
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(StatsSpacing.Small))

                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            AboutSectionCard(
                title = "Contact Information",
                icon = Icons.Default.AlternateEmail,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                ) {
                    ExternalLinkButton(
                        label = stringResource(R.string.open_github_profile),
                        handle = profile.githubHandle,
                        icon = Icons.Default.Code,
                        onClick = onOpenGitHub,
                    )
                    if (profile.instagramUrl != null && onOpenInstagram != null) {
                        ExternalLinkButton(
                            label = stringResource(R.string.open_instagram_profile),
                            handle = profile.instagramHandle,
                            icon = Icons.Default.Person,
                            onClick = onOpenInstagram,
                        )
                    }
                    ExternalLinkButton(
                        label = stringResource(R.string.open_repository),
                        handle = "IRK-Library-II-StatsDroid",
                        icon = Icons.Default.Folder,
                        onClick = onOpenRepository,
                    )
                    if (profile.linkedInUrl != null && onOpenLinkedIn != null) {
                        ExternalLinkButton(
                            label = stringResource(R.string.open_linkedin_profile),
                            onClick = onOpenLinkedIn,
                        )
                    }
                }
            }
        }
    }
}
