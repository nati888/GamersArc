package il.ac.hit.gamersarc;

import android.content.Context;
import android.net.Uri;
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
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindPeopleAdapter extends RecyclerView.Adapter<il.ac.hit.gamersarc.FindPeopleAdapter.UserViewHolder> {

    ArrayList<User> users;
    ArrayList<String> recentSentRequests;
    Context context;
    il.ac.hit.gamersarc.DataBaseClass dataBaseClass = il.ac.hit.gamersarc.DataBaseClass.getInstance();

    interface StrangerClickListener{
        void onStrangerClicked(String strangerId, boolean isRequested);
    }

    interface AddFriendBtnListener{
        void onSendFriendRequest(String strangerId);
        void onCancelFriendRequest(String strangerId);
    }

    private AddFriendBtnListener addFriendCallback;
    private StrangerClickListener strangerClickCallback;

    public void setAddFriendCallback(AddFriendBtnListener addFriendCallback) {
        this.addFriendCallback = addFriendCallback;
    }

    public void setStrangerClickCallback(StrangerClickListener strangerClickCallback) {
        this.strangerClickCallback = strangerClickCallback;
    }

    public FindPeopleAdapter(ArrayList<User> users, Context context, ArrayList<String> recentSentRequests) {
        this.users = users;
        this.context = context;
        this.recentSentRequests = recentSentRequests;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_people_cell, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        User user = users.get(position);

        int userAge = getAge(user.getYear(), user.getMonth(), user.getDayOfMonth());
        holder.userNameTV.setText(user.getFullName() + ", " + userAge);


        OnSuccessListener<Uri> listener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.placeholder_small).into(holder.userImageView);
            }
        };

        dataBaseClass.getImageUserId(user.getUserId(), listener);


        if (recentSentRequests.contains(user.getUserId())){
            holder.addFriendBtn.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }



    private int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = Integer.valueOf(age);


        return ageInt;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userImageView;
        TextView userNameTV;
        ToggleButton addFriendBtn;
        TextView requestSentTV;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.userImageRecyclerCell);
            userNameTV = itemView.findViewById(R.id.userNameTVRecyclerCell);
            addFriendBtn = itemView.findViewById(R.id.addFriendBtn);
            requestSentTV = itemView.findViewById(R.id.requestSentTV);

            addFriendBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    String strangerId = users.get(getAdapterPosition()).getUserId();
                    if (isChecked){//sent request
                        recentSentRequests.add(strangerId);
                        if (buttonView.isPressed()){
                            addFriendCallback.onSendFriendRequest(strangerId);
                        }
                        requestSentTV.setVisibility(View.VISIBLE);

                    }else {
                        recentSentRequests.remove(strangerId);
                        if (buttonView.isPressed()){
                            addFriendCallback.onCancelFriendRequest(strangerId);
                        }
                        requestSentTV.setVisibility(View.GONE);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //report to mainActivity
                    String strangerId = users.get(getAdapterPosition()).getUserId();
                    strangerClickCallback.onStrangerClicked(strangerId, addFriendBtn.isChecked());
                }
            });

        }
    }
}
