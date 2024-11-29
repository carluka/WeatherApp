package app;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.*;

public class MeteoAPI {
    public static final String URL = "https://api.open-meteo.com/v1/forecast";
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/search";

    private static String[] pridobiKoordinate(String mesto) {
        try {
            String url = BASE_URL + "?q=" + mesto + "&format=json&limit=1";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray jsonArray = new JSONArray(response.body());
            
            if (!jsonArray.isEmpty()) {
                JSONObject firstResult = jsonArray.getJSONObject(0);
                String lat = firstResult.getString("lat");
                String lon = firstResult.getString("lon");
                return new String[]{lat, lon};
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void pridobiVremenskoNapoved(String mesto){
        String[] koordinate = pridobiKoordinate(mesto);
        if(koordinate != null){
            //PRIDOBI VREMENSKE PODATKE
        }


    }
}
