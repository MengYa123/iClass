package com.example.mengwork.iclass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Util {
    User getUsetInfo(String id){
        MyThread my = new MyThread(id);
        my.start();
        return my.returnInfo();
    }
    private class MyThread extends Thread{
        private String id;
        private String name;
        private String sex;
        private String phoneNum;
        MyThread(String id){
            this.id = id;
        }
        User returnInfo(){
            return new User(id,name,sex,phoneNum,"",0,0);
        }

        @Override
        public void run() {
            Socket socket;
            try {
                socket = new Socket("119.23.231.17",6666);
                String params = "getinfofromid_" + id;
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(params);
                pw.flush();
                pw.close();

                socket = new Socket("119.23.231.17",6666);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String info = br.readLine();
                br.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
