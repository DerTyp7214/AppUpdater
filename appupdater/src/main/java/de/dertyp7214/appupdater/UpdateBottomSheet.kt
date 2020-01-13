package de.dertyp7214.appupdater

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class UpdateBottomSheet(
    private val versionCode: Int,
    private val newVersionCode: Int,
    private val forceUpdate: Boolean
) :
    BottomSheetDialogFragment() {
    private var close = true

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet, container, false)

        val newVersionText = v.findViewById<TextView>(R.id.newVersion)
        val cancelBtn = v.findViewById<TextView>(R.id.cancel_button)
        val downloadBtn = v.findViewById<TextView>(R.id.downloadBtn)

        newVersionText.text = "New Update with the version: $newVersionCode"

        cancelBtn.setOnClickListener {
            delayed(200) {
                dismiss()
            }
        }

        downloadBtn.setOnClickListener {

            val dialog = MaterialDialog(activity!!).show {
                setContentView(R.layout.download_dialog)
                cancelOnTouchOutside(false)
                cancelable(false)
            }

            download(
                activity!!,
                dialog.window!!.findViewById(R.id.progressBar)
            ) {
                close = false
                dialog.dismiss()
            }

            delayed(200) {
                close = false
                dismiss()
            }
        }

        return v
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (close) BasicUpdater.callback()
        close = true
        super.onDismiss(dialog)
    }

    private fun download(activity: Activity, progressBar: ProgressBar, finished: () -> Unit) {
        BasicUpdater.download(null)
            .addOnProgressListener { progress, _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) progressBar.setProgress(
                    progress.toInt(),
                    true
                )
                else progressBar.progress = progress.toInt()
            }
            .setFinishListener { path, _ ->
                finished()
                PackageUtils.install(activity, File(path), BasicUpdater.callback)
            }
            .start()
    }

    private fun delayed(timeout: Long, callback: () -> Unit) {
        Handler().postDelayed(callback, timeout)
    }
}