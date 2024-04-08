package com.example.suppileragrimart.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.suppileragrimart.R

class ProgressDialog {
    fun createAlertDialog(activity: Activity): AlertDialog {
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.custom_progress_dialog, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogLayout)
        val alert = builder.create()
        alert.show()
        alert.window?.setLayout(600, 300)
        return alert
    }

    companion object {
        fun createMessageDialog(context: Context, message: String): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.message_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val tvMessage: TextView = dialog.findViewById(R.id.tvMessageDialog)
            val btnOk: Button = dialog.findViewById(R.id.btnOk)

            tvMessage.text = message
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }
    }
}