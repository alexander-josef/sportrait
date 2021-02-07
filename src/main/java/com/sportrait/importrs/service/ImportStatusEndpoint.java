package com.sportrait.importrs.service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value="/api/import/status/{albumId}")
public class ImportStatusEndpoint {
    // see https://www.baeldung.com/java-websockets for more sophisticated details
    // and https://www.freecodecamp.org/news/how-to-secure-your-websocket-connections-d0be0996c556/

    private Session session;


    @OnOpen
    public void onOpen(Session session,
                       @PathParam("albumId") String albumId) {
        System.out.println("on open");
        System.out.println("albumId = " + albumId);
        // todo : register endpoint with given albumId
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("ImportStatusEndpoint.onMessage");
        System.out.println("message = " + message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("ImportStatusEndpoint.onClose");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        System.out.println("ImportStatusEndpoint.onError");
    }

}