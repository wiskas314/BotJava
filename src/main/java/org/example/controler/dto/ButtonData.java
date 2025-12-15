package org.example.controler.dto;

/**
 * класс представляющий данные для создания кнопки в телеграм инлайн-клавиатуре
 */
public class ButtonData {
    private final String text;
    private final  String callbackData;

    public ButtonData(String text,String callbackData){
        this.text=text;
        this.callbackData=callbackData;
    }

    public String getText(){
        return text;
    }

    public String getCallbackData(){
        return callbackData;
    }
}
