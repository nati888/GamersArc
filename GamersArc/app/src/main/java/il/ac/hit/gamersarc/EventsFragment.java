package il.ac.hit.gamersarc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.runtime.EventsViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 eventsViewPager = view.findViewById(R.id.eventsViewPager);
        TabLayout eventsTabLayout = view.findViewById(R.id.eventsTabLayout);
        com.example.runtime.EventsViewPagerAdapter adapter = new com.example.runtime.EventsViewPagerAdapter(this);
        eventsViewPager.setAdapter(adapter);

        new TabLayoutMediator(eventsTabLayout, eventsViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               if (position == 0){
                   tab.setText(R.string.upcoming);
               } else if (position == 1){
                   tab.setText(R.string.invitations);
               }else {
                   tab.setText(R.string.managed);
               }
            }
        }).attach();

    }
}
