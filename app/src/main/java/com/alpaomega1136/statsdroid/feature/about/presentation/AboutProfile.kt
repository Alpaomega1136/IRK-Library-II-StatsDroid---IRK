package com.alpaomega1136.statsdroid.feature.about.presentation

data class AboutProfile(
    val name: String,
    val studentId: String,
    val role: String,
    val description: String,
    val motivation: String,
    val assistantVision: String,
    val assistantMissions: List<String>,
    val contactSummary: String,
    val githubUrl: String,
    val repositoryUrl: String,
    val linkedInUrl: String? = null,
)
