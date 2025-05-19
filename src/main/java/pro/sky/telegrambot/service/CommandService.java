package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Update;

public interface CommandService {

    void processCommand(Update update);
}
