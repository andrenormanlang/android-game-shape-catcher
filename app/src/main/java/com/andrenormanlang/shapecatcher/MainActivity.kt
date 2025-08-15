package com.andrenormanlang.shapecatcher

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andrenormanlang.shapecatcher.ui.theme.ShapeCatcherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()
        setContent {
            ShapeCatcherTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "mainMenu") {
                        composable("mainMenu") {
                            MainMenuScreen(
                                onStartGame = { navController.navigate("gameScreen") }
                            )
                        }
                        composable("gameScreen") {
                            // Assuming GameScreen is in the same package or imported correctly
                            GameScreen()
                        }
                    }
                }
            }
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun MainMenuScreen(modifier: Modifier = Modifier, onStartGame: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Shape Catcher",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Start Game", fontSize = 20.sp)
        }
        // TODO: Add "View Scores" button later
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    ShapeCatcherTheme {
        MainMenuScreen(onStartGame = {})
    }
}