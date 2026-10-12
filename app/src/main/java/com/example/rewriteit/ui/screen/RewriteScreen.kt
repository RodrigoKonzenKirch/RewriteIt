package com.example.rewriteit.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rewriteit.model.ModelStatus
import com.example.rewriteit.model.RewriteStyle
import com.example.rewriteit.model.RewriteUiState
import com.example.rewriteit.ui.theme.RewriteItTheme
import com.example.rewriteit.ui.viewmodel.RewriteViewModel
import kotlinx.coroutines.launch

@Composable
fun RewriteScreen(
    viewModel: RewriteViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RewriteScreenContent(
        uiState = uiState,
        onInputTextChanged = viewModel::onInputTextChanged,
        onStyleSelected = viewModel::onStyleSelected,
        onRewriteClicked = viewModel::rewriteText,
        onDownloadModelClicked = viewModel::downloadModel,
        onCheckModelStatusClicked = viewModel::checkModelStatus,
        onClearInputClicked = viewModel::clearInput,
        onClearResultClicked = viewModel::clearResult,
        onSwapResultClicked = viewModel::swapResultToInput,
        onDismissError = viewModel::dismissError,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewriteScreenContent(
    uiState: RewriteUiState,
    onInputTextChanged: (String) -> Unit,
    onStyleSelected: (RewriteStyle) -> Unit,
    onRewriteClicked: () -> Unit,
    onDownloadModelClicked: () -> Unit,
    onCheckModelStatusClicked: () -> Unit,
    onClearInputClicked: () -> Unit,
    onClearResultClicked: () -> Unit,
    onSwapResultClicked: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            onDismissError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Rewrite It",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Model status indicator / banner
            ModelStatusBanner(
                modelStatus = uiState.modelStatus,
                downloadProgress = uiState.downloadProgress,
                onDownloadClicked = onDownloadModelClicked,
                onRetryClicked = onCheckModelStatusClicked
            )

            // Style selector dropdown
            StyleDropdownSelector(
                selectedStyle = uiState.selectedStyle,
                onStyleSelected = onStyleSelected,
                enabled = !uiState.isLoading
            )

            // Multi-line Input Text Field
            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = onInputTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                label = { Text("Input Text") },
                placeholder = { Text("Enter or paste text to rewrite...") },
                trailingIcon = {
                    if (uiState.inputText.isNotEmpty()) {
                        IconButton(onClick = onClearInputClicked) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear input"
                            )
                        }
                    }
                },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "${uiState.inputText.length} characters",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            // Action Button
            val isDownloading = uiState.modelStatus == ModelStatus.DOWNLOADING
            val isBusy = uiState.isLoading || isDownloading
            val buttonEnabled = uiState.inputText.isNotBlank() && !isBusy

            Button(
                onClick = {
                    keyboardController?.hide()
                    onRewriteClicked()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = buttonEnabled,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isBusy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isDownloading) "Downloading Model..." else "Rewriting...",
                        style = MaterialTheme.typography.labelLarge
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rewrite",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Rewritten Result View
            AnimatedVisibility(
                visible = uiState.rewrittenResult.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Rewritten Result (${uiState.selectedStyle.displayName})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = onClearResultClicked) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear result"
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = uiState.rewrittenResult,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Copy button
                            FilledTonalButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(uiState.rewrittenResult))
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Copied to clipboard")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy")
                            }

                            // Swap to Input button
                            OutlinedButton(
                                onClick = {
                                    onSwapResultClicked()
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Moved result to input")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SwapVert,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Use as Input")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ModelStatusBanner(
    modelStatus: ModelStatus,
    downloadProgress: Float,
    onDownloadClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (modelStatus) {
                ModelStatus.READY -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ModelStatus.DOWNLOADING, ModelStatus.CHECKING -> MaterialTheme.colorScheme.surfaceVariant
                ModelStatus.ERROR, ModelStatus.UNAVAILABLE -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                ModelStatus.UNCHECKED -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    when (modelStatus) {
                        ModelStatus.READY -> {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "On-Device AI Model Ready",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        ModelStatus.DOWNLOADING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Downloading On-Device AI Model...",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        ModelStatus.CHECKING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Checking AI Model Status...",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        ModelStatus.UNCHECKED -> {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Model download may be required",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        ModelStatus.ERROR -> {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Model download or check failed",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        ModelStatus.UNAVAILABLE -> {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "On-device AI not available on device",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                if (modelStatus == ModelStatus.UNCHECKED || modelStatus == ModelStatus.ERROR) {
                    OutlinedButton(
                        onClick = onDownloadClicked,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download")
                    }
                } else if (modelStatus == ModelStatus.UNAVAILABLE) {
                    IconButton(onClick = onRetryClicked) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Retry check"
                        )
                    }
                }
            }

            if (modelStatus == ModelStatus.DOWNLOADING) {
                if (downloadProgress > 0f) {
                    LinearProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleDropdownSelector(
    selectedStyle: RewriteStyle,
    onStyleSelected: (RewriteStyle) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedStyle.displayName,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Rewrite Style") },
            supportingText = { Text(selectedStyle.description) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = enabled)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            RewriteStyle.entries.forEach { style ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = style.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (style == selectedStyle) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = style.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onStyleSelected(style)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RewriteScreenPreview() {
    RewriteItTheme {
        RewriteScreenContent(
            uiState = RewriteUiState(
                inputText = "This is sample text to rewrite.",
                selectedStyle = RewriteStyle.PROFESSIONAL,
                modelStatus = ModelStatus.READY,
                rewrittenResult = "Here is a polished and professional version of your sample text."
            ),
            onInputTextChanged = {},
            onStyleSelected = {},
            onRewriteClicked = {},
            onDownloadModelClicked = {},
            onCheckModelStatusClicked = {},
            onClearInputClicked = {},
            onClearResultClicked = {},
            onSwapResultClicked = {},
            onDismissError = {}
        )
    }
}
