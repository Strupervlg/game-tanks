package tanks.event;

import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public interface BrickWallActionListener extends EventListener {

    void damageCaused(@NotNull BrickWallActionEvent event);

    void objectDestroyed(@NotNull BrickWallActionEvent event);
}
