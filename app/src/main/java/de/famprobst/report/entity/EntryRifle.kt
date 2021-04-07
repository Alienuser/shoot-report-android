package de.famprobst.report.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rifle_table")
data class EntryRifle(

    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "prefFile") var prefFile: String,
    @ColumnInfo(name = "show", defaultValue = "true") var show: Boolean,
    @ColumnInfo(name = "order") var order: Int
)