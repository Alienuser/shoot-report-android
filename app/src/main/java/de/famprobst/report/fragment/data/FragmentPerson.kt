package de.famprobst.report.fragment.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import de.famprobst.report.R

class FragmentPerson : Fragment() {

    private lateinit var imagePerson: ImageView
    private var imageUri = Uri.EMPTY
    private lateinit var textName: TextInputEditText
    private lateinit var textAge: TextInputEditText
    private lateinit var textSize: TextInputEditText
    private lateinit var textPerformance: TextInputEditText
    private lateinit var textAssociation1: TextInputEditText
    private lateinit var textAssociation2: TextInputEditText
    private lateinit var textTrainer: TextInputEditText
    private lateinit var textTrainerMail: TextInputEditText
    private lateinit var textSquad: TextInputEditText
    private lateinit var textSquadMail: TextInputEditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Set the default view
        val layout = inflater.inflate(R.layout.fragment_masterdata_person, container, false)

        // Set the layout variables
        setLayoutVariables(layout)

        // Set clickListener
        imagePerson.setOnClickListener { takePhoto() }
        saveButton.setOnClickListener { saveData() }

        // Maybe retrieve data
        retrieveData()

        // Return the layout
        return layout
    }

    private fun setLayoutVariables(layout: View) {
        imagePerson = layout.findViewById(R.id.fragmentPerson_Image)
        textName = layout.findViewById(R.id.fragmentPerson_Name)
        textAge = layout.findViewById(R.id.fragmentPerson_Age)
        textSize = layout.findViewById(R.id.fragmentPerson_Size)
        textAssociation1 = layout.findViewById(R.id.fragmentPerson_Association1)
        textAssociation2 = layout.findViewById(R.id.fragmentPerson_Association2)
        textTrainer = layout.findViewById(R.id.fragmentPerson_Trainer)
        textTrainerMail = layout.findViewById(R.id.fragmentPerson_TrainerMail)
        textSquad = layout.findViewById(R.id.fragmentPerson_Squad)
        textSquadMail = layout.findViewById(R.id.fragmentPerson_SquadMail)
        saveButton = layout.findViewById(R.id.fragmentPerson_Save)
    }

    private fun retrieveData() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        imageUri = Uri.parse(sharedPref.getString("masterData_ImageUri", Uri.EMPTY.toString()))
        if (imageUri.toString().isEmpty()) {
            imagePerson.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_cam
                )
            )
        } else {
            imagePerson.setImageURI(imageUri)
        }
        textName.setText(sharedPref.getString("masterData_Name", ""))
        textAge.setText(sharedPref.getString("masterData_Age", ""))
        textSize.setText(sharedPref.getString("masterData_Size", ""))
        textAssociation1.setText(sharedPref.getString("masterData_Association1", ""))
        textAssociation2.setText(sharedPref.getString("masterData_Association2", ""))
        textTrainer.setText(sharedPref.getString("masterData_Trainer", ""))
        textTrainerMail.setText(sharedPref.getString("masterData_TrainerMail", ""))
        textSquad.setText(sharedPref.getString("masterData_Squad", ""))
        textSquadMail.setText(sharedPref.getString("masterData_SquadMail", ""))
    }

    private fun takePhoto() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePhotoIntent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
            val photo = data.extras?.get("data") as Bitmap
            imageUri = saveImageToStorage(photo)
            imagePerson.setImageBitmap(photo)
        }
    }

    private fun saveData() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        with(sharedPref.edit()) {
            putString("masterData_ImageUri", imageUri.toString())
            putString("masterData_Name", textName.text.toString())
            putString("masterData_Age", textAge.text.toString())
            putString("masterData_Size", textSize.text.toString())
            putString("masterData_Association1", textAssociation1.text.toString())
            putString("masterData_Association2", textAssociation2.text.toString())
            putString("masterData_Trainer", textTrainer.text.toString())
            putString("masterData_TrainerMail", textTrainerMail.text.toString())
            putString("masterData_Squad", textSquad.text.toString())
            putString("masterData_SquadMail", textSquadMail.text.toString())

            // Save all data
            apply()
        }

        // Inform user
        Snackbar.make(
            this.requireView(),
            R.string.fragmentPerson_SaveSuccessful,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun saveImageToStorage(bitmap: Bitmap): Uri {
        val filename = "userPicture.jpg"

        requireContext().openFileOutput(filename, Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        return Uri.parse(requireContext().getFileStreamPath(filename).absolutePath)
    }
}