package com.example.mad_project

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mad_project.model.Order
import com.google.android.material.bottomsheet.BottomSheetDialog

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var order: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        @Suppress("DEPRECATION")
        order = intent.getParcelableExtra("order")
            ?: run { finish(); return }

        bindHeader()
        bindServiceCard()
        bindOrderDetails()
        bindTimeline()
        bindInvoice()
        bindWorkerCard()
        bindSpecialCards()
        bindActionButtons()
    }

    // ─── Header ──────────────────────────────────────────────
    private fun bindHeader() {
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvBadge = findViewById<TextView>(R.id.tvStatusBadge)
        val btnHelp = findViewById<ImageView>(R.id.btnHelp)

        btnBack.setOnClickListener { finish() }

        tvBadge.text = order.status
        val (bg, _) = statusColors(order.status)
        tvBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bg))

        btnHelp.setOnClickListener { showHelpDialog() }

        // Order ID + scheduled
        val orderIndex = AppRepository.orders.indexOf(order) + 1
        val paddedId = orderIndex.toString().padStart(3, '0')
        findViewById<TextView>(R.id.tvOrderId).text = "Order #ORD-$paddedId"
        findViewById<TextView>(R.id.tvScheduledTime).text = order.scheduledTime
    }

    // ─── Service card ─────────────────────────────────────────
    private fun bindServiceCard() {
        val serviceIconRes = getServiceIcon(order.serviceCategoryKey)
        val joinedCategories = order.categories.joinToString(", ")

        findViewById<ImageView>(R.id.ivServiceIcon).setImageResource(serviceIconRes)
        findViewById<TextView>(R.id.tvServiceCategory).text = joinedCategories
        findViewById<TextView>(R.id.tvServiceAddress).text  = order.address
    }

    // ─── Order details card ───────────────────────────────────
    private fun bindOrderDetails() {
        findViewById<TextView>(R.id.tvOrderDescription).text = order.details
        val photoLabel = if (order.photoCount == 1) "1 photo" else "${order.photoCount} photos"
        findViewById<TextView>(R.id.tvPhotoCount).text  = photoLabel
        findViewById<TextView>(R.id.tvScheduled).text   = order.scheduledTime.ifEmpty { "Not scheduled" }
    }

    // ─── Status timeline ──────────────────────────────────────
    private fun bindTimeline() {
        val steps = listOf("Pending", "Accepted", "Assigned", "Confirmed", "Completed")
        val currentIndex = steps.indexOf(order.status).takeIf { it >= 0 }
            ?: if (order.status == "Cancelled") 0 else steps.size

        val dotIds   = listOf(R.id.dotPending, R.id.dotAccepted, R.id.dotAssigned, R.id.dotConfirmed, R.id.dotCompleted)
        val lineIds  = listOf(R.id.linePending, R.id.lineAccepted, R.id.lineAssigned, R.id.lineConfirmed)

        val DONE   = "#4CAF50"; val ACTIVE = "#0F2CBD"; val INACTIVE = "#E0E0E0"

        dotIds.forEachIndexed { i, id ->
            val dot = findViewById<View>(id)
            val color = when {
                i < currentIndex  -> DONE
                i == currentIndex -> ACTIVE
                else              -> INACTIVE
            }
            dot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        }
        lineIds.forEachIndexed { i, id ->
            val line = findViewById<View>(id)
            line.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(if (i < currentIndex) DONE else INACTIVE)
            )
        }

        // Cancelled — all dots grey except first (orange)
        if (order.status == "Cancelled") {
            dotIds.forEachIndexed { i, id ->
                val color = if (i == 0) "#FF9800" else INACTIVE
                findViewById<View>(id).backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(color))
            }
        }
    }

    // ─── Invoice card ─────────────────────────────────────────
    private fun bindInvoice() {
        val cardInvoice = findViewById<androidx.cardview.widget.CardView>(R.id.cardInvoice)
        val showInvoice = order.status in listOf("Assigned", "Confirmed", "Completed")

        if (showInvoice) {
            cardInvoice.visibility = View.VISIBLE
            
            val baseFare = order.offeredFare.toIntOrNull() ?: 800
            val extraWork = if (order.status == "Completed") 200 else 0
            val discount = 50
            
            val total = baseFare + extraWork - discount

            findViewById<TextView>(R.id.tvInvoiceBaseFare).text = "Rs $baseFare"
            
            val rowExtra = findViewById<LinearLayout>(R.id.rowExtraParts)
            if (extraWork > 0) {
                rowExtra.visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvInvoiceExtra).text = "Rs $extraWork"
            } else {
                rowExtra.visibility = View.GONE
            }
            
            findViewById<TextView>(R.id.tvInvoiceDiscount).text = "-Rs $discount"
            findViewById<TextView>(R.id.tvInvoiceTotal).text = "Rs $total"
            
        } else {
            cardInvoice.visibility = View.GONE
        }
    }

    // ─── Worker card ──────────────────────────────────────────
    private fun bindWorkerCard() {
        val cardWorker = findViewById<LinearLayout>(R.id.cardWorker)
        val showWorker = order.status in listOf("Assigned", "Confirmed", "Completed")

        if (showWorker && order.assignedWorkerName.isNotEmpty()) {
            cardWorker.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvWorkerName).text   = order.assignedWorkerName
            findViewById<TextView>(R.id.tvWorkerRating).text = order.assignedWorkerRating.toString()
            findViewById<TextView>(R.id.tvWorkerExp).text    = order.assignedWorkerExperience

            val btnFavoriteWorker = findViewById<ImageView>(R.id.btnFavoriteWorker)
            val workerPhone = order.assignedWorkerPhone.ifEmpty { "unknown_${order.assignedWorkerName.replace(" ", "")}" }
            
            val updateFavoriteIcon = {
                if (AppRepository.isWorkerFavorite(workerPhone)) {
                    btnFavoriteWorker.setImageResource(R.drawable.ic_heart_filled)
                } else {
                    btnFavoriteWorker.setImageResource(R.drawable.ic_heart_outline)
                }
            }
            
            updateFavoriteIcon()
            
            btnFavoriteWorker.setOnClickListener {
                val favWorker = FavoriteWorker(
                    id = java.util.UUID.randomUUID().toString(),
                    name = order.assignedWorkerName,
                    profession = order.categories.firstOrNull() ?: order.serviceCategoryKey.replaceFirstChar { it.uppercase() },
                    rating = order.assignedWorkerRating,
                    phone = workerPhone
                )
                AppRepository.toggleFavoriteWorker(favWorker)
                updateFavoriteIcon()
                val msg = if (AppRepository.isWorkerFavorite(workerPhone)) "Added to Favorites \u2764\uFE0F" else "Removed from Favorites"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }

            findViewById<ImageView>(R.id.btnMessageWorker).setOnClickListener {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("worker_name", order.assignedWorkerName)
                intent.putExtra("worker_phone", workerPhone)
                startActivity(intent)
            }

            findViewById<ImageView>(R.id.btnCallWorker).setOnClickListener {
                if (order.assignedWorkerPhone.isNotEmpty()) {
                    val dialIntent = Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:${order.assignedWorkerPhone}"))
                    startActivity(dialIntent)
                } else {
                    Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            cardWorker.visibility = View.GONE
        }
    }

    // ─── Special info cards ───────────────────────────────────
    private fun bindSpecialCards() {
        val cardCancel = findViewById<LinearLayout>(R.id.cardCancellation)
        val cardDone   = findViewById<LinearLayout>(R.id.cardCompletion)

        if (order.status == "Cancelled" && order.cancellationReason.isNotEmpty()) {
            cardCancel.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvCancellationReason).text = order.cancellationReason
        }
        if (order.status == "Completed" && order.completionNote.isNotEmpty()) {
            cardDone.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvCompletionNote).text = order.completionNote
        }
    }

    // ─── Action buttons ───────────────────────────────────────
    private fun bindActionButtons() {
        val btnCancel      = findViewById<Button>(R.id.btnCancelOrder)
        val btnConfirm     = findViewById<Button>(R.id.btnConfirmWorker)
        val btnHelp        = findViewById<Button>(R.id.btnHelpSupport)
        val btnRate        = findViewById<Button>(R.id.btnRateService)
        val btnNewOrder    = findViewById<Button>(R.id.btnPlaceNewOrder)

        when (order.status) {
            "Pending", "Accepted" -> {
                btnCancel.visibility = View.VISIBLE
            }
            "Assigned" -> {
                btnConfirm.visibility = View.VISIBLE
                btnCancel.visibility  = View.VISIBLE
            }
            "Confirmed" -> {
                btnHelp.visibility = View.VISIBLE
            }
            "Completed" -> {
                btnRate.visibility = View.VISIBLE
            }
            "Cancelled" -> {
                btnNewOrder.visibility = View.VISIBLE
            }
        }

        btnCancel.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_cancel_reason, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()
                
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val rgReasons = dialogView.findViewById<android.widget.RadioGroup>(R.id.rgCancelReasons)
            val etOther = dialogView.findViewById<android.widget.EditText>(R.id.etOtherReason)
            val btnKeep = dialogView.findViewById<Button>(R.id.btnKeepOrder)
            val btnConfirmCancel = dialogView.findViewById<Button>(R.id.btnConfirmCancel)

            rgReasons.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId == R.id.rbReasonOther) {
                    etOther.visibility = View.VISIBLE
                } else {
                    etOther.visibility = View.GONE
                }
            }

            btnKeep.setOnClickListener { dialog.dismiss() }
            
            btnConfirmCancel.setOnClickListener {
                val selectedId = rgReasons.checkedRadioButtonId
                if (selectedId == -1) {
                    Toast.makeText(this, "Please select a reason", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                var reason = ""
                if (selectedId == R.id.rbReasonOther) {
                    reason = etOther.text.toString().trim()
                    if (reason.isEmpty()) {
                        Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                } else {
                    val rb = dialogView.findViewById<android.widget.RadioButton>(selectedId)
                    reason = rb.text.toString()
                }

                order.cancellationReason = reason
                AppRepository.updateOrderStatus(order.id, "Cancelled")
                Toast.makeText(this, "Order cancelled.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            }
            dialog.show()
        }

        btnConfirm.setOnClickListener {
            showConfirmWorkerDialog()
        }

        btnHelp.setOnClickListener { showHelpDialog() }

        btnRate.setOnClickListener { showRateDialog() }

        btnNewOrder.setOnClickListener {
            startActivity(Intent(this, place_order::class.java))
            finish()
        }
    }

    // ─── Dialogs ──────────────────────────────────────────────
    private fun showConfirmWorkerDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Worker")
            .setMessage("Confirm ${order.assignedWorkerName} (⭐ ${order.assignedWorkerRating}) for this job?\n\n${order.assignedWorkerExperience}")
            .setPositiveButton("Confirm") { _, _ ->
                AppRepository.updateOrderStatus(order.id, "Confirmed")
                Toast.makeText(this, "Worker confirmed! Order is now confirmed.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showHelpDialog() {
        val items = arrayOf(
            "📞  Call Support: +92 300 0000000",
            "💬  WhatsApp: +92 300 0000000",
            "📧  Email: support@karigar.pk",
            "❓  How to cancel an order?",
            "❓  When will my worker arrive?",
            "❓  How to reschedule?"
        )
        AlertDialog.Builder(this)
            .setTitle("Help & Support")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+923000000000")))
                    1 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/923000000000")))
                    2 -> {
                        val emailIntent = Intent(Intent.ACTION_SENDTO)
                        emailIntent.data = Uri.parse("mailto:support@karigar.pk")
                        startActivity(emailIntent)
                    }
                    else -> Toast.makeText(this, "Our team will contact you shortly.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showRateDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_review, null)
        bottomSheetDialog.setContentView(view)

        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.setBackgroundResource(android.R.color.transparent)

        val workerName = order.assignedWorkerName.ifEmpty { "the worker" }
        view.findViewById<TextView>(R.id.tvReviewSubtitle).text = "How was the service provided by $workerName?"

        view.findViewById<Button>(R.id.btnSubmitReview).setOnClickListener {
            Toast.makeText(this, "Thank you for your feedback! ⭐", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    // ─── Helpers ──────────────────────────────────────────────
    private fun statusColors(status: String): Pair<String, String> = when (status) {
        "Pending"   -> Pair("#FF9800", "#FFFFFF")
        "Accepted"  -> Pair("#2196F3", "#FFFFFF")
        "Assigned"  -> Pair("#9C27B0", "#FFFFFF")
        "Confirmed" -> Pair("#0F2CBD", "#FFFFFF")
        "Completed" -> Pair("#4CAF50", "#FFFFFF")
        "Cancelled" -> Pair("#F44336", "#FFFFFF")
        else        -> Pair("#9E9E9E", "#FFFFFF")
    }

    private fun getServiceIcon(key: String): Int = when (key) {
        "cleaning"    -> R.drawable.cleaning
        "electrician" -> R.drawable.electrician
        "plumber"     -> R.drawable.plumber
        "carpenter"   -> R.drawable.carpenter
        "painter"     -> R.drawable.painter
        "gardener"    -> R.drawable.gardener
        "ac_repair"   -> R.drawable.ac_repair
        "shifting"    -> R.drawable.shifting
        "security"    -> R.drawable.security
        else          -> R.drawable.details
    }
}
