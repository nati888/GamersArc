package il.ac.hit.gamersarc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

public class InvitationsTabFragment extends Fragment implements il.ac.hit.gamersarc.InvitationsTabAdapter.OnInvitationResponse, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<il.ac.hit.gamersarc.Event> invitations = new ArrayList<>();
    private il.ac.hit.gamersarc.InvitationsTabVM viewModel;
    private il.ac.hit.gamersarc.InvitationsTabAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel= new ViewModelProvider(getActivity()).get(il.ac.hit.gamersarc.InvitationsTabVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.invitation_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView invitationsRecycler = view.findViewById(R.id.invitationsRecycler);
        invitationsRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        invitationsRecycler.setLayoutManager(manager);
        adapter = new il.ac.hit.gamersarc.InvitationsTabAdapter(invitations, this.getContext());
        adapter.setInvitationCallback(this);
        invitationsRecycler.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshInvitationsTab);
        swipeRefreshLayout.setOnRefreshListener(this);

        viewModel.getInvitationsIds();

        viewModel.getSwipeLayoutBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swipeRefreshLayout.setRefreshing(true);
            }
        });


        viewModel.getInvitationsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<il.ac.hit.gamersarc.Event>>() {
            @Override
            public void onChanged(ArrayList<il.ac.hit.gamersarc.Event> events) {
                invitations.clear();
                invitations.addAll(events);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }


    @Override
    public void onJoinToEvent(String userId, String eventId, int position) {

        viewModel.onJoinToEvent(userId, eventId);
    }

    @Override
    public void onRemoveEvent(String userId, String eventId, int position) {

        viewModel.onRemoveEvent(userId, eventId);
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

        viewModel.getInvitationsIds();
    }
}
