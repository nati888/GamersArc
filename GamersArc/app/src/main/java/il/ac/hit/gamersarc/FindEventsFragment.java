package il.ac.hit.gamersarc;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import il.ac.hit.gamersarc.RunnersDialog;
import il.ac.hit.gamersarc.UserInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindEventsFragment extends Fragment implements FindEventsAdapter.OnUpcomingEventListener, SwipeRefreshLayout.OnRefreshListener {

    private FindEventsAdapter adapter;
    private ArrayList<il.ac.hit.gamersarc.Event> relevantEvents = new ArrayList<>();
    private ArrayList<String> myEvents = new ArrayList<>();
    private FindEventsVM viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(FindEventsVM.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.retrieveEventsList();
        viewModel.setMyEvents();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.find_events_fragment,container,false);

        RecyclerView recyclerView = root.findViewById(R.id.findEventsRecycler);
        adapter = new FindEventsAdapter(relevantEvents,getContext(),myEvents);
        recyclerView.setAdapter(adapter);
        adapter.setUpcomingEventCallback(this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshFindEvents);
        swipeRefreshLayout.setOnRefreshListener(this);

        viewModel.getSwipeLayoutEventBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        viewModel.getRelevantEvents().observe(getViewLifecycleOwner(), new Observer<ArrayList<il.ac.hit.gamersarc.Event>>() {
            @Override
            public void onChanged(ArrayList<il.ac.hit.gamersarc.Event> events) {
                relevantEvents.clear();
                relevantEvents.addAll(events);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel.getMyEvents().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> usersEventsIds) {
                myEvents.clear();
                myEvents.addAll(usersEventsIds);
                adapter.notifyDataSetChanged();
            }
        });

        TextView locationTv = root.findViewById(R.id.locationTVRecycler);
        UserInstance userInstance = UserInstance.getInstance();
        double longitude = userInstance.getUser().getLongitude();
        double latitude = userInstance.getUser().getLatitude();
        String streetAddress= geocodeFunction(longitude,latitude);
        locationTv.setText(streetAddress);


        return root;
    }

    public String geocodeFunction(double longitude, double latitude) {
        Geocoder geocoder=new Geocoder(getContext());
        String streetAddress;
        try {
            List<Address>addresses = geocoder.getFromLocation(latitude,longitude,1);
            if(addresses.size()>0){
                Address address=addresses.get(0);
                streetAddress=address.getAddressLine(0);
                return streetAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";


    }

    @Override
    public void onJoinEvent(String eventId, String userId) {
        viewModel.onJoinEvent(eventId,userId);
    }

    @Override
    public void onCancelJoinEvent(String eventId, String userId) {
        viewModel.onCancelJoinEvent(eventId,userId);
    }

    @Override
    public void onSeeMembersClick(String eventId) {
        FragmentManager fm = getFragmentManager();
        RunnersDialog editNameDialogFragment = RunnersDialog.newInstance(eventId);

        assert fm != null;
        editNameDialogFragment.show(fm, "fragment_runners");
    }

    @Override
    public void onRefresh() {
        viewModel.retrieveEventsList();
    }
}
