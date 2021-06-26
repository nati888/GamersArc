package il.ac.hit.gamersarc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import il.ac.hit.gamersarc.model.Message;

import java.util.ArrayList;

//TODO choose message
//TODO forward
//TODO delete message
public class Messages2Adapter extends RecyclerView.Adapter<il.ac.hit.gamersarc.Messages2Adapter.Message2ViewHolder> {
    Context context;
    ArrayList<Message> list;
    public Messages2Adapter(Context context,ArrayList<Message> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public Message2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            return new Message2ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_item_right, parent, false);
            return new Message2ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(Message2ViewHolder holder, int position) {
        Message msg = list.get(position);

        if(msg.getUserIdSent().equals(UserInstance.getInstance().getUser().getUserId())) {

            holder.myMessage.setText(msg.getContent());
            holder.myTime.setText(msg.getTime());
        }else {
            holder.message.setText(msg.getContent());
            holder.time.setText(msg.getTime());
    }
    }

    class Message2ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView myMessage;
        TextView time;
        TextView myTime;

        Message2ViewHolder(@NonNull View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.messageItemTVRight);
            myTime = itemView.findViewById(R.id.messageTimeTVRight);
            message = itemView.findViewById(R.id.messageItemTV);
            time = itemView.findViewById(R.id.messageTimeTV);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getUserIdSent().equals(UserInstance.getInstance().getUser().getUserId())){
            return 1;
        }else {
            return 0;
        }
    }

}

