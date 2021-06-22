package il.ac.hit.gamersarc;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class UpcomingTabFragment extends Fragment implements UpcomingTabAdapter.UpcomingListener, SwipeRefreshLayout.OnRefreshListener {

    private UpcomingTabVM viewModel;
    private ArrayList<Event> upcomingEvents = new ArrayList<>();
    private UpcomingTabAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel= new ViewModelProvider(getActivity()).get(UpcomingTabVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.upcoming_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView upcomingRecycler = view.findViewById(R.id.upcomingRecycler);
        upcomingRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        upcomingRecycler.setLayoutManager(manager);
        adapter = new UpcomingTabAdapter(upcomingEvents, this.getContext());
        adapter.setUpcomingCallback(this);
        upcomingRecycler.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshUpcomingTab);
        swipeRefreshLayout.setOnRefreshListener(this);


        viewModel.getUpcomingEventsIds();


        viewModel.getSwipeLayoutBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        viewModel.getUpcomingEventsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
                upcomingEvents.clear();
                upcomingEvents.addAll(events);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @Override
    public void onUpcomingRemoved(final String userId, final String eventId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String cancel = getString(R.string.is_sure_cancel_participants);


        AlertDialog friendDeleteDialog = builder.setMessage(cancel)
                .setPositiveButton(R.string.cancel_participation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        viewModel.RemoveUpcomingEvent(userId, eventId);
                        Snackbar.make(swipeRefreshLayout, R.string.participation_removed, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.do_not_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false).show();
        friendDeleteDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#f56a45"));
        friendDeleteDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4292ac"));

    }

    @Override
    public void onRunnersClicked(String eventId) {
        //openDialog
        FragmentManager fm = getFragmentManager();
        RunnersDialog editNameDialogFragment = RunnersDialog.newInstance(eventId);

        assert fm != null;
        editNameDialogFragment.show(fm, "fragment_runners");
    }

    @Override
    public void onRefresh() {

        viewModel.getUpcomingEventsIds();
    }
}
