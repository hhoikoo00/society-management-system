package objects;

public class Student {

    private String              id_email;
    private int                 card_number;
    private String              name;
    private String              house;
    private boolean             is_boarder;
    private int                 year_group;
    private int                 events_went;

    /* constructor */
    public Student(String id_email, String name, String house,
            boolean is_boarder, int year_group) {

        this.id_email           = id_email;
        this.card_number        = -1; // default card_number always -1
        this.name               = name;
        this.house              = house;
        this.is_boarder         = is_boarder;
        this.year_group         = year_group;
        this.events_went        = 0; // default events_went always 0

    } // END constructor Student()

    /* Getters and Setters */
    public String getId_email() {
        return id_email;
    }
    public void setId_email(String id_email) {
        this.id_email = id_email;
    }
    public int getCard_number() {
        return card_number;
    }
    public void setCard_number(int card_number) {
        this.card_number = card_number;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHouse() {
        return house;
    }
    public void setHouse(String house) {
        this.house = house;
    }
    public boolean isIs_boarder() {
        return is_boarder;
    }
    public void setIs_boarder(boolean is_boarder) {
        this.is_boarder = is_boarder;
    }
    public int getYear_group() {
        return year_group;
    }
    public void setYear_group(int year_group) {
        this.year_group = year_group;
    }
    public int getEvents_went() {
        return events_went;
    }
    public void setEvents_went(int events_went) {
        this.events_went = events_went;
    }

}
