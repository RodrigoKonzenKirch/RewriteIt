package com.example.rewriteit.data

import com.example.rewriteit.model.ModelStatus
import com.example.rewriteit.model.RewriteStyle
import com.google.mlkit.genai.rewriting.RewriterOptions
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MlKitRewriterRepositoryTest {

    private val repository = MlKitRewriterRepository()

    @Test
    fun testStyleToOutputTypeMapping() {
        assertEquals(RewriterOptions.OutputType.ELABORATE, repository.mapStyleToOutputType(RewriteStyle.ELABORATE))
        assertEquals(RewriterOptions.OutputType.EMOJIFY, repository.mapStyleToOutputType(RewriteStyle.EMOJIFY))
        assertEquals(RewriterOptions.OutputType.SHORTEN, repository.mapStyleToOutputType(RewriteStyle.SHORTEN))
        assertEquals(RewriterOptions.OutputType.FRIENDLY, repository.mapStyleToOutputType(RewriteStyle.FRIENDLY))
        assertEquals(RewriterOptions.OutputType.PROFESSIONAL, repository.mapStyleToOutputType(RewriteStyle.PROFESSIONAL))
        assertEquals(RewriterOptions.OutputType.REPHRASE, repository.mapStyleToOutputType(RewriteStyle.REPHRASE))
    }

    @Test
    fun testCheckModelStatusFlow() = runTest {
        val statuses = repository.checkModelStatus().toList()
        assertTrue(statuses.contains(ModelStatus.CHECKING))
        assertTrue(statuses.contains(ModelStatus.READY))
    }

    @Test
    fun testDownloadModel() = runTest {
        val success = repository.downloadModel()
        assertTrue(success)
    }

    @Test
    fun testRewriteAllStyles() = runTest {
        for (style in RewriteStyle.entries) {
            val result = repository.rewrite("Sample text to rewrite", style)
            assertTrue("Expected success for style $style", result.isSuccess)
            val rewrittenText = result.getOrNull()
            assertTrue(!rewrittenText.isNullOrBlank())
        }
    }

    @Test
    fun testRewriteEmptyTextFails() = runTest {
        val result = repository.rewrite("   ", RewriteStyle.REPHRASE)
        assertTrue(result.isFailure)
        assertEquals("Please enter text to rewrite", result.exceptionOrNull()?.message)
    }
}
