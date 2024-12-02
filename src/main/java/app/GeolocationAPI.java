package app;

import java.net.InetAddress;
import java.net.UnknownHostException;

//https://api.hackertarget.com/geoip/?q=0.0.0.0
//https://ipwho.is/164.8.206.88?output=json&fields=success,city,latitude,longitude

public class GeolocationAPI {
    public static void pridobiLokacijoGledeNaIP() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("IP naslov: " + localHost.getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
