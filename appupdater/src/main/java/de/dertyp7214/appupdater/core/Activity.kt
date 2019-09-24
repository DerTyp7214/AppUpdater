package de.dertyp7214.appupdater.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.downloader.PRDownloader
import de.dertyp7214.appupdater.BasicUpdater
import de.dertyp7214.appupdater.Updater

@SuppressLint("NewApi")
fun Activity.checkUpdate(
    clazz: Class<*> = Updater::class.java,
    updateUrl: String,
    versionCode: Int,
    forceUpdate: Boolean = false
) {
    PRDownloader.initialize(applicationContext)
    BasicUpdater.apply {
        this.updateUrl = updateUrl
        this.versionCode = versionCode
        this.forceUpdate = forceUpdate
        this.context = this@checkUpdate
        checkForUpdate { json, _ ->
            if (json.getBoolean("update")) {
                startActivity(Intent(this@checkUpdate, clazz).apply {
                    putExtra("updateUrl", updateUrl)
                    putExtra("versionCode", versionCode)
                    putExtra("forceUpdate", forceUpdate)
                })
            }
        }
    }
}