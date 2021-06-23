package il.ac.hit.gamersarc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FriendsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.friends_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        il.ac.hit.gamersarc.FriendsViewPagerAdapter adapter = new il.ac.hit.gamersarc.FriendsViewPagerAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.friendsViewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.friendsTabLayout);

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0){
                            tab.setText(R.string.friends);
                        } else {
                            tab.setText(R.string.requests);
                        }

                    }
                }
        ).attach();


    }
}
