package com.abdnezar.unitonequiz.utils

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class Helper {
    companion object {
        const val COUNTRY_PHONE_CODE = "+970"

        fun log(tag: String, message: String) {
            Log.e(tag, message)
        }

        fun toast(message: String) {
             Toast.makeText(MyApp.instance, message, Toast.LENGTH_LONG).show()
        }

        fun snack(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }

        fun getImage(imageView: ImageView, url: String) {
            Glide.with(imageView).load(url).into(imageView)
        }
    }
}