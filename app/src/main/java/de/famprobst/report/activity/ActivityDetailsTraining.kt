package de.famprobst.report.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import de.famprobst.report.R
import de.famprobst.report.entity.EntryTraining
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
    private lateinit var spinner: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_details_training)

        // Define toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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
        spinner = findViewById(R.id.activityDetails_InputKindSpinner)

        setupSpinner()

        // Check what we have to do
        when {
            extras!!.containsKey("trainingId") -> {
                getDataTraining(extras.getInt("trainingId"))
            }
            else -> {
                addNewEntry()
            }
        }

        // Setup the date picker
        setupDatePicker()

        // Set onClickListener for qr button
        findViewById<Button>(R.id.activityDetails_ButtonQR).setOnClickListener {
            scanQR()
        }

        // Set onClickListener for photo button
        findViewById<Button>(R.id.activityDetails_ButtonPhoto).setOnClickListener {
            takePhoto()
        }

        // Set onClickListener for delete photo button
        findViewById<ImageView>(R.id.activityDetails_DeletePhoto).setOnClickListener {
            if (findViewById<ImageView>(R.id.activityDetails_DeletePhoto).isClickable) {
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
        findViewById<ImageView>(R.id.activityDetails_DeletePhoto).isClickable = false

        // Set onClickListener for save button
        findViewById<Button>(R.id.activityDetails_ButtonSave).setOnClickListener {
            saveDetails()
        }

        // Set onClickListener for share button
        findViewById<Button>(R.id.activityDetails_ButtonShare).setOnClickListener {
            shareDetails()
        }

        // Calculation
        findViewById<TextInputLayout>(R.id.activityDetails_InputCount).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText?.doAfterTextChanged { this.calculatePoints() }

        // Set the text when orientation changed
        if (savedInstanceState != null) {
            when (savedInstanceState.getInt("input_mood")) {
                0 -> {
                    findViewById<RadioGroup>(R.id.activityDetails_radioGroup).check(R.id.activityDetails_radioMinusMinus)
                }
                1 -> {
                    findViewById<RadioGroup>(R.id.activityDetails_radioGroup).check(R.id.activityDetails_radioMinus)
                }
                2 -> {
                    findViewById<RadioGroup>(R.id.activityDetails_radioGroup).check(R.id.activityDetails_radioPlus)
                }
                3 -> {
                    findViewById<RadioGroup>(R.id.activityDetails_radioGroup).check(R.id.activityDetails_radioPlusPlus)
                }
            }
            findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.setText(
                savedInstanceState.getString("input_place")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.setText(
                savedInstanceState.getString("input_report")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.setText(
                savedInstanceState.getString("input_points1")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.setText(
                savedInstanceState.getString("input_points2")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.setText(
                savedInstanceState.getString("input_points3")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.setText(
                savedInstanceState.getString("input_points4")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.setText(
                savedInstanceState.getString("input_points5")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.setText(
                savedInstanceState.getString("input_points6")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText?.setText(
                savedInstanceState.getString("input_points7")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText?.setText(
                savedInstanceState.getString("input_points8")
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText?.setText(
                savedInstanceState.getString("input_points9")
            )
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        when (findViewById<RadioButton>(findViewById<RadioGroup>(R.id.activityDetails_radioGroup).checkedRadioButtonId)) {
            findViewById<RadioButton>(R.id.activityDetails_radioMinusMinus) -> {
                outState.putInt("input_mood", 0)
            }
            findViewById<RadioButton>(R.id.activityDetails_radioMinus) -> {
                outState.putInt("input_mood", 1)
            }
            findViewById<RadioButton>(R.id.activityDetails_radioPlus) -> {
                outState.putInt("input_mood", 2)
            }
            findViewById<RadioButton>(R.id.activityDetails_radioPlusPlus) -> {
                outState.putInt("input_mood", 3)
            }
        }
        outState.putString(
            "input_place",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.text.toString()
        )
        outState.putString(
            "input_report",
            findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.text.toString()
        )
        outState.putString(
            "input_points1",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.text.toString()
        )
        outState.putString(
            "input_points2",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.text.toString()
        )
        outState.putString(
            "input_points3",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.text.toString()
        )
        outState.putString(
            "input_points4",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.text.toString()
        )
        outState.putString(
            "input_points5",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.text.toString()
        )
        outState.putString(
            "input_points6",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.text.toString()
        )
        outState.putString(
            "input_points7",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText?.text.toString()
        )
        outState.putString(
            "input_points8",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText?.text.toString()
        )
        outState.putString(
            "input_points9",
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText?.text.toString()
        )
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.activityDetails_InputKindPossibilities,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.onItemClickListener = this
    }

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
                findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText?.setText(
                    sdf.format(
                        cal.time
                    )
                )
            }

        findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText?.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        trainingKind = parent?.getItemAtPosition(position).toString()
    }

    private fun addNewEntry() {
        // Enable all fields
        this.switchEditable()

        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_AddTraining)

        // Set default date
        findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText?.setText(
            SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
        )

        // Set variables
        this.newEntry = true
    }

    private fun getDataTraining(trainingId: Int) {
        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_EditTraining)

        // Set variables
        this.newEntry = false

        // Show data
        val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)
        trainingModel.getById(trainingId).observe(this, { training ->
            // Set training
            this.modelTraining = training

            // Set data
            when (training.indicator) {
                0 -> {
                    findViewById<RadioGroup>(R.id.activityDetails_radioGroup).check(R.id.activityDetails_radioMinusMinus)
                }
                1 -> {
                    findViewById<RadioGroup>(R.id.activityDetails_radioGroup).check(R.id.activityDetails_radioMinus)
                }
                2 -> {
                    findViewById<RadioGroup>(R.id.activityDetails_radioGroup).check(R.id.activityDetails_radioPlus)
                }
                3 -> {
                    findViewById<RadioGroup>(R.id.activityDetails_radioGroup).check(R.id.activityDetails_radioPlusPlus)
                }
            }
            findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.setText(
                training.place
            )
            spinner.setText(training.training)
            trainingKind = training.training
            date = training.date
            findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText?.setText(
                SimpleDateFormat("dd.MM.yyyy").format(training.date)
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputCount).editText?.setText(
                training.shootCount.toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.setText(
                training.shoots[0].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.setText(
                training.shoots[1].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.setText(
                training.shoots[2].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.setText(
                training.shoots[3].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.setText(
                training.shoots[4].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.setText(
                training.shoots[5].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText?.setText(
                training.shoots[6].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText?.setText(
                training.shoots[7].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText?.setText(
                training.shoots[8].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.setText(
                training.report
            )
            if (training.image.isNotEmpty()) {
                findViewById<ImageView>(R.id.activityDetails_Photo).setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        training.image,
                        0,
                        training.image.size
                    ) as Bitmap
                )
                findViewById<ImageView>(R.id.activityDetails_DeletePhoto).visibility = View.VISIBLE
                trainingImage = training.image
            }
        })
    }

    private fun saveDetails() {
        // Get indicator
        var indicator = 2
        when (findViewById<RadioButton>(findViewById<RadioGroup>(R.id.activityDetails_radioGroup).checkedRadioButtonId)) {
            findViewById<RadioButton>(R.id.activityDetails_radioMinusMinus) -> {
                indicator = 0
            }
            findViewById<RadioButton>(R.id.activityDetails_radioMinus) -> {
                indicator = 1
            }
            findViewById<RadioButton>(R.id.activityDetails_radioPlus) -> {
                indicator = 2
            }
            findViewById<RadioButton>(R.id.activityDetails_radioPlusPlus) -> {
                indicator = 3
            }
        }

        // Check what we have to save
        if (this.newEntry) {

            // Save data
            val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)

            trainingModel.insert(
                EntryTraining(
                    0,
                    date,
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.text.toString(),
                    trainingKind,
                    findViewById<TextInputLayout>(R.id.activityDetails_InputCount).editText?.text.toString()
                        .toInt(),
                    listOf(
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.text.toString()
                            .toDouble(),
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.text.toString()
                            .toDouble(),
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.text.toString()
                            .toDouble(),
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.text.toString()
                            .toDouble(),
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.text.toString()
                            .toDouble(),
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.text.toString()
                            .toDouble(),
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText?.text.toString()
                            .toDouble(),
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText?.text.toString()
                            .toDouble(),
                        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText?.text.toString()
                            .toDouble()
                    ),
                    indicator,
                    trainingImage,
                    findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.text.toString(),
                    rifleId
                )
            )
        } else {
            this.modelTraining.date = date
            this.modelTraining.place =
                findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.text.toString()
            this.modelTraining.training = trainingKind
            this.modelTraining.shootCount =
                findViewById<TextInputLayout>(R.id.activityDetails_InputCount).editText?.text.toString()
                    .toInt()
            this.modelTraining.shoots =
                listOf(
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.text.toString()
                        .toDouble(),
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.text.toString()
                        .toDouble(),
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.text.toString()
                        .toDouble(),
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.text.toString()
                        .toDouble(),
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.text.toString()
                        .toDouble(),
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.text.toString()
                        .toDouble(),
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText?.text.toString()
                        .toDouble(),
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText?.text.toString()
                        .toDouble(),
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText?.text.toString()
                        .toDouble()
                )
            this.modelTraining.indicator = indicator
            this.modelTraining.image = trainingImage
            this.modelTraining.report =
                findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.text.toString()

            val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)
            trainingModel.update(this.modelTraining)
        }

        // Exit the activity
        finish()
    }

    private fun switchEditable() {
        findViewById<RadioButton>(R.id.activityDetails_radioMinusMinus).isEnabled =
            !(findViewById<RadioButton>(R.id.activityDetails_radioMinusMinus).isEnabled)
        findViewById<RadioButton>(R.id.activityDetails_radioMinus).isEnabled =
            !(findViewById<RadioButton>(R.id.activityDetails_radioMinus).isEnabled)
        findViewById<RadioButton>(R.id.activityDetails_radioPlus).isEnabled =
            !(findViewById<RadioButton>(R.id.activityDetails_radioPlus).isEnabled)
        findViewById<RadioButton>(R.id.activityDetails_radioPlusPlus).isEnabled =
            !(findViewById<RadioButton>(R.id.activityDetails_radioPlusPlus).isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText!!.isEnabled)
        findViewById<MaterialAutoCompleteTextView>(R.id.activityDetails_InputKindSpinner).isEnabled =
            !(findViewById<MaterialAutoCompleteTextView>(R.id.activityDetails_InputKindSpinner).isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputCount).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputCount).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText!!.isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText!!.isEnabled)
        findViewById<Button>(R.id.activityDetails_ButtonSave).isEnabled =
            !(findViewById<Button>(R.id.activityDetails_ButtonSave).isEnabled)
        findViewById<Button>(R.id.activityDetails_ButtonPhoto).isEnabled =
            !(findViewById<Button>(R.id.activityDetails_ButtonPhoto).isEnabled)
        findViewById<Button>(R.id.activityDetails_ButtonQR).isEnabled =
            !(findViewById<Button>(R.id.activityDetails_ButtonQR).isEnabled)
        findViewById<ImageView>(R.id.activityDetails_DeletePhoto).isClickable =
            !(findViewById<ImageView>(R.id.activityDetails_DeletePhoto).isClickable)
    }

    private fun scanQR() {
        Snackbar.make(
            findViewById(R.id.activity_details),
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
                findViewById<ImageView>(R.id.activityDetails_Photo).setImageURI(data.data)

                // Save image
                val bitmap =
                    (findViewById<ImageView>(R.id.activityDetails_Photo).drawable as BitmapDrawable).bitmap
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
                findViewById<ImageView>(R.id.activityDetails_Photo).setImageURI(null)
                findViewById<ImageView>(R.id.activityDetails_Photo).setImageBitmap(bitmap)

                // Save image
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                trainingImage = stream.toByteArray()
            }
            findViewById<ImageView>(R.id.activityDetails_DeletePhoto).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.activityDetails_DeletePhoto).isClickable = true
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
        findViewById<ImageView>(R.id.activityDetails_Photo).setImageDrawable(null)
        findViewById<ImageView>(R.id.activityDetails_DeletePhoto).visibility = View.GONE
        trainingImage = byteArrayOf()
    }

    private fun shareDetails() {
        Snackbar.make(
            findViewById(R.id.activity_details),
            getText(R.string.activityDetails_InfoShare),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun calculatePoints() {
        var points = 0.0

        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints1
        ).editText?.text.toString().toDouble()
        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints2
        ).editText?.text.toString().toDouble()
        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints3
        ).editText?.text.toString().toDouble()
        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints4
        ).editText?.text.toString().toDouble()
        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints5
        ).editText?.text.toString().toDouble()
        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints6
        ).editText?.text.toString().toDouble()
        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints7).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints7
        ).editText?.text.toString().toDouble()
        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints8).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints8
        ).editText?.text.toString().toDouble()
        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputPoints9).editText?.text.isNullOrEmpty()) points += findViewById<TextInputLayout>(
            R.id.activityDetails_InputPoints9
        ).editText?.text.toString().toDouble()

        // Set value
        findViewById<TextView>(R.id.activityDetails_Points).text = "%.1f".format(points)

        if (!findViewById<TextInputLayout>(R.id.activityDetails_InputCount).editText?.text.isNullOrEmpty() && !findViewById<TextInputLayout>(
                R.id.activityDetails_InputCount
            ).editText?.text.toString().equals("0")
        ) {
            findViewById<TextView>(R.id.activityDetails_Average).text = getString(
                R.string.activityDetails_Average,
                "%.2f".format(
                    (points / findViewById<TextInputLayout>(R.id.activityDetails_InputCount).editText?.text.toString()
                        .toDouble() * 100) / 100.0
                )
            )
        } else {
            findViewById<TextView>(R.id.activityDetails_Average).text =
                getString(R.string.activityDetails_Average, "0")
        }
    }
}