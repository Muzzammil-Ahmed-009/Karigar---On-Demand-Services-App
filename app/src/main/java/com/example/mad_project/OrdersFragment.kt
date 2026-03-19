package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OrdersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_orders, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutOrders)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerOrders)

        viewPager.adapter = OrdersPagerAdapter(requireActivity())

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pending"
                1 -> "History"
                else -> ""
            }
        }.attach()
    }

    inner class OrdersPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> PendingOrdersFragment()
            else -> HistoryOrdersFragment()
        }
    }
}