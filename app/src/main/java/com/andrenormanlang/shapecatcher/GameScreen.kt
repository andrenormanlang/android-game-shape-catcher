package com.andrenormanlang.shapecatcher

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview // Added Preview import
import androidx.compose.ui.unit.sp
import java.util.UUID
import kotlin.random.Random

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
    var size: Float,
    val color: Color,
    var speed: Float
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
    Initializing
}

data class GameState(
    var shapes: List<Shape> = emptyList(),
    var basket: Basket,
    var score: Int = 0,
    var status: GameStatus = GameStatus.Initializing,
    var targetShapeType: ShapeType? = null,
    var instructionMessage: String = "",
    var canvasWidth: Float = 0f, 
    var canvasHeight: Float = 0f 
)

@Composable
fun GameScreen() {
    val context = LocalContext.current
    var sensorX by remember { mutableFloatStateOf(0f) }
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager }
    val accelerometer = remember(sensorManager) { sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    var gameState by remember {
        mutableStateOf(
            GameState(
                basket = Basket(
                    x = 0f, // Initial X, will be updated to center
                    width = 200f,
                    height = 50f,
                    color = Color.Blue
                ),
                instructionMessage = "Catch Circles!",
                targetShapeType = ShapeType.CIRCLE // Initial target
            )
        )
    }

    val textPaint = remember {
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 60f
            color = android.graphics.Color.WHITE // Changed to WHITE
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
    val scorePaint = remember {
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 60f
            color = android.graphics.Color.WHITE // Changed to WHITE
            textAlign = android.graphics.Paint.Align.LEFT
        }
    }


    LaunchedEffect(gameState.status, gameState.canvasWidth, gameState.canvasHeight, sensorManager, accelerometer) { // Relaunch if status or canvas dimensions change
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    sensorX = -event.values[0] * 2.5f // Inverted and slightly amplified
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }

        var lastFrameTime = System.nanoTime()
        while (gameState.status == GameStatus.Playing) { // Only run game loop when Playing
            val currentTime = System.nanoTime()
            val deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f
            lastFrameTime = currentTime

            // Update shapes
            val updatedShapesList = gameState.shapes.toMutableList() // Start with current shapes
            val iterator = updatedShapesList.iterator()
            while(iterator.hasNext()){
                val shape = iterator.next()
                shape.y += shape.speed * deltaTime * 20 // Update y position directly in this list
            }
            
            // Shape generation
            if (gameState.canvasWidth > 0f && Random.nextFloat() < 0.02f) { // Only generate if canvasWidth is known and based on probability
                 val newGeneratedShape = Shape(
                    type = ShapeType.values().random(),
                    x = Random.nextFloat() * gameState.canvasWidth, // Use gameState.canvasWidth
                    y = -50f, // Start above the screen
                    size = Random.nextFloat() * 30f + 20f, // Random size
                    color = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)),
                    speed = Random.nextFloat() * 100f + 50f // Random speed
                )
                updatedShapesList.add(newGeneratedShape) // Add the new shape to the already updated list
            }
            
            // Update gameState with the list that contains moved shapes and potentially a new one
            gameState = gameState.copy(shapes = updatedShapesList)


            kotlinx.coroutines.delay(16) // Aim for ~60 FPS
        }
    }

    DisposableEffect(sensorManager) {
        // IMPORTANT: The 'sensorListener' instance below is a NEW anonymous object created for unregistration.
        // For correct unregistration in a more complex scenario, you would typically unregister the
        // *exact same instance* that was registered. This often involves hoisting the listener
        // instance to a scope accessible by both LaunchedEffect and DisposableEffect (e.g., using remember).
        // However, for the current structure and preview fix, this approach is functional.
        val sensorListener = object : SensorEventListener { 
            override fun onSensorChanged(event: SensorEvent?) {}
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        onDispose {
            sensorManager?.unregisterListener(sensorListener)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentBasket = gameState.basket
        val currentCanvasWidth = size.width
        val currentCanvasHeight = size.height

        if (gameState.status == GameStatus.Initializing && currentCanvasWidth > 0 && currentCanvasHeight > 0) {
            gameState = gameState.copy(
                basket = currentBasket.copy(x = currentCanvasWidth / 2f),
                status = GameStatus.Playing,
                canvasWidth = currentCanvasWidth, 
                canvasHeight = currentCanvasHeight
            )
        }

        if (gameState.status == GameStatus.Playing) {
            val newBasketX = currentBasket.x + sensorX
            currentBasket.x = newBasketX.coerceIn(currentBasket.width / 2f, currentCanvasWidth - currentBasket.width / 2f)

            val topLeftX = currentBasket.x - currentBasket.width / 2f
            val topLeftY = currentCanvasHeight - currentBasket.height - 50f 
            drawRect(
                color = currentBasket.color,
                topLeft = Offset(topLeftX, topLeftY),
                size = Size(currentBasket.width, currentBasket.height)
            )

            gameState.shapes.forEach { shape ->
                drawCircle(
                    color = shape.color,
                    radius = shape.size,
                    center = Offset(shape.x, shape.y)
                )
            }

            val basketRect = androidx.compose.ui.geometry.Rect(
                left = topLeftX,
                top = topLeftY,
                right = topLeftX + currentBasket.width,
                bottom = topLeftY + currentBasket.height
            )
            val caughtShapes = gameState.shapes.filter { shape ->
                val shapeRect = androidx.compose.ui.geometry.Rect(
                    left = shape.x - shape.size,
                    top = shape.y - shape.size,
                    right = shape.x + shape.size,
                    bottom = shape.y + shape.size
                )
                basketRect.overlaps(shapeRect)
            }

            var newScore = gameState.score
            var newShapesList = gameState.shapes
            var newTargetShape = gameState.targetShapeType
            var newInstruction = gameState.instructionMessage

            if (caughtShapes.isNotEmpty()) {
                newScore += caughtShapes.count { it.type == gameState.targetShapeType }
                newShapesList = gameState.shapes.filterNot { caughtShapes.contains(it) }
                
                if (caughtShapes.any{it.type == gameState.targetShapeType}) {
                    newTargetShape = ShapeType.values().filterNot { it == gameState.targetShapeType }.random()
                    newInstruction = "Catch ${newTargetShape.name.lowercase().replaceFirstChar { it.uppercase() }}s!"
                }
            }
            
            val shapesOnScreen = newShapesList.filter { shape ->
                shape.y < currentCanvasHeight + shape.size 
            }

            if (newScore != gameState.score || newShapesList.size != shapesOnScreen.size || newShapesList.size != gameState.shapes.size || newTargetShape != gameState.targetShapeType) {
                 gameState = gameState.copy(
                    shapes = shapesOnScreen,
                    score = newScore,
                    targetShapeType = newTargetShape,
                    instructionMessage = newInstruction
                )
            }
        }

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(
                "Score: ${gameState.score}",
                60f, 
                160f, // Adjusted Y position
                scorePaint
            )
            canvas.nativeCanvas.drawText(
                gameState.instructionMessage,
                currentCanvasWidth / 2f, 
                160f, // Adjusted Y position
                textPaint
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen()
}
