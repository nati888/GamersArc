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

public class InvitationsTabAdapter extends RecyclerView.Adapter<il.ac.hit.gamersarc.InvitationsTabAdapter.InvitationsViewHolder> {

    private ArrayList<il.ac.hit.gamersarc.Event> invitations = new ArrayList<>();
    private Context context;
    RegisterClass registerClass = RegisterClass.getInstance();

    interface OnInvitationResponse{
        void onJoinToEvent(String userId, String eventId, int position);
        void onRemoveEvent(String userId, String eventId, int position);
        void onRunnersClicked(String eventId);
    }

    private OnInvitationResponse invitationCallback;

    public void setInvitationCallback(OnInvitationResponse invitationCallback) {
        this.invitationCallback = invitationCallback;
    }

    public InvitationsTabAdapter(ArrayList<il.ac.hit.gamersarc.Event> invitations, Context context) {
        this.invitations = invitations;
        this.context = context;
    }

    @NonNull
    @Override
    public InvitationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invitation_cell, parent, false);
        return new InvitationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationsViewHolder holder, int position) {
        double longitude = invitations.get(position).getLongitude();
        double latitude = invitations.get(position).getLatitude();
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
        holder.invitationAddressTV.setText(address);

        int year = invitations.get(position).getYear();
        int month = invitations.get(position).getMonth();
        int dayOfMonth = invitations.get(position).getDayOfMonth();
        String date = dayOfMonth + "." + month + "." + year;
        holder.invitationDateTV.setText(date);

        int hourOfDay = invitations.get(position).getHourOfDay();
        int minutes = invitations.get(position).getMinute();
        String time = hourOfDay + ":" + minutes;
        if (minutes < 10){
            time = hourOfDay + ":0" + minutes;
        }
        holder.invitationTimeTV.setText(time);
    }

    @Override
    public int getItemCount() {
        return invitations.size();
    }

    public class InvitationsViewHolder extends RecyclerView.ViewHolder{

        TextView invitationAddressTV;
        TextView invitationDateTV;
        TextView invitationTimeTV;
        Button joinBtn;
        Button removeBtn;
        LinearLayout runners;

        public InvitationsViewHolder(@NonNull View itemView) {
            super(itemView);
            invitationAddressTV = itemView.findViewById(R.id.invitationCellAddressTV);
            invitationDateTV = itemView.findViewById(R.id.invitationCellDateTV);
            invitationTimeTV = itemView.findViewById(R.id.invitationCellTimeTV);
            joinBtn = itemView.findViewById(R.id.invitationCellJoinBtn);
            removeBtn = itemView.findViewById(R.id.invitationCellRemoveBtn);
            runners = itemView.findViewById(R.id.invitationsCellRunners);

            joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    invitationCallback.onJoinToEvent(registerClass.getUserId(), invitations.get(getAdapterPosition()).getEventId(), getAdapterPosition());

                }
            });

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    invitationCallback.onRemoveEvent(registerClass.getUserId(), invitations.get(getAdapterPosition()).getEventId(), getAdapterPosition());
                }
            });

            runners.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    invitationCallback.onRunnersClicked(invitations.get(getAdapterPosition()).getEventId());
                }
            });
        }
    }
}
