package app;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeolocationAPI {
    public static final String BASE_URL =  "https://ipwho.is/";

    public static JSONObject pridobiLokacijoGledeNaIP() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String url = BASE_URL + localHost.getHostAddress() + "?output=json&fields=success,city,latitude,longitude";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
