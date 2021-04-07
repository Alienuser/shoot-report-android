package de.famprobst.report.fragment.competition

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
import de.famprobst.report.adapter.AdapterCompetition
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.model.ModelCompetition

class FragmentCompetitionList : Fragment() {

    private lateinit var competitionModel: ModelCompetition
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_competition_list, container, false)

        // Get shared prefs
        sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Setup list view
        setupListView(layout)

        // Setup the fab
        setupFab(layout)

        // return the layout
        return layout
    }

    private fun setupListView(layout: View) {

        // Setup click listener
        val listener = object : AdapterCompetition.OnItemClickListener {
            override fun onItemClick(competition: EntryCompetition) {
                openCompetition(competition)
            }

            override fun onItemDelete(competition: EntryCompetition) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.fragmentCompetition_DeleteMessage)
                    .setCancelable(false)
                    .setPositiveButton(R.string.fragmentCompetition_DeleteMessageYes) { _, _ ->
                        competitionModel.delete(competition)
                    }
                    .setNegativeButton(R.string.fragmentCompetition_DeleteMessageNo) { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }

        // Set variables
        val recyclerAdapter = AdapterCompetition(emptyList(), listener)
        val recyclerView = layout.findViewById<RecyclerView>(R.id.fragmentCompetition_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recyclerAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Setup model and listener
        competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)
        competitionModel.allCompetitions(
            sharedPref.getInt(
                getString(R.string.preferenceReportRifleId),
                0
            )
        ).observe(viewLifecycleOwner, { competitions ->
            recyclerAdapter.addCompetition(competitions)

            if (competitions.isNotEmpty()) {
                layout.findViewById<TextView>(R.id.fragmentCompetition_InfoText).visibility =
                    View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                layout.findViewById<TextView>(R.id.fragmentCompetition_InfoText).visibility =
                    View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })
    }

    private fun setupFab(layout: View) {
        val fab = layout.findViewById<FloatingActionButton>(R.id.fragmentCompetition_Fab)

        fab.setOnClickListener {
            addCompetition()
        }
    }

    private fun openCompetition(competition: EntryCompetition) {
        val intent = Intent(this.context, ActivityDetails::class.java)
        intent.putExtra("competitionId", competition.id)
        intent.putExtra("kind", "competition")
        startActivity(intent)
    }

    private fun addCompetition() {
        val intent = Intent(this.context, ActivityDetails::class.java)
        intent.putExtra("kind", "competition")
        startActivity(intent)
    }
}