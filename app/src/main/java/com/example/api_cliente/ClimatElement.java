package com.example.api_cliente;

class ClimatElement {
    String temp;
    String time;
    public ClimatElement(String temp,String time){
        this.temp=temp;
        this.time=time;
    }

    public String getTemp() {
        return temp;
    }

    public String getTime() {
        return time;
    }
}