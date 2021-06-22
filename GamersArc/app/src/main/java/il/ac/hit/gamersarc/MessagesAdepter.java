package il.ac.hit.gamersarc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import il.ac.hit.gamersarc.Model.UserWithLastMessage;

public class MessagesAdepter extends RecyclerView.Adapter<com.example.runtime.MessagesAdepter.MessagesViewHolder> {
    private ArrayList<UserWithLastMessage> friend = new ArrayList<>();
    private Context context;
    private DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    private MessagesLitener messagesLitener;

    interface MessagesLitener{
        void onClickedMessages(User user,View view);
    }

    public MessagesAdepter(ArrayList<UserWithLastMessage> friend, Context context) {
        this.friend = friend;
        this.context = context;

    }

    public void setMessagesLitener(MessagesLitener messagesLitener) {
        this.messagesLitener = messagesLitener;
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder{
    private CircleImageView image;
    private TextView textViewName;
    private TextView textViewLastMessage;
    private TextView textViewLastMessageTime;



        public MessagesViewHolder(@NonNull final View itemView) {
        super(itemView);
        this.image =itemView.findViewById(R.id.cardImage);
        this.textViewName = itemView.findViewById(R.id.cardName);
        this.textViewLastMessage = itemView.findViewById(R.id.cardLastMessage);
        this.textViewLastMessageTime = itemView.findViewById(R.id.cardLastMessageTime);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messagesLitener!=null)
                    messagesLitener.onClickedMessages(friend.get(getAdapterPosition()).getUser(),itemView);
            }
        });

    }
}

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessagesViewHolder messagesViewHolder;
        if(viewType==1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_messages, parent, false);
            messagesViewHolder = new MessagesViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_messages_with_new_message, parent, false);
            messagesViewHolder = new MessagesViewHolder(view);
        }
        return messagesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position) {
        final float density = context.getResources().getDisplayMetrics().density;
        User user=friend.get(position).getUser();
        OnSuccessListener onSuccessListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Glide.with(context).load(o).override((int)(120*density),(int)(120*density)).into(holder.image);
            }
        };
        dataBaseClass.getImageUserId(user.getUserId(),onSuccessListener);
        holder.textViewName.setText(user.getFullName());
        holder.textViewLastMessage.setText(friend.get(position).getMessage().getContent());
        holder.textViewLastMessageTime.setText(friend.get(position).getMessage().getTime());
    }

    @Override
    public int getItemViewType(int position) {
        if(friend.get(position).isNew())
            return 0;
        else
            return 1;
    }

    @Override
    public int getItemCount() {
        return friend.size();
    }
}
