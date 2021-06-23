package il.ac.hit.gamersarc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InviteFriendsDialog extends DialogFragment implements InviteFriendsDialogAdapter.InviteFriendToEventListener{

    private InviteFriendsDialogVM viewModel;
    private InviteFriendsDialogAdapter adapter;
    private ArrayList<User> myFriends = new ArrayList<>();
    private ArrayList<String> invitedFriendsIds = new ArrayList<>();

    public interface PassInvitedFriendsIdsToParentListener {
        void onFinishEditDialog(ArrayList<String> invitedFriendsIds);
    }

    public static il.ac.hit.gamersarc.InviteFriendsDialog getInstance(ArrayList<String> invitedFriends){
        il.ac.hit.gamersarc.InviteFriendsDialog dialog = new il.ac.hit.gamersarc.InviteFriendsDialog();
        dialog.invitedFriendsIds.addAll(invitedFriends);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(InviteFriendsDialogVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.invite_friends_dialog, container);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setMyFriendsIds();
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat_Dialog_MinWidth);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button doneBtn = view.findViewById(R.id.doneBtnFriendDialog);
        final RelativeLayout doneLayout = view.findViewById(R.id.done_Layout);
        final RelativeLayout noFriendsLayout = view.findViewById(R.id.no_friend_layout);
        ImageView cancelBtn = view.findViewById(R.id.cancelBtnFriendDialog);
        final RecyclerView inviteFriendsRecycler = view.findViewById(R.id.myFriendsRecycler);
        adapter = new InviteFriendsDialogAdapter(myFriends,getContext(), invitedFriendsIds);
        inviteFriendsRecycler.setAdapter(adapter);
        adapter.setInviteFriendToEventCallback(this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        inviteFriendsRecycler.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(inviteFriendsRecycler.getContext(),
                ((LinearLayoutManager) manager).getOrientation());
        inviteFriendsRecycler.addItemDecoration(dividerItemDecoration);

        viewModel.getMyFriends().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                myFriends.clear();
                myFriends.addAll(users);
                adapter.notifyDataSetChanged();
                if(myFriends.size() < 1){
                    noFriendsLayout.setVisibility(View.VISIBLE);
                    doneLayout.setVisibility(View.GONE);
                   inviteFriendsRecycler.setVisibility(View.GONE);
                }
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassInvitedFriendsIdsToParentListener listener = (PassInvitedFriendsIdsToParentListener) getTargetFragment();
                listener.onFinishEditDialog(invitedFriendsIds);
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    public InviteFriendsDialog() {
    }

    @Override
    public void onInviteFriendRequests(String invitedFriendId) {
        invitedFriendsIds.add(invitedFriendId);
    }

    @Override
    public void onCancelInviteFriendRequests(String invitedFriendId) {
        invitedFriendsIds.remove(invitedFriendId);
    }

}
