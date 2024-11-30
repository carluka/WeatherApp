package app;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUI extends JFrame{
    private JTextField vnosnoPolje;
    private JButton gumbIskanje;
    private JPanel inputPanel;
    private JPanel grafPanel;

    public GUI(){
        setTitle("Weather app");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
            } else {
                JOptionPane.showMessageDialog(this, "Za to lokacijo ni mogoče pridobiti vremenske napovedi.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Prosim vnesite lokacijo.");
        }
    }
}