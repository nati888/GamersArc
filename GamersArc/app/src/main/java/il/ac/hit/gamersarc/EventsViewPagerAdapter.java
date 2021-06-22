package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventsViewPagerAdapter extends FragmentStateAdapter {


    public EventsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new UpcomingTabFragment();
        } else if (position == 1){
            return new InvitationsTabFragment();
        } else{
            return new ManagedTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
