package de.dertyp7214.appupdater.core

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.downloader.PRDownloader
import de.dertyp7214.appupdater.BasicUpdater
import de.dertyp7214.appupdater.UpdateBottomSheet
import de.dertyp7214.appupdater.Updater
import de.dertyp7214.appupdater.UpdaterActivity
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@SuppressLint("NewApi")
fun AppCompatActivity.checkUpdate(
    clazz: KClass<*>? = Updater::class,
    updateUrl: String,
    versionCode: Int,
    forceUpdate: Boolean = false,
    dark: Boolean = false,
    timeout: Long = 2000,
    callback: () -> Unit = {}
) {
    var checking = true
    Thread {
        Thread.sleep(timeout)
        if (checking) {
            checking = false
            callback()
        }
    }.start()
    if (clazz != null && clazz.isSubclassOf(UpdaterActivity::class)) {
        PRDownloader.initialize(applicationContext)
        BasicUpdater.apply {
            this.updateUrl = updateUrl
            this.versionCode = versionCode
            this.forceUpdate = forceUpdate
            this.context = this@checkUpdate
            this.callback = callback
            checkForUpdate { json, _ ->
                if (!checking) {
                    callback()
                } else if (json.getBoolean("update")) {
                    checking = false
                    startActivity(Intent(this@checkUpdate, clazz.java))
                    Log.d("CLAZZ", UpdaterActivity.instance.toString())
                    UpdaterActivity.getInstance(5000, callback) {
                        it.onGetData(
                            versionCode,
                            newVersionCode,
                            forceUpdate
                        )
                    }
                } else {
                    checking = false
                    callback()
                }
            }
        }
    } else if (clazz == null) {
        PRDownloader.initialize(applicationContext)
        BasicUpdater.apply {
            this.updateUrl = updateUrl
            this.versionCode = versionCode
            this.forceUpdate = forceUpdate
            this.context = this@checkUpdate
            this.callback = callback
            checkForUpdate { json, _ ->
                if (!checking) {
                    callback()
                } else if (json.getBoolean("update")) {
                    checking = false
                    UpdateBottomSheet(versionCode, newVersionCode, forceUpdate, dark).show(
                        supportFragmentManager,
                        ""
                    )
                } else {
                    checking = false
                    callback()
                }
            }
        }
    } else {
        checking = false
        callback()
    }
}