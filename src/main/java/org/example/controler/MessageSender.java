package org.example.controler;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface MessageSender {
    void sendMessage(String text, String chatId, InlineKeyboardMarkup keyboard);
}
