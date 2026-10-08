package com.edgechild.ai

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuditLogDatabase(
        @ApplicationContext context: Context
    ): AuditLogDatabase = AuditLogDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideAuditLogDao(db: AuditLogDatabase): AuditLogDao = db.auditLogDao()
}
