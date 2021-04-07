package de.famprobst.report.helper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.famprobst.report.dao.DaoCompetition
import de.famprobst.report.dao.DaoRifle
import de.famprobst.report.dao.DaoTraining
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.entity.EntryRifle
import de.famprobst.report.entity.EntryTraining

@Database(
    entities = [EntryRifle::class, EntryTraining::class, EntryCompetition::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(HelperConverter::class)
abstract class HelperDatabase : RoomDatabase() {

    abstract fun rifleDao(): DaoRifle
    abstract fun trainingDao(): DaoTraining
    abstract fun competitionDao(): DaoCompetition

    companion object {

        @Volatile
        private var INSTANCE: HelperDatabase? = null

        fun getDatabase(context: Context): HelperDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HelperDatabase::class.java,
                    "report.db"
                )
                    .createFromAsset("database/report.db")
                    .addMigrations(
                        HelperDatabaseMigration.MIGRATION_1_2,
                        HelperDatabaseMigration.MIGRATION_2_3,
                        HelperDatabaseMigration.MIGRATION_3_4
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}