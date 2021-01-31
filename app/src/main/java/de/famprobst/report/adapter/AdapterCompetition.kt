package de.famprobst.report.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.famprobst.report.R
import de.famprobst.report.entity.EntryCompetition
import java.text.SimpleDateFormat
import java.util.*

class AdapterCompetition(
    private var competitions: List<EntryCompetition>,
    private var itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<AdapterCompetition.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(competition: EntryCompetition)
        fun onItemDelete(competition: EntryCompetition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_competition, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.competitions[position], itemClickListener)
    }

    override fun getItemCount(): Int = competitions.size

    fun addCompetition(competitions: List<EntryCompetition>) {
        this.competitions = competitions
        notifyDataSetChanged()
    }

    class ViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        // Define all texts boxes
        private val points: TextView = itemsView.findViewById(R.id.rowCompetition_Points)
        private val kind: TextView = itemsView.findViewById(R.id.rowCompetition_Kind)
        private val info: TextView = itemsView.findViewById(R.id.rowCompetition_Info)
        private val delete: ImageView = itemsView.findViewById(R.id.rowCompetition_Delete)

        fun bind(competition: EntryCompetition, clickListener: OnItemClickListener) {

            // Define content for text
            if (competition.shoots.sum().rem(1).equals(0.0)) {
                this.points.text = competition.shoots.sum().toInt().toString()
            } else {
                this.points.text = "%.1f".format(competition.shoots.sum())
            }
            this.kind.text = competition.kind
            this.info.text = itemView.context.getString(
                R.string.fragmentCompetition_Infotext,
                SimpleDateFormat("d. MMM yyyy", Locale.getDefault()).format(competition.date),
                competition.place
            )

            // Set click listener
            this.itemView.setOnClickListener {
                clickListener.onItemClick(competition)
            }

            // Set the delete click listener
            this.delete.setOnClickListener {
                clickListener.onItemDelete(competition)
            }
        }
    }
}