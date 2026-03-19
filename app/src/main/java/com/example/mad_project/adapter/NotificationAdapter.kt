package com.example.mad_project.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.R
import com.example.mad_project.model.AppNotification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(
    private val notifications: List<AppNotification>
) : RecyclerView.Adapter<NotificationAdapter.NotifViewHolder>() {

    inner class NotifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView     = itemView.findViewById(R.id.tvNotifTitle)
        val tvMessage: TextView   = itemView.findViewById(R.id.tvNotifMessage)
        val tvTime: TextView      = itemView.findViewById(R.id.tvNotifTime)
        val ivIcon: ImageView     = itemView.findViewById(R.id.ivNotifIcon)
        val iconBg: View          = itemView.findViewById(R.id.iconBg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotifViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        val notif = notifications[position]

        holder.tvTitle.text   = notif.title
        holder.tvMessage.text = notif.message
        holder.tvTime.text    = formatTimestamp(notif.timestamp)

        // Icon + background color based on type — matching Stitch design
        val (iconRes, bgColor) = getIconAndColor(notif.type)
        holder.ivIcon.setImageResource(iconRes)
        holder.iconBg.backgroundTintList =
            android.content.res.ColorStateList.valueOf(Color.parseColor(bgColor))

        // Bold title for unread
        holder.tvTitle.setTypeface(
            null,
            if (notif.isRead) android.graphics.Typeface.NORMAL else android.graphics.Typeface.BOLD
        )
    }

    override fun getItemCount(): Int = notifications.size

    private fun getIconAndColor(type: String): Pair<Int, String> = when (type) {
        "accepted"     -> Pair(R.drawable.accepted,        "#FF9500")  // orange
        "confirm"      -> Pair(R.drawable.confirm,      "#5856D6")  // purple
        "assigned"     -> Pair(R.drawable.assigned,       "#7B2CBF")  // blue
        "completed"    -> Pair(R.drawable.check,        "#90EE90")  // green
        "cancelled"    -> Pair(R.drawable.cancelled, "#FFFFFF")  // red
        "announcement" -> Pair(R.drawable.announcement, "#3A3A3C")  // dark
        else           -> Pair(R.drawable.bell,         "#C6F000")  // lime (placed)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60_000L     -> "Just now"
            diff < 3_600_000L  -> "${diff / 60_000} min ago"
            diff < 86_400_000L -> "${diff / 3_600_000} hrs ago"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
