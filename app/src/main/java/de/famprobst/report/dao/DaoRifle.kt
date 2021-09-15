package de.famprobst.report.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.famprobst.report.entity.EntryRifle

@Dao
interface DaoRifle {

    @Query("SELECT * from rifle_table ORDER BY `order`")
    fun getAll(): LiveData<List<EntryRifle>>

    @Query("SELECT * from rifle_table WHERE id = :id")
    fun getRifleById(id: Int): LiveData<EntryRifle>

    @Insert
    fun insert(rifle: EntryRifle)

    @Update
    fun update(rifle: EntryRifle)

    @Delete
    fun delete(rifle: EntryRifle)
}