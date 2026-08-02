package com.alpaomega1136.statsdroid.feature.about.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler

@Composable
fun AboutRoute() {
    val uriHandler = LocalUriHandler.current
    val profile = AboutProfile(
        name = "Alpaomega1136",
        studentId = "13524059",
        role = "StatsDroid Developer",
        description = "An informatics student interested in software engineering, algorithms, and interactive statistical visualization.",
        githubUrl = "https://github.com/Alpaomega1136",
        repositoryUrl = "https://github.com/Alpaomega1136/IRK-Library-II-StatsDroid---IRK",
        linkedInUrl = null,
    )

    AboutScreen(
        profile = profile,
        onOpenGitHub = { openExternalUri(profile.githubUrl, uriHandler) },
        onOpenRepository = { openExternalUri(profile.repositoryUrl, uriHandler) },
        onOpenLinkedIn = profile.linkedInUrl?.let { linkedInUrl ->
            { openExternalUri(linkedInUrl, uriHandler) }
        },
    )
}

private fun openExternalUri(uri: String, uriHandler: UriHandler) {
    runCatching {
        uriHandler.openUri(uri)
    }
}
