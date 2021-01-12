package de.famprobst.report.repository

import androidx.lifecycle.LiveData
import de.famprobst.report.dao.DaoTraining
import de.famprobst.report.entity.EntryTraining

class RepositoryTraining(private val trainingDao: DaoTraining) {

    fun allTraining(rifleId: Int): LiveData<List<EntryTraining>> {
        return trainingDao.getAll(rifleId)
    }

    fun getById(id: Int): LiveData<EntryTraining> {
        return trainingDao.getById(id)
    }

    fun insert(training: EntryTraining) {
        trainingDao.insert(training)
    }

    fun update(training: EntryTraining) {
        trainingDao.update(training)
    }

    fun delete(training: EntryTraining) {
        trainingDao.delete(training)
    }
}