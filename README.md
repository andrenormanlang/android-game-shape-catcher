# Shape Catcher 🧸🎮

Shape Catcher is an interactive game designed for young children 🧒👧, where they use the device's motion sensors 📱💨 to catch falling shapes. This project is developed as part of an Android development course, focusing on creating a user-friendly application with good object-oriented practices and sensor integration.

## 🌟 Concept

Simple geometric shapes (like circles 🔵, squares 🟥, and stars ⭐) fall from the top of the screen. The player controls a basket 🧺 at the bottom by tilting their phone or tablet. The goal is to catch the correct shapes as instructed (e.g., "Catch all the stars!") while avoiding others. The game's speed will gradually increase to maintain a fun challenge. 速度

## 🎯 Target Audience

Young children. The interface and gameplay are designed to be intuitive and engaging for this age group.

## ✨ Features

*   **Motion Sensor Controls:** Uses the device's accelerometer to control the basket.
*   **Interactive Gameplay:** Catch falling shapes by tilting the device.
*   **Clear Instructions:** (Planned) Voice prompts 🗣️ or visual cues to guide the player (e.g., "Catch the stars!").
*   **Progressive Difficulty:** The game gets faster 🚀 as the player progresses.
*   **User Interface:**
    *   Main Menu (Start Game, View Scores - planned) 🏆
    *   Game Screen
    *   Results Screen (planned)
*   **Scoring:** Keeps track of the player's score. 💯
*   **State Management:**
    *   Saves game state (score, level) during screen rotations. 🔄
    *   Correctly pauses and resumes the game (e.g., during a phone call). ⏸️▶️
*   **Child-Friendly Design:**
    *   Large, clear shapes and a colorful background. 🌈
    *   Minimalistic buttons, suitable for children.
    *   Sound 🔊 and visual feedback ✨ for actions (catching correct/incorrect shapes).

## ✅ How it Meets Course Requirements

*   **Complexity:** The game involves multiple activities/screens (Menu, Game, Results), game logic (game loop, collision detection, scoring), state management, and sensor integration, aiming for a complexity suitable for approximately 70 hours of work. 🛠️
*   **Sensor Usage:** The core gameplay relies on the motion sensor (accelerometer), fulfilling the requirement to use one of the specified Android sensors.
*   **Adapted Interface:** The UI is designed with Android design principles in mind, focusing on a clear, appealing, and easy-to-use interface for the target audience (young children).
*   **Object-Oriented Programming:** The implementation aims for good OOP practices. 🧑‍💻
*   **Code Comments:** Code will be commented for clarity. 📝
*   **Back-Navigation:** Will be handled appropriately from the user's perspective. ⬅️
*   **State Saving:** Implemented for activity lifecycle events like rotation.

## 🛡️ Ethics and Safety

This application is designed with child safety and ethical considerations in mind:

*   **Screen Time:** The game is intended for short, engaging play sessions. (Consideration: A "take a break" ⏰ reminder after a set duration, e.g., 5-10 minutes).
*   **Child Safety:**
    *   The app will **not** contain any advertisements. 🚫📢
    *   The app will **not** include any in-app purchases. 🚫💳
    *   The app will **not** collect any personal data from the user. 🚫📊
    This creates a safe and controlled environment for children.
*   **Data Privacy:** No user data is collected, stored, or transmitted.

##  प्ले Google Play Store Description (Draft)

"Dive into a world of colors and shapes with Shape Catcher! 🧸 Tilt your device to catch the falling stars ⭐, but watch out for the bouncing balls! 🏀 A fun and educational game that develops your child's motor skills and shape recognition. Completely free of ads and purchases. How many shapes can you catch?"

## 🚀 Building and Running

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Let Gradle sync and download dependencies.
4.  Run the app on an Android device or emulator.

*(Further build/release instructions for the signed APK will be relevant upon project completion.)*

## 💡 Future Enhancements (Ideas from Course Guidelines)

*   Different game modes or levels with varying shape types and speeds.
*   More sophisticated visual effects and animations. 🎇
*   Sound effects for different shapes or game events.
*   A high-score table that persists across game sessions.
*   Customizable themes or basket designs.
