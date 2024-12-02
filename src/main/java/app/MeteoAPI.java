package app;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeteoAPI {
    public static final String URL = "https://api.open-meteo.com/v1/forecast";
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/search";

    private static HttpResponse<String> pokliciAPI(String url){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] pridobiKoordinate(String mesto) {
        String urlEncoded = URLEncoder.encode(mesto, StandardCharsets.UTF_8);
        String url = BASE_URL + "?q=" + urlEncoded + "&format=json&limit=1";
        HttpResponse<String> response = pokliciAPI(url);

        JSONArray jsonArray = new JSONArray(response.body());

        if (!jsonArray.isEmpty()) {
            JSONObject firstResult = jsonArray.getJSONObject(0);
            String lat = firstResult.getString("lat");
            String lon = firstResult.getString("lon");
            return new String[]{lat, lon};
        } else {
            return null;
        }
    }

    public static VremenskaNapoved pridobiVremenskoNapoved(String mesto){
        String[] koordinate = pridobiKoordinate(mesto);
        return pridobiVremenskoNapoved(koordinate);
    }

    public static VremenskaNapoved pridobiVremenskoNapoved(String[] koordinate){
        if(koordinate != null){
            String url = URL + "?latitude=" + koordinate[0] + "&longitude=" + koordinate[1] + "&current=temperature_2m,weather_code&hourly=temperature_2m";

            HttpResponse<String> response = pokliciAPI(url);
            JSONObject jsonResponse = new JSONObject(response.body());

            JSONObject hourly = jsonResponse.getJSONObject("hourly");
            JSONObject current = jsonResponse.getJSONObject("current");

            JSONArray timeResponse = hourly.getJSONArray("time");
            JSONArray temperatureResponse = hourly.getJSONArray("temperature_2m");

            List<String> time = new ArrayList<>();
            List<Double> temperature = new ArrayList<>();

            SimpleDateFormat zacetniFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat koncniFormat = new SimpleDateFormat("EEE HH:mm", Locale.of("sl", "SI"));

            for(int i = 0; i < timeResponse.length(); i++){
                Date datum;
                try {
                    datum = zacetniFormat.parse(timeResponse.getString(i));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String novDatum = koncniFormat.format(datum);
                time.add(novDatum);
                temperature.add(temperatureResponse.getDouble(i));
            }
            return new VremenskaNapoved(time, temperature, current.optString("weather_code"), current.optString("temperature_2m"));
        }
        return null;
    }
}