package il.ac.hit.gamersarc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Collections;

import il.ac.hit.gamersarc.model.UserWithLastMessage;

public class MessagesFragment extends Fragment {

    private MessagesVM messagesVM;
    private ArrayList<UserWithLastMessage> friends = new ArrayList<>();
    private MessagesAdepter adapter;
    BroadcastReceiver receiver;

    interface OnClickOnMessages{
        void onClickMessages(User user);
    }
    private OnClickOnMessages onClickOnMessages;

    @Override
    public void onAttach(@NonNull Context context) {
        messagesVM = new ViewModelProvider(getActivity()).get(MessagesVM.class);
        try {
            onClickOnMessages = (il.ac.hit.gamersarc.MessagesFragment.OnClickOnMessages) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("Activity must implement OnClickMessages");
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.messages_fragment,container,false);
        messagesVM.getFriendsId();
        RecyclerView recyclerView = root.findViewById(R.id.recycleMessages);

        adapter = new MessagesAdepter(friends,getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        messagesVM.getFriends().observe(getViewLifecycleOwner(), new Observer<ArrayList<UserWithLastMessage>>() {
            @Override
            public void onChanged(ArrayList<UserWithLastMessage> users) {
                friends.clear();
                friends.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setMessagesLitener(new MessagesAdepter.MessagesLitener() {
            @Override
            public void onClickedMessages(User user, View view) {
                onClickOnMessages.onClickMessages(user);
                messagesVM.saveIfOpen(false,user.getUserId());
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int i = 0 ,k = 0;
                for (UserWithLastMessage user:friends) {
                    if(intent.getStringExtra("userId").equals(user.getUser().getUserId())){
                        user.getMessage().setContent(intent.getStringExtra("message"));
                        user.getMessage().setTime(intent.getStringExtra("time"));
                        user.setNew(true);
                        messagesVM.saveIfOpen(true,user.getUser().getUserId());
                        k=i;
                    }
                    i++;
                }
                for (int j = k; j > 0 ; j--) {
                    Collections.swap(friends,j,j-1);
                }
                adapter.notifyDataSetChanged();
            }
        };
        IntentFilter filter = new IntentFilter("messagesReceiver");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        return root;
    }
}
