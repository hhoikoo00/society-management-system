package gui;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tools.DBManager;

@SuppressWarnings("serial")
public class DialogRetrieveRecord extends JDialog {

    /*-------- SubPanels  --------*/
    private JPanel      buttonPanel     = new JPanel();

    /*-------- GUI Components  --------*/
    private JTable      tblResults;
    private JButton     btnClose        = new JButton("Close");
    private JScrollPane jp              = new JScrollPane();

    /*-------- Other Components  --------*/
    private DBManager   db;

    public DialogRetrieveRecord(Window win, String title,
            Dialog.ModalityType modalityType, DBManager db) {

        super(win, title, modalityType); // call superclass constructor
        this.db = db;

        getContentPane().setLayout(new BorderLayout());

        /* Set up subpanels and add to contentPane */
        setUpTable(); // call internal method
        getContentPane().add(jp);

        setUpButtonPanel(); // call internal method
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        /* Set up JDialog */
        setBounds(0, 0, 400, 600); // sizze of dialog
        setResizable(false);
        setLocationRelativeTo(null); // centre window
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);

    } // END CreateEventDialog()

    /* Initialize JTable tblResults */
    private void setUpTable() {

        /* Get 2d ArrayList of students */
        ArrayList<ArrayList<String>> arrListStudents;
        try {
            arrListStudents = db.returnStudents();
        }
        catch (Exception e) {
            e.printStackTrace();
            this.dispose();
            return;
        }

        /* Create String[][] 2d array for JTable */
        String[] column = {"Name", "Year Group", "House", "Events Attended"};
        String data[][] = new String[arrListStudents.size()][];
        for (int i=0; i < arrListStudents.size(); i++) {
            ArrayList<String> row = arrListStudents.get(i);
            data[i] = row.toArray(new String[row.size()]);
        }

        /* JTable tblResults */
        tblResults = new JTable(data, column);
        tblResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        jp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jp.setViewportView(tblResults);

    } // END setUpContentPanel()

    /* Initialize JButton */
    private void setUpButtonPanel() {

        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnClose(); // call internal method
            }
        });

        buttonPanel.add(btnClose);

    } // END setUpButtonPanel()

    private void actionBtnClose() {

        this.dispose(); // close window

    } // END actionBtnClose()

}
