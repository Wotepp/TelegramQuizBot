package ru.university;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.university.config.BotConfig;
import ru.university.game.QuizGame;
import ru.university.game.SessionManager;
import ru.university.game.UserSession;
import ru.university.loader.RiddleLoader;
import ru.university.model.Riddle;

import java.util.List;

public class QuizBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final SessionManager sessionManager;
    private final List<Riddle> riddles;

    public QuizBot() {
        this.telegramClient = new OkHttpTelegramClient(BotConfig.BOT_TOKEN);

        String riddlesFilePath = "riddles.txt";
        this.riddles = RiddleLoader.loadFromFile(riddlesFilePath);
        this.sessionManager = new SessionManager(riddles);

        System.out.println("Бот викторины запущен!");
        System.out.println("Доступно загадок: " + riddles.size());
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            String response = handleMessage(chatId, userName, messageText);
            sendMessage(chatId, response);
        }
    }

    private String handleMessage(long chatId, String userName, String message) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.equals("/start")) {
            return "Привет! Я бот-викторина с загадками!\n\n" +
                    "Доступные команды:\n" +
                    "/quiz — начать викторину\n" +
                    "/stats — посмотреть статистику\n" +
                    "/stop — остановить текущую викторину\n" +
                    "/help — показать это сообщение\n\n" +
                    "Отгадай загадку — проверь свой ум!";
        }

        if (lowerMessage.equals("/help")) {
            return "Правила:\n" +
                    "1. Напиши /quiz — я задам загадку\n" +
                    "2. Отправь свой ответ текстом\n" +
                    "3. Если правильно — +1 балл и новый вопрос\n" +
                    "4. /stats — твоя статистика\n" +
                    "5. /stop — закончить игру\n\n" +
                    "Удачи!";
        }

        if (lowerMessage.equals("/stats")) {
            return sessionManager.getStats(chatId);
        }

        if (lowerMessage.equals("/stop")) {
            sessionManager.endQuiz(chatId);
            return "Викторина остановлена. Чтобы начать заново, напиши /quiz";
        }

        if (lowerMessage.equals("/quiz")) {
            UserSession session = sessionManager.getOrCreateSession(chatId, userName);

            if (session.isInGame()) {
                QuizGame currentGame = session.getCurrentGame();
                if (currentGame.getCurrentRiddle() != null) {
                    return "Вы уже в игре! Вот текущая загадка:\n\n" + currentGame.getCurrentRiddle().getQuestion();
                }
            }

            sessionManager.startQuiz(chatId, userName);
            session = sessionManager.getSession(chatId);
            QuizGame game = session.getCurrentGame();

            if (game == null) {
                return "Ошибка: не удалось начать игру";
            }

            String question = game.startNewRound();
            if (question == null) {
                return "Поздравляю! Ты отгадал(а) ВСЕ загадки!\n" +
                        "Напиши /stats, чтобы посмотреть результат, или /quiz, чтобы начать заново (вопросы повторятся)";
            }

            return "Загадка #" + (riddles.size() - game.getRemainingRiddlesCount()) + "\n\n❓ " + question;
        }

        UserSession session = sessionManager.getSession(chatId);
        if (session != null && session.isInGame()) {
            QuizGame game = session.getCurrentGame();
            if (game != null && game.isGameActive()) {
                Riddle currentRiddle = game.getCurrentRiddle();
                if (currentRiddle != null) {
                    boolean isCorrect = game.checkAnswer(message);
                    String userAnswer = message;
                    String correctAnswer = currentRiddle.getAnswer();

                    session.recordAnswer(isCorrect, currentRiddle.getQuestion(), userAnswer, correctAnswer);

                    if (isCorrect) {
                        String nextQuestion = game.startNewRound();
                        if (nextQuestion == null) {
                            return "Правильно!\n\n" +
                                    "Поздравляю! Ты отгадал(а) ВСЕ загадки!\n" +
                                    "Напиши /stats, чтобы посмотреть результат, или /quiz, чтобы начать заново.";
                        }
                        return "Правильно!\n\n" +
                                "Следующая загадка:\n " + nextQuestion;
                    } else {
                        return "Неправильно! Правильный ответ: " + correctAnswer + "\n\n" +
                                "Попробуй ещё раз:\n" + currentRiddle.getQuestion();
                    }
                }
            }
        }

        return "Неизвестная команда. Напиши /start или /help для списка команд.\n" +
                "Чтобы начать викторину, напиши /quiz";
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }
}