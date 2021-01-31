package de.famprobst.report.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.famprobst.report.R
import de.famprobst.report.entity.EntryRifle

class AdapterRifle(
    private var rifles: List<EntryRifle>,
    private var itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<AdapterRifle.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(rifle: EntryRifle)
        fun onItemDelete(rifle: EntryRifle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_rifle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.rifles[position], itemClickListener)
    }

    override fun getItemCount(): Int = rifles.size

    fun addRifle(rifles: List<EntryRifle>) {
        this.rifles = rifles
        notifyDataSetChanged()
    }

    class ViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        // Define all texts boxes
        private val name: TextView = itemsView.findViewById(R.id.rowRifle_Name)
        private val delete: ImageView = itemsView.findViewById(R.id.rowRifle_Delete)

        fun bind(rifle: EntryRifle, clickListener: OnItemClickListener) {

            // Define content for text
            this.name.text =
                itemView.context.resources.getStringArray(R.array.activityRilfe_Weapons)[rifle.id - 1]

            // Set the click listener
            this.itemView.setOnClickListener {
                clickListener.onItemClick(rifle)
            }

            // Set the delete click listener
            this.delete.setOnClickListener {
                clickListener.onItemDelete(rifle)
            }
        }
    }
}