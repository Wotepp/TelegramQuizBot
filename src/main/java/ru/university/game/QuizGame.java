package ru.university.game;

import ru.university.model.Riddle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizGame {
    private final List<Riddle> allRiddles;
    private final Set<Riddle> askedRiddles;
    private Riddle currentRiddle;
    private boolean gameActive;

    public QuizGame(List<Riddle> riddles) {
        this.allRiddles = new ArrayList<>(riddles);
        this.askedRiddles = new HashSet<>();
        this.gameActive = false;
        this.currentRiddle = null;
    }

    public String startNewRound() {
        List<Riddle> remaining = new ArrayList<>();
        for (Riddle r : allRiddles) {
            if (!askedRiddles.contains(r)) {
                remaining.add(r);
            }
        }

        if (remaining.isEmpty()) {
            gameActive = false;
            currentRiddle = null;
            return null;
        }

        int randomIndex = (int) (Math.random() * remaining.size());
        currentRiddle = remaining.get(randomIndex);
        askedRiddles.add(currentRiddle);
        gameActive = true;

        return currentRiddle.getQuestion();
    }

    public boolean checkAnswer(String userAnswer) {
        if (!gameActive || currentRiddle == null) {
            return false;
        }
        return currentRiddle.checkAnswer(userAnswer);
    }

    public void endRound() {
        gameActive = false;
        currentRiddle = null;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public Riddle getCurrentRiddle() {
        return currentRiddle;
    }

    public int getRemainingRiddlesCount() {
        int asked = askedRiddles.size();
        return allRiddles.size() - asked;
    }
}