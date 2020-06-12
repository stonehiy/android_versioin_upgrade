package com.stonehiy.upgrade

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.stonehiy.upgrade.base.core.*
import com.stonehiy.upgrade.base.net.DownloadTaskManager
import com.stonehiy.upgrade.entity.VersionEntity
import com.stonehiy.upgrade.net.Api
import com.stonehiy.upgrade.net.BaseSource
import com.stonehiy.upgrade.net.ViewModelCoroutineScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name

    private val viewModelCoroutineScope: ViewModelCoroutineScope = ViewModelCoroutineScope()

    private var requestVersionNet: Version<VersionEntity>? = null

    private var downloadDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_check_version.setOnClickListener {
            requestVersion()
        }

    }

    private fun requestVersion() {
        requestVersionNet =
            RequestVersion.versionNet<VersionEntity>(this) as Version<VersionEntity>
        requestVersionNet?.onDownload { u, download ->
            if (download) {
                //需要版本更新
                showVersionDialog(u)
            } else {
                Toast.makeText(MainActivity@ this, "不需要更新", Toast.LENGTH_SHORT).show()
            }

        }
        requestVersionNet?.onFail {
            Toast.makeText(MainActivity@ this, it, Toast.LENGTH_SHORT).show()

        }
        version()
    }

    private fun version() {
        val api = BaseSource
            .instance
            .create(Api::class.java)
        viewModelCoroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val await = api.checkVersion().await()
                    if (await.isSuccessful) {
                        val versionEntity = await.body()
                        versionEntity?.let { requestVersionNet?.success(it) }

                    } else {
                        requestVersionNet?.fail(await.errorBody()?.string())
                    }
                } catch (e: Exception) {
                    requestVersionNet?.fail(e.message)
                }

            }

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelCoroutineScope.close()
    }


    private fun downloadTask(downloadUrl: String) {
        val taskManager = DownloadTaskManager.instance()
        taskManager.startTask(this, downloadUrl, 0)
        taskManager.onPrepare { downloadId, taskId ->
            val threadName = Thread.currentThread().name
            Log.i(TAG, "onPrepare.....threadName = $threadName")
            runOnUiThread {
                val threadName = Thread.currentThread().name
                Log.i(TAG, "onPrepare.....runOnUiThread = $threadName")
                showDownloadDialog(downloadUrl)
            }


        }
        taskManager.onDownLoading { progress, taskId ->
            runOnUiThread {
                val progressBar: ProgressBar? =
                    downloadDialog?.findViewById<ProgressBar>(R.id.progressBar)
                progressBar?.progress = progress
            }
        }

        taskManager.onSuccess { path, taskId ->
            runOnUiThread {
                path?.let { installApkO(this@MainActivity, it) }
            }

        }
        taskManager.onFailed { throwable, taskId ->
            runOnUiThread {
                Toast.makeText(this@MainActivity, throwable?.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showVersionDialog(versionEntity: VersionEntity) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("${versionEntity.title()} - ${versionEntity.versionName()} - ${versionEntity.versionCode()}")
            .setMessage(versionEntity.msg)
            .setPositiveButton("升级") { d: DialogInterface, i: Int ->
                //                Toast.makeText(this, versionEntity.versionName, Toast.LENGTH_SHORT) .show()
                versionEntity.apkUrl?.let { downloadTask(BaseSource.baseUrl + it) }
            }
            .setNegativeButton("取消") { dialogInterface: DialogInterface, i: Int ->
                //                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show()
            }
            .show()


    }

    private fun showDownloadDialog(apkName: String) {
        downloadDialog = AlertDialog.Builder(this)
            .setTitle("Apk下载")
            .setMessage(apkName)
            .setView(View.inflate(this, R.layout.dialog_download, null))
            .setNegativeButton("取消") { dialogInterface: DialogInterface, i: Int ->
                //                if (viewModelCoroutineScope.isActive) {
//                    viewModelCoroutineScope.close()

//                }
                val taskManager = DownloadTaskManager.instance()
                taskManager.remove(0)
            }
            .show()
    }


    // 3.下载成功，开始安装,兼容8.0安装位置来源的权限
    private fun installApkO(context: Context, downloadApkPath: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //是否有安装位置来源的权限
            val haveInstallPermission = packageManager.canRequestPackageInstalls()
            if (haveInstallPermission) {
                installApkN(context, downloadApkPath)
            } else {
                Toast.makeText(
                    MainActivity@ this,
                    haveInstallPermission.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                installApkN(context, downloadApkPath)
            }
        } else {
            installApkN(context, downloadApkPath)
        }
    }


    fun installApkN(context: Context, downloadApk: String) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Intent.ACTION_VIEW
        val file = File(downloadApk)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".upgradeFileProvider",
                file
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")

        }
        context.startActivity(intent)

    }

}

