package com.example.seng303_groupb_assignment2.graphcomponents

import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.patrykandpatrick.vico.core.common.DrawingContext
import com.patrykandpatrick.vico.core.common.component.Component

// I need an entire class just to display points when tapping on the graph

class CircleComponent(private val colour: Color, private val radius: Float) : Component {
    private val paint = Paint().apply {
        this.color = colour.toArgb()
        isAntiAlias = true
    }

    override fun draw(context: DrawingContext, left: Float, top: Float, right: Float, bottom: Float) {
        val centerX = (left + right) / 2
        val centerY = (top + bottom) / 2
        context.canvas.drawCircle(centerX, centerY, radius, paint)
    }
}