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
import java.util.List;
import java.util.Objects;

public class GUI extends JFrame{
    private JTextField vnosnoPolje;
    private JButton gumbIskanje;
    private JPanel inputPanel;
    private JPanel grafPanel;
    private JPanel trenutnoVremePanel;
    private JPanel glavniPanel;

    public GUI() {
        setTitle("Weather app");
        setSize(1100,900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vnosnoPolje = new JTextField(30);
        gumbIskanje = new JButton("Prikaži vreme");

        inputPanel = new JPanel();
        inputPanel.add(new JLabel("Vnesite lokacijo:"));
        inputPanel.add(vnosnoPolje);
        inputPanel.add(gumbIskanje);

        grafPanel = new JPanel();

        trenutnoVremePanel = new JPanel();
        trenutnoVremePanel.setLayout(new FlowLayout());

        glavniPanel = new JPanel();
        glavniPanel.setLayout(new BoxLayout(glavniPanel, BoxLayout.Y_AXIS));
        glavniPanel.add(inputPanel);
        glavniPanel.add(trenutnoVremePanel);
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
        }
    }

    private void pridobiVremeVnos(){
        String lokacija = vnosnoPolje.getText().trim();

        if(!lokacija.isEmpty()){
            VremenskaNapoved napoved = MeteoAPI.pridobiVremenskoNapoved(lokacija);
            if(napoved != null){
                prikaziGrafNapovedi(napoved);
                prikaziTrenutnoVreme(napoved);
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
        grafContainer.setPreferredSize(new Dimension(1000, 600));
        grafPanel.removeAll();
        grafPanel.add(grafContainer, BorderLayout.CENTER);
        grafPanel.revalidate();
        grafPanel.repaint();
    }

    private void prikaziTrenutnoVreme(VremenskaNapoved napoved){
        JLabel slika = new JLabel();
        try{
            ImageIcon ikona = new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + napoved.getTrenutno_stanje() + ".png")));
            slika.setIcon(ikona);
        } catch (Exception e) {
            slika.setText("Ikone ni bilo mogoče najti");
        }
        trenutnoVremePanel.removeAll();
        trenutnoVremePanel.add(slika);

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

        trenutnoVremePanel.add(podrobnostiPanel);
        trenutnoVremePanel.revalidate();
        trenutnoVremePanel.repaint();
    }
}