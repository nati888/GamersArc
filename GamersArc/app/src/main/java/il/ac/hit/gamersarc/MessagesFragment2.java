package il.ac.hit.gamersarc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.runtime.model.Message;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesFragment2 extends Fragment {

    Messages2VM messagesVM;
    private Messages2Adapter adapter;
    BroadcastReceiver receiver;
    ArrayList<Message> messageArrayList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        messagesVM = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(Messages2VM.class);
    }

    public static com.example.runtime.MessagesFragment2 newInstance(User user) {
        com.example.runtime.MessagesFragment2 messagesFragment2 = new com.example.runtime.MessagesFragment2();
        Bundle bundle = new Bundle();
        bundle.putString("id", user.getUserId());
        bundle.putString("name", user.getFullName());
        bundle.putString("token",user.getUserToken());
        messagesFragment2.setArguments(bundle);
        return messagesFragment2;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.messages_fragment2, container, false);
        messagesVM.setActiveConversationFriendId(getArguments().getString("id"));
        messagesVM.getAllMessages();
        final CircleImageView circleImageView = root.findViewById(R.id.messagesImage);
        final TextView textViewName = root.findViewById(R.id.messagesTVName);
        final ImageButton buttonSend = root.findViewById(R.id.sendBTN);
        final EditText editTextMessage = root.findViewById(R.id.messageET);
        final RecyclerView rvMessage = root.findViewById(R.id.messages);
        messagesVM.setName(getArguments().getString("name"));
        messagesVM.setImage(getArguments().getString("id"));

        messagesVM.getLiveDataFullName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewName.setText(s);
            }
        });

        messagesVM.getLiveDataImage().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri storageReference) {
                Glide.with(requireContext()).load(storageReference).into(circleImageView);
            }
        });


        adapter = new Messages2Adapter(getContext(),messageArrayList);
        rvMessage.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getContext());
        rvMessage.setLayoutManager(linearLayoutManager);
        rvMessage.setAdapter(adapter);

        if(editTextMessage.getText().toString().equals("")) {
            buttonSend.setEnabled(false);
            buttonSend.setImageResource(R.drawable.ic_send_gray_24dp);
        }

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editTextMessage.getText().toString().equals("")) {
                    buttonSend.setEnabled(false);
                    buttonSend.setImageResource(R.drawable.ic_send_gray_24dp);
                }
                else {
                    buttonSend.setEnabled(true);
                    buttonSend.setImageResource(R.drawable.ic_send_orange_24dp);
                }
            }
        });

        messagesVM.getMessagesLiveData(getArguments().getString("id")).observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                messageArrayList.clear();
                messageArrayList.addAll(messages);
                adapter.notifyDataSetChanged();
                linearLayoutManager.smoothScrollToPosition(rvMessage, null, messages.size());
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    messagesVM.sendMessage(editTextMessage.getText().toString(),getArguments().getString("token"),UserInstance.getInstance().getUser().getFullName());
                    editTextMessage.getText().clear();
                    buttonSend.setEnabled(false);
                    buttonSend.setImageResource(R.drawable.ic_send_gray_24dp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FriendDialog dialog = FriendDialog.newInstance(getArguments().getString("id"));
                assert fm != null;
                dialog.show(fm, "friendDialog");
            }
        });


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                messagesVM.addToList(intent.getStringExtra("message"));



                messagesVM.saveIfOpen(false,getArguments().getString("id"));

            }
        };
        IntentFilter filter = new IntentFilter("messagesReceiver");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}
