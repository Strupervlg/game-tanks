package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.event.BaseActionEvent;
import tanks.event.BaseActionListener;

public class BaseObserverTest implements BaseActionListener {
    @Override
    public void damageCaused(@NotNull BaseActionEvent event) {

    }

}
