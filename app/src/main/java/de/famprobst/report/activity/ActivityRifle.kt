package de.famprobst.report.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.famprobst.report.R
import de.famprobst.report.adapter.AdapterRifle
import de.famprobst.report.databinding.ActivityRifleBinding
import de.famprobst.report.entity.EntryRifle
import de.famprobst.report.helper.HelperRepeat
import de.famprobst.report.model.ModelRifle

class ActivityRifle : AppCompatActivity() {

    private lateinit var binding: ActivityRifleBinding
    private lateinit var rifleModel: ModelRifle
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = ActivityRifleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Define toolbar
        setSupportActionBar(binding.activityRifleToolbar.toolbar)

        // Get shared prefs
        sharedPref = this.getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Setup list view
        setupListView()

        // Check the permissions
        checkPermissions()
    }

    override fun onResume() {
        super.onResume()

        // Start changing ad
        HelperRepeat.startRepeat(window.decorView.rootView, baseContext)
    }

    override fun onPause() {
        super.onPause()

        // Stop changing ad
        HelperRepeat.stopRepeat()
    }

    private fun setupListView() {

        // Setup click listener
        val listener = object : AdapterRifle.OnItemClickListener {
            override fun onItemClick(rifle: EntryRifle) {
                startActivityMain(rifle)
            }

            override fun onItemDelete(rifle: EntryRifle) {
                val builder = AlertDialog.Builder(this@ActivityRifle)
                builder.setMessage(R.string.activityRifle_DeleteMessage)
                    .setCancelable(false)
                    .setPositiveButton(R.string.activityRifle_DeleteMessageYes) { _, _ ->
                        rifle.show = false
                        rifleModel.update(rifle)
                    }
                    .setNegativeButton(R.string.activityRifle_DeleteMessageNo) { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }

        // Set variables
        val recyclerAdapter = AdapterRifle(emptyList(), listener)

        // Set the list
        binding.activityRifleList.layoutManager = LinearLayoutManager(this)
        binding.activityRifleList.adapter = recyclerAdapter
        binding.activityRifleList.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        // Setup model and listener
        rifleModel = ViewModelProvider(this).get(ModelRifle::class.java)
        rifleModel.allRifles.observe(this, { rifles ->
            recyclerAdapter.addRifle(rifles)
        })
    }

    private fun startActivityMain(element: EntryRifle) {
        // Save some sharedPrefs
        with(sharedPref.edit()) {
            putString(
                getString(R.string.preferenceReportRifleName),
                baseContext.resources.getStringArray(R.array.activityRilfe_Weapons)[element.id - 1]
            )
            putInt(getString(R.string.preferenceReportRifleId), element.id)
            commit()
        }

        // Start the main activity
        startActivity(Intent(this, ActivityMain::class.java))
    }

    private fun checkPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {
            //permission denied
            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            //show popup to request runtime permission
            requestPermissions(permissions, 100)
        }
    }
}