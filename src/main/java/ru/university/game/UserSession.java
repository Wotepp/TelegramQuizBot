package ru.university.game;

import java.util.ArrayList;
import java.util.List;

public class UserSession {
    private final long userId;
    private final String userName;
    private QuizGame currentGame;
    private int correctAnswers;
    private int totalAnswered;
    private List<String> gameHistory;

    public UserSession(long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.correctAnswers = 0;
        this.totalAnswered = 0;
        this.gameHistory = new ArrayList<>();
        this.currentGame = null;
    }

    public void startNewGame(List<ru.university.model.Riddle> riddles) {
        this.currentGame = new QuizGame(riddles);
        this.gameHistory.clear();
    }

    public void endGame() {
        this.currentGame = null;
    }

    public boolean isInGame() {
        return currentGame != null && currentGame.isGameActive();
    }

    public QuizGame getCurrentGame() {
        return currentGame;
    }

    public void recordAnswer(boolean correct, String question, String userAnswer, String correctAnswer) {
        totalAnswered++;
        if (correct) {
            correctAnswers++;
        }
        gameHistory.add(String.format("Вопрос: %s | Ответ: %s | %s",
                question, userAnswer, correct ? "Правильно" : "Неправильно (правильно: " + correctAnswer + ")"));
    }

    public String getStats() {
        double percent = totalAnswered == 0 ? 0 : (correctAnswers * 100.0 / totalAnswered);
        return String.format(
                "Статистика игрока: %s\n" +
                        "Правильных ответов: %d\n" +
                        "Всего отвечено: %d\n" +
                        "Процент успеха: %.1f%%",
                userName, correctAnswers, totalAnswered, percent
        );
    }

    public long getUserId() {
        return userId;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalAnswered() {
        return totalAnswered;
    }
}