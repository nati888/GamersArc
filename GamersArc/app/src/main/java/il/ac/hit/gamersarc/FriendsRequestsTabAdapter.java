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

import org.json.JSONException;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRequestsTabAdapter extends RecyclerView.Adapter<com.example.runtime.FriendsRequestsTabAdapter.FriendRequestViewHolder> {

    private ArrayList<User> friendsRequests = new ArrayList<>();
    private Context context;
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private RegisterClass registerClass = RegisterClass.getInstance();
    UserInstance userInstance = UserInstance.getInstance();

    interface OnRequestResponse{
        void onRequestAccepted(String userId, String newFriendId, String fullName, String newFriendToken) throws JSONException;
        void onRequestRemoved(String userId, String strangerId);
    }

    private OnRequestResponse requestCallback;

    public void setRequestCallback(OnRequestResponse requestCallback) {
        this.requestCallback = requestCallback;
    }

    public FriendsRequestsTabAdapter(ArrayList<User> friendsRequests, Context context) {
        this.friendsRequests = friendsRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_cell, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendRequestViewHolder holder, int position) {
        holder.friendRequestTextView.setText(friendsRequests.get(position).getFullName());

        OnSuccessListener<Uri> listener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.placeholder_small).into(holder.friendRequestImageView);
            }
        };

        dataBaseClass.getImageUserId(friendsRequests.get(position).getUserId(), listener);
    }

    @Override
    public int getItemCount() {
        return friendsRequests.size();
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder{

        CircleImageView friendRequestImageView;
        TextView friendRequestTextView;
        Button allowBtn;
        Button removeBtn;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            friendRequestImageView = itemView.findViewById(R.id.friendRequestCellImageView);
            friendRequestTextView = itemView.findViewById(R.id.friendRequestCellNameTV);
            allowBtn = itemView.findViewById(R.id.friendRequestCellAllowBtn);
            removeBtn = itemView.findViewById(R.id.friendRequestCellRemoveBtn);

            allowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        requestCallback.onRequestAccepted(registerClass.getUserId(), friendsRequests.get(getAdapterPosition()).getUserId(), userInstance.getUser().getFullName(), friendsRequests.get(getAdapterPosition()).getUserToken());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestCallback.onRequestRemoved(registerClass.getUserId(), friendsRequests.get(getAdapterPosition()).getUserId());
                }
            });

        }
    }
}
