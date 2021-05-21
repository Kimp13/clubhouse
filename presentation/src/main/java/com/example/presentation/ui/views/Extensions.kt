package com.example.presentation.ui.views

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.presentation.R
import com.example.presentation.ui.delegates.ContactPhotoDelegate

fun Iterable<TextView>.setContents(iterator: Iterator<String>) {
    forEach {
        if (iterator.hasNext()) {
            it.text = iterator.next()
            it.visibility = View.VISIBLE
        } else {
            it.visibility = View.GONE
        }
    }
}

fun TextView.setContents(contents: String) {
    text = contents
    visibility = View.VISIBLE
}

fun ImageView.setPhotoId(photoId: Long?) {
    visibility = View.VISIBLE

    photoId?.let {
        setImageURI(ContactPhotoDelegate.makePhotoUri(it))
        imageTintList = null
    } ?: run {
        setImageResource(R.drawable.ic_baseline_person_24)
        imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.colorPrimary)
        )
    }
}

fun Iterable<View>.hide() {
    forEach {
        it.visibility = View.GONE
    }
}

fun Iterable<View>.show() {
    forEach {
        it.visibility = View.VISIBLE
    }
}
