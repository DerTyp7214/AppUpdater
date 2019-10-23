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
    callback: () -> Unit = {}
) {
    if (clazz != null && clazz.isSubclassOf(UpdaterActivity::class)) {
        PRDownloader.initialize(applicationContext)
        BasicUpdater.apply {
            this.updateUrl = updateUrl
            this.versionCode = versionCode
            this.forceUpdate = forceUpdate
            this.context = this@checkUpdate
            this.callback = callback
            checkForUpdate { json, _ ->
                if (json.getBoolean("update")) {
                    startActivity(Intent(this@checkUpdate, clazz.java))
                    Log.d("CLAZZ", UpdaterActivity.instance.toString())
                    UpdaterActivity.getInstance(5000, callback) {
                        it.onGetData(
                            versionCode,
                            newVersionCode,
                            forceUpdate
                        )
                    }
                } else callback()
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
                if (json.getBoolean("update")) {
                    UpdateBottomSheet(versionCode, newVersionCode, forceUpdate).show(supportFragmentManager, "")
                } else callback()
            }
        }
    } else callback()
}