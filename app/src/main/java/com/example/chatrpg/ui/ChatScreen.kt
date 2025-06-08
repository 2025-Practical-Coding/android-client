//package com.example.chatrpg.ui
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.chatrpg.model.SenderType
//import com.example.chatrpg.ui.screen.chat.ChatBubble
//import com.example.chatrpg.ui.screen.chat.ChatInput
//import com.example.chatrpg.viewmodel.ChatViewModel
//import androidx.compose.foundation.background
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Shadow
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.unit.sp
//
//
//@Composable
//fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
//    val messages by viewModel.chatMessages.collectAsState()
//    val opening by viewModel.openingMessage.collectAsState()
//    val affinity by viewModel.affinity.collectAsState()
//    val convCount by viewModel.convCount.collectAsState()
//    val convLimit by viewModel.convLimit.collectAsState()
//    val currentChar by viewModel.currentCharacter.collectAsState()
//    val selectedRegion by viewModel.selectedRegion.collectAsState()
//
//    val backgroundResId = getBackgroundForRegion(selectedRegion)
//
//    LaunchedEffect(Unit) {
//        viewModel.loadOpening()
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Image(
//            painter = painterResource(id = backgroundResId),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.matchParentSize()
//        )
//
//        Column(modifier = Modifier.fillMaxSize()) {
//            currentChar?.let { char ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(12.dp)
//                ) {
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(16.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
//                        ),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
//                    ) {
//                        Column(modifier = Modifier.padding(12.dp)) {
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    text = if (char.subtitle.isNotBlank()) {
//                                        "${char.name} (${char.subtitle})"
//                                    } else {
//                                        char.name
//                                    },
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier.weight(1f)
//                                )
//                                Text(
//                                    text = "호감도: $affinity",
//                                    style = MaterialTheme.typography.labelSmall
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.height(4.dp))
//
//                            LinearProgressIndicator(
//                                progress = affinity / convLimit.toFloat().coerceAtLeast(1f),
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(6.dp)
//                                    .clip(RoundedCornerShape(3.dp)),
//                                color = MaterialTheme.colorScheme.primary
//                            )
//
//                            Text(
//                                text = "대화 횟수: $convCount / $convLimit",
//                                style = MaterialTheme.typography.bodySmall,
//                                modifier = Modifier.padding(top = 4.dp)
//                            )
//                        }
//                    }
//                }
//            }
//
//            Divider()
//
//            LazyColumn(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(horizontal = 8.dp),
//                reverseLayout = false
//            ) {
//                item {
//                    if (opening.isNotBlank()) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 8.dp, vertical = 4.dp)
//                                .background(
//                                    color = Color.Black.copy(alpha = 0.5f),
//                                    shape = RoundedCornerShape(8.dp)
//                                )
//                                .padding(8.dp)
//                        ) {
//                            Text(
//                                text = opening,
//                                color = Color.White,
//                                style = TextStyle(
//                                    fontSize = 14.sp,
//                                    shadow = Shadow(
//                                        color = Color.Black,
//                                        offset = Offset(2f, 2f),
//                                        blurRadius = 4f
//                                    )
//                                )
//                            )
//                        }
//                    }
//                }
//
//
//                items(messages) { msg ->
//                    ChatBubble(
//                        message = msg.message,
//                        isUser = msg.sender == SenderType.USER,
//                        aiName = msg.aiName
//                    )
//                }
//            }
//
//            ChatInput(onSend = { userInput ->
//                viewModel.sendMessage(userInput)
//            })
//        }
//    }
//}


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
    // ───── ViewModel 상태 수집 ─────
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
    val gameOverMessage = messages.findLast { it.message.contains("게임 종료") }

    // ───── 게임 시작 시 초기화 ─────
    LaunchedEffect(Unit) {
        viewModel.initializeGame()
    }

    // ───── 전체 레이아웃 ─────
    Box(modifier = Modifier.fillMaxSize()) {
        // ── 배경 이미지 설정 ──
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // ───── 상단 정보 및 팀원 버튼 ─────
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── 캐릭터 정보 카드 ──
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
                // ── 팀원 보기 버튼 ──
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showTeammates = !showTeammates }) {
                    Text("팀원 보기")
                }
            }

            // ───── 팀원 목록 표시 ─────
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

                // ── 대화 메시지 ──
                items(messages) { msg ->
                    ChatBubble(
                        message = msg.message,
                        isUser = msg.sender == SenderType.USER,
                        aiName = msg.aiName
                    )

                    // ── 내레이션 메시지 ──
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
                }
            }

            // ───── 게임 종료 메시지 또는 입력창 ─────
            gameOverMessage?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it.message,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            } ?: ChatInput(onSend = { viewModel.sendMessage(it) })
        }
    }
}