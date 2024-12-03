package app;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class GUI extends JFrame{
    private JTextField vnosnoPolje;
    private JButton gumbIskanje;
    private JPanel inputPanel;
    private JPanel grafPanel;
    private JPanel trenutnoVremePanel;
    private JPanel glavniPanel;
    private JPanel tedenskoVremePanel;

    public GUI() {
        setTitle("Weather app");
        setSize(1300,900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vnosnoPolje = new JTextField(30);
        gumbIskanje = new JButton("Prikaži vreme");

        inputPanel = new JPanel();
        inputPanel.add(new JLabel("Vnesite lokacijo:"));
        inputPanel.add(vnosnoPolje);
        inputPanel.add(gumbIskanje);

        trenutnoVremePanel = new JPanel();
        trenutnoVremePanel.setLayout(new BoxLayout(trenutnoVremePanel, BoxLayout.Y_AXIS));

        tedenskoVremePanel = new JPanel();
        tedenskoVremePanel.setLayout(new FlowLayout());

        grafPanel = new JPanel();

        glavniPanel = new JPanel();
        glavniPanel.setLayout(new BoxLayout(glavniPanel, BoxLayout.Y_AXIS));
        glavniPanel.add(inputPanel);
        glavniPanel.add(trenutnoVremePanel);
        glavniPanel.add(tedenskoVremePanel);
        glavniPanel.add(grafPanel);

        setLayout(new BorderLayout());
        add(glavniPanel, BorderLayout.CENTER);

        gumbIskanje.addActionListener(e -> pridobiVremeVnos());

        vnosnoPolje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gumbIskanje.doClick();
                }
            }
        });

        pridobiVremeGledeNaIP();
    }

    private void pridobiVremeGledeNaIP(){
        JSONObject result = GeolocationAPI.pridobiLokacijoGledeNaIP();
        if(Objects.equals(result.get("success").toString(), "true")){
            vnosnoPolje.setText(result.get("city").toString());
            String[] koordinate = new String[]{result.optString("latitude"), result.optString("longitude")};
            VremenskaNapoved napoved = MeteoAPI.pridobiVremenskoNapoved(koordinate);
            prikaziGrafNapovedi(napoved);
            prikaziTrenutnoVreme(napoved);
            prikaziVremeZaTeden(napoved);
        }
    }

    private void pridobiVremeVnos(){
        String lokacija = vnosnoPolje.getText().trim();

        if(!lokacija.isEmpty()){
            VremenskaNapoved napoved = MeteoAPI.pridobiVremenskoNapoved(lokacija);
            if(napoved != null){
                prikaziGrafNapovedi(napoved);
                prikaziTrenutnoVreme(napoved);
                prikaziVremeZaTeden(napoved);
            } else {
                JOptionPane.showMessageDialog(this, "Za to lokacijo ni mogoče pridobiti vremenske napovedi.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Prosim vnesite lokacijo.");
        }
    }

    private void prikaziGrafNapovedi(VremenskaNapoved napoved){
        List<String> time = napoved.getCasi();
        List<Double> temperature = napoved.getTemperature();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i = 0; i < time.size(); i++){
            dataset.addValue(temperature.get(i), "Temperature", time.get(i));
        }
        JFreeChart graf = ChartFactory.createLineChart(
                "Vremenska napoved za 1 teden - " + vnosnoPolje.getText(),
                "Čas",
                "Temperature",
                dataset);

        CategoryAxis xAxis = graf.getCategoryPlot().getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setLowerMargin(0.02);
        xAxis.setUpperMargin(0.02);

        for(int i = 0; i < time.size(); i++){
            if(i % 6 != 0){
                xAxis.setTickLabelPaint(time.get(i), new Color(0,0,0,0));
            }
        }

        ChartPanel grafContainer = new ChartPanel(graf);
        grafContainer.setPreferredSize(new Dimension(1200, 500));
        grafPanel.removeAll();
        grafPanel.add(grafContainer, BorderLayout.CENTER);
        grafPanel.revalidate();
        grafPanel.repaint();
    }


    private ImageIcon naloziIkono(String imeIkone, int sirina, int visina) {
        try {
            ImageIcon ikona = new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + imeIkone + ".png")));
            Image skaliranaSlika = ikona.getImage().getScaledInstance(sirina, visina, Image.SCALE_SMOOTH);
            return new ImageIcon(skaliranaSlika);
        } catch (Exception e) {
            return null;
        }
    }

    private JLabel ustvariOznako(String besedilo, int velikostPisave, int slog) {
        JLabel oznaka = new JLabel(besedilo);
        oznaka.setFont(new Font("Arial", slog, velikostPisave));
        oznaka.setAlignmentX(Component.CENTER_ALIGNMENT);
        return oznaka;
    }

    private JPanel ustvariPodrobnostiPanel(List<Component> komponente) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (Component komponenta : komponente) {
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(komponenta);
        }
        return panel;
    }

    private JPanel ustvariDnevniPanel(String kodaIkone, LocalDate datum, String stanje, double maxTemp, double minTemp) {
        JPanel dnevniPanel = new JPanel();
        dnevniPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        dnevniPanel.setLayout(new BorderLayout());

        JLabel oznakaIkone = new JLabel();
        ImageIcon ikona = naloziIkono(kodaIkone, 50, 50);
        oznakaIkone.setIcon(ikona != null ? ikona : new JLabel("Ikone ni bilo mogoče najti").getIcon());

        JPanel podrobnostiPanel = ustvariPodrobnostiPanel(List.of(
                ustvariOznako(stanje.replace("_", " "), 16, Font.BOLD),
                ustvariOznako(maxTemp + " °C", 14, Font.BOLD),
                ustvariOznako(minTemp + " °C", 12, Font.PLAIN)
        ));

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new FlowLayout());
        subPanel.add(oznakaIkone);
        subPanel.add(podrobnostiPanel);

        JLabel datumLabel = ustvariOznako(datum.format(DateTimeFormatter.ofPattern("dd. MMM")), 18, Font.BOLD);
        JPanel datumPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        datumPanel.add(datumLabel);

        dnevniPanel.add(datumPanel, BorderLayout.NORTH);
        dnevniPanel.add(subPanel, BorderLayout.CENTER);

        return dnevniPanel;
    }

    private void prikaziVremeZaTeden(VremenskaNapoved napoved) {
        tedenskoVremePanel.removeAll();
        LocalDate danes = LocalDate.now();

        for (int i = 0; i < napoved.getVremenske_kode().size(); i++) {
            String kodaIkone = napoved.getVremenske_kode().get(i);
            double minTemp = napoved.getNajnizje_temperature().get(i);
            double maxTemp = napoved.getNajvisje_temperature().get(i);
            LocalDate datum = danes.plusDays(i);

            tedenskoVremePanel.add(ustvariDnevniPanel(kodaIkone, datum, kodaIkone, maxTemp, minTemp));
        }

        tedenskoVremePanel.revalidate();
        tedenskoVremePanel.repaint();
    }

    private void prikaziTrenutnoVreme(VremenskaNapoved napoved) {
        trenutnoVremePanel.removeAll();

        JLabel naslov = ustvariOznako("Trenutno Vreme", 22, Font.BOLD);

        JLabel oznakaIkone = new JLabel();
        ImageIcon ikona = naloziIkono(napoved.getTrenutno_stanje(), 100, 100);
        oznakaIkone.setIcon(ikona != null ? ikona : new JLabel("Ikone ni bilo mogoče najti").getIcon());

        JPanel podrobnostiPanel = ustvariPodrobnostiPanel(List.of(
                ustvariOznako(napoved.getTrenutno_stanje().replace("_", " "), 20, Font.BOLD),
                ustvariOznako(napoved.getTrenutna_temperatura() + " °C", 16, Font.BOLD)
        ));

        JPanel podPanel = new JPanel(new FlowLayout());
        podPanel.add(oznakaIkone);
        podPanel.add(podrobnostiPanel);

        trenutnoVremePanel.add(naslov);
        trenutnoVremePanel.add(podPanel);
        trenutnoVremePanel.revalidate();
        trenutnoVremePanel.repaint();
    }

}

/*private void prikaziTrenutnoVreme(VremenskaNapoved napoved){
        trenutnoVremePanel.removeAll();

        JLabel naslov = new JLabel("Trenutno Vreme");
        naslov.setFont(new Font("Arial", Font.BOLD, 22));
        naslov.setAlignmentX(Component.CENTER_ALIGNMENT);

        trenutnoVremePanel.add(naslov);

        JLabel slika = new JLabel();
        try{
            ImageIcon ikona = new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + napoved.getTrenutno_stanje() + ".png")));
            slika.setIcon(ikona);
        } catch (Exception e) {
            slika.setText("Ikone ni bilo mogoče najti");
        }
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new FlowLayout());
        subPanel.add(slika);

        JPanel podrobnostiPanel = new JPanel();
        podrobnostiPanel.setLayout(new BoxLayout(podrobnostiPanel, BoxLayout.Y_AXIS));

        JLabel trenutno_stanje = new JLabel(napoved.getTrenutno_stanje().replace("_"," "));
        trenutno_stanje.setFont(new Font("Arial", Font.BOLD, 20));
        trenutno_stanje.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel trenutna_temperatura = new JLabel(napoved.getTrenutna_temperatura() + " °C");
        trenutna_temperatura.setFont(new Font("Arial", Font.BOLD, 16));
        trenutna_temperatura.setAlignmentX(Component.CENTER_ALIGNMENT);

        podrobnostiPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        podrobnostiPanel.add(trenutno_stanje);
        podrobnostiPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        podrobnostiPanel.add(trenutna_temperatura);

        subPanel.add(podrobnostiPanel);
        trenutnoVremePanel.add(subPanel);
        trenutnoVremePanel.revalidate();
        trenutnoVremePanel.repaint();
    }

    private void prikaziVremeZaTeden(VremenskaNapoved napoved){
        tedenskoVremePanel.removeAll();
        List<String> vremenske_kode = napoved.getVremenske_kode();
        List<Double> temperature_min = napoved.getNajnizje_temperature();
        List<Double> temperature_max = napoved.getNajvisje_temperature();

        LocalDate datum = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMM");

        for(int i = 0; i < vremenske_kode.size(); i++){
            JPanel posamezenDanPanel = new JPanel();
            posamezenDanPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

            JLabel slika = new JLabel();
            try {
                ImageIcon ikona = new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + vremenske_kode.get(i) + ".png")));
                Image pomanjsanaIkona = ikona.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                slika.setIcon(new ImageIcon(pomanjsanaIkona));
            } catch (Exception e) {
                slika.setText("Ikone ni bilo mogoče najti");
            }
            posamezenDanPanel.add(slika);

            JPanel podrobnostiPanel = new JPanel();
            podrobnostiPanel.setLayout(new BoxLayout(podrobnostiPanel, BoxLayout.Y_AXIS));

            LocalDate dateForDay = datum.plusDays(i);
            String novDatum = dateForDay.format(formatter);
            JLabel datumLabel = new JLabel(novDatum);
            datumLabel.setFont(new Font("Arial", Font.BOLD, 14));
            datumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel trenutno_stanje = new JLabel(vremenske_kode.get(i).replace("_", " "));
            trenutno_stanje.setFont(new Font("Arial", Font.BOLD, 16));
            trenutno_stanje.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel max_temp = new JLabel(temperature_max.get(i) + " °C");
            max_temp.setFont(new Font("Arial", Font.BOLD, 14));
            max_temp.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel min_temp = new JLabel(temperature_min.get(i) + " °C");
            min_temp.setFont(new Font("Arial", Font.BOLD, 12));
            min_temp.setAlignmentX(Component.CENTER_ALIGNMENT);

            podrobnostiPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            podrobnostiPanel.add(datumLabel);
            podrobnostiPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            podrobnostiPanel.add(trenutno_stanje);
            podrobnostiPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            podrobnostiPanel.add(max_temp);
            podrobnostiPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            podrobnostiPanel.add(min_temp);

            posamezenDanPanel.add(podrobnostiPanel);
            tedenskoVremePanel.add(posamezenDanPanel);
        }

        tedenskoVremePanel.revalidate();
        tedenskoVremePanel.repaint();
    }*/