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
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.model.ModelCompetition
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ActivityDetailsCompetition : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var sharedPref: SharedPreferences
    private var rifleId = 0
    private var date: Date = Date(System.currentTimeMillis())
    private var competitionKind = ""
    private var newEntry: Boolean = false
    private lateinit var modelCompetition: EntryCompetition
    private var competitionImage = byteArrayOf()
    private lateinit var output: File

    // Input
    lateinit var spinner: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_details_competition)

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
        spinner = findViewById(R.id.activityCompetition_InputKindSpinner)

        setupSpinner()

        // Check what we have to do
        when {
            extras!!.containsKey("competitionId") -> {
                getDataCompetition(extras.getInt(("competitionId")))
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
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.doAfterTextChanged { this.calculatePoints() }
        findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.doAfterTextChanged { this.calculatePoints() }

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
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        competitionKind = parent?.getItemAtPosition(position).toString()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.activityCompetition_InputKindPossibilities,
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

    private fun addNewEntry() {
        // Enable all fields
        this.switchEditable()

        // Set the title
        supportActionBar?.title = getString(R.string.activityDetails_AddCompetition)

        // Set default date
        findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText?.setText(
            SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
        )

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
            findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.setText(
                competition.place
            )
            spinner.setText(competition.kind)
            competitionKind = competition.kind
            date = competition.date
            findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText?.setText(
                SimpleDateFormat("dd.MM.yyyy").format(competition.date)
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints1).editText?.setText(
                competition.shoots[0].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints2).editText?.setText(
                competition.shoots[1].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints3).editText?.setText(
                competition.shoots[2].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints4).editText?.setText(
                competition.shoots[3].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints5).editText?.setText(
                competition.shoots[4].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputPoints6).editText?.setText(
                competition.shoots[5].toString()
            )
            findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.setText(
                competition.report
            )
            if (competition.image.isNotEmpty()) {
                findViewById<ImageView>(R.id.activityDetails_Photo).setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        competition.image,
                        0,
                        competition.image.size
                    ) as Bitmap
                )
                findViewById<ImageView>(R.id.activityDetails_DeletePhoto).visibility = View.VISIBLE
                competitionImage = competition.image
            }
        })
    }

    private fun saveDetails() {

        // Check what we have to save
        if (this.newEntry) {

            // Save data
            val competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)

            competitionModel.insert(
                EntryCompetition(
                    0,
                    date,
                    findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.text.toString(),
                    competitionKind,
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
                            .toDouble()
                    ),
                    competitionImage,
                    findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.text.toString(),
                    rifleId
                )
            )
        } else {
            this.modelCompetition.date = date
            this.modelCompetition.place =
                findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.text.toString()
            this.modelCompetition.kind = competitionKind
            this.modelCompetition.shoots =
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
                        .toDouble()
                )
            this.modelCompetition.image = competitionImage
            this.modelCompetition.report =
                findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.text.toString()

            val competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)
            competitionModel.update(this.modelCompetition)
        }

        // Exit the activity
        finish()
    }

    private fun switchEditable() {
        findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputPlace).editText!!.isEnabled)
        findViewById<MaterialAutoCompleteTextView>(R.id.activityCompetition_InputKindSpinner).isEnabled =
            !(findViewById<MaterialAutoCompleteTextView>(R.id.activityCompetition_InputKindSpinner).isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputDate).editText!!.isEnabled)
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
        findViewById<Button>(R.id.activityDetails_ButtonPhoto).isEnabled =
            !(findViewById<Button>(R.id.activityDetails_ButtonPhoto).isEnabled)
        findViewById<Button>(R.id.activityDetails_ButtonQR).isEnabled =
            !(findViewById<Button>(R.id.activityDetails_ButtonQR).isEnabled)
        findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText?.isEnabled =
            !(findViewById<TextInputLayout>(R.id.activityDetails_InputReport).editText!!.isEnabled)
        findViewById<Button>(R.id.activityDetails_ButtonSave).isEnabled =
            !(findViewById<Button>(R.id.activityDetails_ButtonSave).isEnabled)
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
        output = File(dir, "competitionTraining.jpeg")
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                competitionImage = stream.toByteArray()
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
                competitionImage = stream.toByteArray()
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
        competitionImage = byteArrayOf()
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

        // Set value
        findViewById<TextView>(R.id.activityDetails_Points).text = "%.1f".format(points)
    }
}