package de.famprobst.report.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.famprobst.report.entity.EntryTraining

@Dao
interface DaoTraining {

    @Query("SELECT * from training_table WHERE rifleId = :rifleId order by date DESC, id DESC")
    fun getAll(rifleId: Int): LiveData<List<EntryTraining>>

    @Query("SELECT * FROM training_table WHERE id = :id")
    fun getById(id: Int): LiveData<EntryTraining>

    @Insert
    fun insert(training: EntryTraining)

    @Update
    fun update(training: EntryTraining)

    @Delete
    fun delete(training: EntryTraining)
}