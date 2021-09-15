package de.famprobst.report.helper

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object HelperDatabaseMigration {

    /**
     * Migration of the database from version 3 to 4.
     */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {

            // Modify the rifle table
            database.execSQL("DROP Table rifle_table")
            database.execSQL("CREATE TABLE IF NOT EXISTS rifle_table (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `prefFile` TEXT NOT NULL, `show` INTEGER NOT NULL DEFAULT true, `order` INTEGER NOT NULL)")

            // Add all rifles
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (1, 'preference_rifle_1', 1, 0)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (2, 'preference_rifle_2', 1, 1)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (3, 'preference_rifle_3', 1, 3)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (4, 'preference_rifle_4', 1, 4)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (5, 'preference_rifle_5', 1, 6)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (6, 'preference_rifle_6', 1, 8)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (7, 'preference_rifle_7', 1, 10)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (8, 'preference_rifle_8', 1, 2)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (9, 'preference_rifle_9', 1, 5)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (10, 'preference_rifle_10', 1, 7)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (11, 'preference_rifle_11', 1, 9)")
            database.execSQL("INSERT INTO `rifle_table` (`id`, `prefFile`, `show`, `order`) VALUES (12, 'preference_rifle_12', 1, 11)")
        }
    }

    /**
     * Migration of the database from version 2 to 3.
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {

            // Modify the competition table
            database.execSQL("ALTER TABLE competition_table RENAME TO competition_table_old")
            database.execSQL("CREATE TABLE IF NOT EXISTS competition_table (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `place` TEXT NOT NULL, `kind` TEXT NOT NULL, `shoot_count` INTEGER NOT NULL, `shoots` TEXT NOT NULL, `image` TEXT NOT NULL, `report` TEXT NOT NULL, `rifleId` INTEGER NOT NULL)")
            database.execSQL("INSERT INTO competition_table (date, place, kind, shoot_count, shoots, image, report, rifleId) SELECT date, place, kind, 60, shoots, '', report, rifleId FROM competition_table_old;")
            database.execSQL("DROP Table competition_table_old")
        }
    }

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