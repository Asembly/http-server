package org.example.handler;

import org.example.util.Request;

import java.io.OutputStream;

@FunctionalInterface
public interface Handler {

    void handle(Request request, OutputStream outputStream);

}
