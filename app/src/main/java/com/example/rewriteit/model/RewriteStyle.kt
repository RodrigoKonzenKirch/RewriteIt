package com.example.rewriteit.model

enum class RewriteStyle(
    val id: String,
    val displayName: String,
    val description: String
) {
    ELABORATE("elaborate", "Elaborate", "Expand text with more detail and depth"),
    EMOJIFY("emojify", "Emojify", "Add expressive emojis to match the tone"),
    SHORTEN("shorten", "Shorten", "Make text concise and to the point"),
    FRIENDLY("friendly", "Friendly", "Warm, casual, and friendly tone"),
    PROFESSIONAL("professional", "Professional", "Polished, formal, and professional tone"),
    REPHRASE("rephrase", "Rephrase", "Rephrase with alternative wording")
}
