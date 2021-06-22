package il.ac.hit.gamersarc;

public class User {
    private String fullName;
    private String gender;
    private int year;
    private int month;
    private int dayOfMonth;
    private String runningLevel;
    private double longitude;
    private double latitude;
    private String userId;
    private String userToken;

    public User() {
    }

    public User(String userToken, String userId, String fullName, String gender, int year, int month, int dayOfMonth, String runningLevel, double longitude, double latitude) {
        this.fullName = fullName;
        this.gender = gender;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.runningLevel = runningLevel;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userToken = userToken;
        this.userId = userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRunningLevel() {
        return runningLevel;
    }

    public void setRunningLevel(String runningLevel) {
        this.runningLevel = runningLevel;
    }


}
