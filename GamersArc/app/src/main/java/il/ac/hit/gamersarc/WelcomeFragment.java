package il.ac.hit.gamersarc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WelcomeFragment extends Fragment {

    interface OnRegisterClick{
        void onSignInClick();
        void onSignUpClick();
    }

    OnRegisterClick callBack;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callBack = (OnRegisterClick) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("Activity must implement OnNextListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.welcome_fragment,container,false);
        Button buttonSignIn = root.findViewById(R.id.sign_in);
        Button buttonSignUp = root.findViewById(R.id.sign_up);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onSignInClick();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onSignUpClick();
            }
        });

        return root;
    }



}
