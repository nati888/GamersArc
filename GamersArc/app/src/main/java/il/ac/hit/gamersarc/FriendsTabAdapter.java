package il.ac.hit.gamersarc;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsTabAdapter extends RecyclerView.Adapter<il.ac.hit.gamersarc.FriendsTabAdapter.FriendViewHolder> {

    private ArrayList<User> friends = new ArrayList<>();
    private Context context;
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private UserInstance userInstance = UserInstance.getInstance();
    private RegisterClass registerClass = RegisterClass.getInstance();

    interface FriendTabListener{
        void onRemoveBtnClicked(String userId, String friendId, String friendName);
        void onFriendCellClicked(String friendId);
    }

    FriendTabListener friendTabCallback;

    public void setFriendTabCallback(FriendTabListener friendTabCallback) {
        this.friendTabCallback = friendTabCallback;
    }

    public FriendsTabAdapter(ArrayList<User> friends, Context context) {
        this.friends = friends;

        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_cell, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendViewHolder holder, int position) {
        holder.friendTextView.setText(friends.get(position).getFullName());

        OnSuccessListener<Uri> listener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.placeholder_small).into(holder.friendImageView);
            }
        };

        dataBaseClass.getImageUserId(friends.get(position).getUserId(), listener);
        double longitude = friends.get(position).getLongitude();
        double latitude = friends.get(position).getLatitude();
        double distance = haversine(latitude, longitude, userInstance.getUser().getLatitude(), userInstance.getUser().getLongitude());
        int meters = (int)(distance * 1000);

        holder.distanceTV.setText(meters + "");
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

    @Override
    public int getItemCount() {
        return friends.size();
    }

    //ViewHolder

    public class FriendViewHolder extends RecyclerView.ViewHolder{

        CircleImageView friendImageView;
        TextView friendTextView;
        TextView distanceTV;
        Button removeBtn;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendImageView = itemView.findViewById(R.id.friendCellImageView);
            friendTextView = itemView.findViewById(R.id.friendCellNameTV);
            removeBtn = itemView.findViewById(R.id.friendCellremoveBtn);
            distanceTV = itemView.findViewById(R.id.friendCellDistanceTV);

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    friendTabCallback.onRemoveBtnClicked(registerClass.getUserId(), friends.get(getAdapterPosition()).getUserId(), friends.get(getAdapterPosition()).getFullName());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendTabCallback.onFriendCellClicked(friends.get(getAdapterPosition()).getUserId());

                }
            });
        }

    }
}
