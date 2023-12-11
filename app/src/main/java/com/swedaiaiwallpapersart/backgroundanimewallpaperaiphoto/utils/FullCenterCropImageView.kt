package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class FullCenterCropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var drawableWidth: Int = 0
    private var drawableHeight: Int = 0

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        drawableWidth = drawable?.intrinsicWidth ?: 0
        drawableHeight = drawable?.intrinsicHeight ?: 0
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val widthScale = widthSize.toFloat() / drawableWidth.toFloat()
        val heightScale = heightSize.toFloat() / drawableHeight.toFloat()

        val scale = if (widthScale > heightScale) widthScale else heightScale

        val scaledWidth = (drawableWidth.toFloat() * scale).toInt()
        val scaledHeight = (drawableHeight.toFloat() * scale).toInt()

        setMeasuredDimension(scaledWidth, scaledHeight)
    }
}
