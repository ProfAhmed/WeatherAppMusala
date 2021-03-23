package com.aosama.weatherapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.aosama.weatherapp.R

class MyProgressDialog {
    companion object {
        private lateinit var progressDialog: Dialog
        fun getDlgProgress(isShowing: Boolean, context: Context?) {
            if (isShowing) {
                progressDialog = Dialog(context!!)
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                progressDialog.setContentView(R.layout.dlg_progress)
                progressDialog.setCancelable(false)
                progressDialog.window!!.setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT)
                )
                progressDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                progressDialog.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                progressDialog.show()
            } else if (!isShowing) {
                progressDialog.dismiss()
            }
        }
    }
}