package com.stonehiy.upgrade

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.stonehiy.updrade.base.net.Net
import com.stonehiy.updrade.base.net.RequestNet
import com.stonehiy.upgrade.entity.VersionEntity
import com.stonehiy.upgrade.net.Api
import com.stonehiy.upgrade.net.BaseSource
import com.stonehiy.upgrade.net.ViewModelCoroutineScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val viewModelCoroutineScope: ViewModelCoroutineScope = ViewModelCoroutineScope()

    private var net: Net<VersionEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_check_version.setOnClickListener {
            net = RequestNet.requestVersionNet<VersionEntity>(this) {
                version()
            } as Net<VersionEntity>
            net?.onDownload { u, download ->
                if (download) {
                    //需要版本更新
                    showVersionDialog(u)
                } else {
                    Toast.makeText(MainActivity@ this, "不需要更新", Toast.LENGTH_SHORT).show()
                }

            }
            net?.onFail {
                Toast.makeText(MainActivity@ this, it, Toast.LENGTH_SHORT).show()

            }
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
                    if (await.code() == 200) {
                        val versionEntity = await.body()
                        versionEntity?.let { net?.success(it) }
                    } else {
                        net?.fail(await.errorBody()?.string())
                    }
                } catch (e: Exception) {
                    net?.fail(e.message)
                }

            }

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelCoroutineScope.close()
    }

    private fun showVersionDialog(versionEntity: VersionEntity) {
        val negativeButton = AlertDialog.Builder(this)
            .setTitle(versionEntity.title())
            .setMessage(versionEntity.msg)
            .setPositiveButton("升级") { d: DialogInterface, i: Int ->
                Toast.makeText(this, "升级", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("取消") { dialogInterface: DialogInterface, i: Int ->
//                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show()

            }
            .show()
    }

}

