package com.edgechild.ai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {

    /** Observe all audit log entries, newest first. The Flow updates the UI automatically. */
    @Query("SELECT * FROM audit_logs ORDER BY id DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    /** Suspend insert – called from a coroutine in SafetyFilter. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}
