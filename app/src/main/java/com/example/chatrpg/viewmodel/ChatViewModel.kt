//package com.example.chatrpg.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.chatrpg.model.*
//import com.example.chatrpg.network.RetrofitInstance
//import com.google.gson.Gson
//import com.google.gson.JsonParser
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import okhttp3.ResponseBody
//import retrofit2.Response
//
//class ChatViewModel : ViewModel() {
//    private val gson = Gson()
//
//    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
//    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages
//
//    private val _openingMessage = MutableStateFlow("")
//    val openingMessage: StateFlow<String> = _openingMessage
//
//    private val _affinity = MutableStateFlow(0)
//    val affinity: StateFlow<Int> = _affinity
//
//    private val _convCount = MutableStateFlow(0)
//    val convCount: StateFlow<Int> = _convCount
//
//    private val _convLimit = MutableStateFlow(10)
//    val convLimit: StateFlow<Int> = _convLimit
//
//    private val _currentCharacter = MutableStateFlow<CharacterInfo?>(null)
//    val currentCharacter: StateFlow<CharacterInfo?> = _currentCharacter
//
//    private val _selectedRegion = MutableStateFlow("도시")
//    val selectedRegion: StateFlow<String> = _selectedRegion
//
//    fun loadOpening() {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.getOpening()
//                if (response.isSuccessful) {
//                    val opening = response.body()?.opening ?: "지역 오프닝을 불러오지 못했습니다."
//                    _openingMessage.value = opening
//                    val slug = response.body()?.slug
//                    slug?.let {
//                        _currentCharacter.value = CharacterInfo(slug = it, name = it, subtitle = "")
//                    }
//                } else {
//                    _openingMessage.value = "Opening API 실패: ${response.code()}"
//                }
//            } catch (e: Exception) {
//                _openingMessage.value = "서버와의 연결에 실패했습니다."
//                _currentCharacter.value = null
//            }
//        }
//    }
//
//    fun sendMessage(userInput: String) {
//        _chatMessages.value += ChatMessage(
//            sender = SenderType.USER,
//            message = userInput
//        )
//
//        viewModelScope.launch {
//            try {
//                val slug = _currentCharacter.value?.slug ?: ""
//                val name = _currentCharacter.value?.name ?: ""
//                val chatRequest = ChatRequest(
//                    slug = slug,
//                    user_input = userInput,
//                    name = name
//                )
////                val response: Response<ResponseBody> = RetrofitInstance.api.postChat(
////                    ChatRequest(slug = slug, user_input = userInput)
////                )
//
//                val response: Response<ResponseBody> =
//                    RetrofitInstance.api.postChat(chatRequest)
//
//
//                if (!response.isSuccessful) {
//                    _chatMessages.value += ChatMessage(
//                        sender = SenderType.AI,
//                        message = "서버 오류: ${response.code()} - ${response.message()}",
//                        aiName = "SYSTEM"
//                    )
//                    return@launch
//                }
//
//                val raw = response.body()?.string() ?: return@launch
//                val element = JsonParser.parseString(raw)
//
//                when {
//                    element.isJsonArray -> {
//                        val list = gson.fromJson(raw, Array<ChatResponse>::class.java).toList()
//
//                        // 1. 첫 메시지: 일반 대화
//                        list.getOrNull(0)?.let {
//                            _chatMessages.value += ChatMessage(
//                                sender = SenderType.AI,
//                                message = it.reply,
//                                aiName = it.character.name,
//                                aiSlug = it.character.slug
//                            )
//                        }
//
//                        // 2. 두 번째 메시지: 작별 멘트
//                        list.getOrNull(1)?.let {
//                            _chatMessages.value += ChatMessage(
//                                sender = SenderType.AI,
//                                message = it.reply,
//                                aiName = it.character.name,
//                                aiSlug = it.character.slug,
//                                isGoodbye = true // <-- ChatMessage에 필드가 있다면 사용
//                            )
//
//                            // 🎯 작별 멘트 후 다음 지역으로 이동
//                            loadNextRegion()
//                        }
//
//                        // 마지막 응답 기준으로 상태 갱신
//                        list.lastOrNull()?.let {
//                            _affinity.value = it.total_affinity
//                            _convCount.value = it.conv_count
//                            _convLimit.value = it.conv_limit
//                        }
//                    }
//
//                    element.isJsonObject -> {
//                        val obj = element.asJsonObject
//
//                        if (obj.has("game_over")) {
//                            val result = gson.fromJson(raw, GameResultResponse::class.java)
//                            _chatMessages.value += ChatMessage(
//                                sender = SenderType.AI,
//                                message = "게임 종료: ${result.result.summary}",
//                                aiName = "SYSTEM"
//                            )
//                        } else {
//                            val res = gson.fromJson(raw, ChatResponse::class.java)
//                            _chatMessages.value += ChatMessage(
//                                sender = SenderType.AI,
//                                message = res.reply,
//                                aiName = res.character.name,
//                                aiSlug = res.character.slug
//                            )
//                            _affinity.value = res.total_affinity
//                            _convCount.value = res.conv_count
//                            _convLimit.value = res.conv_limit
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                _chatMessages.value += ChatMessage(
//                    sender = SenderType.AI,
//                    message = "오류 발생: ${e.message}",
//                    aiName = "SYSTEM"
//                )
//            }
//        }
//    }
//
//    fun loadNextRegion() {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.nextRegion()
//                if (response.isSuccessful) {
//                    val regionName = response.body()?.region ?: "알 수 없음"
//                    val characterSlugs = response.body()?.characters ?: emptyList()
//
//                    _selectedRegion.value = regionName
//                    // 필요 시 characterSlugs로도 추가 처리 가능
//                } else {
//                    _openingMessage.value = "지역 이동 실패: ${response.code()}"
//                }
//            } catch (e: Exception) {
//                _openingMessage.value = "지역 전환 실패: ${e.message}"
//            }
//        }
//    }
//
//
//    fun setCharacter(character: CharacterInfo) {
//        _currentCharacter.value = character
//    }
//
//    fun resetConversation() {
//        _chatMessages.value = emptyList()
//        _convCount.value = 0
//        _affinity.value = 0
//        _currentCharacter.value = null
//        _selectedRegion.value = "도시"
//    }
//}

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
    // ───── 상태 관리 변수들 ─────
    private val _chatMessages = MutableStateFlow(emptyList<ChatMessage>()) // 대화 메시지 리스트
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    private val _openingMessage = MutableStateFlow("") // 지역/캐릭터 오프닝 메시지
    val openingMessage: StateFlow<String> = _openingMessage

    private val _affinity = MutableStateFlow(0) // 현재 캐릭터와의 친밀도
    val affinity: StateFlow<Int> = _affinity

    private val _convCount = MutableStateFlow(0) // 현재 캐릭터와의 대화 횟수
    val convCount: StateFlow<Int> = _convCount

    private val _convLimit = MutableStateFlow(7) // 대화 제한 횟수
    val convLimit: StateFlow<Int> = _convLimit

    private val _currentCharacter = MutableStateFlow<CharacterInfo?>(null) // 현재 대화 중인 캐릭터
    val currentCharacter: StateFlow<CharacterInfo?> = _currentCharacter

    private val _selectedRegion = MutableStateFlow("숲") // 현재 지역 이름
    val selectedRegion: StateFlow<String> = _selectedRegion

    private val _narrationMessage = MutableStateFlow("") // AI 내레이션 메시지
    val narrationMessage: StateFlow<String> = _narrationMessage

    private val _totalRemaining = MutableStateFlow(0) // 전체 남은 대화 횟수
    val totalRemaining: StateFlow<Int> = _totalRemaining

    private val _teammates = MutableStateFlow<List<CharacterInfo>>(emptyList()) // 팀원 목록
    val teammates: StateFlow<List<CharacterInfo>> = _teammates

    private val fixedConvLimit = 7 // 고정 대화 제한값

    // 게임 초기화 (상태 및 오프닝 불러오기)
    fun initializeGame() {
        viewModelScope.launch {
            resetConversation()
            loadState()
            loadOpening()
        }
    }

    // 서버에서 현재 게임 상태 (/state) 불러오기
    private suspend fun loadState() {
        try {
            val state = repository.getState()
            _selectedRegion.value = state.region
            _totalRemaining.value = state.totalRemaining

            state.currentCharacter?.let { char ->
                _currentCharacter.value = char
                _affinity.value = char.affinity
            } ?: run {
                _currentCharacter.value = null
                _affinity.value = 0
            }

            _convLimit.value = fixedConvLimit
            _convCount.value = fixedConvLimit - state.currentRemaining

        } catch (e: Exception) {
            _openingMessage.value = "서버 연결 실패: ${e.message}"
        }
    }

    fun loadOpening() {
        viewModelScope.launch {
            try {
                // 1) 리포지토리에서 OpeningResponse 받기
                val openingResp = repository.getOpening()
                _openingMessage.value = openingResp.opening

                // 2) OpeningResponse.slug 를 꺼내서 currentCharacter 세팅
                val slug = openingResp.slug
                _currentCharacter.value = CharacterInfo(
                    slug     = slug,
                    name     = slug,    // 아직 서버에서 name을 내려주지 않는다면 slug로 임시
                    subtitle = "",       // subtitle이 있다면 같이 채워주세요
                    affinity = 0
                )
            } catch (e: Exception) {
                _openingMessage.value = "Opening 요청 실패: ${e.message}"
                _currentCharacter.value = null
            }
        }
    }

    // 사용자 입력 전송 및 응답 처리 (/chat)
    fun sendMessage(userInput: String) {
        _chatMessages.value += ChatMessage(
            sender = SenderType.USER,
            message = userInput
        )

        viewModelScope.launch {
            try {
                val slug = _currentCharacter.value?.slug ?: ""
                val name = _currentCharacter.value?.name ?: ""

                // user_input 을 반드시 넘겨주셔야 합니다!
                val chatRequest = ChatRequest(
                    slug = slug,
                    name = name,
                    user_input = userInput
                )
//                val result = repository.postChat(ChatRequest(slug, name))
                val result = repository.postChat(chatRequest)

                when (result) {
                    is List<*> -> {
                        val list = result.filterIsInstance<ChatResponse>()

                        // 첫 번째 응답 처리
                        list.getOrNull(0)?.let {
                            _chatMessages.value += ChatMessage(
                                sender = SenderType.AI,
                                message = it.reply,
                                aiName = it.character.name,
                                aiSlug = it.character.slug
                            )
                        }

                        // 작별 인사 응답 처리
                        list.getOrNull(1)?.let {
                            _chatMessages.value += ChatMessage(
                                sender = SenderType.AI,
                                message = it.reply,
                                aiName = it.character.name,
                                aiSlug = it.character.slug,
                                isGoodbye = true
                            )

                            if (_affinity.value >= 5 && !_teammates.value.any { tm -> tm.slug == it.character.slug }) {
                                _teammates.value = _teammates.value + CharacterInfo(
                                    slug = it.character.slug,
                                    name = it.character.name,
                                    subtitle = it.character.subtitle,
                                    affinity = it.total_affinity
                                )
                            }

                            checkGameResult()

                            // 5초 후 게임 재초기화
                            viewModelScope.launch {
                                delay(5000)
                                _chatMessages.value = emptyList()
                                initializeGame()
                            }
                        }

                        // 마지막 응답에서 상태 업데이트
                        list.lastOrNull()?.let {
                            _affinity.value = it.total_affinity
                            _convCount.value = it.conv_count
                            _convLimit.value = it.conv_limit
                            _narrationMessage.value = it.narration

                            checkGameResult()
                        }
                    }

                    // 단일 응답 처리 (기본 대화)
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

                        checkGameResult()
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

    // 게임 종료 조건 체크 (팀원 2명 이상 또는 남은 대화 거의 없음)
    private fun checkGameResult() {
        if (_totalRemaining.value <= 1 && _chatMessages.value.any { it.isGoodbye }) {
            loadResult()
        }
    }

    // 서버로부터 최종 결과 요청 (/result)
    private fun loadResult() {
        viewModelScope.launch {
            try {
                val result = repository.getResult()
                if (result.game_over) {
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

    // 테스트용 수동 캐릭터 설정
    fun setCharacter(character: CharacterInfo) {
        _currentCharacter.value = character
    }

    // 전체 상태 초기화 (게임 재시작 등)
    fun resetConversation() {
        _chatMessages.value = emptyList()
        _convCount.value = 0
        _affinity.value = 0
        _currentCharacter.value = null
        _selectedRegion.value = "숲"
        _totalRemaining.value = 0
    }
}