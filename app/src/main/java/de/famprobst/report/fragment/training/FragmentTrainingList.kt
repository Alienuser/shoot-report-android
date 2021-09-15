package de.famprobst.report.fragment.training

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.famprobst.report.R
import de.famprobst.report.activity.ActivityDetails
import de.famprobst.report.adapter.AdapterTraining
import de.famprobst.report.entity.EntryTraining
import de.famprobst.report.model.ModelTraining

class FragmentTrainingList : Fragment() {

    private lateinit var trainingModel: ModelTraining
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_training_list, container, false)

        // Get shared prefs
        sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Setup list view
        setupListView(layout)

        // Setup the fab
        setupFab(layout)

        // Return the layout
        return layout
    }

    private fun setupListView(layout: View) {

        // Setup click listener
        val listener = object : AdapterTraining.OnItemClickListener {
            override fun onItemClick(training: EntryTraining) {
                openTraining(training)
            }

            override fun onItemDelete(training: EntryTraining) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.fragmentTraining_DeleteMessage)
                    .setCancelable(false)
                    .setPositiveButton(R.string.fragmentTraining_DeleteMessageYes) { _, _ ->
                        trainingModel.delete(training)
                    }
                    .setNegativeButton(R.string.fragmentTraining_DeleteMessageNo) { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }

        // Set variables
        val recyclerAdapter = AdapterTraining(emptyList(), listener)
        val recyclerView = layout.findViewById<RecyclerView>(R.id.fragmentTraining_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recyclerAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Setup model and listener
        trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)
        trainingModel.allTrainings(
            sharedPref.getInt(
                getString(R.string.preferenceReportRifleId),
                0
            )
        ).observe(viewLifecycleOwner, { trainings ->
            recyclerAdapter.addTraining(trainings)

            if (trainings.isNotEmpty()) {
                layout.findViewById<TextView>(R.id.fragmentTraining_InfoText).visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                layout.findViewById<TextView>(R.id.fragmentTraining_InfoText).visibility =
                    View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })
    }

    private fun setupFab(layout: View) {
        val fab = layout.findViewById<FloatingActionButton>(R.id.fragmentTraining_Fab)

        fab.setOnClickListener {
            addTraining()
        }
    }

    private fun openTraining(training: EntryTraining) {
        val intent = Intent(this.context, ActivityDetails::class.java)
        intent.putExtra("trainingId", training.id)
        intent.putExtra("kind", "training")
        startActivity(intent)
    }

    private fun addTraining() {
        val intent = Intent(this.context, ActivityDetails::class.java)
        intent.putExtra("kind", "training")
        startActivity(intent)
    }
}