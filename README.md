# BMI Calculator Android Application

This project is an Android application developed using Jetpack Compose that allows users to calculate and track their Body Mass Index (BMI).  
The application includes user authentication, BMI calculation with unit conversion, persistent storage using Firebase, and BMI history visualization.

---

## Overview

The BMI Calculator app provides a simple and reliable way for users to:
- Authenticate using Google or email and password
- Enter personal health data
- Calculate BMI accurately
- Track BMI changes over time using a graphical representation

The application follows modern Android development practices and uses Firebase services for authentication and data storage.

---

## Features

### Authentication
- Google Sign-In using Firebase Authentication
- Email and password registration and login
- Password reset via email
- Authentication state persistence across app restarts

### User Data Management
- Input weight in kilograms or pounds
- Input height in centimeters or inches
- Gender selection (Male, Female, Other)
- Input validation to prevent invalid or empty values

### BMI Calculation
- Accurate BMI calculation with unit conversion
- Automatic BMI category classification:
    - Underweight
    - Normal
    - Overweight
    - Obese

### BMI History and Visualization
- BMI values stored securely in Firebase Firestore
- History limited to the most recent five entries
- Line chart visualization of BMI trend using MPAndroidChart
- Graph displayed when sufficient data points are available

### User Settings
- Users can update height and weight at any time
- BMI and history update automatically after changes

---

## Technology Stack

- Programming Language: Kotlin
- UI Framework: Jetpack Compose (Material 3)
- Authentication: Firebase Authentication
- Database: Firebase Firestore
- Charting Library: MPAndroidChart
- Development Environment: Android Studio (latest stable version)

---

## Project Setup

### Prerequisites
- Android Studio installed
- A Firebase project configured

### Setup Steps
1. Open the project in Android Studio.

2. Configure Firebase:
    - Create a Firebase project
    - Enable Email/Password authentication
    - Enable Google Sign-In
    - Enable Firestore Database
    - Download the `google-services.json` file
    - Place the file inside the `app/` directory

3. Sync Gradle and run the application on an emulator or physical device.

---

## Usage

1. Launch the application.
2. Sign in using Google or email and password.
3. Enter weight, height, and gender.
4. Calculate BMI and view the result.
5. Recalculate BMI to build history.
6. View BMI history and trend graph.
7. Log out and log back in to verify data persistence.


1. Clone the repository:
   ```bash
   git clone https://github.com/piyussh-22/Android-bmiCalculator.git

