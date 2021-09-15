package de.famprobst.report.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.perf.metrics.AddTrace
import com.google.zxing.integration.android.IntentIntegrator
import de.famprobst.report.R
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.entity.EntryTraining
import de.famprobst.report.helper.HelperImageOrientation
import de.famprobst.report.helper.HelperShare
import de.famprobst.report.model.ModelCompetition
import de.famprobst.report.model.ModelTraining
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

class ActivityDetails : AppCompatActivity() {

    private var rifleId = 0
    private var date: Date = Date(System.currentTimeMillis())
    private var textInputLayoutArray = ArrayList<TextInputLayout>()
    private var points: MutableList<Double> = mutableListOf()
    private var spinnerInput = 0
    private var indicatorInput = 2
    private var inputImage = ""

    private lateinit var sharedPref: SharedPreferences
    private lateinit var extras: Bundle
    private lateinit var filePhoto: File
    private lateinit var modelTraining: EntryTraining
    private lateinit var modelCompetition: EntryCompetition

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
    private lateinit var layoutAverage: LinearLayout
    private lateinit var textPoints: TextView
    private lateinit var textAverage: TextView
    private lateinit var buttonPhoto: Button
    private lateinit var buttonQR: Button
    private lateinit var buttonShare: Button
    private lateinit var buttonSave: Button
    private lateinit var imagePhoto: ImageView
    private lateinit var layoutDeleteRotate: LinearLayout
    private lateinit var imageDelete: ImageView
    private lateinit var imageRotate: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_details)

        // Define toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Add back button to activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        this.layoutAverage = findViewById(R.id.activityDetails_AverageBox)
        this.textAverage = findViewById(R.id.activityDetails_Average)
        this.buttonPhoto = findViewById(R.id.activityDetails_ButtonPhoto)
        this.buttonQR = findViewById(R.id.activityDetails_ButtonQR)
        this.buttonShare = findViewById(R.id.activityDetails_ButtonShare)
        this.buttonSave = findViewById(R.id.activityDetails_ButtonSave)
        this.imagePhoto = findViewById(R.id.activityDetails_Photo)
        this.layoutDeleteRotate = findViewById(R.id.activityDetails_DeleteRotate)
        this.imageDelete = findViewById(R.id.activityDetails_DeletePhoto)
        this.imageRotate = findViewById(R.id.activityDetails_RotatePhoto)

        // Get shared prefs
        this.sharedPref = this.getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Get the rifle
        this.rifleId = this.sharedPref.getInt(getString(R.string.preferenceReportRifleId), 0)

        // Get the passed arguments
        this.extras = intent.extras!!

        // Check what we have to do
        when {
            extras.containsKey("trainingId") -> {
                this.getDataTraining(extras.getInt("trainingId"))
            }
            extras.containsKey("competitionId") -> {
                this.getDataCompetition(extras.getInt("competitionId"))
            }
            extras.containsKey("kind") -> {
                if (extras.get("kind") == "training")
                    this.addNewTraining()
                else
                    this.addNewCompetition()
            }
        }

        // Setup the spinner
        this.setupSpinner()

        // Setup the onClickListener
        this.setupOnClickListener()

        // Setup the data picker
        this.setupDatePicker()

        // Calculation
        this.inputCount.editText?.doAfterTextChanged { editText ->
            (if (!editText.isNullOrEmpty()) this.showInputFields(
                editText.toString().toInt()
            ))
        }

        // Calculate for the first time
        this.calculatePoints()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Delete the file, if the temp file is empty
        if (this::filePhoto.isInitialized && this.filePhoto.length() <= 0) {
            this.filePhoto.delete()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.detailsMenuEdit -> {
                this.switchEditable()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Validate the data
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        // Check if we have a qr result
        if (result != null) {
            // Check if we have data
            if (result.contents != null) {

                // TODO Save image on local storage

                // Set the image
                this.showImage(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)

            // Check if we have a picture or camera
            if (resultCode == Activity.RESULT_OK && requestCode == 200) {
                // Image from data

                // Hide the image
                this.deletePhoto()

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

                    this.inputImage = data.data.toString()
                } else {
                    // Image from Camera

                    // Set the image
                    this.imagePhoto.setImageURI(Uri.fromFile(filePhoto))

                    // Set the right orientation of the image
                    when (HelperImageOrientation.getTheImageOrientation(filePhoto)) {
                        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> this.imagePhoto.rotation =
                            90f
                        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> this.imagePhoto.rotation =
                            180f
                        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> this.imagePhoto.rotation =
                            270f
                        else -> this.imagePhoto.rotation = 0f
                    }

                    // Save image uri as string
                    this.inputImage = filePhoto.absolutePath
                }
            }

            // Show image and delete button
            this.imagePhoto.visibility = View.VISIBLE
            this.layoutDeleteRotate.visibility = View.VISIBLE
            this.imageDelete.isClickable = true
            this.imageRotate.isClickable = true
        }
    }

    private fun setupSpinner() {
        val adapter: ArrayAdapter<CharSequence> = if (extras.get("kind") == "training") {
            ArrayAdapter.createFromResource(
                this,
                R.array.activityDetails_InputKindPossibilities,
                android.R.layout.simple_spinner_item
            )
        } else {
            ArrayAdapter.createFromResource(
                this,
                R.array.activityCompetition_InputKindPossibilities,
                android.R.layout.simple_spinner_item
            )
        }
        this.inputSpinner.setAdapter(adapter)
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

        // Set default date
        this.inputDate.editText?.setText(SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(date))
    }

    private fun setupOnClickListener() {
        // Set onClickListener for qr button
        this.buttonQR.setOnClickListener { this.buttonScanQR() }

        // Set onClickListener for photo button
        this.buttonPhoto.setOnClickListener { this.buttonGetPhoto() }

        // Set onClickListener for save button
        this.buttonSave.setOnClickListener { this.saveDetails() }

        // Set onClickListener for share button
        this.buttonShare.setOnClickListener { this.shareDetails() }

        // Set onClickListener for rotate button
        this.imageRotate.setOnClickListener { this.rotateImage() }

        // Set onItemClickListener for spinner
        this.inputSpinner.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            this.spinnerInput = position
        }

        // Set onCheckedListener for indicator
        this.inputRadioGroup.setOnCheckedChangeListener { _: RadioGroup, position: Int ->
            when (position) {
                this.inputRadioMinusMinus.id -> this.indicatorInput = 0
                this.inputRadioMinus.id -> this.indicatorInput = 1
                this.inputRadioPlus.id -> this.indicatorInput = 2
                this.inputRadioPlusPlus.id -> this.indicatorInput = 3
            }
        }

        // Set onClickListener for delete photo button
        this.imageDelete.setOnClickListener {
            if (this.imageDelete.isClickable) {
                val builder = AlertDialog.Builder(this)
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
    }

    private fun getDataTraining(trainingData: Int) {
        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_EditTraining)

        // Get data and show them
        val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)
        trainingModel.getById(trainingData).observe(this, { training ->
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
                this.showImage(this.modelTraining.image)
            }

            // Disable all fields
            this.switchEditable()
        })
    }

    private fun getDataCompetition(competitionId: Int) {
        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_EditCompetition)

        // Change the layout
        this.changeCompetitionLayout()

        // Get data and show them
        val competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)
        competitionModel.getById(competitionId).observe(this, { competition ->
            // Set competition
            this.modelCompetition = competition

            // Set the data
            this.inputPlace.editText?.setText(competition.place)
            this.inputSpinner.setText(competition.kind)
            this.date = competition.date
            this.inputDate.editText?.setText(
                SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(
                    this.date
                )
            )
            this.inputCount.editText?.setText(competition.shootCount.toString())
            this.showInputFields(competition.shootCount)
            this.textInputLayoutArray.forEachIndexed { index: Int, textInputLayout: TextInputLayout ->
                try {
                    textInputLayout.editText?.setText(competition.shoots[index].toString())
                } catch (e: IndexOutOfBoundsException) {
                    textInputLayout.editText?.setText("0.0")
                }
            }
            this.inputReport.editText?.setText(competition.report)
            if (this.modelCompetition.image.isNotEmpty()) {
                this.showImage(this.modelCompetition.image)
            }

            // Disable all fields
            this.switchEditable()
        })
    }

    private fun showImage(imageString: String) {
        if (File(imageString).isFile) {
            this.imagePhoto.load(File(imageString)) {
                crossfade(true)
                placeholder(R.drawable.ic_cam)
                listener(
                    onSuccess = { _: ImageRequest, _: ImageResult.Metadata ->
                        layoutDeleteRotate.visibility = View.VISIBLE
                        imageDelete.isClickable = true
                        imageRotate.isClickable = true
                        inputImage = imageString
                    },
                    onError = { _: ImageRequest, _: Throwable ->
                        // Show error message
                        Snackbar.make(
                            layoutDetails,
                            getText(R.string.activityDetails_ImageError),
                            Snackbar.LENGTH_LONG
                        ).show()

                        // Disable the image
                        deletePhoto()
                    }
                )
            }
        } else {
            this.imagePhoto.load(imageString) {
                crossfade(true)
                placeholder(R.drawable.ic_cam)
                listener(
                    onSuccess = { _: ImageRequest, _: ImageResult.Metadata ->
                        layoutDeleteRotate.visibility = View.VISIBLE
                        imageDelete.isClickable = true
                        imageRotate.isClickable = true
                        inputImage = imageString
                    },
                    onError = { _: ImageRequest, _: Throwable ->
                        // Show error message
                        Snackbar.make(
                            layoutDetails,
                            getText(R.string.activityDetails_ImageError),
                            Snackbar.LENGTH_LONG
                        ).show()

                        // Disable the image
                        deletePhoto()
                    }
                )
            }
        }
    }

    private fun addNewTraining() {
        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_AddTraining)
    }

    private fun addNewCompetition() {
        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_AddCompetition)

        // Change the layout
        this.changeCompetitionLayout()
    }

    private fun changeCompetitionLayout() {
        inputRadioGroup.visibility = View.GONE
        layoutAverage.visibility = View.GONE
        inputSpinnerLayout.hint = getString(R.string.activityDetails_CompetitionInputKind)
        inputPlace.hint = getString(R.string.activityDetails_CompetitionInputPlace)
        inputReport.hint = getString(R.string.activityDetails_CompetitionInputReport)
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
        this.imageRotate.isClickable = !this.imageRotate.isClickable
        this.textInputLayoutArray.forEach { textInputLayout: TextInputLayout ->
            textInputLayout.isEnabled = !textInputLayout.isEnabled
        }
    }

    private fun buttonScanQR() {
        Snackbar.make(
            this.layoutDetails,
            getText(R.string.activityDetails_InfoQR),
            Snackbar.LENGTH_LONG
        ).show()
        /*val integrator = IntentIntegrator(this).apply {
            captureActivity = CaptureActivity::class.java
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt("")
        }
        integrator.initiateScan()*/
    }

    private fun buttonGetPhoto() {
        val pickIntent = Intent()
        pickIntent.type = "image/*"
        pickIntent.action = Intent.ACTION_OPEN_DOCUMENT

        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.filePhoto = File.createTempFile("detailsPhoto", ".jpg", directoryStorage)
        val providerFile = FileProvider.getUriForFile(this, "de.famprobst.report", this.filePhoto)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)

        val chooserIntent = Intent.createChooser(
            pickIntent,
            getString(R.string.activityDetails_ChooseImageLocation)
        )
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePhotoIntent))
        startActivityForResult(chooserIntent, 200)
    }

    private fun deletePhoto() {
        this.imagePhoto.setImageDrawable(null)
        this.imagePhoto.visibility = View.GONE
        this.layoutDeleteRotate.visibility = View.GONE
        this.inputImage = ""
    }

    private fun saveDetails() {
        // Check what we have to save
        if (extras.get("kind") == "training") {
            // Set the data
            val modelTrainingNew = EntryTraining(
                this.date,
                this.inputPlace.editText?.text.toString(),
                this.inputSpinner.adapter.getItem(this.spinnerInput).toString(),
                this.inputCount.editText?.text.toString().toInt(),
                this.points,
                this.indicatorInput,
                this.inputImage,
                this.inputReport.editText?.text.toString(),
                this.rifleId
            )

            // Get the current training model
            val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)

            // Check if we have to update or create new
            if (this::modelTraining.isInitialized) {
                modelTrainingNew.id = this.modelTraining.id
                trainingModel.update(modelTrainingNew)
            } else {
                trainingModel.insert(modelTrainingNew)
            }
        } else if (extras.get("kind") == "competition") {
            // Set the data
            val modelCompetitionNew = EntryCompetition(
                this.date,
                this.inputPlace.editText?.text.toString(),
                this.inputSpinner.adapter.getItem(this.spinnerInput).toString(),
                this.inputCount.editText?.text.toString().toInt(),
                this.points,
                this.inputImage,
                this.inputReport.editText?.text.toString(),
                this.rifleId
            )

            // Get the current training model
            val competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)

            // Check if we have to update or create new
            if (this::modelCompetition.isInitialized) {
                modelCompetitionNew.id = this.modelCompetition.id
                competitionModel.update(modelCompetitionNew)
            } else {
                competitionModel.insert(modelCompetitionNew)
            }
        }

        // Close the activity
        finish()
    }

    @AddTrace(name = "shareDetails", enabled = true)
    private fun shareDetails() {
        if (extras.get("kind") == "training") {
            if (this::modelTraining.isInitialized && !this.inputReport.isEnabled) {
                // Set the content
                val sharePath = getExternalFilesDir(null)
                val shareFile = HelperShare.shareTraining(
                    this.modelTraining,
                    this.sharedPref.getString(getString(R.string.preferenceReportRifleName), ""),
                    sharePath,
                    baseContext
                )

                // Share the file
                this.shareDetailsFile(shareFile)
            } else {
                Snackbar.make(
                    this.layoutDetails,
                    getText(R.string.activityDetails_ShareError),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else if (extras.get("kind") == "competition") {
            if (this::modelCompetition.isInitialized && !this.inputReport.isEnabled) {
                // Set the content
                val sharePath = getExternalFilesDir(null)
                val shareFile = HelperShare.shareCompetition(
                    this.modelCompetition,
                    this.sharedPref.getString(getString(R.string.preferenceReportRifleName), ""),
                    sharePath,
                    baseContext
                )

                // Share the file
                this.shareDetailsFile(shareFile)
            } else {
                Snackbar.make(
                    this.layoutDetails,
                    getText(R.string.activityDetails_ShareError),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun shareDetailsFile(shareFile: File) {
        val fileURI = FileProvider.getUriForFile(this, "de.famprobst.report", shareFile)
        val sharingIntent = Intent()
        sharingIntent.action = Intent.ACTION_SEND
        sharingIntent.type = "text/csv"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileURI)
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(sharingIntent)
    }

    private fun calculatePoints() {
        // Empty all points
        this.points = mutableListOf()

        // Get all points
        this.textInputLayoutArray.forEach { textInputLayout: TextInputLayout ->
            if (textInputLayout.editText?.text!!.isNotEmpty()) {
                try {
                    this.points.add(textInputLayout.editText?.text.toString().toDouble())
                } catch (e: NumberFormatException) {

                }
            }
        }

        // Set value
        if (this.points.sum().rem(1).equals(0.0)) {
            this.textPoints.text = "%.0f".format(this.points.sum())
        } else {
            this.textPoints.text = "%.1f".format(this.points.sum())
        }

        // Check what to show as average
        if (this.inputCount.editText?.text!!.isNotEmpty() && this.inputCount.editText?.text?.toString() != "0") {
            this.textAverage.text = getString(
                R.string.activityDetails_Average,
                "%.2f".format(
                    (this.points.sum() / this.inputCount.editText?.text.toString()
                        .toDouble() * 100) / 100.0
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
            if (this.findNextTextId(i) != View.NO_ID) {
                this.textInputLayoutArray[i].editText!!.nextFocusDownId = this.findNextTextId(i)
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

    private fun rotateImage() {
        this.imagePhoto.rotation += 90f
    }
}