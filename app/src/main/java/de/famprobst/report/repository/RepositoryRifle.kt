package de.famprobst.report.repository

import androidx.lifecycle.LiveData
import de.famprobst.report.dao.DaoRifle
import de.famprobst.report.entity.EntryRifle

class RepositoryRifle(private val rifleDao: DaoRifle) {

    fun allRifles(): LiveData<List<EntryRifle>> {
        return rifleDao.getAll()
    }

    fun insert(rifle: EntryRifle) {
        rifleDao.insert(rifle)
    }

    fun update(rifle: EntryRifle) {
        rifleDao.update(rifle)
    }

    fun getRifle(id: Int): LiveData<EntryRifle> {
        return rifleDao.getRifleById(id)
    }
}