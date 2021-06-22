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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RunnersDialog extends DialogFragment {

    private com.example.runtime.RunnersDialogVM viewModel;
    private ArrayList<User> runners = new ArrayList<>();
    com.example.runtime.RunnersDialogAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        String eventId;
        assert getArguments() != null;
        eventId = getArguments().getString("eventId");

        viewModel = new com.example.runtime.RunnersModelFactory(eventId).create(com.example.runtime.RunnersDialogVM.class);
    }

    public RunnersDialog() {
    }

    public static com.example.runtime.RunnersDialog newInstance(String eventId) {
        com.example.runtime.RunnersDialog dialog = new com.example.runtime.RunnersDialog();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.runners_dialog, container);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat_Dialog_MinWidth);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageView dismissBtn = view.findViewById(R.id.runnerDialogDismiss);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        final CircleImageView managerImageView = view.findViewById(R.id.managerImageView);
        final TextView managerNameTV = view.findViewById(R.id.runnerDialogManagerName);
        RecyclerView runnersRecycler = view.findViewById(R.id.runnersRecycler);
        adapter = new com.example.runtime.RunnersDialogAdapter(runners, this.getContext());
        runnersRecycler.setAdapter(adapter);
        runnersRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        runnersRecycler.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(runnersRecycler.getContext(),
                ((LinearLayoutManager) manager).getOrientation());
        runnersRecycler.addItemDecoration(dividerItemDecoration);

        viewModel.getManager().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                managerNameTV.setText(user.getFullName());
            }
        });

        viewModel.getManagerImageUriLiveData().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Glide.with(Objects.requireNonNull(getContext())).load(uri).placeholder(R.drawable.placeholder_small).into(managerImageView);
            }


           /* public void onChanged(StorageReference storageReference) {
               // Glide.with(Objects.requireNonNull(getContext())).load(storageReference).into(managerImageView);
            }*/
        });

        viewModel.getRunnersLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                runners.clear();
                runners.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
