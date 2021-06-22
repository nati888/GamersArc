package il.ac.hit.gamersarc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class StrangerFragment extends Fragment {

    private StrangerVM viewModel;
    private UserInstance userInstance = UserInstance.getInstance();
    private String currentStrangerId;
    private boolean isRequested;

    public static com.example.runtime.StrangerFragment newInstance(String userId, boolean isRequested){
        com.example.runtime.StrangerFragment strangerFragment = new com.example.runtime.StrangerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putBoolean("isRequested", isRequested);
        strangerFragment.setArguments(bundle);
        return strangerFragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(StrangerVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.stranger_fragment, container, false);
        final CircleImageView strangerProfileImageView = root.findViewById(R.id.strangerProfileImageView);
        final TextView strangerNameTV = root.findViewById(R.id.strangerNameTV);
        final TextView strangerLevelTV = root.findViewById(R.id.strangerLevelTV);
        final TextView strangerDistance = root.findViewById(R.id.strangerDistanceTV);
        final TextView sentRequestTV = root.findViewById(R.id.requestSentStrangerFragmentTV);
        ToggleButton addStrangerBtn = root.findViewById(R.id.addStrangerToFriendsBtn);

        if (getArguments()!= null){
            currentStrangerId = getArguments().getString("userId");
            isRequested = getArguments().getBoolean("isRequested");

            viewModel.fetchStrangerUser(currentStrangerId);
        }

        addStrangerBtn.setChecked(isRequested);
        if(addStrangerBtn.isChecked()){
            sentRequestTV.setVisibility(View.VISIBLE);
        }


        viewModel.getStrangerUserForDisplay().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                int age = getAge(user.getYear(), user.getMonth(), user.getDayOfMonth());
                double distance =  haversine(user.getLatitude(), user.getLongitude(), userInstance.getUser().getLatitude(), userInstance.getUser().getLongitude());
                double distanceInMeters= distance * 1000;
                strangerNameTV.setText(user.getFullName() + ", " + age );
                strangerLevelTV.setText(user.getRunningLevel());
                strangerDistance.setText(user.getFullName() + " is " + distanceInMeters + "meters away");
                StorageReference profileImageRef = viewModel.getStrangerProfileImageReference(user.getUserId());
                Glide.with(Objects.requireNonNull(getContext())).load(profileImageRef).into(strangerProfileImageView);

            }
        });

        addStrangerBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    viewModel.onSendFriendRequest(currentStrangerId);
                    isRequested = isChecked;
                    sentRequestTV.setVisibility(View.VISIBLE);

                }else{
                    viewModel.onCancelFriendRequest(currentStrangerId);
                    isRequested = isChecked;
                    sentRequestTV.setVisibility(View.GONE);
                }
            }
        });

        return root;
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
