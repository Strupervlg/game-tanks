package tanks.event;

import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public interface BulletActionListener extends EventListener {

    void bulletChangedCell(@NotNull BulletActionEvent event);

    void objectDestroyed(@NotNull BulletActionEvent event);
}
