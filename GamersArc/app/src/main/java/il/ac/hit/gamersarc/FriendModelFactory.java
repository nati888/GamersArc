package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FriendModelFactory implements ViewModelProvider.Factory {

    private String friendId;

    public FriendModelFactory(String friendId) {
        this.friendId = friendId;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(com.example.runtime.FriendDialogVM.class)) {
            return (T) new com.example.runtime.FriendDialogVM(friendId);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
