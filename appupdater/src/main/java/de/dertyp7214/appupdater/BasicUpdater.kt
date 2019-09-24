package de.dertyp7214.appupdater

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import org.json.JSONObject
import java.io.File
import java.net.URL
import kotlin.text.Charsets.UTF_8


@SuppressLint("StaticFieldLeak")
object BasicUpdater {
    var updateUrl: String = ""
    var versionCode: Int = 0
    var newVersionCode: Int = 0
    var forceUpdate: Boolean = false
    var callback = {}
    lateinit var context: Context

    private var fileUrl: String = ""

    fun checkForUpdate(checked: (json: JSONObject, download: (path: String?) -> UpdateHelper) -> Unit) {
        Thread {
            try {
                val response = parseJSON(URL(updateUrl).readText(UTF_8))

                newVersionCode = response.getInt("versionCode")

                fileUrl = response.getString("outputFile")

                response.apply {
                    if (!has("update")) {
                        put("update", newVersionCode > versionCode)
                    }
                }

                checked(
                    response,
                    if (response.getBoolean("update")) this::download else this::nothing
                )
            } catch (e: Exception) {
            }
        }.start()
    }

    fun download(path: String? = null): UpdateHelper {
        val file = getPath("updater")
        if (file.exists()) file.listFiles()?.forEach { it.delete() }
        file.mkdirs()
        return UpdateHelper(fileUrl, path ?: file.absolutePath)
    }

    fun nothing(none: String?): UpdateHelper {
        return UpdateHelper.none()
    }

    private fun getPath(folder: String): File {
        return File(
            context.getExternalFilesDirs(Environment.DIRECTORY_NOTIFICATIONS)[0].absolutePath.removeSuffix(
                "Notifications"
            ), folder
        )
    }

    private fun parseJSON(string: String): JSONObject {
        return try {
            JSONObject(string)
        } catch (e: Exception) {
            JSONObject().apply {
                put("versionName", "0.0")
                put("versionCode", 0)
                put("outputFile", "null")
                put("update", false)
            }
        }
    }
}