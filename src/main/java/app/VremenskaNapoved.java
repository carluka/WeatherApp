package app;

import java.util.List;

public class VremenskaNapoved {
    private List<String> casi;
    private List<Double> temperature;
    private String trenutno_stanje;
    private String trenutna_temperatura;


    public String getTrenutna_temperatura() {
        return trenutna_temperatura;
    }

    public String getTrenutno_stanje() {
        return trenutno_stanje;
    }

    public void setTrenutno_stanje(String trenutno_stanje) {
        this.trenutno_stanje = switch (trenutno_stanje) {
            case "1", "2", "3" -> "Oblačno";
            case "45", "48" -> "Megla";
            case "51", "53", "55", "56", "57" -> "Rošenje";
            case "61", "63", "65", "66", "67" -> "Dež";
            case "71", "73", "75", "77" -> "Sneženje";
            case "80", "81", "82", "85", "86" -> "Dežne_plohe";
            case "95", "96", "99" -> "Nevihta";
            default -> "Jasno";
        };
    }


    public List<Double> getTemperature() {
        return temperature;
    }

    public List<String> getCasi() {
        return casi;
    }

    public VremenskaNapoved(List<String> casi, List<Double> temperature, String trenutno_stanje, String trenutna_temperatura) {
        this.casi = casi;
        this.temperature = temperature;
        setTrenutno_stanje(trenutno_stanje);
        this.trenutna_temperatura = trenutna_temperatura;
    }
}
