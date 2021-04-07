package de.famprobst.report.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import de.famprobst.report.R
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.helper.HelperImageOrientation
import de.famprobst.report.helper.HelperShare
import de.famprobst.report.model.ModelCompetition
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ActivityDetailsCompetition : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var rifleId = 0
    private var date: Date = Date(System.currentTimeMillis())
    private var competitionKind = ""
    private var newEntry: Boolean = false
    private var competitionImage = ""
    private lateinit var sharedPref: SharedPreferences
    private lateinit var modelCompetition: EntryCompetition
    private lateinit var filePhoto: File

    // Input
    private lateinit var layoutDetails: CoordinatorLayout
    private lateinit var inputSpinnerLayout: TextInputLayout
    private lateinit var inputSpinner: MaterialAutoCompleteTextView
    private lateinit var inputPlace: TextInputLayout
    private lateinit var inputDate: TextInputLayout
    private lateinit var inputPoints1: TextInputLayout
    private lateinit var inputPoints2: TextInputLayout
    private lateinit var inputPoints3: TextInputLayout
    private lateinit var inputPoints4: TextInputLayout
    private lateinit var inputPoints5: TextInputLayout
    private lateinit var inputPoints6: TextInputLayout
    private lateinit var inputReport: TextInputLayout
    private lateinit var textPoints: TextView
    private lateinit var buttonPhoto: Button
    private lateinit var buttonQR: Button
    private lateinit var buttonShare: Button
    private lateinit var buttonSave: Button
    private lateinit var imagePhoto: ImageView
    private lateinit var imageDelete: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_details_competition)

        // Define toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Add back button to activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get shared prefs
        this.sharedPref = this.getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Get the rifle
        this.rifleId = this.sharedPref.getInt(getString(R.string.preferenceReportRifleId), 0)

        // Get all inputs
        this.layoutDetails = findViewById(R.id.activity_details)
        this.inputSpinnerLayout = findViewById(R.id.activityDetails_InputKindSpinnerLayout)
        this.inputSpinner = findViewById(R.id.activityDetails_InputKindSpinner)
        this.inputPlace = findViewById(R.id.activityDetails_InputPlace)
        this.inputDate = findViewById(R.id.activityDetails_InputDate)
        this.inputPoints1 = findViewById(R.id.activityDetails_InputPoints1)
        this.inputPoints2 = findViewById(R.id.activityDetails_InputPoints2)
        this.inputPoints3 = findViewById(R.id.activityDetails_InputPoints3)
        this.inputPoints4 = findViewById(R.id.activityDetails_InputPoints4)
        this.inputPoints5 = findViewById(R.id.activityDetails_InputPoints5)
        this.inputPoints6 = findViewById(R.id.activityDetails_InputPoints6)
        this.inputReport = findViewById(R.id.activityDetails_InputReport)
        this.textPoints = findViewById(R.id.activityDetails_Points)
        this.buttonPhoto = findViewById(R.id.activityDetails_ButtonPhoto)
        this.buttonQR = findViewById(R.id.activityDetails_ButtonQR)
        this.buttonShare = findViewById(R.id.activityDetails_ButtonShare)
        this.buttonSave = findViewById(R.id.activityDetails_ButtonSave)
        this.imagePhoto = findViewById(R.id.activityDetails_Photo)
        this.imageDelete = findViewById(R.id.activityDetails_DeletePhoto)

        // Get the passed arguments
        val extras = intent.extras

        // Check what we have to do
        when {
            extras!!.containsKey("competitionId") -> {
                getDataCompetition(extras.getInt(("competitionId")))
            }
            else -> {
                this.addNewEntry()
            }
        }

        // Setup the spinner
        this.setupSpinner()

        // Setup the date picker
        this.setupDatePicker()

        // Setup the onClickListener
        this.setupOnClickListener()

        // Calculation
        this.inputPoints1.editText?.doAfterTextChanged { this.calculatePoints() }
        this.inputPoints2.editText?.doAfterTextChanged { this.calculatePoints() }
        this.inputPoints3.editText?.doAfterTextChanged { this.calculatePoints() }
        this.inputPoints4.editText?.doAfterTextChanged { this.calculatePoints() }
        this.inputPoints5.editText?.doAfterTextChanged { this.calculatePoints() }
        this.inputPoints6.editText?.doAfterTextChanged { this.calculatePoints() }

        // Calculate for the first time
        this.calculatePoints()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.detailsMenuEdit -> {
                this.switchEditable()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.competitionKind = parent?.getItemAtPosition(position).toString()
    }

    private fun setupOnClickListener() {
        // Set onClickListener for qr button
        this.buttonQR.setOnClickListener { scanQR() }

        // Set onClickListener for photo button
        this.buttonPhoto.setOnClickListener { takePhoto() }

        // Set onClickListener for delete photo button
        this.imageDelete.setOnClickListener {
            if (imageDelete.isClickable) {
                val builder = AlertDialog.Builder(this@ActivityDetailsCompetition)
                builder.setMessage(R.string.activityDetails_DeleteMessage)
                    .setCancelable(false)
                    .setPositiveButton(R.string.activityDetails_DeleteMessageYes) { _, _ ->
                        deletePhoto()
                    }
                    .setNegativeButton(R.string.activityDetails_DeleteMessageNo) { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }

        // Set onClickListener for save button
        this.buttonSave.setOnClickListener { saveDetails() }

        // Set onClickListener for share button
        this.buttonShare.setOnClickListener { shareDetails() }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.activityCompetition_InputKindPossibilities,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.inputSpinner.setAdapter(adapter)
        this.inputSpinner.onItemClickListener = this
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDatePicker() {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                this.date = cal.time

                val myFormat = "dd.MM.yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.GERMAN)
                this.inputDate.editText?.setText(sdf.format(cal.time))
            }

        this.inputDate.editText?.setOnTouchListener { p0, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                DatePickerDialog(
                    p0.context, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            true
        }
    }

    private fun addNewEntry() {
        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_AddCompetition)

        // Set default date
        this.inputDate.editText?.setText(SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(date))

        // Set variables
        this.newEntry = true
    }

    private fun getDataCompetition(competitionId: Int) {
        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_EditCompetition)

        // Set variables
        this.newEntry = false

        // Get data and show them
        val competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)
        competitionModel.getById(competitionId).observe(this, { competition ->
            // Set competition
            this.modelCompetition = competition

            // Set data
            this.inputPlace.editText?.setText(competition.place)
            this.inputSpinner.setText(competition.kind)
            this.competitionKind = competition.kind
            this.date = competition.date
            this.inputDate.editText?.setText(
                SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(
                    date
                )
            )
            this.inputPoints1.editText?.setText(competition.shoots[0].toString())
            this.inputPoints2.editText?.setText(competition.shoots[1].toString())
            this.inputPoints3.editText?.setText(competition.shoots[2].toString())
            this.inputPoints4.editText?.setText(competition.shoots[3].toString())
            this.inputPoints5.editText?.setText(competition.shoots[4].toString())
            this.inputPoints6.editText?.setText(competition.shoots[5].toString())
            this.inputReport.editText?.setText(competition.report)

            if (competition.image.isNotEmpty()) {
                // Try to set the image
                try {
                    this.imagePhoto.setImageURI(Uri.parse(competition.image))
                    this.imageDelete.visibility = View.VISIBLE
                    this.competitionImage = competition.image
                } catch (e: Exception) {
                    // Show error message
                    Snackbar.make(
                        this.layoutDetails,
                        getText(R.string.activityDetails_ImageError),
                        Snackbar.LENGTH_LONG
                    ).show()

                    // Delete the image
                    this.deletePhoto()
                }
            }

            // Disable all fields
            this.switchEditable()
        })
    }

    private fun saveDetails() {
        // Get the current competition model
        val competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)

        // Define the new object
        val points: MutableList<Double> = mutableListOf()
        if (this.inputPoints1.editText?.text!!.isNotEmpty()) {
            points.add(this.inputPoints1.editText?.text.toString().toDouble())
        } else {
            points.add(0.0)
        }
        if (this.inputPoints2.editText?.text!!.isNotEmpty()) {
            points.add(this.inputPoints2.editText?.text.toString().toDouble())
        } else {
            points.add(0.0)
        }
        if (this.inputPoints3.editText?.text!!.isNotEmpty()) {
            points.add(this.inputPoints3.editText?.text.toString().toDouble())
        } else {
            points.add(0.0)
        }
        if (this.inputPoints4.editText?.text!!.isNotEmpty()) {
            points.add(this.inputPoints4.editText?.text.toString().toDouble())
        } else {
            points.add(0.0)
        }
        if (this.inputPoints5.editText?.text!!.isNotEmpty()) {
            points.add(this.inputPoints5.editText?.text.toString().toDouble())
        } else {
            points.add(0.0)
        }
        if (this.inputPoints6.editText?.text!!.isNotEmpty()) {
            points.add(this.inputPoints6.editText?.text.toString().toDouble())
        } else {
            points.add(0.0)
        }

        val modelCompetitionNew = EntryCompetition(
            date,
            inputPlace.editText?.text.toString(),
            competitionKind,
            points,
            competitionImage,
            inputReport.editText?.text.toString(),
            rifleId
        )

        // Save ot update the entry
        if (this.newEntry) {
            competitionModel.insert(modelCompetitionNew)
        } else {
            modelCompetitionNew.id = this.modelCompetition.id
            this.modelCompetition = modelCompetitionNew
            competitionModel.update(this.modelCompetition)
        }

        // Entry saved successful
        Snackbar.make(
            layoutDetails,
            getText(R.string.activityDetails_SaveSuccess),
            Snackbar.LENGTH_LONG
        ).show()

        // Close the activity
        finish()
    }

    private fun switchEditable() {
        this.inputPlace.isEnabled = !this.inputPlace.isEnabled
        this.inputSpinnerLayout.isEnabled = !this.inputSpinnerLayout.isEnabled
        this.inputDate.isEnabled = !this.inputDate.isEnabled
        this.inputPoints1.isEnabled = !this.inputPoints1.isEnabled
        this.inputPoints2.isEnabled = !this.inputPoints2.isEnabled
        this.inputPoints3.isEnabled = !this.inputPoints3.isEnabled
        this.inputPoints4.isEnabled = !this.inputPoints4.isEnabled
        this.inputPoints5.isEnabled = !this.inputPoints5.isEnabled
        this.inputPoints6.isEnabled = !this.inputPoints6.isEnabled
        this.inputReport.isEnabled = !this.inputReport.isEnabled
        this.buttonSave.isEnabled = !this.buttonSave.isEnabled
        this.buttonPhoto.isEnabled = !this.buttonPhoto.isEnabled
        this.buttonQR.isEnabled = !this.buttonQR.isEnabled
        this.imageDelete.isClickable = !this.imageDelete.isClickable
    }

    private fun scanQR() {
        Snackbar.make(
            this.layoutDetails,
            getText(R.string.activityDetails_InfoQR),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

    private fun takePhoto() {
        val pickIntent = Intent()
        pickIntent.type = "image/*"
        pickIntent.action = Intent.ACTION_OPEN_DOCUMENT

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.filePhoto = getPhotoFile("competitionPhoto.jpg")
        val providerFile = FileProvider.getUriForFile(this, "de.famprobst.report", filePhoto)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)

        val chooserIntent = Intent.createChooser(
            pickIntent,
            getString(R.string.activityDetails_ChooseImageLocation)
        )
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePhotoIntent))
        startActivityForResult(chooserIntent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            // Hide the image
            this.imagePhoto.visibility = View.GONE

            // Check if we have data
            if (data?.data != null) {
                // Get the data read approval
                contentResolver.takePersistableUriPermission(
                    data.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                // Display the image
                this.imagePhoto.setImageURI(data.data)

                // Save image uri as string
                this.competitionImage = data.data.toString()

                // Single image
                imagePhoto.setImageURI(data.data)
            } else {
                // Camera
                this.imagePhoto.setImageURI(Uri.fromFile(filePhoto))

                // Set the right orientation of the image
                when (HelperImageOrientation.getTheImageOrientation(filePhoto)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> this.imagePhoto.rotation = 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> this.imagePhoto.rotation = 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> this.imagePhoto.rotation = 270f
                    else -> this.imagePhoto.rotation = 0f
                }

                // Save image uri as string
                this.competitionImage = filePhoto.absolutePath
            }

            // Show image and delete button
            this.imagePhoto.visibility = View.VISIBLE
            this.imageDelete.visibility = View.VISIBLE
            this.imageDelete.isClickable = true
        }
    }

    private fun deletePhoto() {
        this.imagePhoto.setImageDrawable(null)
        this.imagePhoto.visibility = View.GONE
        this.imageDelete.visibility = View.GONE
        this.competitionImage = ""
    }

    private fun shareDetails() {
        // Check if the entry was saved first
        if (this::modelCompetition.isInitialized && !this.inputReport.isEnabled) {
            // Set the content
            val sharePath = getExternalFilesDir(null)
            val shareFile = HelperShare.shareCompetition(
                this.modelCompetition,
                this.sharedPref.getString(getString(R.string.preferenceReportRifleName), ""),
                sharePath,
                baseContext
            )

            // Share the csv
            val fileURI = FileProvider.getUriForFile(this, "de.famprobst.report", shareFile)
            val sharingIntent = Intent()
            sharingIntent.action = Intent.ACTION_SEND
            sharingIntent.type = "text/csv"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, fileURI)
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(sharingIntent)
        } else {
            Snackbar.make(
                this.layoutDetails,
                getText(R.string.activityDetails_ShareError),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun calculatePoints() {
        // Add all points to list
        val points: MutableList<Double> = mutableListOf()

        if (this.inputPoints1.editText?.text!!.isNotEmpty()) {
            try {
                points.add(this.inputPoints1.editText?.text.toString().toDouble())
            } catch (e: NumberFormatException) {
            }
        }
        if (this.inputPoints2.editText?.text!!.isNotEmpty()) {
            try {
                points.add(this.inputPoints2.editText?.text.toString().toDouble())
            } catch (e: NumberFormatException) {
            }
        }
        if (this.inputPoints3.editText?.text!!.isNotEmpty()) {
            try {
                points.add(this.inputPoints3.editText?.text.toString().toDouble())
            } catch (e: NumberFormatException) {
            }
        }
        if (this.inputPoints4.editText?.text!!.isNotEmpty()) {
            try {
                points.add(this.inputPoints4.editText?.text.toString().toDouble())
            } catch (e: NumberFormatException) {
            }
        }
        if (this.inputPoints5.editText?.text!!.isNotEmpty()) {
            try {
                points.add(this.inputPoints5.editText?.text.toString().toDouble())
            } catch (e: NumberFormatException) {
            }
        }
        if (this.inputPoints6.editText?.text!!.isNotEmpty()) {
            try {
                points.add(this.inputPoints6.editText?.text.toString().toDouble())
            } catch (e: NumberFormatException) {
            }
        }

        // Set value
        if (points.sum().rem(1).equals(0.0)) {
            this.textPoints.text = "%.0f".format(points.sum())
        } else {
            this.textPoints.text = "%.1f".format(points.sum())
        }
    }
}