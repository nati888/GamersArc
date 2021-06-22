package il.ac.hit.gamersarc;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RunnersModelFactory implements ViewModelProvider.Factory  {

    private String eventId;

    public RunnersModelFactory(String eventId) {
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == RunnersDialogVM.class){
            return (T) new RunnersDialogVM(eventId);
        }
        else
            return null;
    }
}
