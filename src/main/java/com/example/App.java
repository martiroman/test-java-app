package com.example;

import io.vertx.core.Vertx;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class App {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.createHttpServer()
            .requestHandler(request -> {
                request.response().end("¡Hola desde Vert.x!");
                makeHttpRequest("http://10.107.205.57");
            })
            .listen(8080);
    }

    private static void makeHttpRequest(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("HTTP Response Content: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

