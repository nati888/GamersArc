package il.ac.hit.gamersarc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InviteFriendsDialogAdapter extends RecyclerView.Adapter<com.example.runtime.InviteFriendsDialogAdapter.MyFriendsViewHolder>{

    private ArrayList<User> myFriends;
    private ArrayList<String> invitedFriendsIds = new ArrayList<>();
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private Context context;
    private UserInstance userInstance = UserInstance.getInstance();


    interface InviteFriendToEventListener{
        void onInviteFriendRequests(String invitedFriendId);
        void onCancelInviteFriendRequests(String invitedFriendId);
    }

    private InviteFriendToEventListener inviteFriendToEventCallback;

    public void setInviteFriendToEventCallback(InviteFriendToEventListener inviteFriendToEventCallback) {
        this.inviteFriendToEventCallback = inviteFriendToEventCallback;
    }

    public InviteFriendsDialogAdapter(ArrayList<User> myFriends, Context context, ArrayList<String> invitedFriendsIds) {
        this.myFriends = myFriends;
        this.context = context;
        this.invitedFriendsIds = invitedFriendsIds;
    }

    public class MyFriendsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView myFriendIv;
        TextView friendName;
        TextView distance;
        ToggleButton inviteFriendBtn;

        public MyFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            myFriendIv = itemView.findViewById(R.id.myFriendIV);
            friendName = itemView.findViewById(R.id.myFriendName);
            distance = itemView.findViewById(R.id.distanceTV);
            inviteFriendBtn = itemView.findViewById(R.id.inviteBtn);

            inviteFriendBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String invitedFriendId = myFriends.get(getAdapterPosition()).getUserId();
                    if(isChecked){
                        inviteFriendToEventCallback.onInviteFriendRequests(invitedFriendId);
                    }
                    else{
                        inviteFriendToEventCallback.onCancelInviteFriendRequests(invitedFriendId);
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public MyFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_friends_cell, parent, false);
        return new com.example.runtime.InviteFriendsDialogAdapter.MyFriendsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyFriendsViewHolder holder, int position) {
        User user = myFriends.get(position);

        if (invitedFriendsIds.contains(user.getUserId())){
            holder.inviteFriendBtn.setChecked(true);
        }

        OnSuccessListener listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                String image = o.toString();
                Glide.with(context).load(image).placeholder(R.drawable.ic_launcher_background).into(holder.myFriendIv);
            }
        };
        dataBaseClass.getImageUserId(user.getUserId(),listener);

        holder.friendName.setText(user.getFullName());

        double longitude = user.getLongitude();
        double latitude = user.getLatitude();
        double distance = haversine(latitude, longitude, userInstance.getUser().getLatitude(), userInstance.getUser().getLongitude());
        int meters = (int) (distance * 1000);
        holder.distance.setText(meters + "");

    }

    @Override
    public int getItemCount() {
        return myFriends.size();
    }

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

}
