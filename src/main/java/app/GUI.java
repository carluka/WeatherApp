package app;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUI extends JFrame{
    private JTextField vnosnoPolje;
    private JButton gumbIskanje;
    private JPanel inputPanel;
    private JPanel grafPanel;

    public GUI() {
        setTitle("Weather app");
        setSize(1100,900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GeolocationAPI.pridobiLokacijoGledeNaIP();
        vnosnoPolje = new JTextField(30);
        gumbIskanje = new JButton("Prikaži vreme");

        inputPanel = new JPanel();
        inputPanel.add(new JLabel("Vnesite lokacijo:"));
        inputPanel.add(vnosnoPolje);
        inputPanel.add(gumbIskanje);

        grafPanel = new JPanel();

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(grafPanel, BorderLayout.CENTER);

        gumbIskanje.addActionListener(e -> pridobiInPrikaziVreme());
    }

    private void pridobiInPrikaziVreme(){
        String lokacija = vnosnoPolje.getText().trim();

        if(!lokacija.isEmpty()){
            List<List<?>> napoved = MeteoAPI.pridobiVremenskoNapoved(lokacija);
            if(napoved != null){
                List<String> time = (List<String>) napoved.get(0);
                List<Double> temperature = (List<Double>) napoved.get(1);
                prikaziGrafNapovedi(time, temperature);
            } else {
                JOptionPane.showMessageDialog(this, "Za to lokacijo ni mogoče pridobiti vremenske napovedi.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Prosim vnesite lokacijo.");
        }
    }

    private void prikaziGrafNapovedi(List<String> time, List<Double> temperature){
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
        grafPanel.validate();
    }
}