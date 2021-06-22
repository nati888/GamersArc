package il.ac.hit.gamersarc;

import android.app.Application;
import android.location.Geocoder;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ManagedTabVM extends AndroidViewModel {

    private final String API_TOKEN_KEY = "AAAAfwvvO64:APA91bG6RWYJYEROIIoBMpzKm6kMdCbqDdqpzhynZ4YnFKEiQ0vu5QuLfJdGTtlixdzqBoL2Ul99A5Mf9kspOh8Whz9U-AY1-7rQTBiOUNUeYZM3UHh4A7Tm4Kb-u4Hrv98zApJn76NQ";
    private ArrayList<Event> managedEvents = new ArrayList<>();
    private ArrayList<String> managedEventsIds = new ArrayList<>();
    MutableLiveData<ArrayList<Event>> managedEventsLiveData = new MutableLiveData<>();
    private RegisterClass registerClass = RegisterClass.getInstance();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private MutableLiveData<Boolean> swipeLayoutBool = new MutableLiveData<>();


    public ManagedTabVM(@NonNull Application application) {
        super(application);

    }

    public void getManagedEventsIds(){
        swipeLayoutBool.setValue(true);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                managedEventsIds.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        managedEventsIds.add(snapshot1.getKey());
                    }
                    managedEventsIds.remove("false");
                    getManagedEvents();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveManagedEventsIds(registerClass.getUserId(), listener);

    }

    private void getManagedEvents() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                managedEvents.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        if (managedEventsIds.contains(snapshot1.getKey())){
                            Event event = snapshot1.getValue(Event.class);
                            managedEvents.add(event);
                        }
                    }
                    managedEventsLiveData.setValue(managedEvents);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveAllEvents(listener);

    }

    public MutableLiveData<ArrayList<Event>> getManagedEventsLiveData(){
        return managedEventsLiveData;
    }

    public MutableLiveData<Boolean> getSwipeLayoutBool(){
        return swipeLayoutBool;
    }

    public void onEventCancel(final Event event){

        //get runners Ids
        HashMap<String, Boolean> runnersMap = event.getRunners();
        Set<String> keySet = runnersMap.keySet();
        ArrayList<String> runnersIds = new ArrayList<>(keySet);
        runnersIds.remove("false");

        //database
        dataBaseClass.cancelEvent(event.getEventId(), runnersIds, event.getManager());
        getManagedEventsIds();
        //notification to all runners
        for(final String runnerId : runnersIds){
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   String token = snapshot.getValue(String.class);
                    try {
                        notifyRunner(token, event);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            dataBaseClass.retrieveUserToken(runnerId, listener);
        }

    }

    private void notifyRunner(String runnerToken, Event event) throws JSONException {

        double longitude = event.getLongitude();
        double latitude = event.getLatitude();
        Geocoder geocoder = new Geocoder(getApplication().getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            if (geocoder.getFromLocation(latitude,longitude,1) != null){
                if (geocoder.getFromLocation(latitude,longitude,1).size() > 0){
                    address = geocoder.getFromLocation(latitude,longitude,1).get(0).getLocality();
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        int year = event.getYear();
        int month = event.getMonth();
        int dayOfMonth = event.getDayOfMonth();
        String date = dayOfMonth + "." + month + "." + year;


        int hourOfDay = event.getHourOfDay();
        int minutes = event.getMinute();
        String time = hourOfDay + ":" + minutes;
        if (minutes < 10){
            time = hourOfDay + ":0" + minutes;
        }

        final JSONObject rootObject = new JSONObject();
        rootObject.put("to", runnerToken);
        JSONObject data = new JSONObject();
        String title = getApplication().getResources().getString(R.string.event_canceled);
        String theEvent = getApplication().getResources().getString(R.string.the_event);
        String onDate = getApplication().getResources().getString(R.string.on_date);
        String onTime = getApplication().getResources().getString(R.string.on_time);
        String isCanceled = getApplication().getResources().getString(R.string.is_canceled);
        data.put("messageType", "eventCancel");
        data.put("title", title);
        data.put("body", theEvent + " " + address + ", " + onDate + " " + date + ", " + onTime + " " + time + " " + isCanceled);
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



}
