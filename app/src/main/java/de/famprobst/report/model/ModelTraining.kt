package de.famprobst.report.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import de.famprobst.report.entity.EntryTraining
import de.famprobst.report.helper.HelperDatabase
import de.famprobst.report.repository.RepositoryTraining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelTraining(application: Application) : AndroidViewModel(application) {

    private val repository: RepositoryTraining

    init {
        val rifleDao = HelperDatabase.getDatabase(application).trainingDao()
        repository = RepositoryTraining(rifleDao)
    }

    fun allTrainings(rifleId: Int): LiveData<List<EntryTraining>> {
        return repository.allTraining(rifleId)
    }

    fun getById(id: Int): LiveData<EntryTraining> {
        return repository.getById(id)
    }

    fun insert(training: EntryTraining) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(training)
    }

    fun update(training: EntryTraining) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(training)
    }

    fun delete(training: EntryTraining) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(training)
    }
}