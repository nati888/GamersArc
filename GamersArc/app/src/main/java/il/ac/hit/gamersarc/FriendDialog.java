package il.ac.hit.gamersarc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendDialog extends DialogFragment {

    private com.example.runtime.FriendDialogVM viewModel;
    private ArrayList<User> mutualFriends = new ArrayList<>();
    private com.example.runtime.FriendDialogAdapter adapter;

    public FriendDialog() {
    }

    public static com.example.runtime.FriendDialog newInstance(String friendId) {
        com.example.runtime.FriendDialog dialog = new com.example.runtime.FriendDialog();
        Bundle args = new Bundle();
        args.putString("friendId", friendId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        String friendId;
        assert getArguments() != null;
        friendId = getArguments().getString("friendId");

        viewModel = new FriendModelFactory(friendId).create(com.example.runtime.FriendDialogVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friend_dialog, container);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat_Dialog_MinWidth);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView friendName = view.findViewById(R.id.friendDialogNameTV);
        final TextView friendGender = view.findViewById(R.id.friendDialogGenderTV);
        final TextView friendRunningLevel = view.findViewById(R.id.friendDialogRunningLevel);
        final ImageView friendRunningLevelImageView = view.findViewById(R.id.friendDialogRunningLevelImageView);
        final TextView friendLocationTV = view.findViewById(R.id.friendDialogLocationTV);
        final CircleImageView friendImageView = view.findViewById(R.id.friendDialogImageView);
        ImageView dismissBtn = view.findViewById(R.id.friendDialogDismiss);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        RecyclerView friendDialogRecycler = view.findViewById(R.id.friendDialogRecycler);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this.getContext(), 3);
        friendDialogRecycler.setLayoutManager(manager);
        friendDialogRecycler.setHasFixedSize(true);
        adapter = new com.example.runtime.FriendDialogAdapter(mutualFriends, this.getContext());
        friendDialogRecycler.setAdapter(adapter);


        viewModel.getNameLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                friendName.setText(s);
            }
        });

        viewModel.getGenderLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("female")){
                    friendGender.setText(R.string.female);
                }else if (s.equals("male")) {
                    friendGender.setText(R.string.male);
                }

            }
        });

        viewModel.getRunningLevelLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "easy":
                        friendRunningLevel.setText(R.string.easy);
                        friendRunningLevelImageView.setImageResource(R.drawable.easy_orange);
                        break;
                    case "medium":
                        friendRunningLevel.setText(R.string.medium);
                        friendRunningLevelImageView.setImageResource(R.drawable.medium_orange);
                        break;
                    case "expert":
                        friendRunningLevel.setText(R.string.expert);
                        friendRunningLevelImageView.setImageResource(R.drawable.hard_orange);
                        break;
                }

            }
        });

        viewModel.getAddressLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                friendLocationTV.setText(s);
            }
        });



        viewModel.getImageUriLiveData().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Glide.with(Objects.requireNonNull(getContext())).load(uri).placeholder(R.drawable.placeholder_small).into(friendImageView);
            }
        });

        viewModel.getMutualFriendsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                mutualFriends.clear();
                mutualFriends.addAll(users);
                adapter.notifyDataSetChanged();

            }
        });

    }


}
