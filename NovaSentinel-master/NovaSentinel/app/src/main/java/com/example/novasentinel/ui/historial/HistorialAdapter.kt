package com.example.novasentinel.ui.historial


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novasentinel.Alert
import com.example.novasentinel.databinding.ItemHistorialBinding
import com.example.novasentinel.R

class HistorialAdapter(private var alerts: List<Alert>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val alert = alerts[position]
        holder.tvTitle.text = alert.titulo
        holder.tvBody.text = alert.body
        holder.tvUser.text = "Entidad ID: ${alert.entityID}"

        val latitude = alert.getLatitudeAsDouble()
        val longitude = alert.getLongitudeAsDouble()
    }

    override fun getItemCount(): Int = alerts.size

    fun updateData(newAlerts: List<Alert>) {
        alerts = newAlerts
        notifyDataSetChanged()
    }

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvBody: TextView = itemView.findViewById(R.id.tvBody)
        val tvUser: TextView = itemView.findViewById(R.id.tvUser)
    }
}
