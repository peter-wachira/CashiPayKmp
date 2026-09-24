package com.peterwachira.cashipay.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme

/**
 * Displays a payment form field with optional supporting or error text.
 */
@Composable
internal fun PaymentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val message = supportingText

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        label = {
            Text(text = label)
        },
        supportingText = if (message != null) {
            {
                Text(text = message)
            }
        } else {
            null
        },
        isError = isError,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Preview(showBackground = true)
@Composable
private fun PaymentTextFieldPreview() {
    CashiPayTheme {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PaymentTextField(
                    value = "customer@example.com",
                    onValueChange = {},
                    label = "Recipient email",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )

                PaymentTextField(
                    value = "invalid-email",
                    onValueChange = {},
                    label = "Recipient email",
                    supportingText = "Enter a valid email address",
                    isError = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )
            }
        }
    }
}