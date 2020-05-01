package ru.spbstu.gyboml

import android.app.Activity
import android.view.Gravity
import android.widget.Toast

object AndroidUtils {
    fun showToast(activity: Activity, text: String, length: Int = Toast.LENGTH_SHORT) {
        activity.runOnUiThread {
            val toast = Toast.makeText(activity, text, length)
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        }
    }
}