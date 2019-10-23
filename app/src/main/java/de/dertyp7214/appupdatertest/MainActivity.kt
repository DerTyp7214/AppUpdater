package de.dertyp7214.appupdatertest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.dertyp7214.appupdater.core.checkUpdate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUpdate(
            clazz = null,
            updateUrl = "https://api.dertyp7214.de/debug",
            versionCode = BuildConfig.VERSION_CODE,
            forceUpdate = true
        ) {

        }
    }
}
