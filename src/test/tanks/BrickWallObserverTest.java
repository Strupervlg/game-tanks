package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.event.BrickWallActionEvent;
import tanks.event.BrickWallActionListener;

public class BrickWallObserverTest implements BrickWallActionListener {
    @Override
    public void damageCaused(@NotNull BrickWallActionEvent event) {

    }

    @Override
    public void objectDestroyed(@NotNull BrickWallActionEvent event) {

    }
}
