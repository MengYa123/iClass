package com.example.mengwork.iclass;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class OneParamsPush extends Thread{
    private String command;
    private String params;
    OneParamsPush(String command,String params){
        this.command = command;
        this.params = params;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket("119.23.231.17",6666);
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String param = command + "_" + params;
            pw.write(param);
            pw.flush();
            pw.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
