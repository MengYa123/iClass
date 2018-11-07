package com.example.mengwork.iclass;

public class LostItem {
    private String name;
    private String lost_name;
    private String contract;
    private String addr;
    String[] addr_data = {"一教","综合楼","二基楼","一基楼","体育馆","图书馆","文科楼","建环楼","西园食堂","东园食堂","校外"};
    public LostItem(String name, String lost_name, String contract, int addr) {
        this.name = name;
        this.lost_name = lost_name;
        this.contract = contract;
        this.addr = addr_data[addr];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLost_name() {
        return lost_name;
    }

    public void setLost_name(String lost_name) {
        this.lost_name = lost_name;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr_data[addr];
    }



}
