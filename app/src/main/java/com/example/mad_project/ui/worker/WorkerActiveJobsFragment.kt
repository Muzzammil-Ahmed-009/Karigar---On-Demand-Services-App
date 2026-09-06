package com.karigar.app.ui.worker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karigar.app.data.repository.AuthRepository
import com.karigar.app.data.repository.OrderRepository
import com.karigar.app.ui.theme.KarigarTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WorkerActiveJobsFragment : Fragment() {

    @Inject
    lateinit var orderRepository: OrderRepository

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                KarigarTheme {
                    val orders = orderRepository.orders.collectAsStateWithLifecycle(initialValue = emptyList()).value
                    val uid = authRepository.getCurrentUserId() ?: ""
                    val activeJobs = orders.filter { 
                        it.status in listOf("Assigned", "Confirmed", "On the Way") && it.assignedWorkerId == uid 
                    }

                    WorkerActiveJobsScreen(
                        activeJobs = activeJobs,
                        onJobClick = { orderId ->
                            val intent = Intent(requireContext(), WorkerJobDetailActivity::class.java).apply {
                                putExtra("order_id", orderId)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
