package org.example.controler.dto;

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
