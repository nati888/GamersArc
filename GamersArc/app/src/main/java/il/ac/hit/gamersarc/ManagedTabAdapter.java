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

public class ManagedTabAdapter extends RecyclerView.Adapter<il.ac.hit.gamersarc.ManagedTabAdapter.ManagedViewHolder> {

    private ArrayList<Event> managedEvents = new ArrayList<>();
    private Context context;

    interface ManagedListener{
        void onRunnersClicked(String eventId);
        void onCancelButtonClicked(Event event);

    }

    private ManagedListener managedCallback;

    public void setManagedCallback(ManagedListener managedCallback) {
        this.managedCallback = managedCallback;
    }

    public ManagedTabAdapter(ArrayList<Event> managedEvents, Context context) {
        this.managedEvents = managedEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public ManagedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.managed_cell, parent, false);
        return new ManagedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagedViewHolder holder, int position) {
        double longitude = managedEvents.get(position).getLongitude();
        double latitude = managedEvents.get(position).getLatitude();
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
        holder.managedAddress.setText(address);

        int year = managedEvents.get(position).getYear();
        int month = managedEvents.get(position).getMonth();
        int dayOfMonth = managedEvents.get(position).getDayOfMonth();
        String date = dayOfMonth + "." + month + "." + year;
        holder.managedDate.setText(date);

        int hourOfDay = managedEvents.get(position).getHourOfDay();
        int minutes = managedEvents.get(position).getMinute();
        String time = hourOfDay + ":" + minutes;
        if (minutes < 10){
            time = hourOfDay + ":0" + minutes;
        }
        holder.managedTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return managedEvents.size();
    }

    public class ManagedViewHolder extends RecyclerView.ViewHolder{

        TextView managedAddress;
        TextView managedDate;
        TextView managedTime;
        LinearLayout runners;
        Button cancelBtn;


        public ManagedViewHolder(@NonNull View itemView) {
            super(itemView);
            managedAddress = itemView.findViewById(R.id.managedCellAddressTV);
            managedDate = itemView.findViewById(R.id.managedCellDateTV);
            managedTime = itemView.findViewById(R.id.managedCellTimeTV);
            runners = itemView.findViewById(R.id.managedCellRunners);
            cancelBtn = itemView.findViewById(R.id.managedCellCancelBtn);


            runners.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    managedCallback.onRunnersClicked(managedEvents.get(getAdapterPosition()).getEventId());
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    managedCallback.onCancelButtonClicked(managedEvents.get(getAdapterPosition()));
                }
            });



        }
    }
}
