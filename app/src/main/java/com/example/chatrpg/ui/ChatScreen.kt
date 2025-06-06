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
    // ───── 상태 수집 ─────
    val messages by viewModel.chatMessages.collectAsState()
    val opening by viewModel.openingMessage.collectAsState()
    val affinity by viewModel.affinity.collectAsState()
    val convCount by viewModel.convCount.collectAsState()
    val convLimit by viewModel.convLimit.collectAsState()
    val currentChar by viewModel.currentCharacter.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val narration by viewModel.narrationMessage.collectAsState()
    val backgroundResId = getBackgroundForRegion(selectedRegion)

    // ───── 상태 및 게임 종료 처리 ─────
    val lastAiMsg = messages.lastOrNull { it.sender == SenderType.AI }
    var isGameOver by remember { mutableStateOf(false) }
    var gameResult by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.initializeGame()
    }

    // ───── 배경 이미지 포함 전체 레이아웃 ─────
    Box(modifier = Modifier.fillMaxSize()) {
        // ── 배경 이미지 ──
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // ── 전체 UI 구성 ──
        Column(modifier = Modifier.fillMaxSize()) {

            // ───── 캐릭터 정보 카드 ─────
            currentChar?.let { char ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (char.subtitle.isNotBlank()) {
                                        "${char.name} (${char.subtitle})"
                                    } else {
                                        char.name
                                    },
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
            }

            Divider()

            // ───── 메시지 리스트 ─────
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                reverseLayout = false
            ) {
                // ── 오프닝 메시지 ──
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

                // ── 대화 메시지 반복 표시 ──
                items(messages) { msg ->
                    ChatBubble(
                        message = msg.message,
                        isUser = msg.sender == SenderType.USER,
                        aiName = msg.aiName
                    )

                    // ── AI 메시지 이후 내레이션 출력 ──
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
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 4.dp)
                        )
                    }

                    // ── 작별 인사 시 자동 초기화 ──
                    if (msg.isGoodbye) {
                        LaunchedEffect("goodbye") {
                            kotlinx.coroutines.delay(10000)
                            viewModel.initializeGame()
                        }
                    }

                    // ── 게임 종료 시 처리 ──
                    if (msg.message.contains("게임 종료")) {
                        isGameOver = true
                        gameResult = msg.message
                    }
                }
            }

            // ───── 게임 종료 여부에 따른 하단 UI ─────
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
                // ── 유저 입력 영역 ──
                ChatInput(onSend = { userInput ->
                    viewModel.sendMessage(userInput)
                })
            }
        }
    }
}
