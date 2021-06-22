package il.ac.hit.gamersarc;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UpcomingTabVM extends ViewModel {

    private ArrayList<Event> upcomingEvents = new ArrayList<>();
    private ArrayList<String> upcomingEventsIds = new ArrayList<>();
    private MutableLiveData<ArrayList<Event>> upcomingEventsLiveData = new MutableLiveData<>();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private RegisterClass registerClass = RegisterClass.getInstance();
    private MutableLiveData<Boolean> swipeLayoutBool = new MutableLiveData<>();

    public UpcomingTabVM() {


    }

    public void getUpcomingEventsIds(){
        swipeLayoutBool.setValue(true);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                upcomingEventsIds.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    upcomingEventsIds.add(snapshot1.getKey());
                }
                upcomingEventsIds.remove("false");
                getUpcomingEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseClass.retrieveUpcomingEventsIds(registerClass.getUserId(), listener);

    }

    private void getUpcomingEvents() {

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                upcomingEvents.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (upcomingEventsIds.contains(snapshot1.getKey())){
                        Event event = snapshot1.getValue(Event.class);
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
                        if(dateNow.compareTo(date) < 0)
                            upcomingEvents.add(event);
                    }
                }

                upcomingEventsLiveData.setValue(upcomingEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveAllEvents(listener);

    }

    public MutableLiveData<ArrayList<Event>> getUpcomingEventsLiveData(){
        return upcomingEventsLiveData;
    }

    public MutableLiveData<Boolean> getSwipeLayoutBool(){
        return swipeLayoutBool;
    }

    public void RemoveUpcomingEvent(String userId, String eventId){
        dataBaseClass.removeUpcomingEvent(userId,eventId);
        getUpcomingEventsIds();
    }
}
