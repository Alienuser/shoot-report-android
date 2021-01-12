package de.famprobst.report.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.helper.HelperDatabase
import de.famprobst.report.repository.RepositoryCompetition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelCompetition(application: Application) : AndroidViewModel(application) {

    private val repository: RepositoryCompetition

    init {
        val competitionDao = HelperDatabase.getDatabase(application).competitionDao()
        repository = RepositoryCompetition(competitionDao)
    }

    fun allCompetitions(rifleId: Int): LiveData<List<EntryCompetition>> {
        return repository.allCompetitions(rifleId)
    }

    fun getById(id: Int): LiveData<EntryCompetition> {
        return repository.getById(id)
    }

    fun insert(competition: EntryCompetition) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(competition)
    }

    fun update(competition: EntryCompetition) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(competition)
    }

    fun delete(competition: EntryCompetition) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(competition)
    }
}