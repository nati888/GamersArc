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

public class FriendsTabFragment extends Fragment implements FriendsTabAdapter.FriendTabListener, SwipeRefreshLayout.OnRefreshListener {

    private FriendsTabVM viewModel;
    private ArrayList<User> friends = new ArrayList<>();
    private FriendsTabAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel= new ViewModelProvider(getActivity()).get(FriendsTabVM.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView friendTabRecycler = view.findViewById(R.id.friendsRecycler);
        adapter = new FriendsTabAdapter(friends, getContext());
        adapter.setFriendTabCallback(this);
        friendTabRecycler.setAdapter(adapter);
        friendTabRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        friendTabRecycler.setLayoutManager(manager);



        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshFriendsTab);
        swipeRefreshLayout.setOnRefreshListener(this);

        viewModel.getFriendsIds();

        viewModel.getSwipeLayoutBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swipeRefreshLayout.setRefreshing(true);
            }
        });


        viewModel.getFriendsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {

                friends.clear();
                friends.addAll(users);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    @Override
    public void onRemoveBtnClicked(final String userId, final String friendId, final String friendName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String remove = getString(R.string.is_sure_delete_friend);
        String fromFriendList = getString(R.string.from_friend_list);


        AlertDialog friendDeleteDialog = builder.setMessage(remove + " " + friendName + " " + fromFriendList)
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.removeFriend(userId,friendId);
                        Snackbar.make(swipeRefreshLayout, R.string.friend_successfuly_removed, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false).show();
        friendDeleteDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#f56a45"));
        friendDeleteDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4292ac"));
    }

    @Override
    public void onFriendCellClicked(String friendId) {
        FragmentManager fm = getFragmentManager();
        FriendDialog dialog = FriendDialog.newInstance(friendId);
        assert fm != null;
        dialog.show(fm, "friendDialog");
    }

    @Override
    public void onRefresh() {

        viewModel.getFriendsIds();
    }
}
