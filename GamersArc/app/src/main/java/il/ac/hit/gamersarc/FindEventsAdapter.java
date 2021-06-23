package il.ac.hit.gamersarc;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindEventsAdapter extends RecyclerView.Adapter<il.ac.hit.gamersarc.FindEventsAdapter.EventsViewHolder> {

    private ArrayList<il.ac.hit.gamersarc.Event> events;
    private Context context;
    private ArrayList<String> myEvents;

    public interface OnUpcomingEventListener {
        void onJoinEvent(String eventId,String userId);
        void onCancelJoinEvent(String eventId,String userId);
        void onSeeMembersClick(String eventId);
    }

    private OnUpcomingEventListener upcomingEventCallback;

    public FindEventsAdapter(ArrayList<il.ac.hit.gamersarc.Event> events, Context context, ArrayList<String> myEvents)  {
        this.events = events;
        this.context = context;
        this.myEvents = myEvents;
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder {

        TextView locationTv;
        TextView dateTv;
        TextView timeTv;
        TextView runningLevelTv;
        TextView runnersAmountTv;
        ImageView runningLevelIm;
        ToggleButton joinBtn;
        LinearLayout seeMembers;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);

            locationTv = itemView.findViewById(R.id.locationTVRecyclerCell);
            dateTv = itemView.findViewById(R.id.dateTVRecyclerCell);
            timeTv = itemView.findViewById(R.id.timeTVRecyclerCell);
            runningLevelTv = itemView.findViewById(R.id.runningLevelTVRecyclerCell);
            runnersAmountTv = itemView.findViewById(R.id.runnersAmountTVRecyclerCell);
            runningLevelIm = itemView.findViewById(R.id.runningLevelIm);
            joinBtn = itemView.findViewById(R.id.joinEventBtn);
            seeMembers = itemView.findViewById(R.id.seeMembersTv);

            joinBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String eventId = events.get(getAdapterPosition()).getEventId();
                    String userId = UserInstance.getInstance().getUser().getUserId();
                    if(isChecked){
                        upcomingEventCallback.onJoinEvent(eventId,userId);
                    }
                    else
                        upcomingEventCallback.onCancelJoinEvent(eventId,userId);
                }
            });

            seeMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String eventId = events.get(getAdapterPosition()).getEventId();
                    upcomingEventCallback.onSeeMembersClick(eventId);
                }
            });



        }
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_events_cell,parent,false);
        return new EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {

        il.ac.hit.gamersarc.Event event = events.get(position);
        Geocoder geocoder = new Geocoder(context);
        String streetAddress;
        double longitude = event.getLongitude();
        double latitude = event.getLatitude();
        LatLng latLng = new LatLng(latitude, longitude);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                streetAddress = address.getAddressLine(0);
                holder.locationTv.setText(streetAddress);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        int year = event.getYear();
        int month = event.getMonth();
        int dayOfMonth = event.getDayOfMonth();

        holder.dateTv.setText(dayOfMonth+"/"+month+'/'+year);

        int hourOfDay = event.getHourOfDay();
        int minute = event.getMinute();
        String chosenHour, chosenMinute;

        if(hourOfDay < 10)
            chosenHour="0"+hourOfDay;
        else
            chosenHour=hourOfDay+"";

        if(minute < 10)
            chosenMinute="0"+minute;
        else
            chosenMinute=minute+"";

        holder.timeTv.setText(chosenHour+":"+chosenMinute);

        String runningLevel = event.getRunningLevel();

        holder.runningLevelTv.setText(runningLevel);

        switch (runningLevel){
            case "easy":
                holder.runningLevelIm.setImageResource(R.drawable.easy);
                break;
            case "medium":
                holder.runningLevelIm.setImageResource(R.drawable.medium);
                break;
            case "expert":
                holder.runningLevelIm.setImageResource(R.drawable.hard);
                break;

        }

        if(myEvents.contains(event.getEventId())){
            holder.joinBtn.setChecked(true);
        }

        holder.runnersAmountTv.setText(String.valueOf(amountOfRunners(event)));



    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setUpcomingEventCallback(OnUpcomingEventListener upcomingEventCallback) {
        this.upcomingEventCallback = upcomingEventCallback;
    }

    public int amountOfRunners(il.ac.hit.gamersarc.Event event){
        HashMap<String,Boolean> runners = event.getRunners();
        int amount = runners.size();
        return amount;
    }


}
