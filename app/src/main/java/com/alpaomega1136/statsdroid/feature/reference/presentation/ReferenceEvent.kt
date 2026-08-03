package com.alpaomega1136.statsdroid.feature.reference.presentation

import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial

sealed interface ReferenceEvent {
    data class SearchChanged(
        val query: String,
    ) : ReferenceEvent

    data class OpenMaterial(
        val material: ReferenceMaterial,
    ) : ReferenceEvent

    data object CloseReader : ReferenceEvent
    data object PreviousPage : ReferenceEvent
    data object NextPage : ReferenceEvent
    data object RetryReader : ReferenceEvent
    data object RefreshLibrary : ReferenceEvent
}
