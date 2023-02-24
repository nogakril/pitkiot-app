package com.example.pitkiot.viewmodel.factory

// import com.example.pitkiot.viewmodel.GameViewModel
//
// class GameViewModelFactory(
//    private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepository
// ) : ViewModelProvider.Factory {
//    private val pitkiotApi = PitkiotApi.instance
//    private var gameViewModel = GameViewModel(pitkiotRepositoryFactory.invoke(pitkiotApi))
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        @Suppress("UNCHECKED_CAST")
//        return gameViewModel as T
//    }
// }