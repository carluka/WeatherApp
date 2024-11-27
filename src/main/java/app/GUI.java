package app;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame{
    private JTextField vnosnoPolje;
    private JButton gumbIskanje;

    public GUI(){
        setTitle("Weather app");
        setSize(600,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vnosnoPolje = new JTextField(30);
        gumbIskanje = new JButton("Prikaži vreme");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Vnesite lokacijo:"));
        inputPanel.add(vnosnoPolje);
        inputPanel.add(gumbIskanje);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
    }
}
