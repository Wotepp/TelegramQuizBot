package ru.university.loader;

import ru.university.model.Riddle;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RiddleLoader {

    public static List<Riddle> loadFromFile(String filePath) {
        List<Riddle> riddles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), "UTF-8"))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) {
                    System.err.println("Ошибка в строке " + lineNum + ": " + line);
                    continue;
                }

                String question = parts[0].trim();
                String answer = parts[1].trim();
                riddles.add(new Riddle(question, answer));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл с загадками не найден: " + filePath);
            System.err.println("Используются встроенные загадки по умолчанию");
            return getDefaultRiddles();
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            return getDefaultRiddles();
        }

        System.out.println("Загружено загадок: " + riddles.size());
        return riddles;
    }

    private static List<Riddle> getDefaultRiddles() {
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("Что можно сломать, даже не касаясь?", "Обещание"));
        riddles.add(new Riddle("Что становится больше, если его поставить вверх ногами?", "Число 6 (становится 9)"));
        riddles.add(new Riddle("Что принадлежит вам, но другие пользуются этим чаще?", "Имя"));
        riddles.add(new Riddle("Что можно увидеть с закрытыми глазами?", "Сон"));
        riddles.add(new Riddle("Что не имеет веса, но может заставить человека плакать?", "Лук"));
        riddles.add(new Riddle("Что идёт вверх, но никогда не спускается вниз?", "Возраст"));
        riddles.add(new Riddle("Что можно держать, не касаясь его?", "Слово"));
        riddles.add(new Riddle("Что не задаёт вопросов, но требует ответов?", "Телефонный звонок"));
        return riddles;
    }
}