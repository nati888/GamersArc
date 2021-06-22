package il.ac.hit.gamersarc;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment {

    private GoogleMap map;
    private double longitude;
    private double latitude;
    private Geocoder geocoder;
    private SearchView searchView;
    private UserInstance userInstance;
    private LatLng latLng;

    private String streetAddress;
    private TextView searchResult;

    private CreateEventVM viewModel;



    interface OnCreateEventListener{
        void onCreateEventFromMap(boolean isNew);

    }

    OnCreateEventListener createEventCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            createEventCallback = (com.example.runtime.MapFragment.OnCreateEventListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("Activity must implement OnCreateEventListener");
        }

         viewModel= new ViewModelProvider(getActivity()).get(CreateEventVM.class);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.map_activity,container,false);

        userInstance=UserInstance.getInstance();
        geocoder=new Geocoder(getContext());
        searchResult = root.findViewById(R.id.search_result);
        Button vBtn = root.findViewById(R.id.vBtn);
        Button focusBtn = root.findViewById(R.id.focus_btn);


        //location set by text
        searchView=root.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addresses = null;

                if(location != null || !location.equals("")){
                    try {
                        addresses=geocoder.getFromLocationName(location,1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(addresses.size()>0){
                        Address address = addresses.get(0);
                        streetAddress=address.getAddressLine(0);
                        latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        map.clear();
                        map.addMarker(new MarkerOptions().position(latLng).title(streetAddress).draggable(true));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        searchResult.setText(streetAddress);
                    }
                    else
                        Snackbar.make(getView(),R.string.type, Snackbar.LENGTH_LONG).show();
                }

                else
                    Snackbar.make(getView(),R.string.type, Snackbar.LENGTH_LONG).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SupportMapFragment mapFragment=(SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map=googleMap;
                longitude=userInstance.getUser().getLongitude();
                latitude=userInstance.getUser().getLatitude();

                    //current location of user
                    float zoomLevel = 16.0f;
                    latLng = new LatLng(latitude, longitude);
                    try {
                        List<Address>addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                        if(addresses.size()>0){
                            Address address=addresses.get(0);
                            streetAddress=address.getAddressLine(0);
                            map.clear();
                            map.addMarker(new MarkerOptions().position(latLng).title(streetAddress).draggable(true));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoomLevel));
                            searchResult.setText(streetAddress);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                //location drag by marker
                map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        latLng = marker.getPosition();
                        try {
                            List<Address>addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                            if(addresses.size()>0){
                                Address address=addresses.get(0);
                                streetAddress=address.getAddressLine(0);
                                marker.hideInfoWindow();
                                marker.setTitle(streetAddress);
                                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                searchResult.setText(streetAddress);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });

        vBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longitude = latLng.longitude;
                latitude = latLng.latitude;

                viewModel.setLongitudeLatitude(longitude,latitude);

                viewModel.setStreetAddress(streetAddress);
                createEventCallback.onCreateEventFromMap(false);

            }
        });

        focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }
        });

        return root;
    }
}
