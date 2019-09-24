package de.dertyp7214.appupdater

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.util.regex.Pattern

object PackageUtils {
    fun install(context: Activity, file: File) {
        try {
            if (file.exists()) {
                val fileNameArray =
                    file.name.split(Pattern.quote(".").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (fileNameArray[fileNameArray.size - 1] == "apk") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val downloadedApk = getFileUri(context, file)
                        val intent = Intent(Intent.ACTION_VIEW).setDataAndType(
                            downloadedApk,
                            "application/vnd.android.package-archive"
                        )
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivityForResult(intent, 187)
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(
                            Uri.fromFile(file),
                            "application/vnd.android.package-archive"
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivityForResult(intent, 187)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("install", e.message!!)
        }
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.applicationContext
                .packageName + ".fileprovider", file
        )
    }
}