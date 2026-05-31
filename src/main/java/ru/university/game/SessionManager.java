package ru.university.game;

import ru.university.model.Riddle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionManager {
    private final Map<Long, UserSession> sessions;  // chatId -> UserSession
    private final List<Riddle> allRiddles;

    public SessionManager(List<Riddle> riddles) {
        this.sessions = new HashMap<>();
        this.allRiddles = riddles;
    }

    public UserSession getOrCreateSession(long chatId, String userName) {
        if (!sessions.containsKey(chatId)) {
            sessions.put(chatId, new UserSession(chatId, userName));
        }
        return sessions.get(chatId);
    }

    public void startQuiz(long chatId, String userName) {
        UserSession session = getOrCreateSession(chatId, userName);
        session.startNewGame(allRiddles);
    }

    public void endQuiz(long chatId) {
        UserSession session = sessions.get(chatId);
        if (session != null) {
            session.endGame();
        }
    }

    public UserSession getSession(long chatId) {
        return sessions.get(chatId);
    }

    public String getStats(long chatId) {
        UserSession session = sessions.get(chatId);
        if (session == null) {
            return "У вас пока нет статистики. Начните викторину командой /quiz!";
        }
        return session.getStats();
    }
}