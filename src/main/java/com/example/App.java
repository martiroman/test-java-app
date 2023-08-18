package com.example;

import org.bson.Document;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.List;
import java.util.ArrayList;
import com.mongodb.client.MongoCursor;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class App {
    private static final String username = "admin";
    private static final String password = "adminpassword";

    public static void main(String[] args) {

	Vertx vertx = Vertx.vertx(new VertxOptions()
  		.setTracingOptions(new OpenTelemetryOptions())
	);
        
	Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/").handler(App::handleHome);
        router.post("/submit").handler(App::handleSubmit);
	router.get("/lista").handler(App::handleLista);


        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080);
    }


    private static void handleHome(RoutingContext context) {
	context.response().putHeader("Content-Type", "text/html");
        context.response().end("<form action=\"/submit\" method=\"post\">\n" +
                "  <label for=\"nombre\">Nombre:</label><br>\n" +
                "  <input type=\"text\" id=\"nombre\" name=\"nombre\" required><br><br>\n" +
                "  <label for=\"apellido\">Apellido:</label><br>\n" +
                "  <input type=\"text\" id=\"apellido\" name=\"apellido\" required><br><br>\n" +
                "  <input type=\"submit\" value=\"Enviar\">\n" +
                "</form><br><br><a href='/lista'>Ver lista</a>");
    }

    private static void handleSubmit(RoutingContext context) {
    	String nombre = context.request().getParam("nombre");
    	String apellido = context.request().getParam("apellido");
    	guardarEnDB(nombre, apellido);
    	context.response().putHeader("Content-Type", "text/plain").end("Datos guardados: " + nombre + " " + apellido);
    }

    public static void handleLista(RoutingContext context) {
	List<String> personas = obtenerPersonasDesdeDB();
    	String listaHtml = "<h2>Lista de Personas</h2><ul>";

    	for (String persona : personas) {
           listaHtml += "<li>" + persona + "</li>";
    	}

    	listaHtml += "</ul>";

    	context.response().putHeader("Content-Type", "text/html").end(listaHtml);
    }


    private static void guardarEnDB(String nombre, String apellido) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://"+username+":"+password+"@10.40.0.11:27017")) {
            MongoDatabase database = mongoClient.getDatabase("midb");
            MongoCollection<Document> collection = database.getCollection("datos");
        
	    Document persona = new Document("nombre", nombre)
            	.append("apellido", apellido);
        
            collection.insertOne(persona);
	    makeHttpRequest("http://10.107.205.57");
        }
    }

    private static List<String> obtenerPersonasDesdeDB() {
    	List<String> personas = new ArrayList<>();

    	try (MongoClient mongoClient = MongoClients.create("mongodb://"+username+":"+password+"@10.40.0.11:27017")) {
            MongoDatabase database = mongoClient.getDatabase("midb");
            MongoCollection<Document> collection = database.getCollection("datos");

            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
               Document personaDoc = cursor.next();
               String nombre = personaDoc.getString("nombre");
               String apellido = personaDoc.getString("apellido");
               personas.add(nombre + " " + apellido);
            }
    	}

    	return personas;
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
