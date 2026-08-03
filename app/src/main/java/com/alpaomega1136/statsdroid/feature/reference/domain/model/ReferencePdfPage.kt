package com.alpaomega1136.statsdroid.feature.reference.domain.model

import android.graphics.Bitmap

data class ReferencePdfPage(
    val bitmap: Bitmap,
    val pageIndex: Int,
    val pageCount: Int,
)
