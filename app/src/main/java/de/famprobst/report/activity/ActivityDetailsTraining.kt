package de.famprobst.report.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
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
import de.famprobst.report.entity.EntryTraining
import de.famprobst.report.helper.HelperShare
import de.famprobst.report.model.ModelTraining
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ActivityDetailsTraining : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var sharedPref: SharedPreferences
    private var rifleId = 0
    private var date: Date = Date(System.currentTimeMillis())
    private var trainingKind = ""
    private var newEntry: Boolean = false
    private lateinit var modelTraining: EntryTraining
    private var trainingImage = byteArrayOf()
    private lateinit var output: File

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
    private lateinit var inputPoints1: TextInputLayout
    private lateinit var inputPoints2: TextInputLayout
    private lateinit var inputPoints3: TextInputLayout
    private lateinit var inputPoints4: TextInputLayout
    private lateinit var inputPoints5: TextInputLayout
    private lateinit var inputPoints6: TextInputLayout
    private lateinit var inputPoints7: TextInputLayout
    private lateinit var inputPoints8: TextInputLayout
    private lateinit var inputPoints9: TextInputLayout
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
        sharedPref = this.getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Get the rifle
        rifleId = sharedPref.getInt(getString(R.string.preferenceReportRifleId), 0)

        // Get the passed arguments
        val extras = intent.extras

        // Get all inputs
        layoutDetails = findViewById(R.id.activity_details)
        inputRadioGroup = findViewById(R.id.activityDetails_radioGroup)
        inputRadioMinusMinus = findViewById(R.id.activityDetails_radioMinusMinus)
        inputRadioMinus = findViewById(R.id.activityDetails_radioMinus)
        inputRadioPlus = findViewById(R.id.activityDetails_radioPlus)
        inputRadioPlusPlus = findViewById(R.id.activityDetails_radioPlusPlus)
        inputSpinnerLayout = findViewById(R.id.activityDetails_InputKindSpinnerLayout)
        inputSpinner = findViewById(R.id.activityDetails_InputKindSpinner)
        inputPlace = findViewById(R.id.activityDetails_InputPlace)
        inputDate = findViewById(R.id.activityDetails_InputDate)
        inputCount = findViewById(R.id.activityDetails_InputCount)
        inputPoints1 = findViewById(R.id.activityDetails_InputPoints1)
        inputPoints2 = findViewById(R.id.activityDetails_InputPoints2)
        inputPoints3 = findViewById(R.id.activityDetails_InputPoints3)
        inputPoints4 = findViewById(R.id.activityDetails_InputPoints4)
        inputPoints5 = findViewById(R.id.activityDetails_InputPoints5)
        inputPoints6 = findViewById(R.id.activityDetails_InputPoints6)
        inputPoints7 = findViewById(R.id.activityDetails_InputPoints7)
        inputPoints8 = findViewById(R.id.activityDetails_InputPoints8)
        inputPoints9 = findViewById(R.id.activityDetails_InputPoints9)
        inputReport = findViewById(R.id.activityDetails_InputReport)
        textPoints = findViewById(R.id.activityDetails_Points)
        textAverage = findViewById(R.id.activityDetails_Average)
        buttonPhoto = findViewById(R.id.activityDetails_ButtonPhoto)
        buttonQR = findViewById(R.id.activityDetails_ButtonQR)
        buttonShare = findViewById(R.id.activityDetails_ButtonShare)
        buttonSave = findViewById(R.id.activityDetails_ButtonSave)
        imagePhoto = findViewById(R.id.activityDetails_Photo)
        imageDelete = findViewById(R.id.activityDetails_DeletePhoto)

        // Check what we have to do
        when {
            extras!!.containsKey("trainingId") -> {
                getDataTraining(extras.getInt("trainingId"))
            }
            else -> {
                addNewEntry()
            }
        }

        // Setup the spinner
        setupSpinner()

        // Setup the date picker
        setupDatePicker()

        // Set onClickListener for qr button
        buttonQR.setOnClickListener { scanQR() }

        // Set onClickListener for photo button
        buttonPhoto.setOnClickListener { takePhoto() }

        // Set onClickListener for delete photo button
        imageDelete.setOnClickListener {
            if (imageDelete.isClickable) {
                val builder = AlertDialog.Builder(this@ActivityDetailsTraining)
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
        buttonSave.setOnClickListener { saveDetails() }

        // Set onClickListener for share button
        buttonShare.setOnClickListener { shareDetails() }

        // Calculation
        inputCount.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints1.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints2.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints3.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints4.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints5.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints6.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints7.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints8.editText?.doAfterTextChanged { this.calculatePoints() }
        inputPoints9.editText?.doAfterTextChanged { this.calculatePoints() }

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
        trainingKind = parent?.getItemAtPosition(position).toString()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.activityDetails_InputKindPossibilities,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputSpinner.setAdapter(adapter)
        inputSpinner.onItemClickListener = this
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDatePicker() {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                date = cal.time

                val myFormat = "dd.MM.yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.GERMAN)
                inputDate.editText?.setText(sdf.format(cal.time))
            }

        inputDate.editText?.setOnTouchListener { p0, event ->
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
        // Enable all fields
        this.switchEditable()

        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_AddTraining)

        // Set default date
        inputDate.editText?.setText(SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(date))

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
                0 -> inputRadioGroup.check(inputRadioMinusMinus.id)
                1 -> inputRadioGroup.check(inputRadioMinus.id)
                2 -> inputRadioGroup.check(inputRadioPlus.id)
                3 -> inputRadioGroup.check(inputRadioPlusPlus.id)
            }
            inputPlace.editText?.setText(training.place)
            inputSpinner.setText(training.training)
            trainingKind = training.training
            date = training.date
            inputDate.editText?.setText(SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(date))
            inputCount.editText?.setText(training.shootCount.toString())
            inputPoints1.editText?.setText(training.shoots[0].toString())
            inputPoints2.editText?.setText(training.shoots[1].toString())
            inputPoints3.editText?.setText(training.shoots[2].toString())
            inputPoints4.editText?.setText(training.shoots[3].toString())
            inputPoints5.editText?.setText(training.shoots[4].toString())
            inputPoints6.editText?.setText(training.shoots[5].toString())
            inputPoints7.editText?.setText(training.shoots[6].toString())
            inputPoints8.editText?.setText(training.shoots[7].toString())
            inputPoints9.editText?.setText(training.shoots[8].toString())
            inputReport.editText?.setText(training.report)

            if (training.image.isNotEmpty()) {
                imagePhoto.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        training.image,
                        0,
                        training.image.size
                    ) as Bitmap
                )
                imageDelete.visibility = View.VISIBLE
                trainingImage = training.image
            }
        })
    }

    private fun saveDetails() {
        // Get the current training model
        val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)

        // Get indicator
        var indicator = 2
        when (inputRadioGroup.checkedRadioButtonId) {
            inputRadioMinusMinus.id -> indicator = 0
            inputRadioMinus.id -> indicator = 1
            inputRadioPlus.id -> indicator = 2
            inputRadioPlusPlus.id -> indicator = 3
        }

        // Define the new object
        val modelTrainingNew = EntryTraining(
            date,
            inputPlace.editText?.text.toString(),
            trainingKind,
            inputCount.editText?.text.toString().toInt(),
            listOf(
                inputPoints1.editText?.text.toString().toDouble(),
                inputPoints2.editText?.text.toString().toDouble(),
                inputPoints3.editText?.text.toString().toDouble(),
                inputPoints4.editText?.text.toString().toDouble(),
                inputPoints5.editText?.text.toString().toDouble(),
                inputPoints6.editText?.text.toString().toDouble(),
                inputPoints7.editText?.text.toString().toDouble(),
                inputPoints8.editText?.text.toString().toDouble(),
                inputPoints9.editText?.text.toString().toDouble()
            ),
            indicator,
            trainingImage,
            inputReport.editText?.text.toString(),
            rifleId
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
            layoutDetails,
            getText(R.string.activityDetails_SaveSuccess),
            Snackbar.LENGTH_LONG
        ).show()

        // Close the activity
        finish()
    }

    private fun switchEditable() {
        inputRadioMinusMinus.isEnabled = !inputRadioMinusMinus.isEnabled
        inputRadioMinus.isEnabled = !inputRadioMinus.isEnabled
        inputRadioPlus.isEnabled = !inputRadioPlus.isEnabled
        inputRadioPlusPlus.isEnabled = !inputRadioPlusPlus.isEnabled
        inputPlace.isEnabled = !inputPlace.isEnabled
        inputSpinnerLayout.isEnabled = !inputSpinnerLayout.isEnabled
        inputDate.isEnabled = !inputDate.isEnabled
        inputCount.isEnabled = !inputCount.isEnabled
        inputPoints1.isEnabled = !inputPoints1.isEnabled
        inputPoints2.isEnabled = !inputPoints2.isEnabled
        inputPoints3.isEnabled = !inputPoints3.isEnabled
        inputPoints4.isEnabled = !inputPoints4.isEnabled
        inputPoints5.isEnabled = !inputPoints5.isEnabled
        inputPoints6.isEnabled = !inputPoints6.isEnabled
        inputPoints7.isEnabled = !inputPoints7.isEnabled
        inputPoints8.isEnabled = !inputPoints8.isEnabled
        inputPoints9.isEnabled = !inputPoints9.isEnabled
        inputReport.isEnabled = !inputReport.isEnabled
        buttonSave.isEnabled = !buttonSave.isEnabled
        buttonPhoto.isEnabled = !buttonPhoto.isEnabled
        buttonQR.isEnabled = !buttonQR.isEnabled
        imageDelete.isClickable = !imageDelete.isClickable
    }

    private fun scanQR() {
        Snackbar.make(
            layoutDetails,
            getText(R.string.activityDetails_InfoQR),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun takePhoto() {
        val pickIntent = Intent()
        pickIntent.type = "image/*"
        pickIntent.action = Intent.ACTION_GET_CONTENT

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val dir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        output = File(dir, "activityTraining.jpeg")
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output))

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
            if (data?.data != null) {
                // Single image
                imagePhoto.setImageURI(data.data)

                // Save image
                val bitmap = (imagePhoto.drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                trainingImage = stream.toByteArray()
            } else {
                // Camera
                val bitmap = rotateImage(
                    MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        Uri.fromFile(output)
                    ), 90f
                )
                imagePhoto.setImageURI(null)
                imagePhoto.setImageBitmap(bitmap)

                // Save image
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                trainingImage = stream.toByteArray()
            }
            imageDelete.visibility = View.VISIBLE
            imageDelete.isClickable = true
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    private fun deletePhoto() {
        imagePhoto.setImageDrawable(null)
        imageDelete.visibility = View.GONE
        trainingImage = byteArrayOf()
    }

    private fun shareDetails() {
        // Check if the entry was saved first
        if (this::modelTraining.isInitialized && !inputPoints1.isEnabled) {
            // Set the content
            val sharePath = getExternalFilesDir(null)
            val shareFile = HelperShare.shareTraining(
                modelTraining,
                sharedPref.getString(getString(R.string.preferenceReportRifleName), ""),
                sharePath,
                baseContext
            )

            // Share the csv
            val fileURI = FileProvider.getUriForFile(this, "de.famprobst.report", shareFile)
            val sharingIntent = Intent()
            sharingIntent.action = Intent.ACTION_SEND
            sharingIntent.type = "text/csv";
            sharingIntent.putExtra(Intent.EXTRA_STREAM, fileURI)
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(sharingIntent)
        } else {
            Snackbar.make(
                layoutDetails,
                getText(R.string.activityDetails_ShareError),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun calculatePoints() {
        // Add all points to list
        val points: MutableList<Double> = mutableListOf()

        if (inputPoints1.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints1.editText?.text.toString().toDouble())
        }
        if (inputPoints2.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints2.editText?.text.toString().toDouble())
        }
        if (inputPoints3.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints3.editText?.text.toString().toDouble())
        }
        if (inputPoints4.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints4.editText?.text.toString().toDouble())
        }
        if (inputPoints5.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints5.editText?.text.toString().toDouble())
        }
        if (inputPoints6.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints6.editText?.text.toString().toDouble())
        }
        if (inputPoints7.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints7.editText?.text.toString().toDouble())
        }
        if (inputPoints8.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints8.editText?.text.toString().toDouble())
        }
        if (inputPoints9.editText?.text!!.isNotEmpty()) {
            points.add(inputPoints9.editText?.text.toString().toDouble())
        }

        // Set value
        if (points.sum().rem(1).equals(0.0)) {
            textPoints.text = "%.0f".format(points.sum())
        } else {
            textPoints.text = "%.1f".format(points.sum())
        }

        // Check what to show as average
        if (inputCount.editText?.text!!.isNotEmpty() && inputCount.editText?.text?.toString() != "0") {
            textAverage.text = getString(
                R.string.activityDetails_Average,
                "%.2f".format(
                    (points.sum() / inputCount.editText?.text.toString().toDouble() * 100) / 100.0
                )
            )
        } else {
            textAverage.text = getString(R.string.activityDetails_Average, "0")
        }
    }
}