package com.andrenormanlang.shapecatcher

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class ShapeType {
    CIRCLE,
    SQUARE,
    STAR
}

data class Shape(
    val id: UUID = UUID.randomUUID(),
    val type: ShapeType,
    var x: Float,
    var y: Float,
    val size: Float,
    val color: Color,
    val speed: Float
)

@Composable
fun GameScreen() {
    // Game logic and UI will go here
}
