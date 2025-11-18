package org.example.controler;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Интерфейс обратного вызова (callback) для взаимодействия между игровой логикой
 * и пользовательским интерфейсом.
 */
public interface GameCallBack {

    /**
     * Отправляет игровое сообщение указанному получателю с возможностью
     * прикрепления интерактивной клавиатуры.
     *
     * <p>Этот метод вызывается игровой логикой для информирования пользователя
     * о текущем состоянии игры, запроса выбора или уведомления о результатах.</p>
     */
    void sendGameMessage(String chatId, String text, InlineKeyboardMarkup keyboard);
}
