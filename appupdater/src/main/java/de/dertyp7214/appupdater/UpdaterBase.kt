package de.dertyp7214.appupdater

interface UpdaterBase {
    fun onGetData(versionCode: Int, newVersionCode: Int, forceUpdate: Boolean)
}