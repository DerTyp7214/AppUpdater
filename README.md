# AppUpdater

[![](https://jitpack.io/v/de.dertyp7214/AppUpdater.svg)](https://jitpack.io/#de.dertyp7214/AppUpdater)


## usage

at first register the activity and import material ui

in your `build.gradle (:app)` import:

```gradle
implementation 'com.google.android.material:material:<version>'
```

in your manifest:

```xml
<activity android:name="de.dertyp7214.appupdater.Updater" android:theme="@style/AppUpdaterTheme" />
```

in your activity just call this function.

```kotlin
checkUpdate(
    updateUrl = "your url"/*the url with the given response*/, versionCode = BuildConfig.VERSION_CODE/*curren versioncode*/, forceUpdate = false/*<(true|false) wont let the user back>*/
) {
    runOnUiThread {
      // if you need some work on ui thread you have to add runOnUiThread
    }
}
```

the api should return something like:

```json
{
  "versionCode": 0,
  "outputFile": "total url for the update apk"
}
```
