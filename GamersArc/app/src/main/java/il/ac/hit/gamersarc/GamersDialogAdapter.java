package il.ac.hit.gamersarc;

import android.content.Context;
import android.net.Uri;
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

public class GamersDialogAdapter extends RecyclerView.Adapter<il.ac.hit.gamersarc.GamersDialogAdapter.gamersViewHolder> {

    private ArrayList<User> gamers = new ArrayList<>();
    DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    Context context;

    public GamersDialogAdapter(ArrayList<User> gamers, Context context) {
        this.gamers = gamers;
        this.context = context;
    }

    @NonNull
    @Override
    public gamersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gamer_cell, parent, false);
        return new gamersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final gamersViewHolder holder, int position) {


        OnSuccessListener<Uri> listener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.placeholder_small).into(holder.gamerImageView);
            }
        };

        dataBaseClass.getImageUserId(gamers.get(position).getUserId(), listener);
        holder.gamerName.setText(gamers.get(position).getFullName());
    }

    @Override
    public int getItemCount() {
        return gamers.size();
    }

    class gamersViewHolder extends RecyclerView.ViewHolder{

        CircleImageView gamerImageView;
        TextView gamerName;

        public gamersViewHolder(@NonNull View itemView) {
            super(itemView);
            gamerImageView = itemView.findViewById(R.id.gamerImageView);
            gamerName = itemView.findViewById(R.id.gamerName);

        }
    }
}
