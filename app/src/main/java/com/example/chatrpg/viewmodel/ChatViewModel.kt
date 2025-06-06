package com.example.chatrpg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatrpg.model.*
import com.example.chatrpg.repository.ChatRepository
import com.example.chatrpg.repository.RealChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ChatViewModel(
    private val repository: ChatRepository = RealChatRepository()
) : ViewModel() {

    // ────────────── 상태 관리 변수 ──────────────

    private val _chatMessages = MutableStateFlow(emptyList<ChatMessage>())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    private val _openingMessage = MutableStateFlow("")
    val openingMessage: StateFlow<String> = _openingMessage

    private val _affinity = MutableStateFlow(0)
    val affinity: StateFlow<Int> = _affinity

    private val _convCount = MutableStateFlow(0)
    val convCount: StateFlow<Int> = _convCount

    private val _convLimit = MutableStateFlow(7)
    val convLimit: StateFlow<Int> = _convLimit

    private val _currentCharacter = MutableStateFlow<CharacterInfo?>(null)
    val currentCharacter: StateFlow<CharacterInfo?> = _currentCharacter

    private val _selectedRegion = MutableStateFlow("숲")
    val selectedRegion: StateFlow<String> = _selectedRegion

    private val _narrationMessage = MutableStateFlow("")
    val narrationMessage: StateFlow<String> = _narrationMessage

    private val _totalRemaining = MutableStateFlow(0)
    val totalRemaining: StateFlow<Int> = _totalRemaining

    private val _teammates = MutableStateFlow<List<CharacterInfo>>(emptyList())
    val teammates: StateFlow<List<CharacterInfo>> = _teammates

    private val fixedConvLimit = 7 // 서버 기준 대화 제한 고정값

    // ────────────── 게임 초기화 ──────────────

    fun initializeGame() {
        viewModelScope.launch {
            loadState()    // 상태 불러오기
            loadOpening()  // 오프닝 메시지 불러오기
        }
    }

    // ────────────── 서버 상태 불러오기 (/state) ──────────────

    private suspend fun loadState() {
        try {
            val state = repository.getState()
            _selectedRegion.value = state.region
            _totalRemaining.value = state.total_remaining

            state.current_character?.let { char ->
                _currentCharacter.value = CharacterInfo(
                    slug = char.slug,
                    name = char.name,
                    subtitle = char.subtitle
                )
                _affinity.value = char.affinity
            } ?: run {
                _currentCharacter.value = null
                _affinity.value = 0
            }

            _convLimit.value = fixedConvLimit
            _convCount.value = fixedConvLimit - state.current_remaining

        } catch (e: Exception) {
            _openingMessage.value = "서버 연결 실패: ${e.message}"
        }
    }

    // ────────────── 오프닝 메시지 불러오기 (/opening) ──────────────

    fun loadOpening() {
        viewModelScope.launch {
            try {
                val opening = repository.getOpening()
                _openingMessage.value = opening.opening
            } catch (e: Exception) {
                _openingMessage.value = "Opening 요청 실패: ${e.message}"
            }
        }
    }

    // ────────────── 유저 입력 처리 및 응답 수신 (/chat) ──────────────

    fun sendMessage(userInput: String) {
        _chatMessages.value += ChatMessage(
            sender = SenderType.USER,
            message = userInput
        )

        viewModelScope.launch {
            try {
                val slug = _currentCharacter.value?.slug ?: ""
                val result = repository.postChat(ChatRequest(slug, userInput))

                when (result) {
                    is List<*> -> {
                        val list = result.filterIsInstance<ChatResponse>()

                        list.getOrNull(0)?.let {
                            _chatMessages.value += ChatMessage(
                                sender = SenderType.AI,
                                message = it.reply,
                                aiName = it.character.name,
                                aiSlug = it.character.slug
                            )
                        }

                        list.getOrNull(1)?.let {
                            _chatMessages.value += ChatMessage(
                                sender = SenderType.AI,
                                message = it.reply,
                                aiName = it.character.name,
                                aiSlug = it.character.slug,
                                isGoodbye = true
                            )

                            // → 팀원 영입 조건 (호감도 >= 10)
                            if (_affinity.value >= 10 && !_teammates.value.any { tm -> tm.slug == it.character.slug }) {
                                _teammates.value = _teammates.value + it.character
                            }

                            // 작별 인사 후 5초 대기 → 초기화
                            viewModelScope.launch {
                                delay(5000)
                                _chatMessages.value = emptyList()
                                initializeGame()
                            }
                        }

                        list.lastOrNull()?.let {
                            _affinity.value = it.total_affinity
                            _convCount.value = it.conv_count
                            _convLimit.value = it.conv_limit
                            _narrationMessage.value = it.narration

                            if (_totalRemaining.value <= 1) {
                                loadResult()
                            }
                        }
                    }

                    is ChatResponse -> {
                        _chatMessages.value += ChatMessage(
                            sender = SenderType.AI,
                            message = result.reply,
                            aiName = result.character.name,
                            aiSlug = result.character.slug
                        )
                        _affinity.value = result.total_affinity
                        _convCount.value = result.conv_count
                        _convLimit.value = result.conv_limit
                        _narrationMessage.value = result.narration
                    }

                    is GameResultResponse -> {
                        _chatMessages.value += ChatMessage(
                            sender = SenderType.AI,
                            message = "게임 종료: ${result.result.summary}",
                            aiName = "SYSTEM"
                        )
                    }
                }
            } catch (e: Exception) {
                _chatMessages.value += ChatMessage(
                    sender = SenderType.AI,
                    message = "오류 발생: ${e.message}",
                    aiName = "SYSTEM"
                )
            }
        }
    }

    // ────────────── 게임 종료 결과 요청 (/result) ──────────────

    private fun loadResult() {
        viewModelScope.launch {
            try {
                val result = repository.getResult()
                _chatMessages.value += ChatMessage(
                    sender = SenderType.AI,
                    message = "게임 종료: ${result.result.summary}",
                    aiName = "SYSTEM"
                )
            } catch (e: Exception) {
                _chatMessages.value += ChatMessage(
                    sender = SenderType.AI,
                    message = "결과 요청 실패: ${e.message}",
                    aiName = "SYSTEM"
                )
            }
        }
    }

    // ────────────── 수동 캐릭터 설정 (테스트용 등) ──────────────

    fun setCharacter(character: CharacterInfo) {
        _currentCharacter.value = character
    }

    // ────────────── 전체 상태 초기화 ──────────────

    fun resetConversation() {
        _chatMessages.value = emptyList()
        _convCount.value = 0
        _affinity.value = 0
        _currentCharacter.value = null
        _selectedRegion.value = "숲"
        _totalRemaining.value = 0
        _teammates.value = emptyList()
    }
}
