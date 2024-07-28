package com.anpe.bingewatch.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anpe.bingewatch.data.local.entity.WatchNewEntity

@Database(entities = [WatchNewEntity::class], version = 1, exportSchema = false)
abstract class WatchDatabase : RoomDatabase() {
    companion object {
        private var instance: WatchDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val sql = "ALTER TABLE WatchEntity ADD COLUMN sex INTEGER DEFAULT 1"
                val sql2 = "ALTER TABLE WatchEntity ADD COLUMN sex2 INTEGER DEFAULT 1"
                db.execSQL(sql)
                db.execSQL(sql2)
            }
        }

        private val MIGRATION_1_20: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 创建一个新表
                db.execSQL("CREATE TABLE `WatchEntityTemp` ( `id` INTEGER PRIMARY KEY NOT NULL, `title` TEXT NOT NULL, `remarks` TEXT, `last_time` INTEGER NOT NULL DEFAULT 0,)")
                // 将旧表中数据复制到新表中
                db.execSQL("INSERT INTO WatchEntityTemp (cover_url,item_id,last_time) select  cover_url , item_id , last_time  from NewsFeedData")
                // 删除旧表
                db.execSQL("DROP TABLE WatchEntity")
                // 将新表生命名为旧表名
                db.execSQL("ALTER TABLE WatchEntityTemp RENAME TO WatchEntity")
            }
        }

        @Synchronized
        fun getWatchDatabase(context: Context): WatchDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    WatchDatabase::class.java,
                    "watch_database.db"
                ).build()
            }
            return instance as WatchDatabase
        }
    }

    abstract fun getWatchDao(): WatchDao
}