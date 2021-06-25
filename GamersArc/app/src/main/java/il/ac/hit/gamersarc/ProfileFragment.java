package il.ac.hit.gamersarc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private il.ac.hit.gamersarc.ProfileVM profileVM;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        profileVM= new ViewModelProvider(getActivity()).get(il.ac.hit.gamersarc.ProfileVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        profileVM.setNewUser();
        View root = inflater.inflate(R.layout.profile_fragment, container,false);
        final CircleImageView circleImageView = root.findViewById(R.id.profile_image);
        profileVM.getImageFromData();
        final Context context = getActivity();
        Observer<Uri> resultObserverImage = new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Glide.with(context).load(uri).into(circleImageView);
            }
        };


        profileVM.getImageLivedata().observe(getViewLifecycleOwner() , resultObserverImage);

        final TextView textViewLocation = root.findViewById(R.id.locationProfileTV);
        //final TextView textViewAge = root.findViewById(R.id.ageProfileTV);
        final TextView textViewName = root.findViewById(R.id.fullNameProfileTV);
        final TextView textViewLevel = root.findViewById(R.id.levelProfileTV);
        final TextView textViewGender = root.findViewById(R.id.genderProfile);
        final ImageView imageViewLevel = root.findViewById(R.id.imageProfileLevel);
        Observer<User> observerUser = new Observer<User>() {
            @Override
            public void onChanged(User user) {


                switch (user.getRunningLevel()){
                    case "easy" :
                        textViewLevel.setText(R.string.easy);
                        imageViewLevel.setImageResource(R.drawable.easy_orange);
                        break;
                    case "medium" :
                        textViewLevel.setText(R.string.medium);
                        imageViewLevel.setImageResource(R.drawable.medium_orange);
                        break;
                    case "expert" :
                        textViewLevel.setText(R.string.expert);
                        imageViewLevel.setImageResource(R.drawable.hard_orange);
                        break;
                }
                //textViewAge.setText(profileVM.getAge(user.getYear(),user.getMonth(),user.getDayOfMonth())+"");
                //textViewLevel.setText(user.getRunningLevel());
                textViewLocation.setText(profileVM.getAddress(context,user.getLatitude(),user.getLongitude()));
                textViewName.setText(user.getFullName()+", " + profileVM.getAge(user.getYear(),user.getMonth(),user.getDayOfMonth()));
                textViewGender.setText(user.getGender());
            }
        };

        profileVM.getLiveDataUser().observe(getViewLifecycleOwner(),observerUser);





        return root;
    }
}
