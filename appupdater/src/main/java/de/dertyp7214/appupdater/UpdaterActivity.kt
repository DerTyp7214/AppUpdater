package de.dertyp7214.appupdater

import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

open class UpdaterActivity : AppCompatActivity(), UpdaterBase {
    companion object {
        private var callback: ((UpdaterActivity) -> Unit)? = null
        var instance: UpdaterActivity? = null
            set(value) {
                field = value
                value?.let { callback?.invoke(it) }
            }

        fun getInstance(callback: (UpdaterActivity) -> Unit = {}) {
            this.callback = callback
        }

        fun getInstance(
            timeout: Long,
            catch: () -> Unit,
            callback: (UpdaterActivity) -> Unit = {}
        ) {
            getInstance(callback)
            Handler().postDelayed({
                if (instance == null) catch()
            }, timeout)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        instance = this
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        instance = this
    }

    override fun onGetData(versionCode: Int, newVersionCode: Int, forceUpdate: Boolean) {}
}