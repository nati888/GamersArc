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
        if (modelClass.isAssignableFrom(il.ac.hit.gamersarc.FriendDialogVM.class)) {
            return (T) new il.ac.hit.gamersarc.FriendDialogVM(friendId);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
