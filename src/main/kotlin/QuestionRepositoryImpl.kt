package com.quiz

import com.quiz.db.QuestionTable
import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup
import com.quiz.model.Question
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class QuestionRepositoryImpl : QuestionRepository {

    override suspend fun getQuestions(): AllLevelsQuestions = newSuspendedTransaction {
        val questions = QuestionTable.selectAll().map {
            Question(
                question = it[QuestionTable.question],
                options = listOf(
                    it[QuestionTable.optionA],
                    it[QuestionTable.optionB],
                    it[QuestionTable.optionC],
                    it[QuestionTable.optionD]
                ),
                correctAnswerIndex = it[QuestionTable.correctIndex]
            ) to it[QuestionTable.level]
        }

        val grouped = questions.groupBy { it.second }.map { (level, items) ->
            LevelQuestionGroup(
                level = level,
                questions = items.map { it.first }
            )
        }

        AllLevelsQuestions(levels = grouped.sortedBy { it.level })
    }

    override suspend fun getQuestionByLevel(level: Int): LevelQuestionGroup = newSuspendedTransaction {
        val questions = QuestionTable.select { QuestionTable.level eq level }.map {
            Question(
                question = it[QuestionTable.question],
                options = listOf(
                    it[QuestionTable.optionA],
                    it[QuestionTable.optionB],
                    it[QuestionTable.optionC],
                    it[QuestionTable.optionD]
                ),
                correctAnswerIndex = it[QuestionTable.correctIndex]
            )
        }

        LevelQuestionGroup(level = level, questions = questions)
    }
}
