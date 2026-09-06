package com.karigar.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.karigar.app.data.model.User
import com.karigar.app.data.repository.AuthRepository
import com.karigar.app.data.repository.OrderRepository
import com.karigar.app.ui.main.MainActivity
import com.karigar.app.ui.onboarding.OnboardingActivity
import com.karigar.app.ui.settings.ContactUsActivity
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.worker.BecomeWorkerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var orderRepository: OrderRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uid = remember { authRepository.getCurrentUserId() }

                var currentUser by remember { mutableStateOf<User?>(null) }
                var totalOrders by remember { mutableIntStateOf(0) }
                var completedOrders by remember { mutableIntStateOf(0) }
                var pendingOrders by remember { mutableIntStateOf(0) }

                var showEditNameDialog by remember { mutableStateOf(false) }
                var showLogoutDialog by remember { mutableStateOf(false) }

                LaunchedEffect(uid) {
                    if (uid != null) {
                        authRepository.getUserProfile(uid).collect { result ->
                            result.onSuccess { user ->
                                currentUser = user
                            }
                        }
                    }
                }

                LaunchedEffect(uid) {
                    if (uid != null) {
                        orderRepository.orders.collect { orders ->
                            val myOrders = orders.filter { it.userId == uid }
                            totalOrders = myOrders.size
                            completedOrders = myOrders.count { it.status == "Completed" }
                            pendingOrders = myOrders.count { it.status in listOf("Pending", "Accepted", "Assigned", "Confirmed") }
                        }
                    }
                }

                KarigarTheme {
                    ProfileScreen(
                        user = currentUser,
                        totalOrders = totalOrders,
                        completedOrders = completedOrders,
                        pendingOrders = pendingOrders,
                        onEditNameClick = { showEditNameDialog = true },
                        onEditProfileClick = {
                            startActivity(Intent(requireContext(), ProfileSettingActivity::class.java))
                        },
                        onSwitchRoleClick = {
                            val user = currentUser ?: return@ProfileScreen
                            if (user.currentRole == "customer" && !user.isRegisteredWorker) {
                                startActivity(Intent(requireContext(), BecomeWorkerActivity::class.java))
                                return@ProfileScreen
                            }

                            val newRole = if (user.currentRole == "worker") "customer" else "worker"
                            lifecycleScope.launch {
                                val result = authRepository.updateUserRole(newRole)
                                if (result.isSuccess) {
                                    Toast.makeText(requireContext(), "Switched to ${newRole.replaceFirstChar { it.uppercase() }} Mode", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(requireContext(), "Failed to switch role", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onContactUsClick = {
                            startActivity(Intent(requireContext(), ContactUsActivity::class.java))
                        },
                        onLogoutClick = { showLogoutDialog = true }
                    )

                    if (showLogoutDialog) {
                        com.karigar.app.ui.components.LogoutDialog(
                            onDismiss = { showLogoutDialog = false },
                            onConfirm = {
                                showLogoutDialog = false
                                lifecycleScope.launch {
                                    authRepository.logout()
                                    startActivity(Intent(requireContext(), OnboardingActivity::class.java))
                                    requireActivity().finish()
                                }
                            }
                        )
                    }

                    if (showEditNameDialog) {
                        var newName by remember { mutableStateOf(currentUser?.name ?: "") }
                        
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { showEditNameDialog = false },
                            title = { androidx.compose.material3.Text("Edit Name") },
                            text = {
                                androidx.compose.material3.OutlinedTextField(
                                    value = newName,
                                    onValueChange = { newName = it },
                                    label = { androidx.compose.material3.Text("Full Name") }
                                )
                            },
                            confirmButton = {
                                androidx.compose.material3.TextButton(
                                    onClick = {
                                        if (newName.isNotBlank()) {
                                            lifecycleScope.launch {
                                                val result = authRepository.updateUserName(newName.trim())
                                                if (result.isSuccess) {
                                                    Toast.makeText(requireContext(), "Name updated!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                        showEditNameDialog = false
                                    }
                                ) {
                                    androidx.compose.material3.Text("Save")
                                }
                            },
                            dismissButton = {
                                androidx.compose.material3.TextButton(
                                    onClick = { showEditNameDialog = false }
                                ) {
                                    androidx.compose.material3.Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
