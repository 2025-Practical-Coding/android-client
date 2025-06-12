package com.example.chatrpg.viewmodel

import android.util.Log
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

    private val _chatMessages = MutableStateFlow(emptyList<ChatMessage>())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    private val _openingMessage = MutableStateFlow("")
    val openingMessage: StateFlow<String> = _openingMessage

    private val _affinity = MutableStateFlow(0)
    val affinity: StateFlow<Int> = _affinity

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

    private val _teammates = MutableStateFlow<List<CharacterSimple>>(emptyList())
    val teammates: StateFlow<List<CharacterSimple>> = _teammates

    private val _maxAffinity = MutableStateFlow(20)
    val maxAffinity: StateFlow<Int> = _maxAffinity

    private val _convCount = MutableStateFlow(0)
    val convCount: StateFlow<Int> = _convCount

    fun initializeGame() {
        viewModelScope.launch {
            val loaded = loadState()
            if (loaded?.currentCharacter != null) {
                loadOpening()
            }
        }
    }

    private suspend fun loadState(): StateResponse? {
        return try {
            val state = repository.getState()
            Log.d("ChatViewModel", "상태: $state")
            _selectedRegion.value = state.region
            _totalRemaining.value = state.totalRemaining
            _convLimit.value = state.convLimit
            _maxAffinity.value = state.maxAffinity

            state.currentCharacter?.let {
                _currentCharacter.value = it
                _affinity.value = it.affinity
                _convCount.value = it.convCount
            } ?: run {
                _currentCharacter.value = null
                _affinity.value = 0
                _convCount.value = 0
            }

            state
        } catch (e: Exception) {
            _openingMessage.value = "서버 연결 실패: ${e.message}"
            null
        }
    }

    fun loadOpening() {
        viewModelScope.launch {
            try {
                val opening = repository.getOpening()
                Log.d("ChatViewModel", "opening: $opening")
                _openingMessage.value = opening.opening
            } catch (e: Exception) {
                _openingMessage.value = "Opening 요청 실패: ${e.message}"
            }
        }
    }

    fun sendMessage(userInput: String) {
        _chatMessages.value += ChatMessage(
            sender = SenderType.USER,
            message = userInput
        )

        viewModelScope.launch {
            try {
                val slug = _currentCharacter.value?.slug.orEmpty()
                val name = _currentCharacter.value?.name.orEmpty()
                val chatRequest = ChatRequest(slug, name, userInput)

                val result = repository.postChat(chatRequest) as ChatEndWrapper
                val responses = result.responses

                responses.forEachIndexed { index, response ->
                    _chatMessages.value += ChatMessage(
                        sender = SenderType.AI,
                        message = response.reply,
                        aiName = response.character.name,
                        aiSlug = response.character.slug,
                        isGoodbye = (index == 1 && responses.size > 1) // 두 번째 응답이 goodbye
                    )

                    updateChatState(response)
                }

                when {
                    result.gameOver -> {
                        // 게임 종료
                        _chatMessages.value += ChatMessage(
                            sender = SenderType.SYSTEM,
                            message = "[게임이 종료되었습니다. 결과를 불러옵니다.]"
                        )
                        delay(3000)
                        loadResult()
                    }
                    responses.size > 1 -> {
                        // 대화 종료 → 다음 캐릭터로
                        _chatMessages.value += ChatMessage(
                            sender = SenderType.SYSTEM,
                            message = "[${_currentCharacter.value?.name}와의 대화 종료. 다음 캐릭터로 이동합니다.]"
                        )
                        delay(10000)
                        _chatMessages.value = emptyList()
                        initializeGame()
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

    private fun updateChatState(response: ChatResponse) {
        _affinity.value = response.totalAffinity
        _convCount.value = response.convCount
        _convLimit.value = response.convLimit
        _narrationMessage.value = response.narration
        updateAllies(response)
    }

    private fun updateAllies(response: ChatResponse) {
        response.allies?.let {
            Log.d("ChatViewModel", "🔥 allies: $it")
            _teammates.value = it
        }
    }

    private fun loadResult() {
        viewModelScope.launch {
            try {
                val result = repository.getResult()
                if (result.gameOver) {
                    _chatMessages.value += ChatMessage(
                        sender = SenderType.AI,
                        message = "게임 종료: ${result.narration}",
                        aiName = "SYSTEM"
                    )
                }
            } catch (e: Exception) {
                _chatMessages.value += ChatMessage(
                    sender = SenderType.AI,
                    message = "결과 요청 실패: ${e.message}",
                    aiName = "SYSTEM"
                )
            }
        }
    }
}