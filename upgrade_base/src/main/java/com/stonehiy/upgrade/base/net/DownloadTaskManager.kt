package com.stonehiy.upgrade.base.net

import android.content.Context
import com.stonehiy.upgrade.base.core.AndroidDownloadManager
import com.stonehiy.upgrade.base.core.DownloadListener
import java.util.concurrent.Executors

class DownloadTaskManager private constructor() {
    private var _onPrepare: ((downloadId: Long, taskId: Int) -> Unit)? = null
    private var _onDownLoading: ((progress: Int, taskId: Int) -> Unit)? = null
    private var _onSuccess: ((path: String?, taskId: Int) -> Unit)? = null
    private var _onFailed: ((throwable: Throwable?, taskId: Int) -> Unit)? = null


    companion object {
        private var instance: DownloadTaskManager? = null
            get() {
                if (field == null) {
                    field = DownloadTaskManager()
                }
                return field
            }

        @JvmStatic
        fun instance(): DownloadTaskManager {
            return instance!!
        }
    }

    private val fixedThreadPool = Executors.newFixedThreadPool(3)
    private val tasKMap = HashMap<Int, Long>()
    private val androidDownloadManagerMap = HashMap<Int, AndroidDownloadManager>()

    fun startTask(context: Context, downloadUrl: String, taskId: Int) {
        if (tasKMap.containsKey(taskId)) {
            return
        }
        val androidDownloadManager = AndroidDownloadManager(context, downloadUrl, taskId)
        androidDownloadManager.setListener(object : DownloadListener {
            override fun onPrepare(downloadId: Long, taskId: Int) {
                tasKMap[taskId] = downloadId
                androidDownloadManagerMap[taskId] = androidDownloadManager
                _onPrepare?.invoke(downloadId, taskId)
//                Log.i(TAG, "准备下载")
//                showDownloadDialog(downloadUrl)

            }

            override fun onDownLoading(progress: Int, taskId: Int) {
                _onDownLoading?.invoke(progress, taskId)
//                Log.i(TAG, "onDownLoading = $progress");
//                val progressBar: ProgressBar? =
//                    downloadDialog?.findViewById<ProgressBar>(R.id.progressBar)
//                progressBar?.progress = progress
            }

            override fun onSuccess(path: String?, taskId: Int) {
                tasKMap.remove(taskId)
                androidDownloadManagerMap.remove(taskId)
                _onSuccess?.invoke(path, taskId)
//                Log.i(TAG, "下载成功 path = $path");
//                path?.let { installApkO(this@MainActivity, it) }
            }

            override fun onFailed(throwable: Throwable?, taskId: Int) {
                tasKMap.remove(taskId)
                androidDownloadManagerMap.remove(taskId)
                _onFailed?.invoke(throwable, taskId)
//                Log.i(TAG, "onFailed = ${throwable?.message}");
//                Toast.makeText(this@MainActivity, throwable?.message, Toast.LENGTH_SHORT).show()
            }

        })

        val thread = Thread(Runnable {
            androidDownloadManager.download()
        })
        fixedThreadPool.execute(thread)


    }

    fun onPrepare(listener: (downloadId: Long, taskId: Int) -> Unit) {
        _onPrepare = listener
    }

    fun onDownLoading(listener: (progress: Int, taskId: Int) -> Unit) {
        _onDownLoading = listener
    }

    fun onSuccess(listener: (path: String?, taskId: Int) -> Unit) {
        _onSuccess = listener
    }

    fun onFailed(listener: (throwable: Throwable?, taskId: Int) -> Unit) {
        _onFailed = listener
    }

    fun remove(taskId: Int) {
        val id = tasKMap[taskId]
        val androidDownloadManager = androidDownloadManagerMap[taskId]
        id?.let { androidDownloadManager?.remove(it) }
        androidDownloadManagerMap.remove(taskId)
        tasKMap.remove(taskId)
    }


}