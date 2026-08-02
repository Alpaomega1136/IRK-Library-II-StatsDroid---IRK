package com.alpaomega1136.statsdroid.feature.about.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.about.presentation.components.AboutHeaderCard
import com.alpaomega1136.statsdroid.feature.about.presentation.components.AboutSectionCard
import com.alpaomega1136.statsdroid.feature.about.presentation.components.ExternalLinkButton

@Composable
fun AboutScreen(
    profile: AboutProfile,
    onOpenGitHub: () -> Unit,
    onOpenRepository: () -> Unit,
    onOpenLinkedIn: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Text(text = stringResource(R.string.about_title), style = MaterialTheme.typography.headlineMedium)
        }

        item {
            AboutHeaderCard(profile = profile)
        }

        item {
            AboutSectionCard(title = stringResource(R.string.about_statsdroid)) {
                Text(
                    text = "StatsDroid is an interactive Android application for learning probability distributions, hypothesis testing, and the Central Limit Theorem.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "The application connects statistical formulas with visual explanations and interactive simulations.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        item {
            AboutSectionCard(title = stringResource(R.string.about_features)) {
                FeatureText("Probability lookup for Binomial, Poisson, and Standard Normal distributions.")
                FeatureText("Visual Z-Test and t-Test with p-value and rejection-region visualization.")
                FeatureText("Interactive Central Limit Theorem simulation using Uniform, Exponential, and Bimodal populations.")
                FeatureText("Responsive Jetpack Compose interface with configuration-safe state.")
            }
        }

        item {
            AboutSectionCard(title = stringResource(R.string.about_technology)) {
                listOf(
                    "Kotlin",
                    "Jetpack Compose",
                    "Material 3",
                    "MVVM",
                    "Kotlin Coroutines",
                    "StateFlow",
                    "Hilt",
                    "Navigation Compose",
                    "Apache Commons Statistics",
                    "JUnit",
                ).forEach { TechnologyText(it) }
            }
        }

        item {
            AboutSectionCard(title = stringResource(R.string.about_architecture)) {
                Text(text = "The application uses feature-based MVVM architecture.", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Compose -> ViewModel -> Repository -> Local Data Source -> Statistical Calculator",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Statistical calculations are separated from Android and Compose APIs, allowing the domain logic to be tested as pure Kotlin.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        item {
            AboutSectionCard(title = stringResource(R.string.about_project_links)) {
                ExternalLinkButton(label = stringResource(R.string.open_github_profile), onClick = onOpenGitHub)
                ExternalLinkButton(label = stringResource(R.string.open_repository), onClick = onOpenRepository)
                if (profile.linkedInUrl != null && onOpenLinkedIn != null) {
                    ExternalLinkButton(label = stringResource(R.string.open_linkedin_profile), onClick = onOpenLinkedIn)
                }
            }
        }

        item {
            Text(
                text = "Built as part of the IRK laboratory selection assignment.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FeatureText(text: String) {
    Text(text = "- $text", style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun TechnologyText(technology: String) {
    Text(text = "- $technology", style = MaterialTheme.typography.bodyMedium)
}
