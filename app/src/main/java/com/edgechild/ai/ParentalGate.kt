package com.edgechild.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun ParentalGateDialog(
    onVerified: () -> Unit,
    onDismiss: () -> Unit
) {
    // Generate random single-digit multiplication for parent verification
    val num1 = remember { Random.nextInt(3, 9) }
    val num2 = remember { Random.nextInt(3, 9) }
    val correctAnswer = num1 * num2

    var userAnswer by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("🛡️ Parental Gate", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Please solve this math problem to continue:", fontSize = 12.sp, color = Color(0xFF546E7A))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFE8EAF6),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "What is $num1 × $num2 ?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF283593),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { 
                        userAnswer = it
                        showError = false
                    },
                    label = { Text("Enter answer") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(160.dp)
                )

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("❌ Incorrect answer. Try again!", color = Color(0xFFD32F2F), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (userAnswer.trim().toIntOrNull() == correctAnswer) {
                        onVerified()
                    } else {
                        showError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
            ) {
                Text("Verify & Enter", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF757575))
            }
        }
    )
}
