package com.example.mengwork.iclass;

public class Msg {
    private String connent;
    private int number;
    public Msg(String connent,int number ) {
        this.connent=connent;
        this.number=number;
    }
    public String getConnent (){
        return connent;
    }

    public int getNumber() {
        return number;
    }
}
