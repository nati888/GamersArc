package il.ac.hit.gamersarc.model;


import il.ac.hit.gamersarc.User;

public class UserWithLastMessage {
    User user;
    Message message;
    boolean isNew;

    public UserWithLastMessage(User user, Message message,boolean isNew) {
        this.user = user;
        this.message = message;
        this.isNew = isNew;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }


}
