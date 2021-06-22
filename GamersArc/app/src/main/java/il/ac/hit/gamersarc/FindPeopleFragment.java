package il.ac.hit.gamersarc;

import android.content.Context;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

public class FindPeopleFragment extends Fragment implements FindPeopleAdapter.AddFriendBtnListener, FindPeopleAdapter.StrangerClickListener, SwipeRefreshLayout.OnRefreshListener {

    private com.example.runtime.FindPeopleVM viewModel;
    private ArrayList<User> relevantUsers = new ArrayList<>();
    private FindPeopleAdapter adapter;
    private ArrayList<String> recentSentRequests = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(com.example.runtime.FindPeopleVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.find_people_fragment, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.findPeopleRecycler);
        final TextView locationTV = root.findViewById(R.id.findPeopleLocationTV);//geocode address




        adapter = new FindPeopleAdapter(relevantUsers, getContext(), recentSentRequests);
        recyclerView.setAdapter(adapter);
        adapter.setAddFriendCallback(this);
        adapter.setStrangerClickCallback(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(manager);

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshFindPeople);
        swipeRefreshLayout.setOnRefreshListener(this);

        viewModel.getAddressLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                locationTV.setText(s);
            }
        });


        viewModel.getSwipeLayoutBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swipeRefreshLayout.setRefreshing(true);
            }
        });


        viewModel.getRelevantUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {

                relevantUsers.clear();
                relevantUsers.addAll(users);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getSentRequests();
        viewModel.getRecentSentRequests().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {

                recentSentRequests.clear();
                recentSentRequests.addAll(strings);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onSendFriendRequest(String strangerId) {
       viewModel.onSendFriendRequest(strangerId);
    }

    @Override
    public void onCancelFriendRequest(String strangerId) {
        viewModel.onCancelFriendRequest(strangerId);
    }

    @Override
    public void onStrangerClicked(String strangerId, boolean isRequested) {

        FragmentManager fm = getFragmentManager();
        com.example.runtime.FriendDialog dialog = com.example.runtime.FriendDialog.newInstance(strangerId);
        assert fm != null;
        dialog.show(fm, "friendDialog");
    }

    @Override
    public void onRefresh() {

       viewModel.retrieveUsersList();
       viewModel.getSentRequests();
    }
}
