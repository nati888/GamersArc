package il.ac.hit.gamersarc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


public class HomeFragment extends Fragment implements UserInstance.OnGetUserListener {

    private il.ac.hit.gamersarc.HomeVM viewModel;
    private UserInstance user;
    private TextView title;
    private TextView locationtext;
    private TextView areYouComing;
    private ProgressBar progressBar;
    private  Button buttonNewEvent;
    private Button findPeopleButton;
    private Button findEventsBtn;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel= new ViewModelProvider(getActivity()).get(il.ac.hit.gamersarc.HomeVM.class);
        viewModel.setContext(getContext());

    }

    @Override
    public void onGetUser() {

        String hello = getString(R.string.hello);
        title.setText(hello+ " " +user.getUser().getFullName()+"!");

        if(user.getUser().getLongitude()!=0||user.getUser().getLatitude()!=0)
            locationtext.setText(viewModel.getAddress(getContext(),user.getUser().getLatitude(),user.getUser().getLongitude()));
        progressBar.setVisibility(View.GONE);
        buttonNewEvent.setVisibility(View.VISIBLE);
        findEventsBtn.setVisibility(View.VISIBLE);
        findPeopleButton.setVisibility(View.VISIBLE);
        areYouComing.setVisibility(View.VISIBLE);
        locationtext.setVisibility(View.VISIBLE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate( R.layout.home_fragment,container,false);
        user = UserInstance.getInstance();
        user.getUserFromDataBase();
        user.setCallBackUserGet(this);
        title = root.findViewById(R.id.helloLabelMain);
        areYouComing = root.findViewById(R.id.areYouComingTV);
        progressBar = root.findViewById(R.id.homeFragmentProgressBar);
        buttonNewEvent = root.findViewById(R.id.createNewEventBT);
        findPeopleButton = root.findViewById(R.id.findPeopleBtn);
        findEventsBtn = root.findViewById(R.id.findUpcomingEventsBT);
        locationtext = root.findViewById(R.id.locationText);
        if(user.getUser().getFullName()!=null){

            String hello = getString(R.string.hello);
            title.setText(hello+ " " +user.getUser().getFullName()+"!");
            progressBar.setVisibility(View.GONE);
            buttonNewEvent.setVisibility(View.VISIBLE);
            findEventsBtn.setVisibility(View.VISIBLE);
            findPeopleButton.setVisibility(View.VISIBLE);
            areYouComing.setVisibility(View.VISIBLE);
            locationtext.setVisibility(View.VISIBLE);

        }


        Observer<String> observerLocation = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                locationtext.setText(s);
            }
        };

        viewModel.getLocation().observe(getViewLifecycleOwner(),observerLocation);




        buttonNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = il.ac.hit.gamersarc.CreateEventFragment.getCreateEventFragment(true);
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.rootLayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });


        findPeopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new il.ac.hit.gamersarc.FindPeopleFragment();
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.rootLayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        findEventsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new il.ac.hit.gamersarc.FindEventsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.rootLayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return root;


    }
}
