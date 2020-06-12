package com.stonehiy.upgrade.base.core


import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.math.BigDecimal


/**
 * 下载管理类
 */
class AndroidDownloadManager constructor(
    private val context: Context,
    private val url: String,
    private val taskId: Int = 0
) {
    private val TAG = "AndroidDownloadManager"
    private var name: String = getFileNameByUrl(url)
    private var downloadManager: DownloadManager? = null
    private var downloadId: Long = 0
    private var path: String? = null
    private var listener: DownloadListener? = null

    fun setListener(listener: DownloadListener?) {
        this.listener = listener
    }

    /**
     * 开始下载
     */
    fun download() {

        val request = DownloadManager.Request(Uri.parse(url))
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false)
        /*
       * 设置在通知栏是否显示下载通知(下载进度), 有 3 个值可选:
       *    VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
       *    VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
       *    VISIBILITY_HIDDEN:                    始终不显示通知
       */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setTitle(name)
        request.setDescription("正在下载中...")
//        request.setVisibleInDownloadsUi(true)
        ///storage/emulated/0/Android/data/com.example.dell.mvvmdagger/files/Download/mobileqq_android.apk
        //设置下载的路径
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            name
        )
        request.setDestinationUri(Uri.fromFile(file))
        path = file.absolutePath

        //获取DownloadManager
        if (downloadManager == null) {
            downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        }
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，
        // 通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            downloadId = downloadManager?.enqueue(request) ?: 0L
            listener?.onPrepare(downloadId, taskId)
        }
        checkStatus()
    }

    private fun checkStatus() {
        var isRuning = true
        while (isRuning) {
            val query = DownloadManager.Query()
            //通过下载的id查找
            query.setFilterById(downloadId)
            val cursor = downloadManager?.query(query)
            val bytesAndStatus = intArrayOf(
                -1, -1, 0
            )
            if (cursor != null && cursor.moveToFirst()) {
                val status =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_PAUSED -> {
                    }
                    DownloadManager.STATUS_PENDING -> {
                    }
                    DownloadManager.STATUS_RUNNING -> {
                        Log.i(TAG, "下载中")
                        //已经下载文件大小
                        bytesAndStatus[0] =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        //下载文件的总大小
                        bytesAndStatus[1] =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        //下载状态
                        bytesAndStatus[2] =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        Log.i(
                            TAG,
                            "下载文件的进度" + bytesAndStatus[0].toString() + "/" + bytesAndStatus[1].toString()
                        )
                        val dProgress =
                            deciMal(bytesAndStatus[0], bytesAndStatus[1])
                        listener?.onDownLoading((dProgress * 100).toInt(), taskId)
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        listener?.onDownLoading(100, taskId)
                        listener?.onSuccess(path, taskId)
                        cursor.close()
                        isRuning = false
                    }
                    DownloadManager.STATUS_FAILED -> {
                        listener?.onFailed(Exception("下载失败"), taskId)
                        cursor.close()
                        isRuning = false
                    }
                }
            } else {
                Log.i(TAG, "下载文件不存在")
                cursor?.close()
            }
        }
    }

    private fun deciMal(top: Int, below: Int): Double {
        return BigDecimal((top.toDouble() / below))
            .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    /**
     * 通过URL获取文件名
     *
     * @param url
     * @return
     */
    private fun getFileNameByUrl(url: String): String {
        var filename = url.substring(url.lastIndexOf("/") + 1)
        filename = filename.substring(
            0,
            if (filename.indexOf("?") == -1) filename.length else filename.indexOf("?")
        )
        return filename
    }

}





















































































































































































