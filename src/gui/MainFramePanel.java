package gui;

import java.util.*;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import javax.swing.*;

import objects.Event;
import tools.DBManager;

@SuppressWarnings("serial")
public class MainFramePanel extends JPanel {
    /*-------- GUI Components --------*/
    private JComboBox<String> cbCurrentEvent;
    private JButton     btnRegisterMode     = new JButton("Register Mode");
    private JButton     btnModifyDetails    = new JButton("Modify Details");
    private JButton     btnDeleteEvent      = new JButton("Delete Event");
    private JButton     btnCreateEvent      = new JButton("Create Event");
    private JButton     btnRetrieveRecord   = new JButton("Retrieve Record");

    /*-------- OTHER Components --------*/
    private DBManager           db;
    private ArrayList<Event>    listEvent;
    private String[]            arrayEvent; // For use with cbCurrentEvent
    private int                 indexCurrentEvent   = 0;

    public MainFramePanel() throws Exception { // Exception returned to Main class

        /* Database init */
        try {
            db = new DBManager();
        }
        catch (Exception e) { // Exception in creating database
            throw e;
        }

        /* GUI init */
        initGUI(); // call internal method

    } // END MainFramePanel()

    private void initGUI() {

        /* MainFramePanel set layout to absolute */
        this.setLayout(null);

        /* JComboBox cbCurrentEvent */
        cbCurrentEvent = new JComboBox<String>();
        resetCbCurrentEvent(); // call internal method
        cbCurrentEvent.setBounds(25, 25, 250, 27);

        cbCurrentEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionCbCurrentEvent(); // call internal method
            } // END actionPerformed()
        }); // END addActionListener()

        /* JButon btnRegisterMode */
        btnRegisterMode.setBounds(25, 100, 117, 48);

        btnRegisterMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnRegisterMode(); // call internal method
            } // END actionPerformed()
        }); // END addActionListener()

        /* JButon btnModifyDetails */
        btnModifyDetails.setBounds(158, 100, 117, 48);

        btnModifyDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnModifyDetails(); // call internal method
            } // END actionPerformed()
        }); // END addActionListener()

        /* JButon btnDeleteEvent */
        btnDeleteEvent.setBounds(25, 190, 250, 29);

        btnDeleteEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnDeleteEvent(); // call internal method
            } // END actionPerformed()
        }); // END addActionListener()

        /* JButon btnCreateEvent */
        btnCreateEvent.setBounds(325, 24, 150, 29);

        btnCreateEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnCreateEvent(); // call internal method
            } // END actionPerformed()
        }); // END addActionListener()

        /* JButon btnRetrieveRecord */
        btnRetrieveRecord.setBounds(325, 190, 150, 29);

        btnRetrieveRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnRetrieveRecord(); // call internal method
            } // END actionPerformed()
        }); // END addActionListener()

        /* Add components to MainFramePanel */
        add(cbCurrentEvent);
        add(btnRegisterMode);
        add(btnModifyDetails);
        add(btnDeleteEvent);
        add(btnCreateEvent);
        add(btnRetrieveRecord);

    } // END initGUI()

    /* Reset JComboBox */
    private void resetCbCurrentEvent() {

        updateListEvent(); // call internal method
        cbCurrentEvent.removeAllItems();
        for (String temp : arrayEvent) {
            cbCurrentEvent.addItem(temp);
        }

    } // END resetCbCurrentEvent()

    /* get ArrayList of event from database and convert into array */
    private void updateListEvent() {

        listEvent = db.returnEventList();
        arrayEvent = new String[listEvent.size()];

        for (int i=0; i < listEvent.size(); i++) {
            Event curEvent = listEvent.get(i);
            String arrayEventName = curEvent.getType() +" - "+ curEvent.getName();
            arrayEvent[i] = arrayEventName;
        }

    } // END updateListEvents()

    private void actionCbCurrentEvent() {

        indexCurrentEvent = cbCurrentEvent.getSelectedIndex();

    } // END actionCbCurrentEvent()

    private void actionBtnRegisterMode() {

        /* Modal Dialog takes over */
        Window win = SwingUtilities.getWindowAncestor(this);
        int eventId = listEvent.get(indexCurrentEvent).getIdEvent();
        new DialogRegisterMode(win, "Register Mode",
                ModalityType.APPLICATION_MODAL, db, eventId);

    } // END actionBtnRegisterMode()

    private void actionBtnModifyDetails() {

        if (listEvent.size() == 0) { // no Events in db
            JOptionPane.showMessageDialog(null,
                    "There are no events to modify: add an event");
            return;
        }

        /* Modal dialog takes over */
        Window win = SwingUtilities.getWindowAncestor(this);
        int eventId = listEvent.get(indexCurrentEvent).getIdEvent();
        new DialogModifyEvent(win, "Modify Event",
                ModalityType.APPLICATION_MODAL, db, eventId);

        /* Executed after dialog closed */
        resetCbCurrentEvent(); // call internal method

    } // END actionBtnModifyDetails()

    private void actionBtnDeleteEvent() {

        Event currentEvent = listEvent.get(indexCurrentEvent);
        String message = "Do you really want to delete " // warning message
                + currentEvent.getType() + " - "
                + currentEvent.getName() + "?\n"
                + "WARNING: THIS ACTION IS ABSOLUTELY IRREVERSIBLE";

        /* Confirmation dialogue for deletion */
        int result = JOptionPane.showConfirmDialog(null, message,
                "Warning", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            db.deleteEvent(currentEvent.getIdEvent());
            resetCbCurrentEvent();
        }

    } // END actionBtnDeleteEvent()

    private void actionBtnCreateEvent() {

        /* Modal dialog takes over */
        Window win = SwingUtilities.getWindowAncestor(this);
        new DialogCreateEvent(win, "Create Event",
                ModalityType.APPLICATION_MODAL, db);

        /* Executed after dialog closed */
        resetCbCurrentEvent();

    } // END actionBtnCreateEvent()

    private void actionBtnRetrieveRecord() {

        /* Modal dialog takes over */
        Window win = SwingUtilities.getWindowAncestor(this);
        new DialogRetrieveRecord(win, "Record of Students",
                ModalityType.APPLICATION_MODAL, db);

    } // END actionBtnRetrieveRecord()

} // END class MainFramePanel
