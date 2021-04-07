package de.famprobst.report.helper

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object HelperDatabaseMigration {

    /**
     * Migration of the database from version 1 to 2.
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

            // Modify the training table
            database.execSQL("ALTER TABLE training_table RENAME TO training_table_old")
            database.execSQL("CREATE TABLE IF NOT EXISTS training_table (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `place` TEXT NOT NULL, `training` TEXT NOT NULL, `shoot_count` INTEGER NOT NULL, `shoots` TEXT NOT NULL, `indicator` INTEGER NOT NULL, `image` TEXT NOT NULL, `report` TEXT NOT NULL, `rifleId` INTEGER NOT NULL)")
            database.execSQL("INSERT INTO training_table (date, place, training, shoot_count, shoots, indicator, image, report, rifleId) SELECT date, place, training, shoot_count, shoots, indicator, '', report, rifleId FROM training_table_old;")
            database.execSQL("DROP Table training_table_old")

            // Modify the competition table
            database.execSQL("ALTER TABLE competition_table RENAME TO competition_table_old")
            database.execSQL("CREATE TABLE IF NOT EXISTS competition_table (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `place` TEXT NOT NULL, `kind` TEXT NOT NULL, `shoots` TEXT NOT NULL, `image` TEXT NOT NULL, `report` TEXT NOT NULL, `rifleId` INTEGER NOT NULL)")
            database.execSQL("INSERT INTO competition_table (date, place, kind, shoots, image, report, rifleId) SELECT date, place, kind, shoots, '', report, rifleId FROM competition_table_old;")
            database.execSQL("DROP Table competition_table_old")
        }
    }
}