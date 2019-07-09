package com.mariefismi02.messagehiding.utils

import android.content.Context
import android.net.Uri


object UriUtils {
    fun UriFilePath(context: Context, uri: Uri): String?{
        return FileUtils.getFilePath(context, uri)
    }

    fun UriFileName(context: Context, uri: Uri) : String?{
        val filepath = FileUtils.getFilePath(context, uri)
        val split = filepath?.split("/")
        return split?.get(split.size - 1)
    }

    fun UriFileFormat(context: Context, uri: Uri): String?{
        val filepath = FileUtils.getFilePath(context, uri)
        val split = filepath?.split("/")
        val filename = split?.get(split.size - 1)
        return filename?.split(".")?.get(filename.split(".").size - 1)
    }
}