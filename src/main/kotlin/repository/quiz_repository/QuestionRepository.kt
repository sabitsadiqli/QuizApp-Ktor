package repository.quiz_repository

import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup
import model.HighScoreResult
import model.QuizResult

interface QuestionRepository {
    suspend fun getQuestions(userId:String): AllLevelsQuestions
    suspend fun getQuestionByLevel(level: Int): LevelQuestionGroup
    suspend fun calculateResult(userId: String,level: Int, answers: List<Int>): QuizResult
    suspend fun insertResult(result: QuizResult)
    suspend fun getAllResults(): List<QuizResult>
    suspend fun unlockNextLevel(currentLevel: Int): Boolean
    suspend fun getAllUsersHighestScores(): List<HighScoreResult>
}