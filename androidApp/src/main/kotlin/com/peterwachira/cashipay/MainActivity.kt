package com.peterwachira.cashipay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme

/**
 * Hosts the Android Compose content for CashiPay.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            CashiPayApp()
        }
    }
}

/**
 * Defines the root of the CashiPay Compose hierarchy.
 */
@Composable
internal fun CashiPayApp() {
    CashiPayTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Text(text = "CashiPay KMP")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CashiPayAppPreview() {
    CashiPayApp()
}
