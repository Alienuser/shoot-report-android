package de.famprobst.report.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.famprobst.report.R
import de.famprobst.report.entity.EntryTraining
import java.text.SimpleDateFormat
import java.util.*

class AdapterTraining(
    private var trainings: List<EntryTraining>,
    private var itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<AdapterTraining.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(training: EntryTraining)
        fun onItemDelete(training: EntryTraining)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_training, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.trainings[position], itemClickListener)
    }

    override fun getItemCount(): Int = trainings.size

    fun addTraining(trainings: List<EntryTraining>) {
        this.trainings = trainings
        notifyDataSetChanged()
    }

    class ViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        // Define all texts boxes
        private val points: TextView = itemsView.findViewById(R.id.rowTraining_Points)
        private val training: TextView = itemsView.findViewById(R.id.rowTraining_Training)
        private val info: TextView = itemsView.findViewById(R.id.rowTraining_Info)
        private val delete: ImageView = itemsView.findViewById(R.id.rowTraining_Delete)

        fun bind(training: EntryTraining, clickListener: OnItemClickListener) {

            // Define content for text
            if (training.shoots.sum().rem(1).equals(0.0)) {
                this.points.text = training.shoots.sum().toInt().toString()
            } else {
                this.points.text = "%.1f".format(training.shoots.sum())
            }
            this.training.text = training.training
            this.info.text = itemView.context.getString(
                R.string.fragmentTraining_Infotext,
                SimpleDateFormat("d. MMM yyyy", Locale.getDefault()).format(training.date),
                training.place
            )

            // Set the color of the points according to the indicator
            when (training.indicator) {
                0 -> {
                    this.points.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_mood_bad,
                        0,
                        0,
                        0
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.points.compoundDrawablesRelative[0].setTint(Color.RED)
                    }
                }
                1 -> {
                    this.points.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_mood_dissatisfied,
                        0,
                        0,
                        0
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.points.compoundDrawablesRelative[0].setTint(Color.RED)
                    }
                }
                2 -> {
                    this.points.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_mood_satisfied,
                        0,
                        0,
                        0
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.points.compoundDrawablesRelative[0].setTint(Color.GREEN)
                    }
                }
                3 -> {
                    this.points.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_mood_verysatisfied,
                        0,
                        0,
                        0
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.points.compoundDrawablesRelative[0].setTint(Color.GREEN)
                    }
                }
            }

            // Set click listener
            this.itemView.setOnClickListener {
                clickListener.onItemClick(training)
            }

            // Set the delete click listener
            this.delete.setOnClickListener {
                clickListener.onItemDelete(training)
            }
        }
    }
}