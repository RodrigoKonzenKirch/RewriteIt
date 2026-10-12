## Rewrite It

App that uses the ML Kit API to rewrite the user's input text based on the selected style and displays the result.

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
