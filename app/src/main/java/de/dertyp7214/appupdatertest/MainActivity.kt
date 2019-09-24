package de.dertyp7214.appupdatertest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.dertyp7214.appupdater.core.checkUpdate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUpdate(updateUrl = "yourUrl", versionCode = BuildConfig.VERSION_CODE, forceUpdate = true)
    }
}
