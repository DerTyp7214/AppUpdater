package de.dertyp7214.appupdater

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import de.dertyp7214.appupdater.PackageUtils.install
import kotlinx.android.synthetic.main.activity_updater.*
import java.io.File


class Updater : UpdaterActivity() {
    private var forceUpdate = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updater)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetData(versionCode: Int, newVersionCode: Int, forceUpdate: Boolean) {
        newVersion.text = "Version: ${BasicUpdater.newVersionCode}"

        downloadBtn.isEnabled = true
        downloadBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                download()
            else ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1337
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 1337)
            download()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 187) {
            finish()
            BasicUpdater.callback()
        }
    }

    private fun download() {
        downloadBtn.isEnabled = false
        downloadBtn.setTextColor(Color.LTGRAY)
        ChangeBounds().apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            TransitionManager.beginDelayedTransition(progressBar.parent as ViewGroup, this)
        }
        progressBar.apply {
            if (layoutParams != null) layoutParams.height = WRAP_CONTENT
            else layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            requestLayout()
        }
        BasicUpdater.download(null)
            .addOnProgressListener { progress, bytes, total ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) progressBar.setProgress(
                    progress.toInt(),
                    true
                )
                else progressBar.progress = progress.toInt()
            }
            .setFinishListener { path, duration ->
                install(this, File(path), BasicUpdater.callback)
            }
            .start()
    }

    override fun onBackPressed() {
        if (!forceUpdate) {
            super.onBackPressed()
            BasicUpdater.callback()
        }
    }
}