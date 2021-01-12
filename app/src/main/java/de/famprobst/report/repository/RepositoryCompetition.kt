package de.famprobst.report.repository

import androidx.lifecycle.LiveData
import de.famprobst.report.dao.DaoCompetition
import de.famprobst.report.entity.EntryCompetition

class RepositoryCompetition(private val competitionDao: DaoCompetition) {

    fun allCompetitions(rifleId: Int): LiveData<List<EntryCompetition>> {
        return competitionDao.getAll(rifleId)
    }

    fun getById(id: Int): LiveData<EntryCompetition> {
        return competitionDao.getById(id)
    }

    fun insert(competition: EntryCompetition) {
        competitionDao.insert(competition)
    }

    fun update(competition: EntryCompetition) {
        competitionDao.update(competition)
    }

    fun delete(competition: EntryCompetition) {
        competitionDao.delete(competition)
    }
}