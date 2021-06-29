package il.ac.hit.gamersarc.model;

public class LastMessage {
    private String content;
    private String time;
    private int id;
    private String userIdSent;
    boolean isNew;

    public LastMessage(String content, String time, int id, String userIdSent, boolean isNew) {
        this.content = content;
        this.time = time;
        this.id = id;
        this.userIdSent = userIdSent;
        this.isNew = isNew;
    }

    public LastMessage() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserIdSent() {
        return userIdSent;
    }

    public void setUserIdSent(String userIdSent) {
        this.userIdSent = userIdSent;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
