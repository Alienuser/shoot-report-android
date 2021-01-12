package de.famprobst.report.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import de.famprobst.report.helper.HelperConverter
import java.util.*

@Entity(tableName = "training_table")
data class EntryTraining(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @TypeConverters(HelperConverter::class) @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "place") var place: String,
    @ColumnInfo(name = "training") var training: String,
    @ColumnInfo(name = "shoot_count") var shootCount: Int,
    @TypeConverters(HelperConverter::class) @ColumnInfo(name = "shoots") var shoots: List<Double>,
    @ColumnInfo(name = "indicator") var indicator: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) var image: ByteArray,
    @ColumnInfo(name = "report") var report: String,
    @ColumnInfo(name = "rifleId") var rifleId: Int
)