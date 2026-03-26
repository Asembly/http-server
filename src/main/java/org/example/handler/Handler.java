package org.example.handler;

import org.example.util.Request;
import org.example.util.Response;

@FunctionalInterface
public interface Handler {

    void handle(Request request, Response response);

}
