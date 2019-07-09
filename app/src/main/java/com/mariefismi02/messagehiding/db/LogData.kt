package com.mariefismi02.messagehiding.db

import com.mariefismi02.messagehiding.models.StegoData

data class LogData(val id: Long?, val type: String?, val imageURI: String?, val executionTime: Long, val executedTime: String, val message: String, val sizeBefore: Int, val sizeAfter: Int) {
    companion object{
        const val TABLE_NAME: String = "TABLE_LOGDATA"
        const val ID: String = "ID_"
        const val TYPE: String = "LOG_TYPE"
        const val URI: String = "LOG_URI"
        const val EXECUTION_TIME: String = "LOG_TIME"
        const val EXECUTED_TIME: String = "LOG_CREATED"
        const val MESSAGE: String = "LOG_MESSAGE"
        const val SIZE_BEFORE: String = "LOG_SIZE"
        const val SIZE_AFTER: String = "LOG_RSIZE"
    }

    fun logToStegoData(): StegoData{
        return StegoData(imageURI, executionTime, sizeBefore, sizeAfter, message, executedTime)
    }
}