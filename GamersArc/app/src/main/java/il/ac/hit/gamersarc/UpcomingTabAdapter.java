package il.ac.hit.gamersarc;

import android.content.Context;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class UpcomingTabAdapter extends RecyclerView.Adapter<com.example.runtime.UpcomingTabAdapter.UpcomingViewHolder> {

    private ArrayList<Event> upcomingEvents = new ArrayList<>();
    private Context context;
    RegisterClass registerClass = RegisterClass.getInstance();

    public UpcomingTabAdapter(ArrayList<Event> upcomingEvents, Context context) {
        this.upcomingEvents = upcomingEvents;
        this.context = context;
    }

    interface UpcomingListener{
        void onUpcomingRemoved(String userId, String eventId);
        void onRunnersClicked(String eventId);
    }

    UpcomingListener upcomingCallback;

    public void setUpcomingCallback(UpcomingListener upcomingCallback) {
        this.upcomingCallback = upcomingCallback;
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_cell, parent, false);
        return new UpcomingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        //geocode address
        double longitude = upcomingEvents.get(position).getLongitude();
        double latitude = upcomingEvents.get(position).getLatitude();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";
        try {
            if (geocoder.getFromLocation(latitude,longitude,1) != null){
                if (geocoder.getFromLocation(latitude,longitude,1).size() > 0){
                    address = geocoder.getFromLocation(latitude,longitude,1).get(0).getAddressLine(0);
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        holder.upcomingAddress.setText(address);

        int year = upcomingEvents.get(position).getYear();
        int month = upcomingEvents.get(position).getMonth();
        int dayOfMonth = upcomingEvents.get(position).getDayOfMonth();
        String date = dayOfMonth + "." + month + "." + year;
        holder.upcomingDate.setText(date);

        int hourOfDay = upcomingEvents.get(position).getHourOfDay();
        int minutes = upcomingEvents.get(position).getMinute();
        String time = hourOfDay + ":" + minutes;
        if (minutes < 10){
            time = hourOfDay + ":0" + minutes;
        }

        holder.upcomingTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return upcomingEvents.size();
    }

    public class UpcomingViewHolder extends RecyclerView.ViewHolder{

        TextView upcomingAddress;
        TextView upcomingDate;
        TextView upcomingTime;
        LinearLayout runners;
        Button removeBtn;

        public UpcomingViewHolder(@NonNull View itemView) {
            super(itemView);
            upcomingAddress = itemView.findViewById(R.id.upcomingCellAddressTV);
            upcomingDate = itemView.findViewById(R.id.upcomingCellDateTV);
            upcomingTime = itemView.findViewById(R.id.upcomingCellTimeTV);
            runners = itemView.findViewById(R.id.upcomingCellRunners);
            removeBtn = itemView.findViewById(R.id.upcomingCellremoveBtn);

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upcomingCallback.onUpcomingRemoved(registerClass.getUserId(), upcomingEvents.get(getAdapterPosition()).getEventId());
                }
            });

            runners.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //openDialog
                    upcomingCallback.onRunnersClicked(upcomingEvents.get(getAdapterPosition()).getEventId());
                }
            });
        }
    }
}
