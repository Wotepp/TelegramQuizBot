package ru.university.model;

public class Riddle {
    private final String question;
    private final String answer;

    public Riddle(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null) return false;
        return answer.equalsIgnoreCase(userAnswer.trim());
    }
}