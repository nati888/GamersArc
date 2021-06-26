package il.ac.hit.gamersarc.model;

import java.util.Comparator;

public class SortFriendMessage implements Comparator<il.ac.hit.gamersarc.model.UserWithLastMessage> {
    @Override
    public int compare(il.ac.hit.gamersarc.model.UserWithLastMessage o1, il.ac.hit.gamersarc.model.UserWithLastMessage o2) {
        return o2.message.getTime().compareTo(o1.message.getTime());
    }
}
