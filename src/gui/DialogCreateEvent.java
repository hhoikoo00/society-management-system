package gui;

import java.io.*;
import java.time.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import com.github.lgooddatepicker.components.*;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement;
import com.github.lgooddatepicker.optionalusertools.*;

import objects.Event;
import objects.Student;
import tools.DBManager;

@SuppressWarnings("serial")
public class DialogCreateEvent extends JDialog {
    /*-------- SubPanels  --------*/
    private JPanel      contentPanel    = new JPanel();
    private JPanel      buttonPanel     = new JPanel();

    /*-------- GUI Components  --------*/
    private JLabel      label1          = new JLabel("Event Name");
    private JLabel      label2          = new JLabel("Event Type");
    private JLabel      label3          = new JLabel("Date");
    private JLabel      label4          = new JLabel("Start Time");
    private JLabel      label5          = new JLabel("Notify HMs?");
    private JLabel      label6          = new JLabel("Duration");
    private JLabel      label7          = new JLabel("minutes");
    private JTextField  txfEventName    = new JTextField();
    private JTextField  txfEventType    = new JTextField();
    private DatePicker  dpDate;
    private TimePicker  tpStartTime;
    private JCheckBox   cbNotifyHms     = new JCheckBox();
    private JTextField  txfDuration     = new JTextField();
    private JButton     btnImport       = new JButton("Import Spreadsheet");
    private JButton     btnOk           = new JButton("OK");
    private JButton     btnCancel       = new JButton("Cancel");

    /*-------- Other Components  --------*/
    private File        selectedFile;
    private DBManager   db;

    public DialogCreateEvent(Window win, String title,
            Dialog.ModalityType modalityType, DBManager db) {

        super(win, title, modalityType); // Call superclass constructor
        this.db = db;

        getContentPane().setLayout(new BorderLayout());

        /* Set up subpanels and add to contentPane */
        setUpContentPanel();
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        setUpButtonPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        /* Set up JDialog */
        setBounds(0, 0, 300, 350); // size of dialog
        setResizable(false);
        setLocationRelativeTo(null); // centre dialog
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);

    } // END CreateEventDialog()

    private void setUpContentPanel() {

        contentPanel.setLayout(null);

        /* JLabels */
        label1.setBounds(20,15,80,26);
        contentPanel.add(label1);
        label2.setBounds(20,55,80,26);
        contentPanel.add(label2);
        label3.setBounds(20,95,80,26);
        contentPanel.add(label3);
        label4.setBounds(20,135,80,26);
        contentPanel.add(label4);
        label5.setBounds(20,175,80,26);
        contentPanel.add(label5);
        label6.setBounds(20,215,80,26);
        contentPanel.add(label6);
        label7.setBounds(228,215,55,26);
        contentPanel.add(label7);

        /* JTextField txfEventName */
        txfEventName.setBounds(113, 15, 170, 26);
        contentPanel.add(txfEventName);

        /* JTextField txfEventType */
        txfEventType.setBounds(113, 55, 170, 26);
        contentPanel.add(txfEventType);

        /* DatePicker dpDate */
        // init settings for DatePicker
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setAllowKeyboardEditing(false);
        dateSettings.setAllowEmptyDates(false);
        dateSettings.setFormatForDatesCommonEra("yyyyMMdd");
        dateSettings.setFormatForDatesBeforeCommonEra("uuuuMMdd");
        /// init DatePicker
        dpDate = new DatePicker(dateSettings);
        dpDate.setDateToToday();
        dpDate.setBounds(113, 95, 170, 30);
        contentPanel.add(dpDate);

        /* TimePicker tpStartTime */
        // init settings for TimePicker
        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.use24HourClockFormat();
        timeSettings.generatePotentialMenuTimes(TimeIncrement.TenMinutes,null,null);
        timeSettings.setAllowEmptyTimes(false);
        timeSettings.setAllowKeyboardEditing(false);
        timeSettings.initialTime = LocalTime.of(12, 00);
        // init TimePicker
        tpStartTime = new TimePicker(timeSettings);
        timeSettings.setVetoPolicy(new TimeVetoPolicy() {
            @Override
            public boolean isTimeAllowed(LocalTime time) {
                return PickerUtilities.isLocalTimeInRange(
                        time, LocalTime.of(7, 50), LocalTime.of(22, 0), true);
            }
        });
        tpStartTime.setBounds(113, 135, 170, 30);
        contentPanel.add(tpStartTime);

        /* JCheckBox cbNotifyHms */
        cbNotifyHms.setHorizontalAlignment(SwingConstants.RIGHT);
        cbNotifyHms.setBounds(255, 176, 28, 23);
        contentPanel.add(cbNotifyHms);

        /* JTextField txfDuration */
        txfDuration.setBounds(176, 215, 44, 26);
        contentPanel.add(txfDuration);

        /* JButton btnImport */
        btnImport.setBounds(6, 254, 290, 29);
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnImport(); // call internal method
            }
        });
        contentPanel.add(btnImport);

    } // END setUpContentPanel()

    /* import .csv file */
    private void actionBtnImport() {

        JFileChooser chooser = new JFileChooser(); // init JFileChooser Dialog
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter restrict =
                new FileNameExtensionFilter("Only .csv files", "csv");
        chooser.addChoosableFileFilter(restrict);

        int returnValue = chooser.showOpenDialog(null); // call JFileChooser
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            btnImport.setText(selectedFile.getName());
        }

    } // END actionBtnImport()

    private void setUpButtonPanel() {

        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnOk(); // Call internal method
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnCancel(); // Call internal method
            }
        });

        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);

    } // END setUpButtonPanel()

    /* perform validation and add to db */
    private void actionBtnOk() {

        /* Event Name validation */
        String eventName = txfEventName.getText();
        if (eventName.equals("")) { // presence check
            JOptionPane.showMessageDialog(null,
                    "(Event Name) Enter a value");
            return; // invalid: return
        }
        else if (!eventName.matches("[\\w ]+")) { // type check: only alphanumeric + space
            JOptionPane.showMessageDialog(null,
                    "(Event Name) Invalid Characters");
            return; // invalid: return
        }
        else if (eventName.length() > 50) { // length check
            JOptionPane.showMessageDialog(null,
                    "(Event Name) Too long - reduce to 50 characters");
            return; // invalid: return
        }

        /* Event Type validation */
        String eventType = txfEventType.getText();
        if (eventType.equals("")) { // presence check
            JOptionPane.showMessageDialog(null,
                    "(Event Type) Enter a value");
            return; // invalid: return
        }
        else if (!eventType.matches("[\\w ]+")) { // type check: only alphanumeric + space

            JOptionPane.showMessageDialog(null,
                    "(Event Type) Invalid Characters");
            return; // invalid: return
        }
        else if (eventType.length() > 20) { // length check
            JOptionPane.showMessageDialog(null,
                    "(Event Type) Too long - reduce to 20 characters");
            return; // invalid: return
        }

        /* date validation */
        String date = dpDate.getText();
        int year = Integer.parseInt(date.substring(0, 4) ); // get year
        if (year > 2100 || year < 2011) { // range check
            JOptionPane.showMessageDialog(null,
                    "(Date) invalid year");
            return; // invalid: return
        }

        /* startTime */
        String startTime = tpStartTime.getText();
        startTime = startTime.replaceAll(":", "");

        /* cbNotifyHms */
        boolean notifyHms = cbNotifyHms.isSelected();

        /* txfDuration validation */
        int duration;
        try {
            duration = Integer.parseInt(txfDuration.getText());
        }
        catch (NumberFormatException ne) { // type check
            JOptionPane.showMessageDialog(null,
                    "(Duration) Type in a number");
            return; // invalid: return
        }
        if (duration <= 0) { // range check: lower bound
            JOptionPane.showMessageDialog(null,
                    "(Duration) Must be bigger than 0");
            return; // invalid: return
        }
        else if (duration > 360) { // range check: upper bound
            JOptionPane.showMessageDialog(null,
                    "(Duration) Too big - event should be less than 6 hours long");
            return; // invalid: return
        }

        /* Create Event and assign ID */
        Event thisEvent = new Event(eventName, eventType, date,
                                    startTime, duration, notifyHms);
        int idEvent = thisEvent.getIdEvent();

        /* btnImport validation */
        if (selectedFile == null) { // No file selected
            JOptionPane.showMessageDialog(null,
                    "(Import) Select .csv file downloaded from Google Forms");
            return;
        }

        /* import .csv File and add students to student and event_student db */
        String line = "";
        String separator = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            br.readLine(); // Skip first line

            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\"", ""); // remove " character from .csv
                String[] entry = line.split(separator); // separate out data

                // get different fields of each record
                String email = entry[1].substring(0, entry[1].indexOf("@"));
                String name = entry[2].replaceAll("[^\\w ]", "");
                boolean isBoarder = (entry[4].equals("Yes")) ? true : false;
                int yearGroup = Integer.parseInt(entry[5]);

                Student entryStudent=
                        new Student(email, name, entry[3], isBoarder, yearGroup);

                db.addStudent(entryStudent, idEvent); // add student to db
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        /* Add event to event db */
        db.addEvent(thisEvent);

        /* Only get here if all checks passed */
        this.dispose();

    } // END actionBtnOk()

    private void actionBtnCancel() {

        this.dispose(); // dispose window without performing any actions

    } // END actionBtnCancel()

} // END class CreateEventPanel
