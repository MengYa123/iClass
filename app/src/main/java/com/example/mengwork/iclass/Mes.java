package com.example.mengwork.iclass;

import android.widget.Adapter;

class Mes {
    private String from_id;
    private String mes;

    Mes(String from_id,String mes){
        this.from_id = from_id;
        this.mes = mes;
    }
    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
}