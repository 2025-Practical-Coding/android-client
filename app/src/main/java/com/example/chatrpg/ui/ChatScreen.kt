package com.example.chatrpg.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatrpg.model.SenderType
import com.example.chatrpg.ui.screen.chat.ChatBubble
import com.example.chatrpg.ui.screen.chat.ChatInput
import com.example.chatrpg.viewmodel.ChatViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.chatMessages.collectAsState()
    val opening by viewModel.openingMessage.collectAsState()
    val affinity by viewModel.affinity.collectAsState()
    val convCount by viewModel.convCount.collectAsState()
    val convLimit by viewModel.convLimit.collectAsState()
    val currentChar by viewModel.currentCharacter.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val narration by viewModel.narrationMessage.collectAsState()
    val teammates by viewModel.teammates.collectAsState()
    val backgroundResId = getBackgroundForRegion(selectedRegion)

    var showTeammates by remember { mutableStateOf(false) }
    val lastAiMsg = messages.lastOrNull { it.sender == SenderType.AI }
    var isGameOver by remember { mutableStateOf(false) }
    var gameResult by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.initializeGame()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentChar?.let { char ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (char.subtitle.isNotBlank()) "${char.name} (${char.subtitle})" else char.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "호감도: $affinity",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = affinity / convLimit.toFloat().coerceAtLeast(1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "대화 횟수: $convCount / $convLimit",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showTeammates = !showTeammates }) {
                    Text("팀원 보기")
                }
            }

            if (showTeammates && teammates.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF222222).copy(alpha = 0.9f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💠 현재 팀원 (${teammates.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFFEB3B)
                        )
                        teammates.forEach {
                            Text(
                                text = "- ${it.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Divider()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                reverseLayout = false
            ) {
                item {
                    if (opening.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = opening,
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    shadow = Shadow(
                                        color = Color.Black,
                                        offset = Offset(2f, 2f),
                                        blurRadius = 4f
                                    )
                                )
                            )
                        }
                    }
                }

                items(messages) { msg ->
                    ChatBubble(
                        message = msg.message,
                        isUser = msg.sender == SenderType.USER,
                        aiName = msg.aiName
                    )

                    if (msg == lastAiMsg && narration.isNotBlank()) {
                        Text(
                            text = "${msg.aiName}: $narration",
                            color = Color(0xFFFFD54F),
                            style = TextStyle(
                                fontSize = 13.sp,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(1f, 1f),
                                    blurRadius = 2f
                                )
                            ),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                        )
                    }

                    if (msg.isGoodbye) {
                        LaunchedEffect("goodbye") {
                            kotlinx.coroutines.delay(10000)
                            viewModel.initializeGame()
                        }
                    }

                    if (msg.message.contains("게임 종료")) {
                        isGameOver = true
                        gameResult = msg.message
                    }
                }
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = gameResult,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            } else {
                ChatInput(onSend = { userInput ->
                    viewModel.sendMessage(userInput)
                })
            }
        }
    }
}
