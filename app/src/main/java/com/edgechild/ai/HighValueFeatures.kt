package com.edgechild.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object HighValueFeatures {

    // 1. "Ask Me Why" Curiosity Mode - Daily random prompt generator
    fun getDailyCuriosityPrompt(): String {
        val prompts = listOf(
            "Did you know whales sing underwater? 🐋",
            "Did you know honey never spoils? 🍯",
            "Did you know octopus has three hearts? 🐙",
            "Did you know stars twinkle because of air? ✨"
        )
        return prompts.random()
    }

    // 2. Gamified Learning Badges State Manager
    // userStars: delegated mutableStateOf triggers recomposition when incremented
    var userStars by mutableStateOf(12)

    // unlockedBadges: SnapshotStateList ensures Compose recomposes when a badge is added
    val unlockedBadges = mutableStateListOf("Space Explorer 🚀", "Curious Mind 🧠")

    fun rewardQuestionAsked() {
        userStars += 5
        if (userStars >= 30 && !unlockedBadges.contains("Science Wizard 🧙‍♂️")) {
            unlockedBadges.add("Science Wizard 🧙‍♂️")
        }
    }
}

@Composable
fun CuriosityPopupCard(onPromptClicked: (String) -> Unit) {
    val promptText = remember { HighValueFeatures.getDailyCuriosityPrompt() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡 'Ask Me Why' Curiosity Pop-Up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF57F17)
                )
                Badge(containerColor = Color(0xFFFFB300)) {
                    Text("+5 ⭐", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = promptText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF37474F)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onPromptClicked(promptText) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Explore with Mascot!", color = Color(0xFF37474F), fontWeight = FontWeight.Bold)
            }
        }
    }
}
