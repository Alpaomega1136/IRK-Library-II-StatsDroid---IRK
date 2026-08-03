package com.alpaomega1136.statsdroid.feature.reference.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceFileType
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterialOrigin
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun ReferenceScreen(
    uiState: ReferenceUiState,
    onEvent: (ReferenceEvent) -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isReaderOpen) {
        ReferenceReaderScreen(
            uiState = uiState,
            onEvent = onEvent,
            modifier = modifier,
        )
    } else {
        ReferenceLibraryScreen(
            uiState = uiState,
            onEvent = onEvent,
            onOpenExternalUrl = onOpenExternalUrl,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReferenceLibraryScreen(
    uiState: ReferenceUiState,
    onEvent: (ReferenceEvent) -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Probstat Library")
                },
                actions = {
                    IconButton(
                        onClick = {
                            onEvent(ReferenceEvent.RefreshLibrary)
                        },
                        enabled = !uiState.isRefreshingLibrary,
                    ) {
                        if (uiState.isRefreshingLibrary) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription =
                                    "Refresh online materials",
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        if (uiState.isLoadingLibrary) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = StatsSpacing.Medium,
                vertical = StatsSpacing.Small,
            ),
            verticalArrangement =
                Arrangement.spacedBy(StatsSpacing.Medium),
        ) {
            item {
                LibraryIntroductionCard()
            }

            item {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { query ->
                        onEvent(
                            ReferenceEvent.SearchChanged(query),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(text = "Search materials")
                    },
                )
            }

            uiState.libraryWarning?.let { warning ->
                item {
                    LibraryWarningCard(message = warning)
                }
            }

            item {
                LibrarySectionHeader(
                    title = "Bundled Probstat materials",
                    subtitle = "",
                    itemCount =
                        uiState.filteredBundledMaterials.size,
                )
            }

            if (uiState.filteredBundledMaterials.isEmpty()) {
                item {
                    EmptyLibrarySection(
                        message =
                            "No bundled material matches the search.",
                    )
                }
            } else {
                items(
                    items = uiState.filteredBundledMaterials,
                    key = ReferenceMaterial::id,
                ) { material ->
                    ReferenceMaterialCard(
                        material = material,
                        onOpenInApp = {
                            onEvent(
                                ReferenceEvent.OpenMaterial(
                                    material,
                                ),
                            )
                        },
                        onOpenExternal = onOpenExternalUrl,
                    )
                }
            }

            item {
                HorizontalDivider()
            }

            item {
                LibrarySectionHeader(
                    title = "Latest from Rinaldi Munir",
                    subtitle = uiState.latestAcademicYear
                        ?.let { year ->
                            "Automatically discovered from academic year $year."
                        }
                        ?: "Automatically discovered from the official Probstat website.",
                    itemCount =
                        uiState.filteredScrapedMaterials.size,
                )
            }

            if (uiState.filteredScrapedMaterials.isEmpty()) {
                item {
                    EmptyLibrarySection(
                        message =
                            "No online material is available. Check the internet connection or refresh the library.",
                    )
                }
            } else {
                items(
                    items = uiState.filteredScrapedMaterials,
                    key = ReferenceMaterial::id,
                ) { material ->
                    ReferenceMaterialCard(
                        material = material,
                        onOpenInApp = {
                            onEvent(
                                ReferenceEvent.OpenMaterial(
                                    material,
                                ),
                            )
                        },
                        onOpenExternal = onOpenExternalUrl,
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryIntroductionCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(StatsSpacing.Large),
            horizontalArrangement =
                Arrangement.spacedBy(StatsSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(StatsSpacing.ExtraSmall),
            ) {
                Text(
                    text = "Probability and Statistics materials",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text =
                        "Choose a slide deck from the local collection or the automatically synchronized official source.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun LibrarySectionHeader(
    title: String,
    subtitle: String,
    itemCount: Int,
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(StatsSpacing.ExtraSmall),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = itemCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReferenceMaterialCard(
    material: ReferenceMaterial,
    onOpenInApp: () -> Unit,
    onOpenExternal: (String) -> Unit,
) {
    val cardModifier = if (material.canOpenInApp) {
        Modifier.clickable(onClick = onOpenInApp)
    } else {
        Modifier
    }

    Card(
        modifier = cardModifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(StatsSpacing.Medium),
            verticalArrangement =
                Arrangement.spacedBy(StatsSpacing.Small),
        ) {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(StatsSpacing.Medium),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = when (material.fileType) {
                        ReferenceFileType.PDF ->
                            Icons.Default.PictureAsPdf

                        ReferenceFileType.PPT,
                        ReferenceFileType.PPTX,
                        -> Icons.Default.Slideshow
                    },
                    contentDescription = null,
                    tint = when (material.fileType) {
                        ReferenceFileType.PDF ->
                            MaterialTheme.colorScheme.error

                        ReferenceFileType.PPT,
                        ReferenceFileType.PPTX,
                        -> MaterialTheme.colorScheme.tertiary
                    },
                    modifier = Modifier.size(30.dp),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(StatsSpacing.ExtraSmall),
                ) {
                    Text(
                        text = material.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = material.description,
                        style = MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buildString {
                        append(material.fileType.displayName)
                        append(" · ")
                        append(material.sourceName)
                        material.academicYear?.let { year ->
                            append(" · ")
                            append(year)
                        }
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.size(StatsSpacing.Small))

                if (material.canOpenInApp) {
                    FilledTonalButton(
                        onClick = onOpenInApp,
                    ) {
                        Text(text = "Read")
                    }
                } else {
                    val url = material.remoteUrl
                    OutlinedButton(
                        onClick = {
                            if (url != null) {
                                onOpenExternal(url)
                            }
                        },
                        enabled = url != null,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = "Source")
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryWarningCard(
    message: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(StatsSpacing.Medium),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun EmptyLibrarySection(
    message: String,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(StatsSpacing.Medium),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReferenceReaderScreen(
    uiState: ReferenceUiState,
    onEvent: (ReferenceEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val material = uiState.selectedMaterial ?: return

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onEvent(ReferenceEvent.CloseReader)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to library",
                        )
                    }
                },
                title = {
                    Text(
                        text = material.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = StatsSpacing.Medium),
            verticalArrangement =
                Arrangement.spacedBy(StatsSpacing.Small),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    uiState.isLoadingPage -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.spacedBy(StatsSpacing.Medium),
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = if (
                                    material.origin ==
                                    ReferenceMaterialOrigin.RINALDI_MUNIR
                                ) {
                                    "Downloading and opening PDF…"
                                } else {
                                    "Opening PDF…"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    uiState.readerError != null -> {
                        ReaderErrorState(
                            message = uiState.readerError,
                            onRetry = {
                                onEvent(ReferenceEvent.RetryReader)
                            },
                        )
                    }

                    uiState.bitmap != null -> {
                        ZoomablePdfPage(
                            bitmap = uiState.bitmap.asImageBitmap(),
                            pageNumber = uiState.pageIndex + 1,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }

            if (uiState.pageCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = StatsSpacing.Medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            onEvent(ReferenceEvent.PreviousPage)
                        },
                        enabled = uiState.canGoPrevious,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous page",
                        )
                    }

                    Text(
                        text =
                            "Page ${uiState.pageIndex + 1} of ${uiState.pageCount}",
                        style = MaterialTheme.typography.labelLarge,
                    )

                    FilledTonalIconButton(
                        onClick = {
                            onEvent(ReferenceEvent.NextPage)
                        },
                        enabled = uiState.canGoNext,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next page",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ZoomablePdfPage(
    bitmap: androidx.compose.ui.graphics.ImageBitmap,
    pageNumber: Int,
    modifier: Modifier = Modifier,
) {
    var scale by remember(pageNumber) {
        mutableFloatStateOf(1f)
    }
    var offset by remember(pageNumber) {
        mutableStateOf(Offset.Zero)
    }

    val transformableState = rememberTransformableState {
            zoomChange,
            panChange,
            _,
        ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset = if (scale <= 1f) {
            Offset.Zero
        } else {
            offset + panChange
        }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                )
                .transformable(transformableState)
                .semantics {
                    contentDescription =
                        "PDF page $pageNumber. Pinch to zoom and drag to pan."
                },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    },
            )
        }
    }
}

@Composable
private fun ReaderErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(StatsSpacing.Medium),
        modifier = Modifier.padding(StatsSpacing.Large),
    ) {
        Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "PDF unavailable",
            style = MaterialTheme.typography.titleLarge,
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Button(onClick = onRetry) {
            Text(text = "Try again")
        }
    }
}
