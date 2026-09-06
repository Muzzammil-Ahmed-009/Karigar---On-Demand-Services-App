package com.karigar.app.ui.worker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karigar.app.data.model.WorkerBid
import com.karigar.app.data.repository.AuthRepository
import com.karigar.app.data.repository.OrderRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class WorkerFeedFragment : Fragment() {

    @Inject
    lateinit var orderRepository: OrderRepository

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val orders = orderRepository.orders.collectAsStateWithLifecycle(initialValue = emptyList()).value
                    val coroutineScope = rememberCoroutineScope()
                    val uid = authRepository.getCurrentUserId() ?: ""
                    
                    WorkerFeedScreen(
                        orders = orders,
                        currentWorkerId = uid,
                        onSubmitBid = { job, bidAmount, coverLetter ->
                            if (uid.isNotEmpty()) {
                                coroutineScope.launch {
                                    val result = authRepository.getUserProfile(uid).first()
                                    result.onSuccess { user ->
                                        if (user != null) {
                                            // Real bidding logic
                                            val bid = WorkerBid(
                                                id = UUID.randomUUID().toString(),
                                                workerId = uid,
                                                workerName = user.name.ifEmpty { "Karigar Pro" },
                                                workerRating = 5.0f, // TODO: Get from real worker profile
                                                jobsCompleted = 10,  // TODO: Get from real worker profile
                                                bidAmount = "Rs. $bidAmount",
                                                etaString = "15 mins", // Optional cover letter can go here if extended
                                                workerImageResId = com.karigar.app.R.drawable.emp_profile
                                            )
                                            orderRepository.submitBid(job.id, bid)
                                            Toast.makeText(requireContext(), "Bid placed successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}
