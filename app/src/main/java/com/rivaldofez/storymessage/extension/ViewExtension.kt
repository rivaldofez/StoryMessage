package com.rivaldofez.storymessage.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.setImageFromUrl(
    context: Context,
    url: String,
    placeholder: Drawable?,
    error: Drawable?){

    Glide.with(context)
        .load(url)
        .placeholder(placeholder)
        .error(error)
        .into(this)
}