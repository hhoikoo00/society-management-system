package tools;

import java.util.*;
import java.sql.*;
import java.time.*;
import javax.swing.*;

import objects.Event;
import objects.Student;

public class DBManager {

    private Connection con;
    private String conStr = "jdbc:hsqldb:file:db-data/masterdb"; // path for db files

    public DBManager() throws Exception { // Exception thrown to class calling this class

        try { // find JDBC driver
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        }
        catch (ClassNotFoundException e) { // Error: Driver not found
            JOptionPane.showMessageDialog(null,
                    "DATABASE ERROR: JDBCDriver not found");
            throw e;
        }

        // SQL command: Create tables
        String createTables =
                "CREATE TABLE IF NOT EXISTS student ("
                    + "id_email VARCHAR(20),"
                    + "card_number INT,"
                    + "name VARCHAR(20),"
                    + "house VARCHAR(15),"
                    + "is_boarder BIT,"
                    + "year_group TINYINT,"
                    + "events_went INT,"
                    + "PRIMARY KEY (id_email));\n"
              + "CREATE TABLE IF NOT EXISTS event ("
                    + "id_event INT,"
                    + "name VARCHAR(50),"
                    + "type VARCHAR(20),"
                    + "date DATE,"
                    + "time_start TIME,"
                    + "duration INT,"
                    + "notify_hm BIT,"
                    + "PRIMARY KEY (id_event));\n"
              + "CREATE TABLE IF NOT EXISTS event_student ("
                    + "id_email VARCHAR(20),"
                    + "id_event INT,"
                    + "attended BIT);";

        try {
            /* Create DB if not exists */
            con = DriverManager.getConnection(conStr, "SA", "");

            /* Create tables */
            con.createStatement().executeUpdate(createTables);
        }
        catch (Exception e) { // Error in DB
            JOptionPane.showMessageDialog(null,
                    "DATABASE ERROR: table could not be created");
            throw e;
        }

    } // END constructor DBManager()

    /* add an event to event db */
    public void addEvent(Event event) {

        // SQL command: insert event to event db
        String insertIntoEvent = "INSERT INTO event "
                + "VALUES (?, ?, ?, ?, ?, ? ,?);";

        LocalDate date = Event.strDateToLocalDate(event.getDate());
        LocalTime startTime = Event.strTimeToLocalTime(event.getStartTime());

        try {
            PreparedStatement addEvent = con.prepareStatement(insertIntoEvent);
            addEvent.setInt(1, event.getIdEvent());
            addEvent.setString(2, event.getName());
            addEvent.setString(3, event.getType());
            addEvent.setDate(4, java.sql.Date.valueOf(date));
            addEvent.setTime(5, java.sql.Time.valueOf(startTime));
            addEvent.setInt(6, event.getDuration());
            addEvent.setBoolean(7, event.isNotifyHm());

            addEvent.executeUpdate(); // execute command
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    } // END addEvent()

    /* delete event from event db and event_student db */
    public void deleteEvent(int event_id) {

        // SQL commands
        String deleteFromEvent = "DELETE FROM event "
                + "WHERE id_event = ?;";
        String deleteFromEvtStd = "DELETE FROM event_student "
                + "WHERE id_event = ?;";

        try {
            // Delete event from event db
            PreparedStatement deleteEvent = con.prepareStatement(deleteFromEvent);
            deleteEvent.setInt(1, event_id);

            deleteEvent.executeUpdate(); // execute command

            // Delete event from event_student db
            PreparedStatement deleteEvtStd = con.prepareStatement(deleteFromEvtStd);
            deleteEvtStd.setInt(1, event_id);

            deleteEvtStd.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    } // END deleteEvent()

    /* modify event from DialogModifyEvent */
    public void modifyEvent(Event event, int curEventId) {

        if (event.getIdEvent() == curEventId) { // if ID of new event is same as current
            JOptionPane.showMessageDialog(null,
                    "Event not modified!");
            return;
        }

        // SQL command
        /* Change id_event of event_student */
        String updateEvtStd = "UPDATE event_student SET id_event = ? WHERE id_event = ?;";

        try {
            PreparedStatement modifyEvtStd = con.prepareStatement(updateEvtStd);
            modifyEvtStd.setInt(1, event.getIdEvent());
            modifyEvtStd.setInt(2, curEventId);

            modifyEvtStd.executeUpdate(); // execute command
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        /* Delete existing event with id_event */
        deleteEvent(curEventId);

        /* Add the modified event */
        addEvent(event);

    } // END modifyEvent()

    /* find event return */
    public Event getEvent(int eventId) {

        // SQL command
        String selectEvent = "SELECT * FROM event WHERE id_event = ?;";

        try {
            PreparedStatement retrieveEvent = con.prepareStatement(selectEvent);
            retrieveEvent.setInt(1, eventId);

            ResultSet results = retrieveEvent.executeQuery(); // execute command

            results.next(); // retrieve result
            LocalDate date = results.getDate(4).toLocalDate();
            String strDate = Event.localDateToStrDate(date);
            LocalTime time = results.getTime(5).toLocalTime();
            String strTime = Event.localTimeToStrTime(time);
            // create new Event object
            Event output = new Event(
                    results.getInt(1),
                    results.getString(2),
                    results.getString(3),
                    strDate,
                    strTime,
                    results.getInt(6),
                    results.getBoolean(7)
            );

            return output;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    } // END getEvent()

    /* return event list for combobox */
    public ArrayList<Event> returnEventList() {


        ArrayList<Event> output = new ArrayList<Event>();

        String selectAllEvents = "SELECT * FROM event;";
        try {
            PreparedStatement retrieveEvent = con.prepareStatement(selectAllEvents);
            retrieveEvent.clearParameters();
            ResultSet results = retrieveEvent.executeQuery();

            while (results.next()) {
                output.add(new Event( // Only ID, name, type needed for ComboBox
                        results.getInt(1),
                        results.getString(2),
                        results.getString(3)
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;

    } // END returnEventList()


    /* add student to student db */
    public void addStudent(Student student, int idEvent) {

        // SQL command
        String updateStudent = "INSERT INTO student VALUES (?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement addStudent = con.prepareStatement(updateStudent);
            addStudent.setString(1, student.getId_email());
            addStudent.setInt(2, student.getCard_number());
            addStudent.setString(3, student.getName());
            addStudent.setString(4, student.getHouse());
            addStudent.setBoolean(5, student.isIs_boarder());
            addStudent.setInt(6, student.getYear_group());
            addStudent.setInt(7, student.getEvents_went());

            addStudent.executeUpdate(); // execute command
        }
        catch (SQLIntegrityConstraintViolationException se) {}
        catch (Exception e) {
            e.printStackTrace();
        }

        // SQL command
        String updateEvtStd = "INSERT INTO event_student VALUES (?, ?, ?);";

        try {
            PreparedStatement addEvtStd = con.prepareStatement(updateEvtStd);
            addEvtStd.setString(1, student.getId_email());
            addEvtStd.setInt(2, idEvent);
            addEvtStd.setInt(3, 0);

            addEvtStd.executeUpdate(); // execute command
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    } // END addStudent()

    /* Search student on database using ID card number */
    public int searchStudent(int idCardNumber) {

        // SQL command
        String searchId = "SELECT COUNT(*) FROM student WHERE card_number = ?;";

        try {
            PreparedStatement searchStudent = con.prepareStatement(searchId);
            searchStudent.setInt(1, idCardNumber);

            ResultSet rs = searchStudent.executeQuery(); // execute command

            rs.next();
            return rs.getInt(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    /* Search student using email ID */
    public int searchStudent(String idEmail) {

        // SQL command
        String searchEmail = "SELECT COUNT(*) FROM student WHERE id_email = ?;";

        try {
            PreparedStatement searchStudent = con.prepareStatement(searchEmail);
            searchStudent.setString(1, idEmail);

            ResultSet rs = searchStudent.executeQuery(); // execute command

            rs.next();
            return rs.getInt(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /* change student ID using email ID */
    public void updateStudentId(String textEmail, int idNumber) {

        // SQL command
        String updateId = "UPDATE student SET card_number = ? WHERE id_email = ?;";

        try {
            PreparedStatement updateStudent = con.prepareStatement(updateId);
            updateStudent.setInt(1, idNumber);
            updateStudent.setString(2, textEmail);

            updateStudent.executeUpdate(); // execute command
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Use ID card number to find email and then call overloaded method */
    public void incrementEvent(int idNumber, int idEvent) {

        // SQL command
        String getIdEmail = "SELECT id_email FROM student WHERE card_number = ?;";

        try {
            PreparedStatement ps = con.prepareStatement(getIdEmail);
            ps.setInt(1, idNumber);

            ResultSet rs = ps.executeQuery(); // execute command
            rs.next();
            String idEmail = rs.getString(1); // get email ID

            incrementEvent(idEmail, idEvent); // call internal method
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* increment events_went */
    public int incrementEvent(String idEmail, int idEvent) {

        // SQL commands
        String getAttended =
                "SELECT attended FROM event_student WHERE id_email = ? AND id_event = ?;";
        String updateEventsWent =
                "UPDATE student SET events_went = events_went+1 WHERE id_email = ?;";
        String updateAttended =
                "UPDATE event_student SET attended = 1 WHERE id_email = ? AND id_event = ?;";

        try {
            PreparedStatement ps;
            ResultSet rs;

            ps = con.prepareStatement(getAttended);
            ps.setString(1, idEmail);
            ps.setInt(2, idEvent);

            rs = ps.executeQuery(); // execute command

            if (!rs.next()) { // No student updated
                return 0; // 0=error
            }

            /* Check if student already attended */
            int attended = rs.getInt(1); // attended in event_student db
            if (attended != 0) { // Student already attended
                return 1; // 1=success: already updated
            }

            /* Increment events_went in student */
            ps = con.prepareStatement(updateEventsWent);
            ps.setString(1, idEmail);
            ps.executeUpdate(); // execute command

            /* Update attended to true in event_student */
            ps = con.prepareStatement(updateAttended);
            ps.setString(1, idEmail);
            ps.setInt(2, idEvent);
            ps.executeUpdate(); // execute command

            return 1; // 1=success: updated
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0; // 0=error
        }

    }

    /* return the 2d ArrayList of students for DialogRetrieveRecord */
    public ArrayList<ArrayList<String>> returnStudents() throws Exception {

        // Declare and initialise output ArrayList<ArrayList>>
        ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();

        // SQL command
        String getStudents =
                "SELECT name, year_group, house, events_went "
              + "FROM student ORDER BY events_went DESC";

        try {
            PreparedStatement retrieveStudents = con.prepareStatement(getStudents);
            retrieveStudents.clearParameters();

            ResultSet results = retrieveStudents.executeQuery(); // execute command

            while (results.next()) {
                ArrayList<String> curArrayList = new ArrayList<String>();
                // add to inner ArrayList
                curArrayList.add(results.getString(1));
                curArrayList.add(String.valueOf(results.getInt(2)));
                curArrayList.add(results.getString(3));
                curArrayList.add(String.valueOf(results.getInt(4)));
                // add to outer ArrayList
                output.add(curArrayList);
            }
        }
        catch (Exception e) {
            throw e;
        }

        return output;

    } // END returnStudents()

} // END class DBManager
