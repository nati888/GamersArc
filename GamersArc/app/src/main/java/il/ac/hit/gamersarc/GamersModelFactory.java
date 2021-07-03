package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GamersModelFactory implements ViewModelProvider.Factory  {

    private String eventId;

    public GamersModelFactory(String eventId) {
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == GamersDialogVM.class){
            return (T) new GamersDialogVM(eventId);
        }
        else
            return null;
    }
}
