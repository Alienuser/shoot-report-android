package de.famprobst.report.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.famprobst.report.R
import de.famprobst.report.entity.EntryTraining
import de.famprobst.report.helper.HelperImageOrientation
import de.famprobst.report.helper.HelperShare
import de.famprobst.report.model.ModelTraining
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

class ActivityDetailsTraining : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var rifleId = 0
    private var date: Date = Date(System.currentTimeMillis())
    private var trainingKind = ""
    private var newEntry: Boolean = false
    private var trainingImage = ""
    private var textInputLayoutArray = ArrayList<TextInputLayout>()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var modelTraining: EntryTraining
    private lateinit var filePhoto: File

    // Input
    private lateinit var layoutDetails: CoordinatorLayout
    private lateinit var inputRadioGroup: RadioGroup
    private lateinit var inputRadioMinusMinus: RadioButton
    private lateinit var inputRadioMinus: RadioButton
    private lateinit var inputRadioPlus: RadioButton
    private lateinit var inputRadioPlusPlus: RadioButton
    private lateinit var inputSpinnerLayout: TextInputLayout
    private lateinit var inputSpinner: MaterialAutoCompleteTextView
    private lateinit var inputPlace: TextInputLayout
    private lateinit var inputDate: TextInputLayout
    private lateinit var inputCount: TextInputLayout
    private lateinit var flexBoxLayout: FlexboxLayout
    private lateinit var inputReport: TextInputLayout
    private lateinit var textPoints: TextView
    private lateinit var textAverage: TextView
    private lateinit var buttonPhoto: Button
    private lateinit var buttonQR: Button
    private lateinit var buttonShare: Button
    private lateinit var buttonSave: Button
    private lateinit var imagePhoto: ImageView
    private lateinit var imageDelete: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_details_training)

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
        this.inputRadioGroup = findViewById(R.id.activityDetails_radioGroup)
        this.inputRadioMinusMinus = findViewById(R.id.activityDetails_radioMinusMinus)
        this.inputRadioMinus = findViewById(R.id.activityDetails_radioMinus)
        this.inputRadioPlus = findViewById(R.id.activityDetails_radioPlus)
        this.inputRadioPlusPlus = findViewById(R.id.activityDetails_radioPlusPlus)
        this.inputSpinnerLayout = findViewById(R.id.activityDetails_InputKindSpinnerLayout)
        this.inputSpinner = findViewById(R.id.activityDetails_InputKindSpinner)
        this.inputPlace = findViewById(R.id.activityDetails_InputPlace)
        this.inputDate = findViewById(R.id.activityDetails_InputDate)
        this.inputCount = findViewById(R.id.activityDetails_InputCount)
        this.flexBoxLayout = findViewById(R.id.activityDetails_FlexLayout)
        this.inputReport = findViewById(R.id.activityDetails_InputReport)
        this.textPoints = findViewById(R.id.activityDetails_Points)
        this.textAverage = findViewById(R.id.activityDetails_Average)
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
            extras!!.containsKey("trainingId") -> {
                getDataTraining(extras.getInt("trainingId"))
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
        this.inputCount.editText?.doAfterTextChanged { editText ->
            (if (!editText.isNullOrEmpty()) {
                this.showInputFields(editText.toString().toInt())
                this.calculatePoints()
            })
        }

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
        this.trainingKind = parent?.getItemAtPosition(position).toString()
    }

    private fun setupOnClickListener() {
        // Set onClickListener for qr button
        this.buttonQR.setOnClickListener { scanQR() }

        // Set onClickListener for photo button
        this.buttonPhoto.setOnClickListener { takePhoto() }

        // Set onClickListener for delete photo button
        this.imageDelete.setOnClickListener {
            if (this.imageDelete.isClickable) {
                val builder = AlertDialog.Builder(this@ActivityDetailsTraining)
                builder.setMessage(R.string.activityDetails_DeleteMessage)
                    .setCancelable(false)
                    .setPositiveButton(R.string.activityDetails_DeleteMessageYes) { _, _ ->
                        this.deletePhoto()
                    }
                    .setNegativeButton(R.string.activityDetails_DeleteMessageNo) { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }

        // Set onClickListener for save button
        this.buttonSave.setOnClickListener { this.saveDetails() }

        // Set onClickListener for share button
        this.buttonShare.setOnClickListener { this.shareDetails() }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.activityDetails_InputKindPossibilities,
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
        supportActionBar?.title = getString(R.string.activityDetails_AddTraining)

        // Set default date
        this.inputDate.editText?.setText(SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(date))

        // Set variables
        this.newEntry = true
    }

    private fun getDataTraining(trainingId: Int) {
        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_EditTraining)

        // Set variables
        this.newEntry = false

        // Get data and show them
        val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)
        trainingModel.getById(trainingId).observe(this, { training ->
            // Set training
            this.modelTraining = training

            // Set data
            when (training.indicator) {
                0 -> this.inputRadioGroup.check(this.inputRadioMinusMinus.id)
                1 -> this.inputRadioGroup.check(this.inputRadioMinus.id)
                2 -> this.inputRadioGroup.check(this.inputRadioPlus.id)
                3 -> this.inputRadioGroup.check(this.inputRadioPlusPlus.id)
            }
            this.inputPlace.editText?.setText(training.place)
            this.inputSpinner.setText(training.training)
            this.trainingKind = training.training
            this.date = training.date
            this.inputDate.editText?.setText(
                SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(
                    this.date
                )
            )
            this.inputCount.editText?.setText(training.shootCount.toString())
            this.showInputFields(training.shootCount)
            this.textInputLayoutArray.forEachIndexed { index: Int, textInputLayout: TextInputLayout ->
                textInputLayout.editText?.setText(training.shoots[index].toString())
            }
            this.inputReport.editText?.setText(training.report)

            if (this.modelTraining.image.isNotEmpty()) {
                // Try to set the image
                try {
                    this.imagePhoto.setImageURI(Uri.parse(training.image))
                    this.imageDelete.visibility = View.VISIBLE
                    this.trainingImage = training.image
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
        // Get the current training model
        val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)

        // Get indicator
        var indicator = 2
        when (this.inputRadioGroup.checkedRadioButtonId) {
            this.inputRadioMinusMinus.id -> indicator = 0
            this.inputRadioMinus.id -> indicator = 1
            this.inputRadioPlus.id -> indicator = 2
            this.inputRadioPlusPlus.id -> indicator = 3
        }

        // Define the new object
        val points: MutableList<Double> = mutableListOf()

        // Save all points
        this.textInputLayoutArray.forEach { textInputLayout: TextInputLayout ->
            if (textInputLayout.editText?.text!!.isNotEmpty()) {
                points.add(textInputLayout.editText?.text.toString().toDouble())
            } else {
                points.add(0.0)
            }
        }

        val modelTrainingNew = EntryTraining(
            this.date,
            this.inputPlace.editText?.text.toString(),
            this.trainingKind,
            this.inputCount.editText?.text.toString().toInt(),
            points,
            indicator,
            this.trainingImage,
            this.inputReport.editText?.text.toString(),
            this.rifleId
        )

        // Save ot update the entry
        if (this.newEntry) {
            trainingModel.insert(modelTrainingNew)
        } else {
            modelTrainingNew.id = this.modelTraining.id
            this.modelTraining = modelTrainingNew
            trainingModel.update(this.modelTraining)
        }

        // Entry saved successful
        Snackbar.make(
            this.layoutDetails,
            getText(R.string.activityDetails_SaveSuccess),
            Snackbar.LENGTH_LONG
        ).show()

        // Close the activity
        finish()
    }

    private fun switchEditable() {
        this.inputRadioMinusMinus.isEnabled = !this.inputRadioMinusMinus.isEnabled
        this.inputRadioMinus.isEnabled = !this.inputRadioMinus.isEnabled
        this.inputRadioPlus.isEnabled = !this.inputRadioPlus.isEnabled
        this.inputRadioPlusPlus.isEnabled = !this.inputRadioPlusPlus.isEnabled
        this.inputPlace.isEnabled = !this.inputPlace.isEnabled
        this.inputSpinnerLayout.isEnabled = !this.inputSpinnerLayout.isEnabled
        this.inputDate.isEnabled = !this.inputDate.isEnabled
        this.inputCount.isEnabled = !this.inputCount.isEnabled
        this.inputReport.isEnabled = !this.inputReport.isEnabled
        this.buttonSave.isEnabled = !this.buttonSave.isEnabled
        this.buttonPhoto.isEnabled = !this.buttonPhoto.isEnabled
        this.buttonQR.isEnabled = !this.buttonQR.isEnabled
        this.imageDelete.isClickable = !this.imageDelete.isClickable
        this.textInputLayoutArray.forEach { textInputLayout: TextInputLayout ->
            textInputLayout.isEnabled = !textInputLayout.isEnabled
        }
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
        this.filePhoto = getPhotoFile("trainingPhoto.jpg")
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
                this.trainingImage = data.data.toString()
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
                this.trainingImage = filePhoto.absolutePath
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
        this.trainingImage = ""
    }

    private fun shareDetails() {
        // Check if the entry was saved first
        if (this::modelTraining.isInitialized && !this.inputReport.isEnabled) {
            // Set the content
            val sharePath = getExternalFilesDir(null)
            val shareFile = HelperShare.shareTraining(
                this.modelTraining,
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

        // Get all points
        this.textInputLayoutArray.forEach { textInputLayout: TextInputLayout ->

            if (textInputLayout.editText?.text!!.isNotEmpty()) {
                try {
                    points.add(textInputLayout.editText?.text.toString().toDouble())
                } catch (e: NumberFormatException) {

                }
            }
        }

        // Set value
        if (points.sum().rem(1).equals(0.0)) {
            this.textPoints.text = "%.0f".format(points.sum())
        } else {
            this.textPoints.text = "%.1f".format(points.sum())
        }

        // Check what to show as average
        if (this.inputCount.editText?.text!!.isNotEmpty() && this.inputCount.editText?.text?.toString() != "0") {
            this.textAverage.text = getString(
                R.string.activityDetails_Average,
                "%.2f".format(
                    (points.sum() / inputCount.editText?.text.toString().toDouble() * 100) / 100.0
                )
            )
        } else {
            this.textAverage.text = getString(R.string.activityDetails_Average, "0")
        }
    }

    private fun showInputFields(amounts: Int) {
        // Remove all views
        this.flexBoxLayout.removeAllViews()
        this.textInputLayoutArray.clear()

        // Check if we have a good number
        if (amounts < 1) return

        // Set the inputFields
        for (i in 1..ceil(amounts / 10.0).toInt()) {

            // Set the text layout
            val textLayout = TextInputLayout(
                ContextThemeWrapper(
                    this,
                    R.style.Widget_MaterialComponents_TextInputLayout_FilledBox
                )
            )

            // Set the text input
            val textInput = TextInputEditText(textLayout.context)
            textInput.id = View.generateViewId()
            textInput.hint = String.format(getString(R.string.activityDetails_InputPoints), i)
            textInput.inputType = InputType.TYPE_CLASS_NUMBER
            textInput.keyListener = DigitsKeyListener.getInstance("0123456789,.")
            textInput.setSelectAllOnFocus(true)
            textInput.doAfterTextChanged { this.calculatePoints() }

            // Add the text layout
            textLayout.addView(textInput)
            this.textInputLayoutArray.add(textLayout)

            // Add the text input
            this.flexBoxLayout.addView(textLayout)

            // Set parameters for every text layout
            val layoutParams = textLayout.layoutParams as FlexboxLayout.LayoutParams
            layoutParams.flexBasisPercent = 0.3f
            layoutParams.setMargins(0, 25, 0, 0)
        }

        // Set next id
        for (i in 0..this.textInputLayoutArray.size) {
            if (findNextTextId(i) != View.NO_ID) {
                this.textInputLayoutArray[i].editText!!.nextFocusDownId = findNextTextId(i)
            }
        }
    }

    private fun findNextTextId(index: Int): Int {
        return if (index < this.textInputLayoutArray.size - 1) {
            this.textInputLayoutArray[index + 1].editText!!.id
        } else {
            View.NO_ID
        }
    }
}