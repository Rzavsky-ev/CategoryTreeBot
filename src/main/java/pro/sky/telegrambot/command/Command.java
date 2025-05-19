package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;


public interface Command {

    SendMessage execute(Long chatId, String commandText);

    NamesCommand getNameCommand();
}
