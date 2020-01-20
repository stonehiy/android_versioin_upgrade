package com.stonehiy.upgrade

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.stonehiy.updrade.base.net.Download
import com.stonehiy.updrade.base.net.Version
import com.stonehiy.updrade.base.net.RequestVersion
import com.stonehiy.upgrade.entity.VersionEntity
import com.stonehiy.upgrade.net.Api
import com.stonehiy.upgrade.net.BaseSource
import com.stonehiy.upgrade.net.ViewModelCoroutineScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.dialog_download.*
import kotlinx.coroutines.isActive


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name

    private val viewModelCoroutineScope: ViewModelCoroutineScope = ViewModelCoroutineScope()

    private var requestVersionNet: Version<VersionEntity>? = null
    private var requestDownloadNet: Download? = null

    private var downloadDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_check_version.setOnClickListener {
            requestVersion()
        }

    }

    private fun requestVersion() {
        requestVersionNet = RequestVersion.requestVersionNet<VersionEntity>(this) {
            version()
        } as Version<VersionEntity>
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

    private fun download(fileUrl: String) {
        val api = BaseSource
            .instance
            .create(Api::class.java)

        viewModelCoroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val await = api.downloadApk(fileUrl).await()
                    if (await.isSuccessful) {
                        val body = await.body()
                        val byteStream = body?.byteStream()
                        val contentLength = body?.contentLength() ?: 0L
                        requestDownloadNet?.writeFile2Disk(
                            byteStream,
                            contentLength,
                            File("${getExternalFilesDir(null)?.absoluteFile}${File.separator}${fileUrl}")!!
                        )

                    } else {
                        requestVersionNet?.fail(await.errorBody()?.string())
                    }
                } catch (e: Exception) {
                    requestVersionNet?.fail(e.message)
                }

            }

        }
    }

    private fun showVersionDialog(versionEntity: VersionEntity) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("${versionEntity.title()} - ${versionEntity.versionName()} - ${versionEntity.versionCode()}")
            .setMessage(versionEntity.msg)
            .setPositiveButton("升级") { d: DialogInterface, i: Int ->
                //                Toast.makeText(this, versionEntity.versionName, Toast.LENGTH_SHORT) .show()
                requestDownload(versionEntity)
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
            }
            .show()
    }

    private fun requestDownload(versionEntity: VersionEntity) {
        requestDownloadNet = RequestVersion.requestDownloadNet {
            versionEntity.apkUrl()?.let {
                download(it)
            }

        }
        requestDownloadNet?.onStart {
            showDownloadDialog(versionEntity.apkUrl)

        }
        requestDownloadNet?.onFailure {
            Log.i(TAG, "onFailure = $it")
            Toast.makeText(MainActivity@ this, it, Toast.LENGTH_SHORT).show()

        }
        requestDownloadNet?.onFinish {
            Log.i(TAG, "onFinish =  $it")
            Toast.makeText(MainActivity@ this, it, Toast.LENGTH_SHORT).show()

        }
        requestDownloadNet?.onProgress {
            Log.i(TAG, "onProgress =  $it")
            val progressBar: ProgressBar? =
                downloadDialog?.findViewById<ProgressBar>(R.id.progressBar)
            progressBar?.progress = it
        }

    }

}

