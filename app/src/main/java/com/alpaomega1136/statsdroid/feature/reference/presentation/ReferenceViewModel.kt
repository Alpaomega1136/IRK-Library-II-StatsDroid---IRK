package com.alpaomega1136.statsdroid.feature.reference.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial
import com.alpaomega1136.statsdroid.feature.reference.domain.repository.ReferenceLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReferenceViewModel @Inject constructor(
    private val repository: ReferenceLibraryRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReferenceUiState(
            query = savedStateHandle.get<String>(KEY_QUERY).orEmpty(),
        ),
    )
    val uiState: StateFlow<ReferenceUiState> =
        _uiState.asStateFlow()

    private var libraryJob: Job? = null
    private var readerJob: Job? = null

    init {
        loadLibrary(refreshRemote = false)
    }

    fun onEvent(event: ReferenceEvent) {
        when (event) {
            is ReferenceEvent.SearchChanged -> {
                savedStateHandle[KEY_QUERY] = event.query
                _uiState.update { state ->
                    state.copy(query = event.query)
                }
            }

            is ReferenceEvent.OpenMaterial -> {
                if (event.material.canOpenInApp) {
                    openMaterial(event.material)
                }
            }

            ReferenceEvent.CloseReader -> closeReader()
            ReferenceEvent.PreviousPage -> {
                if (_uiState.value.canGoPrevious) {
                    loadPage(_uiState.value.pageIndex - 1)
                }
            }

            ReferenceEvent.NextPage -> {
                if (_uiState.value.canGoNext) {
                    loadPage(_uiState.value.pageIndex + 1)
                }
            }

            ReferenceEvent.RetryReader -> {
                loadPage(_uiState.value.pageIndex)
            }

            ReferenceEvent.RefreshLibrary -> {
                loadLibrary(refreshRemote = true)
            }
        }
    }

    private fun loadLibrary(
        refreshRemote: Boolean,
    ) {
        libraryJob?.cancel()
        libraryJob = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoadingLibrary = state.bundledMaterials.isEmpty(),
                    isRefreshingLibrary = refreshRemote,
                    libraryWarning = null,
                )
            }

            try {
                val library = repository.loadLibrary(
                    refreshRemote = refreshRemote,
                )

                _uiState.update { state ->
                    state.copy(
                        isLoadingLibrary = false,
                        isRefreshingLibrary = false,
                        bundledMaterials = library.bundledMaterials,
                        scrapedMaterials = library.scrapedMaterials,
                        latestAcademicYear = library.latestAcademicYear,
                        libraryWarning = library.warningMessage,
                    )
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoadingLibrary = false,
                        isRefreshingLibrary = false,
                        libraryWarning = exception.message
                            ?: "Unable to load the reference library.",
                    )
                }
            }
        }
    }

    private fun openMaterial(
        material: ReferenceMaterial,
    ) {
        savedStateHandle[KEY_SELECTED_MATERIAL_ID] = material.id
        _uiState.update { state ->
            state.copy(
                selectedMaterial = material,
                pageIndex = 0,
                pageCount = 0,
                bitmap = null,
                readerError = null,
            )
        }
        loadPage(pageIndex = 0)
    }

    private fun closeReader() {
        readerJob?.cancel()
        savedStateHandle.remove<String>(KEY_SELECTED_MATERIAL_ID)
        savedStateHandle.remove<Int>(KEY_PAGE_INDEX)
        _uiState.update { state ->
            state.copy(
                selectedMaterial = null,
                isLoadingPage = false,
                bitmap = null,
                pageIndex = 0,
                pageCount = 0,
                readerError = null,
            )
        }
    }

    private fun loadPage(
        pageIndex: Int,
    ) {
        val material = _uiState.value.selectedMaterial
            ?: return

        readerJob?.cancel()
        readerJob = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoadingPage = true,
                    readerError = null,
                )
            }

            try {
                val page = repository.renderPdfPage(
                    material = material,
                    pageIndex = pageIndex,
                )

                if (_uiState.value.selectedMaterial?.id != material.id) {
                    return@launch
                }

                savedStateHandle[KEY_PAGE_INDEX] = page.pageIndex
                _uiState.update { state ->
                    state.copy(
                        isLoadingPage = false,
                        bitmap = page.bitmap,
                        pageIndex = page.pageIndex,
                        pageCount = page.pageCount,
                        readerError = null,
                    )
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoadingPage = false,
                        bitmap = null,
                        readerError = exception.message
                            ?: "Unable to open the selected PDF.",
                    )
                }
            }
        }
    }

    companion object {
        private const val KEY_QUERY = "reference_query"
        private const val KEY_SELECTED_MATERIAL_ID =
            "reference_selected_material_id"
        private const val KEY_PAGE_INDEX =
            "reference_page_index"
    }
}
