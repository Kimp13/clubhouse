package com.example.presentation.ui.views

import android.view.View
import android.widget.TextView

fun Iterable<CharSequence>.fillViewsWithContents(vararg views: TextView) {
    val iterator = iterator()

    views.forEach { view ->
        if (iterator.hasNext()) {
            view.text = iterator.next()
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}

fun showAll(vararg views: View) {
    views.forEach {
        it.visibility = View.VISIBLE
    }
}

fun hideAll(vararg views: View) {
    views.forEach {
        it.visibility = View.GONE
    }
}
