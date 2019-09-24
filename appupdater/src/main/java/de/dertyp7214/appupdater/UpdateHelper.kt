package de.dertyp7214.appupdater

import android.util.Log
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader

class UpdateHelper(private val url: String, private val path: String) {
    companion object {
        fun none(): UpdateHelper = UpdateHelper("", "")
    }

    private val listeners = ArrayList<(progress: Long, bytes: Long, total: Long) -> Unit>()
    private var finishListener: (path: String, duration: Long) -> Unit = { _, _ -> }

    fun addOnProgressListener(listener: (progress: Long, bytes: Long, total: Long) -> Unit): UpdateHelper {
        listeners.add(listener)
        return this
    }

    fun setFinishListener(listener: (path: String, duration: Long) -> Unit): UpdateHelper {
        finishListener = listener
        return this
    }

    fun start() {
        val startTime = System.currentTimeMillis()
        PRDownloader.download(url, path, "update.apk").build()
            .setOnStartOrResumeListener {
                Log.d("START", "START")
            }
            .setOnProgressListener { progress ->
                listeners.forEach {
                    val percentage = (progress.currentBytes / progress.totalBytes) * 100
                    it(percentage, progress.currentBytes, progress.totalBytes)
                }
            }
            .setOnCancelListener { }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    finishListener("$path/update.apk", System.currentTimeMillis() - startTime)
                }

                override fun onError(error: Error?) {
                    Log.d("URL", path)
                    Log.d("ERROR", error?.connectionException?.message ?: "EHH")
                }
            })
    }
}