package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tools.DBManager;

@SuppressWarnings("serial")
public class DialogRegisterMode extends JDialog {
    /*-------- GUI Components  --------*/
    private Container           cp;
    private JLabel              label1          = new JLabel("ID card");
    private JLabel              label2          = new JLabel("Email");
    private JPasswordField      txfID           = new JPasswordField();
    private JTextField          txfEmail        = new JTextField();
    private JButton             btnEnter        = new JButton("Enter");

    /*-------- Other Components  --------*/
    private DBManager           db;
    private int                 idEvent;

    public DialogRegisterMode(Window win, String title,
            Dialog.ModalityType modalityType, DBManager db, int curEventId) {

        super(win, title, modalityType); // call superclass constructor
        this.db = db;
        this.idEvent = curEventId;

        /* Set up GUI */
        cp = getContentPane();
        cp.setLayout(null);
        setGUI(); // call internal method

        /* Set JDialog */
        setBounds(0, 0, 250, 170);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /* Set up GUI Elements */
    private void setGUI() {

        /* JLabel "ID" */
        label1.setBounds(20, 20, 61, 16);
        cp.add(label1);

        /* JLabel "Email" */
        label2.setBounds(20, 60, 61, 16);
        cp.add(label2);

        /* JTextField txfID card */
        txfID.setBounds(80, 15, 145, 26);
        txfID.addKeyListener(new KeyListener() { // KeyListener for txfID
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) { // When Enter key pressed
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionBtnEnter(); // call internal method
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        cp.add(txfID);
        txfID.setEchoChar(' '); // Show space characters instead of id

        /* JTextField txfEmail */
        txfEmail.setBounds(80, 55, 145, 26);
        cp.add(txfEmail);

        /* JButton btnEnter */
        btnEnter.setBounds(155, 100, 70, 29);
        btnEnter.addActionListener(new ActionListener() { // manually call method
            @Override
            public void actionPerformed(ActionEvent e) {
                actionBtnEnter(); // call internal method
            }
        });
        cp.add(btnEnter);

    }

    private void actionBtnEnter() {
        /* Get value of text fields */
        String textIdNumber = String.valueOf(txfID.getPassword());
        String textEmail = txfEmail.getText();
        boolean isIdEmpty = (textIdNumber.length() == 0);
        boolean isEmailEmpty = (textEmail.length() == 0);

        /* If both empty */
        if (isIdEmpty && isEmailEmpty) {
            JOptionPane.showMessageDialog(null,
                    "Tap your ID card or enter id part of email");
            return; // Invalid: return
        }

        /* If ID card tagged */
        else if (!isIdEmpty) {

            int idNumber = Integer.parseInt(textIdNumber);
            int resultId = db.searchStudent(idNumber);

            /* If no student found with matching ID: Initialize email for the student */
            if (resultId == 0) {
                do {
                    String inputEmail = "Type in your email ID \n"
                            + "(XXXXXX part of XXXXXX@pupils.nlcsjeju.kr)";
                    textEmail = JOptionPane.showInputDialog(this, inputEmail);
                }
                while (!textEmail.matches("[\\w]+")); // Type check: only alphanumeric

                /* Search for student using email ID */
                int resultEmail = db.searchStudent(textEmail);
                if (resultEmail == 0) { // If email not found in db
                    JOptionPane.showMessageDialog(null,
                            "Email ID not found - please register for an event "
                          + "before attempting to sign in");
                    txfID.setText("");
                    return; // Invalid: return
                }

                /* Verification of email ID input */
                String confirmMessage =
                        "Are you sure " + textEmail
                      + " is really your Email address "
                      + "and you are using your own ID card?";
                int confirmDialog = JOptionPane.showConfirmDialog(null,
                        confirmMessage, "Confirm Dialog",
                        JOptionPane.YES_NO_OPTION);
                if (confirmDialog == JOptionPane.NO_OPTION) { // If cancelled
                    JOptionPane.showMessageDialog(null, "Please register again");
                    txfID.setText("");
                    return; // Invalid: return
                }

                /* Update card ID for student with email ID */
                db.updateStudentId(textEmail, idNumber);
            }

            /* Increment event_went of student with card ID */
            db.incrementEvent(idNumber, idEvent);

        }
        /* END ID card tagged */

        /* If email entered */
        else {
            if (!(textEmail.matches("[\\w]+")) ) { // Type check: only alphanumeric
                JOptionPane.showMessageDialog(null,
                        "Please only enter ID part of email address");
                return; // Invalid: return
            }

            /* Increment and check if email ID exists */
            int resultIncrementEvent = db.incrementEvent(textEmail, idEvent);
            if (resultIncrementEvent == 0) { // Email not found
                JOptionPane.showMessageDialog(null,
                        "Email ID not found - please register for the event");
                // Invalid: return
            }

        }
        /* END email entered */

        /* Reset the text fields */
        txfID.setText("");
        txfEmail.setText("");
        txfID.grabFocus();

    }

}
