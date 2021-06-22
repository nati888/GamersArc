package il.ac.hit.gamersarc;



import java.util.HashMap;

public class UserLists {
    //turn all lists to HashMaps
    private HashMap<String, Boolean> myFriends ;
    private HashMap<String, Boolean> myEvents ;
    private HashMap<String, Boolean> friendsRequests;
    private HashMap<String, Boolean> eventRequests;
    private HashMap<String, Boolean> managedEvents;
    private HashMap<String, Boolean> sentFriendsRequests;

    public UserLists() {
        myFriends = new HashMap<>();
        myEvents = new HashMap<>();
        friendsRequests = new HashMap<>();
        eventRequests = new HashMap<>();
        managedEvents = new HashMap<>();
        sentFriendsRequests = new HashMap<>();

        myFriends.put("false", false);
        myEvents.put("false", false);
        friendsRequests.put("false", false);
        eventRequests.put("false", false);
        managedEvents.put("false", false);
        sentFriendsRequests.put("false", false);
    }

    public HashMap<String, Boolean> getMyFriends() {
        return myFriends;
    }

    public void setMyFriends(HashMap<String, Boolean> myFriends) {
        this.myFriends = myFriends;
    }

    public HashMap<String, Boolean> getMyEvents() {
        return myEvents;
    }

    public void setMyEvents(HashMap<String, Boolean> myEvents) {
        this.myEvents = myEvents;
    }

    public HashMap<String, Boolean> getFriendsRequests() {
        return friendsRequests;
    }

    public void setFriendsRequests(HashMap<String, Boolean> friendsRequests) {
        this.friendsRequests = friendsRequests;
    }

    public HashMap<String, Boolean> getEventRequests() {
        return eventRequests;
    }

    public void setEventRequests(HashMap<String, Boolean> eventRequests) {
        this.eventRequests = eventRequests;
    }

    public HashMap<String, Boolean> getManagedEvents() {
        return managedEvents;
    }

    public void setManagedEvents(HashMap<String, Boolean> managedEvents) {
        this.managedEvents = managedEvents;
    }

    public HashMap<String, Boolean> getSentFriendsRequests() {
        return sentFriendsRequests;
    }

    public void setSentFriendsRequests(HashMap<String, Boolean> sentFriendsRequests) {
        this.sentFriendsRequests = sentFriendsRequests;
    }
}