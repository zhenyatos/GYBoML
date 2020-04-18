package ru.spbstu.gyboml.server;

import java.util.Date;

import com.esotericsoftware.minlog.Log.Logger;

public class GybomlLogger extends Logger {
    @Override
    public void log(int level, String category, String message, Throwable ex) {
        System.out.print(new Date().toString());
        super.log(level, category, message, ex);
    }

}