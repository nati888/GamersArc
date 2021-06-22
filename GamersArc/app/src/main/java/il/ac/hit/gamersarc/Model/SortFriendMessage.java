package il.ac.hit.gamersarc.Model;

import java.util.Comparator;

public class SortFriendMessage implements Comparator<il.ac.hit.gamersarc.Model.UserWithLastMessage> {
    @Override
    public int compare(il.ac.hit.gamersarc.Model.UserWithLastMessage o1, il.ac.hit.gamersarc.Model.UserWithLastMessage o2) {
        return o2.message.getTime().compareTo(o1.message.getTime());
    }
}
