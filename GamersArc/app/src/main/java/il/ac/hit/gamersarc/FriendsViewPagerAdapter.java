package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FriendsViewPagerAdapter extends FragmentStateAdapter {

    public FriendsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new com.example.runtime.FriendsTabFragment();
        } else {
            return new FriendsRequestsTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
