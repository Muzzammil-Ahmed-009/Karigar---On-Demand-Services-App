package com.example.mad_project

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class ServiceItem(
    val name: String,
    val description: String,
    val iconRes: Int,
    val iconColor: String,
    val categoryKey: String
)

class AllServicesActivity : AppCompatActivity() {

    private val allServices = listOf(
        ServiceItem("Cleaning",         "Deep clean your home, office or kitchen",          R.drawable.cleaning,    "#4CAF50", "cleaning"),
        ServiceItem("Electrician",      "Wiring, sockets, fans, panels & more",             R.drawable.electrician, "#FF9800", "electrician"),
        ServiceItem("Plumber",          "Pipe repairs, leaks, taps & bathroom fitting",     R.drawable.plumber,     "#2196F3", "plumber"),
        ServiceItem("Carpenter",        "Furniture repair, doors, windows & woodwork",      R.drawable.carpenter,   "#795548", "carpenter"),
        ServiceItem("Painter",          "Interior & exterior wall painting",                R.drawable.painter,     "#E91E63", "painter"),
        ServiceItem("AC Repair",        "AC service, installation & gas refilling",         R.drawable.ac_repair,   "#00BCD4", "ac_repair"),
        ServiceItem("Shifting",         "Home & office moving & relocation service",        R.drawable.shifting,    "#9C27B0", "shifting"),
        ServiceItem("Gardening",        "Lawn care, tree trimming & garden design",         R.drawable.gardener,    "#8BC34A", "gardener"),
        ServiceItem("Security Guard",   "Trained guards for homes & businesses",            R.drawable.security,    "#607D8B", "security"),
        ServiceItem("Mechanic",         "Car, bike & generator repair at home",             R.drawable.repairing,   "#FF5722", "mechanic"),
        ServiceItem("Pest Control",     "Insects, termites & cockroach treatment",          R.drawable.details,     "#FFC107", "pest_control"),
        ServiceItem("Laundry",          "Wash, dry-clean & press your clothes",             R.drawable.details,     "#03A9F4", "laundry"),
        ServiceItem("Driver",           "Dedicated driver for daily commute & trips",       R.drawable.details,     "#3F51B5", "driver"),
        ServiceItem("Cook / Chef",      "Home-cooked meals & event catering",               R.drawable.details,     "#FF7043", "cook"),
        ServiceItem("Baby Sitter",      "Trusted childcare at your home",                   R.drawable.details,     "#F48FB1", "baby_sitter"),
        ServiceItem("Elder Care",       "Nursing & assistance for elderly family members",  R.drawable.details,     "#78909C", "elder_care"),
        ServiceItem("Interior Design",  "Modern home decor & renovation consultation",      R.drawable.details,     "#7E57C2", "interior"),
        ServiceItem("Solar Installation","Solar panel setup & net metering guidance",       R.drawable.details,     "#FDD835", "solar"),
        ServiceItem("CCTV / Camera",    "Security camera setup & monitoring",               R.drawable.details,     "#546E7A", "cctv"),
        ServiceItem("Water Tanker",     "Fresh water delivery to your location",            R.drawable.details,     "#29B6F6", "water_tanker"),
    )

    private lateinit var adapter: ServicesListAdapter
    private val displayedServices = mutableListOf<ServiceItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_services)

        findViewById<ImageView>(R.id.btnAllServicesBack).setOnClickListener { finish() }

        // Setup RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rvAllServices)
        rv.layoutManager = LinearLayoutManager(this)
        displayedServices.addAll(allServices)
        adapter = ServicesListAdapter(displayedServices) { service ->
            val intent = Intent(this, place_order::class.java)
            intent.putExtra("category_name", service.name)
            startActivity(intent)
        }
        rv.adapter = adapter

        // Search filter
        findViewById<EditText>(R.id.etServiceSearch).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                displayedServices.clear()
                if (query.isEmpty()) {
                    displayedServices.addAll(allServices)
                } else {
                    displayedServices.addAll(allServices.filter {
                        it.name.lowercase().contains(query)
                    })
                }
                adapter.notifyDataSetChanged()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}

// ─── Adapter ──────────────────────────────────────────────
class ServicesListAdapter(
    private val items: List<ServiceItem>,
    private val onClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<ServicesListAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView  = itemView.findViewById(R.id.tvServiceName)
        val tvDesc: TextView  = itemView.findViewById(R.id.tvServiceDesc)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivServiceIcon)
        val iconBg: View      = itemView.findViewById(R.id.serviceIconBg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = items[position]
        holder.tvName.text = s.name
        holder.tvDesc.text = s.description
        holder.ivIcon.setImageResource(s.iconRes)
        holder.iconBg.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(s.iconColor)).withAlpha(30)
        holder.ivIcon.imageTintList =
            ColorStateList.valueOf(Color.parseColor(s.iconColor))
        holder.itemView.setOnClickListener { onClick(s) }
    }

    override fun getItemCount() = items.size
}
