package app;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

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



    public static List<List<?>> pridobiVremenskoNapoved(String mesto){
        String[] koordinate = pridobiKoordinate(mesto);
        if(koordinate != null){
            try {
                String url = URL + "?latitude=" + koordinate[0] + "&longitude=" + koordinate[1] + "&current=temperature_2m,weather_code&hourly=temperature_2m";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonResponse = new JSONObject(response.body());

                JSONObject hourly = jsonResponse.getJSONObject("hourly");
                JSONObject current = jsonResponse.getJSONObject("current");

                JSONArray timeResponse = hourly.getJSONArray("time");
                JSONArray temperatureResponse = hourly.getJSONArray("temperature_2m");

                List<String> time = new ArrayList<>();
                List<Double> temperature = new ArrayList<>();
                for(int i = 0; i < timeResponse.length(); i++){
                    time.add(timeResponse.getString(i));
                    temperature.add(temperatureResponse.getDouble(i));
                }

                List<List<?>> skupaj = new ArrayList<>();
                skupaj.add(time);
                skupaj.add(temperature);
                return skupaj;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}