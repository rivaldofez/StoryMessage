package com.rivaldofez.storymessage.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.setImageFromUrl(
    context: Context,
    url: String,
    placeholder: Drawable? = null,
    error: Drawable? = null){

    Glide.with(context)
        .load(url)
        .placeholder(placeholder)
        .error(error)
        .into(this)
}

fun TextView.setLocaleDateFormat(timestamp: String){
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = dateFormat.parse(timestamp) as Date

    val resultDateFormatted = DateFormat.getDateInstance(DateFormat.FULL).format(date)
    this.text = resultDateFormatted
}