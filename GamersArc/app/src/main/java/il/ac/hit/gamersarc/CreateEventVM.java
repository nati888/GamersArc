package il.ac.hit.gamersarc;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.runtime.UserInstance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateEventVM extends AndroidViewModel {

    private int eventYear;
    private int eventMonth;
    private int eventDayOfMonth;
    private int eventHourOfDay;
    private int eventMinute;
    private double longitude;
    private double latitude;
    private String runningLevel;
    private String eventStatus;
    private UserInstance userInstance = UserInstance.getInstance();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private String eventDate;
    private String eventTime;
    private ArrayList<String> invitedUsersIds;

    private final String API_TOKEN_KEY = "AAAAfwvvO64:APA91bG6RWYJYEROIIoBMpzKm6kMdCbqDdqpzhynZ4YnFKEiQ0vu5QuLfJdGTtlixdzqBoL2Ul99A5Mf9kspOh8Whz9U-AY1-7rQTBiOUNUeYZM3UHh4A7Tm4Kb-u4Hrv98zApJn76NQ";

    private MutableLiveData<String> streetAddress = new MutableLiveData<>();

    public CreateEventVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getStreetAddress(){
        return streetAddress;
    }

    public void setStreetAddress(String address){
        streetAddress.setValue(address);
    }
    public void setLongitudeLatitude (double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setEventData (int eventYear, int eventMonth, int eventDayOfMonth, int eventHourOfDay, int eventMinute, String runningLevel, String eventStatus, ArrayList<String> invitedFriendsIds) {
        this.eventYear = eventYear;
        this.eventMonth = eventMonth;
        this.eventDayOfMonth = eventDayOfMonth;
        this.eventHourOfDay = eventHourOfDay;
        this.eventMinute = eventMinute;
        this.runningLevel = runningLevel;
        this.eventStatus = eventStatus;

        Event event = new Event(eventYear,eventMonth,eventDayOfMonth,eventHourOfDay,eventMinute,longitude,latitude,runningLevel,eventStatus);
        String userId = userInstance.getUser().getUserId();
        dataBaseClass.createNewEvent(event,userId,invitedFriendsIds);

        if(invitedFriendsIds!=null){
            userTokenFromDatabase(invitedFriendsIds);
        }

    }

    public void setEventDate(String eventDate){
        this.eventDate = eventDate;
    }

    public String getEventDate(){
        return eventDate;
    }

    public void setEventTime(String eventTime){
        this.eventTime = eventTime;
    }

    public String getEventTime(){
        return  eventTime;
    }

    public void userTokenFromDatabase(final ArrayList<String> invitedFriendsIds){

        for( String invitedFriendId: invitedFriendsIds){
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String invitedFriendToken = snapshot.getValue(String.class);
                        try {
                            createFriendRequestNotificationMessage(invitedFriendToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            dataBaseClass.retrieveUserToken(invitedFriendId, listener);

        }
    }


    private void createFriendRequestNotificationMessage(String token) throws JSONException {

        final JSONObject rootObject = new JSONObject();
        rootObject.put("to", token);
        JSONObject data = new JSONObject();
        String title = getApplication().getString(R.string.new_invitation);
        String body = getApplication().getString(R.string.see_in_invitations);
        data.put("title", title);
        data.put("body", body);
        data.put("messageType", "eventRequest");
        rootObject.put("data", data);

        String url = "https://fcm.googleapis.com/fcm/send";

        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return rootObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + API_TOKEN_KEY);
                return headers;
            }
        };

        queue.add(request);
        queue.start();

    }

    public Event getEventData(){
        Event event = new Event(eventYear,eventMonth,eventDayOfMonth,eventHourOfDay,eventMinute,longitude,latitude,runningLevel,eventStatus);
        return event;
    }

    public void setEventYear(int eventYear) {
        this.eventYear = eventYear;
    }

    public void setEventMonth(int eventMonth) {
        this.eventMonth = eventMonth;
    }

    public void setEventDayOfMonth(int eventDayOfMonth) {
        this.eventDayOfMonth = eventDayOfMonth;
    }

    public void setEventHourOfDay(int eventHourOfDay) {
        this.eventHourOfDay = eventHourOfDay;
    }

    public void setEventMinute(int eventMinute) {
        this.eventMinute = eventMinute;
    }

    public void setRunningLevel(String runningLevel) {
        this.runningLevel = runningLevel;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public ArrayList<String> getInvitedUsersIds() {
        return invitedUsersIds;
    }

    public void setInvitedUsersIds(ArrayList<String> invitedUsersIds) {
        this.invitedUsersIds = invitedUsersIds;
    }

}
