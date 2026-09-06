package com.karigar.app.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karigar.app.data.model.Order
import com.karigar.app.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    val orders: StateFlow<List<Order>> = repository.orders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            repository.placeOrder(order)
        }
    }

    fun acceptBid(orderId: String, bid: com.karigar.app.data.model.WorkerBid) {
        viewModelScope.launch {
            repository.acceptBid(orderId, bid)
        }
    }

    fun submitReview(orderId: String, rating: Int, reviewText: String) {
        viewModelScope.launch {
            repository.submitReview(orderId, rating, reviewText)
        }
    }
}
