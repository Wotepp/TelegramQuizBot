package ru.university;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.university.config.BotConfig;

public class QuizBotApplication {
    public static void main(String[] args) {
        try {
            TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication();
            QuizBot bot = new QuizBot();

            app.registerBot(BotConfig.BOT_TOKEN, bot);

            System.out.println("Бот викторины успешно запущен!");
            System.out.println("Имя бота: @" + BotConfig.BOT_USERNAME);
            System.out.println("Ожидание сообщений...");
            System.out.println("Для остановки нажмите Ctrl+C");

            Thread.currentThread().join();

        } catch (TelegramApiException e) {
            System.err.println("Ошибка Telegram API: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Бот остановлен");
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}