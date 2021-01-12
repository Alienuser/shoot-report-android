package de.famprobst.report.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import de.famprobst.report.entity.EntryRifle
import de.famprobst.report.helper.HelperDatabase
import de.famprobst.report.repository.RepositoryRifle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelRifle(application: Application) : AndroidViewModel(application) {

    private val repository: RepositoryRifle

    val allRifles: LiveData<List<EntryRifle>>

    init {
        val rifleDao = HelperDatabase.getDatabase(application).rifleDao()
        repository = RepositoryRifle(rifleDao)
        allRifles = repository.allRifles()
    }

    fun insert(rifle: EntryRifle) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(rifle)
    }

    fun update(rifle: EntryRifle) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(rifle)
    }

    fun getRifle(id: Int): LiveData<EntryRifle> {
        return repository.getRifle(id)
    }
}