package sexy.kostya.animatron.integration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ViewManager {

    public final static ViewManager INSTANCE = new ViewManager();

    private final Map<Integer, ModelView> registeredViews = new ConcurrentHashMap<>();

    private ViewManager() {}

    public Collection<Integer> getRegisteredViews() {
        return registeredViews.keySet();
    }

    public ModelView getView(int id) {
        return registeredViews.get(id);
    }

    void register(ModelView view) {
        registeredViews.put(view.getEntity().getEntityId(), view);
    }

    void unregister(ModelView view) {
        registeredViews.remove(view.getEntity().getEntityId());
    }

}
