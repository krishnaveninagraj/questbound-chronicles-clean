package com.edgechild.ai

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.edgechild.ai.model.AssistantState

@Composable
fun ChildDashboardScreen(
    currentLanguage: String = "English", 
    onLanguageChange: (String) -> Unit = {},
    onOpenParentDashboard: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AssistantViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()
    val state = uiState.value

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.startListening()
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val (backgroundAvatar, topBar, questionText, micButton, statusOverlay, errorCard, voiceControls) = createRefs()

        // 1. Full-screen background avatar image
        state.illustration?.let { ill ->
            Image(
                painter = painterResource(id = ill.drawableRes),
                contentDescription = ill.category,
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(backgroundAvatar) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.Crop
            )
        }

        // 2. Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌟 QuestBound",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Button(
                onClick = onOpenParentDashboard,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🛡️ Parent", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // 3. Status Overlay (Listening / Thinking)
        if (state.isListening || state.isLoading) {
            Text(
                text = if (state.isListening) "🎙️ Listening..." else "🤔 Thinking...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .constrainAs(statusOverlay) {
                        top.linkTo(topBar.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }

        // 4. Live Question Display
        if (state.query.isNotBlank()) {
            Text(
                text = state.query,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .constrainAs(questionText) {
                        top.linkTo(statusOverlay.bottom, margin = 16.dp, goneMargin = 16.dp)
                        start.linkTo(parent.start, margin = 24.dp)
                        end.linkTo(parent.end, margin = 24.dp)
                    }
            )
        }

        // 5. Error Box
        val isError = state.answer.startsWith("Oops!") || 
                      state.answer.startsWith("The AI servers") || 
                      state.answer.startsWith("The selected") || 
                      state.answer.startsWith("A network error")
                      
        if (isError) {
            Text(
                text = state.answer,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color(0xFFD32F2F).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .constrainAs(errorCard) {
                        top.linkTo(questionText.bottom, margin = 16.dp, goneMargin = 16.dp)
                        start.linkTo(parent.start, margin = 24.dp)
                        end.linkTo(parent.end, margin = 24.dp)
                    }
            )
        }

        // 5.5 Answer Subtitles
        val answerSubtitles = createRef()
        if (state.answer.isNotBlank() && !isError) {
            Text(
                text = state.answer,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .constrainAs(answerSubtitles) {
                        bottom.linkTo(voiceControls.top, margin = 16.dp)
                        start.linkTo(parent.start, margin = 24.dp)
                        end.linkTo(parent.end, margin = 24.dp)
                    }
            )
        }

        // 6. Voice Controls
        if (state.answer.isNotBlank() && !isError) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
                    .constrainAs(voiceControls) {
                        bottom.linkTo(micButton.top, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                TextButton(onClick = { viewModel.stopSpeaking() }) {
                    Text("⏹️ Stop", fontSize = 14.sp, color = Color.White)
                }
                TextButton(onClick = { viewModel.submitQuery(state.query) }) {
                    Text("🔊 Replay", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 7. Giant Microphone Button
        FloatingActionButton(
            onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) -> {
                        viewModel.startListening()
                    }
                    else -> {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            },
            modifier = Modifier
                .size(90.dp)
                .constrainAs(micButton) {
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            shape = CircleShape,
            containerColor = if (state.isListening) Color(0xFF4CAF50) else Color(0xFFFF5722)
        ) {
            Text("🎙️", fontSize = 40.sp)
        }
    }
}
