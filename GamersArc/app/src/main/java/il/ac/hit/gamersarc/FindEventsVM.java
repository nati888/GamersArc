package il.ac.hit.gamersarc;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FindEventsVM extends AndroidViewModel {

    private MutableLiveData<ArrayList<il.ac.hit.gamersarc.Event>> relevantEvents = new MutableLiveData<>();
    private ArrayList<il.ac.hit.gamersarc.Event> eventsFromDatabase = new ArrayList<>();
    private il.ac.hit.gamersarc.DataBaseClass dataBaseClass = il.ac.hit.gamersarc.DataBaseClass.getInstance();
    private ArrayList<il.ac.hit.gamersarc.Event> relevantEventsTemp = new ArrayList<>();
    private User currentUser;
    private MutableLiveData<ArrayList<String>> myEvents = new MutableLiveData<>();
    private ArrayList<String> myEventsTemp = new ArrayList<>();

    private MutableLiveData<Boolean> swipeLayoutEventBool = new MutableLiveData<>();

    public FindEventsVM(@NonNull Application application) {
        super(application);
        //retrieveEventsList();
        //setMyEvents();
    }

    //distance calculation
    private double haversine(double lat1, double lon1,
                             double lat2, double lon2)
    {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c; //distance in km
    }


    public MutableLiveData<ArrayList<il.ac.hit.gamersarc.Event>> getRelevantEvents(){
        return relevantEvents;
    }

    public MutableLiveData<ArrayList<String>> getMyEvents(){return  myEvents;}

    public void retrieveEventsList(){
        getAllEventsList();
        swipeLayoutEventBool.setValue(true);
    }

    private void getAllEventsList(){

        eventsFromDatabase.clear();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        il.ac.hit.gamersarc.Event event = snapshot1.getValue(il.ac.hit.gamersarc.Event.class);
                        eventsFromDatabase.add(event);
                    }
                    getCurrentUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveAllEventsList(listener);

    }

    private void getCurrentUser() {
        currentUser = UserInstance.getInstance().getUser();
        findRelevantEvents();
    }

    private void findRelevantEvents(){

        relevantEventsTemp.clear();

        double longitude = currentUser.getLongitude();
        double latitude = currentUser.getLatitude();

        for (il.ac.hit.gamersarc.Event event: eventsFromDatabase) {

            int year = event.getYear();
            int month = event.getMonth();
            int dayOfMonth = event.getDayOfMonth();

            int hour = event.getHourOfDay();
            int minute = event.getMinute();

            String hour1;
            String minute1;
            String month1;
            String dayOfMonth1;

            if(hour < 10)
                hour1="0"+hour;
            else
                hour1=hour+"";

            if(minute < 10)
                minute1="0"+minute;
            else
                minute1=minute+"";

            if(month < 10)
                month1="0"+month;
            else
                month1=month+"";

            if(dayOfMonth < 10)
                dayOfMonth1="0"+dayOfMonth;
            else
                dayOfMonth1=dayOfMonth+"";

            String date = year+"-"+month1+"-"+dayOfMonth1+" "+hour1+":"+minute1;
            Log.d("date",date);

            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String dateNow = simpleDateFormat.format(new Date());
            Log.d("date",dateNow);

            double distance = haversine(event.getLatitude(), event.getLongitude(), latitude, longitude);

            if(dateNow.compareTo(date) < 0){
                if(distance < 20 && event.getEventStatus().equals("publicEvent") &&
                        !event.getManager().equals(currentUser.getUserId())){
                    {
                        relevantEventsTemp.add(event);
                    }
            }
        }

        }

        relevantEvents.setValue(relevantEventsTemp);

    }

    // add event to user "myEvents" list.
    public void onJoinEvent(String eventId,String userId) {
        dataBaseClass.addEventToMyEventsList(eventId,userId);
    }

    // remove event to user "myEvents" list.
    public void onCancelJoinEvent(String eventId,String userId) {
        dataBaseClass.removeEventFromMyEventsList(eventId,userId);
    }

    // update myEvents list from database.
    public void setMyEvents(){
        getCurrentUser();
        myEventsTemp.clear();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        myEventsTemp.add(snapshot1.getKey());
                    }
                }
                myEventsTemp.remove("false");
                myEvents.setValue(myEventsTemp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveMyEvents(listener,currentUser.getUserId());

    }

    public MutableLiveData<Boolean> getSwipeLayoutEventBool() {
        return swipeLayoutEventBool;
    }
}

