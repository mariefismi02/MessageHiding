package com.mariefismi02.messagehiding.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import java.time.temporal.Temporal

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "LogData.db", null, 1) {

    companion object{
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance as MyDatabaseOpenHelper
        }
    }



    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(LogData.TABLE_NAME, true,
                LogData.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                LogData.TYPE to TEXT,
                LogData.URI to TEXT,
                LogData.EXECUTION_TIME to INTEGER,
                LogData.EXECUTED_TIME to TEXT,
                LogData.MESSAGE to TEXT,
                LogData.SIZE_BEFORE to INTEGER,
                LogData.SIZE_AFTER to INTEGER
            )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(LogData.TABLE_NAME, true)
    }

}

// Access property for Context
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)