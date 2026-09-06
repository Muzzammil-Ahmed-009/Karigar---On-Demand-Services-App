package com.karigar.app.ui.PromotionsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.karigar.app.ui.promotions.PromotionsScreen
import com.karigar.app.ui.theme.KarigarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromotionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                KarigarTheme {
                    PromotionsScreen()
                }
            }
        }
    }
}
