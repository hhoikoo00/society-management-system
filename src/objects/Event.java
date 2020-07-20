package objects;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    private int         idEvent = -1; // Default value
    private String      name;
    private String      type;
    private String      date;
    private String      startTime;
    private int         duration;
    private boolean     notifyHm;

    // Constructor for new events
    public Event(String name, String type, String date, String startTime,
                 int duration, boolean notifyHm) {
        this.name       = name;
        this.type       = type;
        this.date       = date;
        this.startTime  = startTime;
        this.duration   = duration;
        this.notifyHm   = notifyHm;

        this.idEvent = generateIdEvent(this);
    } // END Event()

    // constructor for JComboBox
    public Event(int idEvent, String name, String type) {
        this.idEvent    = idEvent;
        this.name       = name;
        this.type       = type;
        this.date       = null;
        this.startTime  = null;
        this.duration   = 0;
        this.notifyHm   = false;
    } // END Event()

    // Constructor for existing events
    public Event(int idEvent, String name, String type, String date,
            String startTime, int duration, boolean notifyHm) {
        this.idEvent    = idEvent;
        this.name       = name;
        this.type       = type;
        this.date       = date;
        this.startTime  = startTime;
        this.duration   = duration;
        this.notifyHm   = notifyHm;
    } // END Event()

    /* Getters and Setters */
    public int getIdEvent() {
        return idEvent;
    }
    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public boolean isNotifyHm() {
        return notifyHm;
    }
    public void setNotifyHm(boolean notifyHm) {
        this.notifyHm = notifyHm;
    }

    /* Generate the unique id of each event */
    public static int generateIdEvent(Event event) {
        /*
         * ID in a format XXXXYYYYZ
         * XXXX is the sum of all char in name and type
         * YYYY is the sum of year,month,day,hour,minute,duration
         * Z is notifyHm (0 false 1 true)
         */
        if (event.getIdEvent() != -1) { // If id already exists use that id
            return event.getIdEvent();
        }
        else {
            int generatedId = 0;
            // add name's String length
            int lenName = event.getName().length();
            for (int x=0; x < lenName; x++) {
                generatedId += event.getName().charAt(x);
            }
            // add type's String length
            int lenType = event.getType().length();
            for (int y=0; y < lenType; y++) {
                generatedId += event.getType().charAt(y);
            }
            generatedId = generatedId * 10000; // Change digit
            // Add sum of year, month, day
            generatedId += Integer.parseInt(event.getDate().substring(0, 4)); // year
            generatedId += Integer.parseInt(event.getDate().substring(4, 6)); // month
            generatedId += Integer.parseInt(event.getDate().substring(6, 8)); // day
            // add sum of hour, minute
            generatedId += Integer.parseInt(event.getStartTime().substring(0, 2)); // hour
            generatedId += Integer.parseInt(event.getStartTime().substring(2, 4)); // minute
            // add duration
            generatedId += event.getDuration();
            generatedId = generatedId * 10; // change digit
            // add notifyHm
            generatedId += (event.isNotifyHm()) ? 1 : 0; // 1: true, 0: false

            return generatedId;
        }
    } // END static generateIdEvent()

    /* String date -> LocalDate date */
    public static LocalDate strDateToLocalDate(String strDate) {
        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(4, 6));
        int day = Integer.parseInt(strDate.substring(6, 8));
        LocalDate date = LocalDate.of(year, month, day);

        return date;

    } // END static strDateToLocalDate()

    /* LocalDate date -> String date */
    public static String localDateToStrDate(LocalDate locDate) {
        return String.format("%04d%02d%02d",
                locDate.getYear(), locDate.getMonthValue(), locDate.getDayOfMonth());

    } // END static localDateToStrDate()

    /* String time -> LocalTime time */
    public static LocalTime strTimeToLocalTime(String strTime) {
        int hour = Integer.parseInt(strTime.substring(0, 2));
        int min = Integer.parseInt(strTime.substring(2, 4));
        LocalTime startTime = LocalTime.of(hour, min);

        return startTime;

    } // END static strTimeToLocalTime()

    /* LocalTime time -> String time */
    public static String localTimeToStrTime(LocalTime locTime) {
        return String.format("%02d%02d",
                locTime.getHour(), locTime.getMinute());

    } // END static strTimeToLocalTime()

} // END class Event
