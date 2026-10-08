package com.edgechild.ai

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Single Room database for the app.
 * Access via [AuditLogDatabase.getInstance] (initialised in EdgeChildApplication).
 */
@Database(entities = [AuditLogEntity::class], version = 1, exportSchema = false)
abstract class AuditLogDatabase : RoomDatabase() {

    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AuditLogDatabase? = null

        fun getInstance(context: Context): AuditLogDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AuditLogDatabase::class.java,
                    "audit_log_db"
                ).build().also { INSTANCE = it }
            }
    }
}
