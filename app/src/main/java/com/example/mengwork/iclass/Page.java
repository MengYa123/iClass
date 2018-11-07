package com.example.mengwork.iclass;

class Page{
    private int pagenum;
    private int num;
    Page(int pagenum,int num){
        this.pagenum = pagenum;
        this.num = num;
    }

    public int getPagenum() {
        return pagenum;
    }

    public void setPagenum(int pagenum) {
        this.pagenum = pagenum;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}