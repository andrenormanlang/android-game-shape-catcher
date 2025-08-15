package com.andrenormanlang.shapecatcher

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val pressStart2pFamily = FontFamily(
    Font(R.font.press_start_2p_regular, FontWeight.Normal)
)

data class Ball(
    val x: Float,
    val y: Float,
    val speedX: Float,
    val speedY: Float,
    val radius: Float,
    val color: Color
)

data class Paddle(
    val x: Float, // Center X of the paddle
    val y: Float, // Center Y of the paddle
    val width: Float,
    val height: Float,
    val color: Color
)

enum class GameStatus {
    Playing,
    GameOver
}

data class GameState(
    val ball: Ball,
    val paddleTop: Paddle,
    val paddleBottom: Paddle,
    val score: Int = 0,
    val status: GameStatus = GameStatus.Playing
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
                ball = Ball(x = 0f, y = 0f, speedX = 8f, speedY = 8f, radius = 30f, color = Color.White),
                paddleTop = Paddle(x = 0f, y = 0f, width = 250f, height = 40f, color = Color.White),
                paddleBottom = Paddle(x = 0f, y = 0f, width = 250f, height = 40f, color = Color.White)
            )
        )
    }

    DisposableEffect(sensorManager) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    sensorX = -event.values[0] * 2.5f
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager?.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager?.unregisterListener(sensorListener)
        }
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    Box(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(gameState.status, screenWidth, screenHeight) {
            if (gameState.paddleBottom.y == 0f && screenHeight > 0) {
                // *** CHANGE THESE VALUES TO MOVE PADDLES CLOSER TO THE EDGE ***
                val topPaddleY = screenHeight * 0.10f
                val bottomPaddleY = screenHeight * 0.90f

                gameState = gameState.copy(
                    ball = gameState.ball.copy(
                        x = screenWidth / 2f,
                        y = screenHeight / 2f
                    ),
                    paddleTop = gameState.paddleTop.copy(
                        x = screenWidth / 2f,
                        y = topPaddleY
                    ),
                    paddleBottom = gameState.paddleBottom.copy(
                        x = screenWidth / 2f,
                        y = bottomPaddleY
                    )
                )
            }

            while (gameState.status == GameStatus.Playing && screenWidth > 0) {
                val newPaddleX = (gameState.paddleBottom.x + sensorX).coerceIn(
                    gameState.paddleBottom.width / 2f,
                    screenWidth - gameState.paddleBottom.width / 2f
                )

                val newBall = gameState.ball.copy(
                    x = gameState.ball.x + gameState.ball.speedX,
                    y = gameState.ball.y + gameState.ball.speedY
                )

                var newSpeedX = gameState.ball.speedX
                var newSpeedY = gameState.ball.speedY
                var newScore = gameState.score
                var newStatus = gameState.status

                if (newBall.x < newBall.radius || newBall.x > screenWidth - newBall.radius) {
                    newSpeedX *= -1
                }

                val paddleTopRect = androidx.compose.ui.geometry.Rect(
                    left = newPaddleX - gameState.paddleTop.width / 2,
                    top = gameState.paddleTop.y - gameState.paddleTop.height / 2,
                    right = newPaddleX + gameState.paddleTop.width / 2,
                    bottom = gameState.paddleTop.y + gameState.paddleTop.height / 2
                )
                val paddleBottomRect = androidx.compose.ui.geometry.Rect(
                    left = newPaddleX - gameState.paddleBottom.width / 2,
                    top = gameState.paddleBottom.y - gameState.paddleBottom.height / 2,
                    right = newPaddleX + gameState.paddleBottom.width / 2,
                    bottom = gameState.paddleBottom.y + gameState.paddleBottom.height / 2
                )

                val topPaddleCollision = (newBall.y - newBall.radius < paddleTopRect.bottom && newBall.y + newBall.radius > paddleTopRect.top && newSpeedY < 0 && newBall.x > paddleTopRect.left && newBall.x < paddleTopRect.right)
                val bottomPaddleCollision = (newBall.y + newBall.radius > paddleBottomRect.top && newBall.y - newBall.radius < paddleBottomRect.bottom && newSpeedY > 0 && newBall.x > paddleBottomRect.left && newBall.x < paddleBottomRect.right)

                if (topPaddleCollision || bottomPaddleCollision) {
                    newSpeedY *= -1.1f
                    newSpeedX *= 1.1f
                    newScore++
                } else {
                    val topPaddleY = gameState.paddleTop.y
                    val bottomPaddleY = gameState.paddleBottom.y

                    if ((newBall.y < topPaddleY && newSpeedY < 0) || (newBall.y > bottomPaddleY && newSpeedY > 0)) {
                        newStatus = GameStatus.GameOver
                    }
                }

                gameState = gameState.copy(
                    ball = newBall.copy(speedX = newSpeedX, speedY = newSpeedY),
                    paddleTop = gameState.paddleTop.copy(x = newPaddleX),
                    paddleBottom = gameState.paddleBottom.copy(x = newPaddleX),
                    score = newScore,
                    status = newStatus
                )

                delay(16)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = Color.Black, size = size)

            drawRect(
                color = gameState.paddleTop.color,
                topLeft = Offset(gameState.paddleTop.x - gameState.paddleTop.width / 2, gameState.paddleTop.y - gameState.paddleTop.height / 2),
                size = Size(gameState.paddleTop.width, gameState.paddleTop.height)
            )

            drawRect(
                color = gameState.paddleBottom.color,
                topLeft = Offset(gameState.paddleBottom.x - gameState.paddleBottom.width / 2, gameState.paddleBottom.y - gameState.paddleBottom.height / 2),
                size = Size(gameState.paddleBottom.width, gameState.paddleBottom.height)
            )

            drawCircle(
                color = gameState.ball.color,
                radius = gameState.ball.radius,
                center = Offset(gameState.ball.x, gameState.ball.y)
            )
        }

        Text(
            text = "Score: ${gameState.score}",
            style = TextStyle(
                fontFamily = pressStart2pFamily,
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        )

        if (gameState.status == GameStatus.GameOver) {
            Text(
                text = "Game Over",
                style = TextStyle(
                    fontFamily = pressStart2pFamily,
                    fontSize = 36.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameScreenPreview() {
    GameScreen()
}