package com.example.mad_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

class WalletFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_wallet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutEmpty = view.findViewById<View>(R.id.layoutEmptyWallet)
        layoutEmpty?.findViewById<android.widget.ImageView>(R.id.ivEmptyIcon)?.setImageResource(R.drawable.wallet)
        layoutEmpty?.findViewById<android.widget.TextView>(R.id.tvEmptyTitle)?.text = "No transactions yet"
        layoutEmpty?.findViewById<android.widget.TextView>(R.id.tvEmptyDesc)?.text = "Your recent wallet activity will appear here"

        view.findViewById<android.widget.LinearLayout>(R.id.btnAddMoney)?.setOnClickListener {
            Toast.makeText(requireContext(), "Add Money feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<android.widget.LinearLayout>(R.id.btnWithdraw)?.setOnClickListener {
            Toast.makeText(requireContext(), "Withdraw feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<android.widget.Button>(R.id.btnReferFriend)?.setOnClickListener {
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_TEXT, "Use my referral code to sign up on Karigar and get Rs 500 off your first order! Download now: https://karigar.pk/app")
                type = "text/plain"
            }
            startActivity(android.content.Intent.createChooser(shareIntent, "Share Referral Code"))
        }
    }
}
