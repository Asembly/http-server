package org.example;

import org.example.handler.FileHandler;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try{
            HttpServer server = new HttpServer("192.168.0.180", 80);
            server.addHandler("GET", "/files", new FileHandler());
            server.addHandler("POST", "/files", new FileHandler());
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());;
        }
    }
}