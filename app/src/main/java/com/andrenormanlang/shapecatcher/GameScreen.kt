package com.andrenormanlang.shapecatcher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

data class Basket(
    var x: Float, // Center X of the basket
    val width: Float,
    val height: Float,
    val color: Color
)

enum class GameStatus {
    Playing,
    Paused,
    GameOver,
    Initializing // Added for the initial basket setup
}

data class GameState(
    val shapes: List<Shape> = emptyList(),
    var basket: Basket, // Made basket mutable within GameState for easier update
    val score: Int = 0,
    var status: GameStatus = GameStatus.Initializing, // Start in Initializing state
    val targetShapeType: ShapeType? = null,
    val instructionMessage: String = ""
)

@Composable
fun GameScreen() {
    var gameState by remember {
        mutableStateOf(
            GameState(
                basket = Basket(
                    x = 0f, // Initial X, will be updated to center
                    width = 200f,
                    height = 50f,
                    color = Color.Blue
                ),
                instructionMessage = "Get ready!"
            )
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentBasket = gameState.basket
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Initialize basket position to center of screen once, and update status
        if (gameState.status == GameStatus.Initializing && canvasWidth > 0) {
            gameState = gameState.copy(
                basket = currentBasket.copy(x = canvasWidth / 2f),
                status = GameStatus.Playing // Move to Playing status after init
            )
        }

        // Only draw if not initializing (i.e., basket x has been set)
        if (gameState.status != GameStatus.Initializing) {
            // Calculate top-left for drawing, based on basket's center x
            val topLeftX = currentBasket.x - currentBasket.width / 2f
            val topLeftY = canvasHeight - currentBasket.height - 50f // 50f padding from bottom

            drawRect(
                color = currentBasket.color,
                topLeft = Offset(topLeftX, topLeftY),
                size = Size(currentBasket.width, currentBasket.height)
            )
        }

        // TODO: Draw shapes
        // TODO: Draw score
        // TODO: Draw instruction message
    }
}

