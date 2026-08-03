package com.alpaomega1136.statsdroid.feature.clt.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationRequest
import com.alpaomega1136.statsdroid.feature.clt.domain.model.PopulationShape
import com.alpaomega1136.statsdroid.feature.clt.domain.model.SimulationCount
import com.alpaomega1136.statsdroid.feature.clt.domain.repository.CltRepository
import com.alpaomega1136.statsdroid.feature.clt.domain.simulation.CltSimulator
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
class CltViewModel @Inject constructor(
    private val repository: CltRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<CltUiState> = _uiState.asStateFlow()

    private var simulationJob: Job? = null
    private var requestGeneration: Long = 0L

    private fun currentRequest(): CltSimulationRequest {
        val state = _uiState.value
        return CltSimulationRequest(
            populationShape = state.selectedPopulationShape,
            sampleSize = state.sampleSize,
            simulationCount = state.simulationCount,
        )
    }

    fun onEvent(event: CltEvent) {
        when (event) {
            is CltEvent.PopulationShapeChanged -> {
                _uiState.update {
                    it.copy(selectedPopulationShape = event.populationShape, result = null, errorMessage = null)
                }
                saveState()
            }
            is CltEvent.SampleSizeChanged -> {
                val boundedValue = event.sampleSize.coerceIn(CltSimulator.MIN_SAMPLE_SIZE, CltSimulator.MAX_SAMPLE_SIZE)
                _uiState.update { it.copy(sampleSize = boundedValue, result = null, errorMessage = null) }
                saveState()
            }
            is CltEvent.SimulationCountChanged -> {
                _uiState.update { it.copy(simulationCount = event.simulationCount, result = null, errorMessage = null) }
                saveState()
            }
            CltEvent.Simulate -> runSimulation()
        }
    }

    private fun runSimulation() {
        simulationJob?.cancel()

        val request = currentRequest()
        val generation = ++requestGeneration

        simulationJob = viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, errorMessage = null) }

            try {
                val result = repository.runSimulation(request)

                val requestIsStillCurrent = request == currentRequest()
                val generationIsStillCurrent = generation == requestGeneration

                if (!requestIsStillCurrent || !generationIsStillCurrent) {
                    return@launch
                }

                _uiState.update { it.copy(isRunning = false, result = result, errorMessage = null) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                if (generation != requestGeneration) {
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isRunning = false,
                        result = null,
                        errorMessage = exception.message ?: "Simulation failed.",
                    )
                }
            }
        }
    }

    private fun createInitialState(): CltUiState {
        return CltUiState(
            selectedPopulationShape = restoreEnum(
                key = KEY_POPULATION_SHAPE,
                defaultValue = PopulationShape.UNIFORM,
                parser = PopulationShape::valueOf,
            ),
            sampleSize = savedStateHandle[KEY_SAMPLE_SIZE] ?: DEFAULT_SAMPLE_SIZE,
            simulationCount = restoreEnum(
                key = KEY_SIMULATION_COUNT,
                defaultValue = SimulationCount.ONE_THOUSAND,
                parser = SimulationCount::valueOf,
            ),
        )
    }

    private fun saveState() {
        val state = _uiState.value
        savedStateHandle[KEY_POPULATION_SHAPE] = state.selectedPopulationShape.name
        savedStateHandle[KEY_SAMPLE_SIZE] = state.sampleSize
        savedStateHandle[KEY_SIMULATION_COUNT] = state.simulationCount.name
    }

    private fun <T> restoreEnum(key: String, defaultValue: T, parser: (String) -> T): T {
        return savedStateHandle.get<String>(key)?.let { runCatching { parser(it) }.getOrNull() } ?: defaultValue
    }

    companion object {
        private const val DEFAULT_SAMPLE_SIZE = 30
        private const val KEY_POPULATION_SHAPE = "clt_population_shape"
        private const val KEY_SAMPLE_SIZE = "clt_sample_size"
        private const val KEY_SIMULATION_COUNT = "clt_simulation_count"
    }
}
