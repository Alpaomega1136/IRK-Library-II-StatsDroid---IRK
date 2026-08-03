package com.alpaomega1136.statsdroid.feature.reference.domain.model

enum class ReferenceFileType(
    val displayName: String,
) {
    PDF("PDF"),
    PPT("PPT"),
    PPTX("PPTX"),
}

enum class ReferenceMaterialOrigin {
    BUNDLED,
    RINALDI_MUNIR,
}

data class ReferenceMaterial(
    val id: String,
    val title: String,
    val description: String,
    val sourceName: String,
    val academicYear: String?,
    val fileType: ReferenceFileType,
    val origin: ReferenceMaterialOrigin,
    val assetPath: String? = null,
    val remoteUrl: String? = null,
) {
    val canOpenInApp: Boolean
        get() = fileType == ReferenceFileType.PDF &&
            (assetPath != null || remoteUrl != null)
}
