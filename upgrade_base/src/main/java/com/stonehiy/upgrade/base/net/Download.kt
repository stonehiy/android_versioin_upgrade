package com.stonehiy.upgrade.base.net

import java.io.*
import java.lang.Exception

class Download : IDownload {

    private var _onStart: (() -> Unit)? = null
    private var _onProgress: ((currentLength: Int) -> Unit)? = null
    private var _onFinish: ((localPath: String) -> Unit)? = null
    private var _onFailure: ((error: String?) -> Unit)? = null


    override fun start() {
        _onStart?.invoke()
    }

    override fun progress(currentLength: Int) {
        _onProgress?.invoke(currentLength)
    }

    override fun finish(localPath: String) {
        _onFinish?.invoke(localPath)
    }

    override fun failure(error: String?) {
        _onFailure?.invoke(error)
    }

    override fun writeFile2Disk(inputStream: InputStream?, totalLength: Long, file: File) {
        start()
        var currentLength: Long = 0
        var os: OutputStream? = null
        try {
            os = FileOutputStream(file)
            val buff = ByteArray(1024 * 2)
            while (true) {
                var len = inputStream?.read(buff) ?: -1
                if (-1 == len) {
                    break
                }
                os.write(buff, 0, len)
                currentLength += len
                val progress = (100 * currentLength / totalLength).toInt()
                progress(progress)
                if (progress == 100) {
                    finish(file.absolutePath)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            os?.close()
            inputStream?.close()
        }

    }

    fun onStart(listener: () -> Unit) {
        _onStart = listener

    }

    fun onProgress(listener: (currentLength: Int) -> Unit) {
        _onProgress = listener
    }

    fun onFinish(listener: (localPath: String) -> Unit) {
        _onFinish = listener
    }

    fun onFailure(listener: (error: String?) -> Unit) {
        _onFinish = listener
    }


}