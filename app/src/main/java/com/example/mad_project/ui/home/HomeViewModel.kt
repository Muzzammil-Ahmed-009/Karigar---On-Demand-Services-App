package com.karigar.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karigar.app.data.model.User
import com.karigar.app.data.repository.AuthRepository
import com.karigar.app.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    val categories = orderRepository.categories

    private val _popularWorkers = MutableStateFlow<List<User>>(emptyList())
    val popularWorkers: StateFlow<List<User>> = _popularWorkers.asStateFlow()

    init {
        fetchPopularWorkers()
    }

    private fun fetchPopularWorkers() {
        viewModelScope.launch {
            authRepository.getPopularWorkers().collectLatest { result ->
                result.onSuccess { workers ->
                    _popularWorkers.value = workers
                }.onFailure {
                    // Handle error gracefully if needed
                    _popularWorkers.value = emptyList()
                }
            }
        }
    }
}
