package app;

import java.util.ArrayList;
import java.util.List;

public class VremenskaNapoved {
    private List<String> casi;
    private List<Double> temperature;
    private String trenutno_stanje;
    private String trenutna_temperatura;

    private List<String> vremenske_kode;
    private List<Double> najvisje_temperature;
    private List<Double> najnizje_temperature;

    public String getTrenutna_temperatura() {
        return trenutna_temperatura;
    }

    public String getTrenutno_stanje() {
        return trenutno_stanje;
    }

    public void setTrenutno_stanje(String trenutno_stanje) {
        this.trenutno_stanje = kodaVOpis(trenutno_stanje);
    }

    public List<Double> getTemperature() {
        return temperature;
    }

    public List<String> getCasi() {
        return casi;
    }

    public List<String> getVremenske_kode() {
        return vremenske_kode;
    }

    public List<Double> getNajvisje_temperature() {
        return najvisje_temperature;
    }

    public List<Double> getNajnizje_temperature() {
        return najnizje_temperature;
    }

    public VremenskaNapoved(List<String> casi, List<Double> najnizje_temperature, List<Double> najvisje_temperature, List<String> vremenske_kode, String trenutna_temperatura, String trenutno_stanje, List<Double> temperature) {
        this.casi = casi;
        this.najnizje_temperature = najnizje_temperature;
        this.najvisje_temperature = najvisje_temperature;
        this.vremenske_kode = transformirajKode(vremenske_kode);
        this.trenutna_temperatura = trenutna_temperatura;
        setTrenutno_stanje(trenutno_stanje);
        this.temperature = temperature;
    }

    private String kodaVOpis(String koda) {
        return switch (koda) {
            case "1", "2", "3" -> "Oblačno";
            case "45", "48" -> "Megla";
            case "51", "53", "55", "56", "57" -> "Pršenje";
            case "61", "63", "65", "66", "67" -> "Dež";
            case "71", "73", "75", "77" -> "Sneženje";
            case "80", "81", "82", "85", "86" -> "Dežne_plohe";
            case "95", "96", "99" -> "Nevihta";
            default -> "Jasno";
        };
    }

    private List<String> transformirajKode(List<String> vremenske_kode) {
        List<String> kode = new ArrayList<>();
        for (String koda : vremenske_kode) {
            kode.add(kodaVOpis(koda));
        }
        return kode;
    }
}
