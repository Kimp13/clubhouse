package com.example.clubhouse.ui.views

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.clubhouse.R

class EllipsizedTextView(context: Context, attributeSet: AttributeSet) :
    AppCompatTextView(context, attributeSet) {
    override fun setText(text: CharSequence?, type: BufferType?) {
        shrink()

        super.setText(text, type)

        post {
            layout?.getEllipsisCount(0)?.takeIf {
                it > 0
            }?.let {
                background = ContextCompat.getDrawable(
                    context,
                    R.drawable.clickable_primary_background
                )

                setOnClickListener {
                    expand()
                }
            } ?: run {
                setOnClickListener(null)
                background = null
            }
        }
    }

    private fun shrink() {
        ellipsize = TextUtils.TruncateAt.END
        maxLines = 1
    }

    private fun expand() {
        ellipsize = null
        maxLines = Int.MAX_VALUE
        background = null
    }
}