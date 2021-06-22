package il.ac.hit.gamersarc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavBarFragment extends Fragment {



    interface OnNavigationListener{
        void onNavChange(String page);
    }
    private OnNavigationListener callBack;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callBack = (OnNavigationListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("Activity must implement OnNextListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.navigtion_bar,container ,false);
        BottomNavigationView bottomNavigationView = root.findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nevHome:
                        callBack.onNavChange("home");
                        break;
                    case R.id.nevGroup:
                        callBack.onNavChange("group");
                        break;
                    case R.id.nevLocation:
                        callBack.onNavChange("location");
                        break;
                    case R.id.nevProfile:
                        callBack.onNavChange("profile");
                        break;
                    case R.id.nevMessage:
                        callBack.onNavChange("message");
                        break;
                }

                return true;
            }
        });
        return root;
    }
}
