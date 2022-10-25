package srv.api.Messages;

import java.util.Collection;

public interface CollectionMessage<T> {
    public Collection<T> getCollection();
}
