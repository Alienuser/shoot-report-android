package de.famprobst.report.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.famprobst.report.entity.EntryCompetition

@Dao
interface DaoCompetition {

    @Query("SELECT * from competition_table WHERE rifleId = :rifleId order by date DESC, id DESC")
    fun getAll(rifleId: Int): LiveData<List<EntryCompetition>>

    @Query("SELECT * FROM competition_table WHERE id = :id")
    fun getById(id: Int): LiveData<EntryCompetition>

    @Insert
    fun insert(competition: EntryCompetition)

    @Update
    fun update(competition: EntryCompetition)

    @Delete
    fun delete(competition: EntryCompetition)
}