package com.example.rewriteit

import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.rewriting.RewriterOptions
import com.google.mlkit.genai.rewriting.RewritingRequest
import com.google.mlkit.genai.rewriting.RewritingResult
import com.google.mlkit.genai.rewriting.RewritingSuggestion
import org.junit.Assert.assertNotNull
import org.junit.Test

class ReflectionTest {

    @Test
    fun testApiCompilation() {
        val elaborateType = RewriterOptions.OutputType.ELABORATE
        val emojifyType = RewriterOptions.OutputType.EMOJIFY
        val shortenType = RewriterOptions.OutputType.SHORTEN
        val friendlyType = RewriterOptions.OutputType.FRIENDLY
        val professionalType = RewriterOptions.OutputType.PROFESSIONAL
        val rephraseType = RewriterOptions.OutputType.REPHRASE

        val available = FeatureStatus.AVAILABLE
        val downloadable = FeatureStatus.DOWNLOADABLE
        val downloading = FeatureStatus.DOWNLOADING
        val unavailable = FeatureStatus.UNAVAILABLE

        val req = RewritingRequest.builder("Hello").build()
        assertNotNull(req)

        val callback = object : DownloadCallback {
            override fun onDownloadStarted(bytesToDownload: Long) {}
            override fun onDownloadCompleted() {}
            override fun onDownloadProgress(bytesDownloaded: Long) {}
            override fun onDownloadFailed(exception: GenAiException) {}
        }
        assertNotNull(callback)
    }
}
