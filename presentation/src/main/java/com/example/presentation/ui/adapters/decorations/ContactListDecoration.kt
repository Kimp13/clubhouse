package com.example.presentation.ui.adapters.decorations

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

data class ContactListDecorationProperties(
    @Px val verticalOffset: Int = 0,
    @Px val horizontalOffset: Int = 0,
    @Px val junctionWidth: Float = 0F,
    @ColorInt val junctionColor: Int = Color.TRANSPARENT
)

class ContactListDecoration(
    private val props: ContactListDecorationProperties
) : RecyclerView.ItemDecoration() {
    private val paint = Paint().apply {
        color = props.junctionColor
        strokeWidth = props.junctionWidth
        style = Paint.Style.STROKE
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = props.horizontalOffset
        outRect.right = props.horizontalOffset
        outRect.bottom = props.verticalOffset

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = props.verticalOffset
        }
    }

    override fun onDraw(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val path = Path()

        parent.children.forEachIndexed { index, view ->
            if (index < parent.childCount - 1) {
                canvas.drawPath(
                    path.apply {
                        val leftX =
                            view.left.toFloat() + props.junctionWidth / 2
                        val rightX =
                            view.right.toFloat() - props.junctionWidth / 2

                        val topY = view.bottom.toFloat()
                        val bottomY = topY + props.verticalOffset
                        val middleY = topY + (bottomY - topY) / 2

                        moveTo(leftX, topY)
                        lineTo(leftX, bottomY)

                        moveTo(leftX, middleY)
                        lineTo(rightX, middleY)

                        moveTo(rightX, topY)
                        lineTo(rightX, bottomY)

                        close()
                    },
                    paint
                )
            }
        }
    }
}
