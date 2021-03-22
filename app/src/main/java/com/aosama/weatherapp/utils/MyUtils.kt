package com.aosama.weatherapp.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.aosama.weatherapp.R

class MyUtils {
    companion object {
        fun getDlgProgress(context: Context?): Dialog? {
            val dialog = Dialog(context!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dlg_progress)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            dialog.window?.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            return dialog
        }
    }
}