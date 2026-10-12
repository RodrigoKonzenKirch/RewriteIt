# Project Plan

Create a Rewrite Text application using GenAI Rewrite API from ML Kit (Google ML Kit / On-device GenAI Rewrite Text API). UI in Jetpack Compose.
The user can input text and choose, from a dropdown menu, the rewrite style: Elaborate, Emojify, Shorten, Friendly, Professional, Rephrase.
When the user taps the "Rewrite" button, the app uses the ML Kit API to rewrite the user's input text based on the selected style and displays the result.

## Project Brief

# Project Brief: Rewrite Text App

## Features
1. **Text Input & Editing**: A clean, multi-line text field allowing users to enter, paste, or modify text to be rewritten.
2. **Style Selection Dropdown**: Dropdown menu allowing selection among preset rewrite styles: *Elaborate*, *Emojify*, *Shorten*, *Friendly*, *Professional*, and *Rephrase*.
3. **On-Device GenAI Text Rewriting**: Direct integration with Google ML Kit's On-Device GenAI Rewrite Text API to process input text locally according to the chosen style.
4. **Result View & Clipboard Action**: Display area showing the rewritten output text with a single-tap action to copy the result to the device clipboard.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library (`androidx.compose.material3.adaptive`)
- **Architecture & Asynchronous Core**: ViewModel, StateFlow, Kotlin Coroutines
- **On-Device AI Engine**: Google ML Kit On-Device GenAI Rewrite Text API

## Implementation Steps
**Total Duration:** 13h 6m 51s

### Task_1_SetupDependenciesAndViewModel: Configure project dependencies for ML Kit Rewrite API, Jetpack Compose Material 3, ViewModel, and Coroutines. Implement ViewModel and state handling for text input, selected style (Elaborate, Emojify, Shorten, Friendly, Professional, Rephrase), model status, and rewritten result.
- **Status:** COMPLETED
- **Updates:** Configured com.google.mlkit:genai-rewriting:1.0.0-beta1 dependency. Created RewriteStyle enum (Elaborate, Emojify, Shorten, Friendly, Professional, Rephrase), ModelStatus enum, RewriteUiState data class, RewriterRepository interface & MlKitRewriterRepository implementation, RewriteViewModel, and unit tests. Verified `./gradlew assembleDebug` and `./gradlew testDebugUnitTest` passed.
- **Acceptance Criteria:**
  - Dependencies configured for ML Kit Rewriter and Compose M3
  - ViewModel manages state for input text, style selection, loading state, and rewritten result
  - build pass
- **Duration:** 16m 56s

### Task_2_MLKitRewriterIntegration: Integrate Google ML Kit On-Device GenAI Rewrite Text API into repository/service layer to handle model initialization, download state management, text rewriting according to selected style, and error handling.
- **Status:** COMPLETED
- **Updates:** Integrated Google ML Kit GenAI Rewriting APIs into `MlKitRewriterRepository` with mapping for all 6 styles (ELABORATE, EMOJIFY, SHORTEN, FRIENDLY, PROFESSIONAL, REPHRASE), feature status checking, model download flow, and error handling. Verified unit tests (`./gradlew testDebugUnitTest`) and build (`./gradlew assembleDebug`) passed.
- **Acceptance Criteria:**
  - ML Kit Rewrite Text API integrated locally
  - Handles preset styles: Elaborate, Emojify, Shorten, Friendly, Professional, Rephrase
  - Model downloading and error states handled properly
  - build pass
- **Duration:** 12h 45m 6s

### Task_3_UIImplementation: Implement Jetpack Compose UI using Material 3. Build multi-line text input, style selection dropdown menu, Rewrite button with loading indicator, and result view with a single-tap copy to clipboard action.
- **Status:** COMPLETED
- **Updates:** Implemented Compose UI in `RewriteScreen.kt` with Material 3 styling: multi-line text field with clear button, ExposedDropdownMenuBox for 6 style options (Elaborate, Emojify, Shorten, Friendly, Professional, Rephrase), Rewrite action button with circular progress indicator, Model Status Banner, and Result Card with Copy to Clipboard and Snackbar notification. Connected UI to `MainActivity`. Verified build and unit tests pass.
- **Acceptance Criteria:**
  - Clean multi-line input field and style dropdown implemented
  - Rewrite button triggers ML Kit processing and displays output
  - Copy to clipboard action working on result text
  - build pass
- **Duration:** 4m 49s

### Task_4_RunAndVerify: Build and execute the application. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - Application verified and aligned with user requirements
- **StartTime:** 2026-09-30 09:57:56 GMT-03:00

