package org.example.controler;

import org.example.controler.dto.KeyboardMarkup;

/**
 * Интерфейс для отправки сообщений
 */
public interface MessageSender {
    /**
     *Отправляет сообщение в указанный чат
     */
    void sendMessage(String text, String chatId, KeyboardMarkup keyboard);
}
