package library.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class BookService {
    private static final String BASE_URL = "http://localhost:5000/api/books";

    @SuppressWarnings("CallToPrintStackTrace")
    public static List<Book> fetchBooks() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            // Check for a successful response
            if (response.statusCode() == 200) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                return gson.fromJson(response.body(), new TypeToken<List<Book>>(){}.getType());
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    


    @SuppressWarnings("CallToPrintStackTrace")
    public static String addBook(String title, String author, String isbn, String category) {
        try {
            String bookData = "{ \"title\": \"" + title + "\", \"author\": \"" + author + "\", \"isbn\": \"" + isbn + "\", \"category\": \"" + category + "\" }";
    
            System.out.println("Sending request to: " + BASE_URL);
            System.out.println("Payload: " + bookData);
    
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bookData))
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            // Print response status and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
    
            if (response.statusCode() == 201) {
                return response.body();
            } else {
                System.out.println("Book addition failed. Status Code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    

    @SuppressWarnings("CallToPrintStackTrace")
    public static String editBook(String bookId, String title, String author, String isbn, String category) {
        try {
            // Manually constructing the JSON string
            String bookData = "{"
                    + "\"title\": \"" + title + "\","
                    + "\"author\": \"" + author + "\","
                    + "\"isbn\": \"" + isbn + "\","
                    + "\"category\": \"" + category + "\""
                    + "}";
    
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + bookId)) // Ensure you're using the correct ID
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(bookData))
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("Error updating book: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    @SuppressWarnings("CallToPrintStackTrace")
    public static String deleteBook(String id) {
        try {
            // Ensure the id is properly formatted
            String bookUrl = BASE_URL + "/" + id;
    
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(bookUrl))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
