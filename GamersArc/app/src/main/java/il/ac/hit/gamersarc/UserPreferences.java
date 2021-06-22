package il.ac.hit.gamersarc;

public class UserPreferences {
    private int fromAge;
    private int toAge;
    private String gender;
    private String runingLevel;


    public UserPreferences() {
    }

    public UserPreferences(int fromAge, int toAge, String gender, String runingLevel) {
        this.fromAge = fromAge;
        this.toAge = toAge;
        this.gender = gender;
        this.runingLevel = runingLevel;
    }

    public int getFromAge() {
        return fromAge;
    }

    public void setFromAge(int fromAge) {
        this.fromAge = fromAge;
    }

    public int getToAge() {
        return toAge;
    }

    public void setToAge(int toAge) {
        this.toAge = toAge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRuningLevel() {
        return runingLevel;
    }

    public void setRuningLevel(String runingLevel) {
        this.runingLevel = runingLevel;
    }
}
