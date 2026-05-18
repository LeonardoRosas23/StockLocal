package com.example.stocklocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stocklocal.data.repository.PreferencesRepository
import com.example.stocklocal.data.repository.StockRepository
import com.example.stocklocal.di.TokenProvider
import com.example.stocklocal.ui.StockLocalNavigation
import com.example.stocklocal.ui.theme.StockLocalTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var tokenProvider: TokenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            val savedToken = preferencesRepository.apiToken.first()
            if (savedToken.isNotEmpty()) {
                tokenProvider.token = savedToken
            }
        }
        enableEdgeToEdge()
        setContent {
            StockLocalTheme {
                StockLocalNavigation(
                    preferencesRepository = preferencesRepository
                )
            }
        }
    }
}