package com.example.rewriteit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.rewriteit.ui.screen.RewriteScreen
import com.example.rewriteit.ui.theme.RewriteItTheme
import com.example.rewriteit.ui.viewmodel.RewriteViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: RewriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RewriteItTheme {
                RewriteScreen(viewModel = viewModel)
            }
        }
    }
}
