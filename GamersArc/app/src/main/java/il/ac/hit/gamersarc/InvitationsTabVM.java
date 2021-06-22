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

public class InvitationsTabVM extends ViewModel {

    private ArrayList<Event> invitations = new ArrayList<>();
    private ArrayList<String> invitationsIds = new ArrayList<>();
    MutableLiveData<ArrayList<Event>> invitationsLiveData = new MutableLiveData<>();
    DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    RegisterClass registerClass = RegisterClass.getInstance();
    private MutableLiveData<Boolean> swipeLayoutBool = new MutableLiveData<>();

    public InvitationsTabVM() {

    }

    public void getInvitationsIds(){
        swipeLayoutBool.setValue(true);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invitationsIds.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    invitationsIds.add(snapshot1.getKey());
                }
                invitationsIds.remove("false");
                getInvitations();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveInvitationsIds(registerClass.getUserId(), listener);
    }

    private void getInvitations() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invitations.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (invitationsIds.contains(snapshot1.getKey())){
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
                            invitations.add(event);
                    }
                }
                invitationsLiveData.setValue(invitations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dataBaseClass.retrieveAllEvents(listener);
    }

    public MutableLiveData<ArrayList<Event>> getInvitationsLiveData(){
        return invitationsLiveData;
    }

    public void onJoinToEvent(String userId, String eventId){
        dataBaseClass.joinToEvent(userId, eventId);
        getInvitationsIds();
    }

    public void onRemoveEvent(String userId, String eventId){
        dataBaseClass.removeEventFromInvitations(userId, eventId);
        getInvitationsIds();
    }

    public MutableLiveData<Boolean> getSwipeLayoutBool(){
        return swipeLayoutBool;
    }
}
