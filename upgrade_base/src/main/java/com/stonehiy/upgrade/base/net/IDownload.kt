package com.stonehiy.upgrade.base.net

import java.io.File
import java.io.InputStream

interface IDownload {

    fun start()

    fun progress(currentLength: Int)

    fun finish(localPath: String)

    fun failure(error: String?)


    fun writeFile2Disk(inputStream: InputStream?, contentLength: Long = 0L, file: File)
}