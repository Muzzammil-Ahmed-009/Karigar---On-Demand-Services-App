package com.karigar.app.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.karigar.app.data.repository.AuthRepository
import com.karigar.app.ui.theme.KarigarTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WalletFragment : Fragment() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var currentRole by remember { mutableStateOf("customer") }
                var walletBalance by remember { mutableStateOf(0.0) }
                val uid = remember { authRepository.getCurrentUserId() }

                LaunchedEffect(uid) {
                    if (uid != null) {
                        authRepository.getUserProfile(uid).collect { result ->
                            result.onSuccess { user ->
                                if (user != null) {
                                    currentRole = user.currentRole
                                    walletBalance = user.walletBalance
                                }
                            }
                        }
                    }
                }

                KarigarTheme {
                    WalletScreen(
                        currentRole = currentRole,
                        balance = walletBalance,
                        transactions = emptyList(),
                        onAddMoneyClick = {
                            Toast.makeText(requireContext(), "Add Money flow starting...", Toast.LENGTH_SHORT).show()
                        },
                        onWithdrawClick = {
                            Toast.makeText(requireContext(), "Withdraw flow starting...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
