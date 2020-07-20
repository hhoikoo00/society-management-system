package main;

import javax.swing.*;

import gui.MainFramePanel;

public class Main {

    public static void main(String[] args) {

        // Run in a dispatch thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startGUI();
            }
        });
    } // END static main()

    private static void startGUI() {

        JFrame frame = new JFrame("Society Management System");

        frame.setBounds(0, 0, 500, 260); // window size
        frame.setLocationRelativeTo(null); // centre window
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try { // create the panel
            frame.getContentPane().add(new MainFramePanel());
        }
        catch (Exception e) { // Exception in creating a new panel
            System.exit(1);
        }
    } // END static startGUI()

} // END class Main

