package de.famprobst.report.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import de.famprobst.report.helper.HelperConverter
import java.util.*

@Entity(tableName = "competition_table")
data class EntryCompetition(

    @PrimaryKey(autoGenerate = true) var id: Int,
    @TypeConverters(HelperConverter::class) @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "place") var place: String,
    @ColumnInfo(name = "kind") var kind: String,
    @TypeConverters(HelperConverter::class) @ColumnInfo(name = "shoots") var shoots: List<Double>,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) var image: ByteArray,
    @ColumnInfo(name = "report") var report: String,
    @ColumnInfo(name = "rifleId") var rifleId: Int
)