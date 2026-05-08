package com.example.stocklocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stocklocal.data.repository.PreferencesRepository
import com.example.stocklocal.ui.StockLocalNavigation
import com.example.stocklocal.ui.theme.StockLocalTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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