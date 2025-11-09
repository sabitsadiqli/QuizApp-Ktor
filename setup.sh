#!/bin/bash

# Create main directory structure
mkdir -p src/main/kotlin/com/quiz/{models,services,routes}
mkdir -p src/main/resources
mkdir -p src/test/http

# Create necessary files if they don't exist
touch src/main/kotlin/com/quiz/Application.kt
touch src/main/kotlin/com/quiz/models/{Topic.kt,Question.kt}
touch src/main/kotlin/com/quiz/services/{AIQuestionGenerator.kt,QuestionService.kt}
touch src/main/kotlin/com/quiz/routes/QuestionRoutes.kt
touch src/main/resources/application.conf
touch build.gradle.kts
touch settings.gradle.kts

# Make the script executable
chmod +x gradlew 