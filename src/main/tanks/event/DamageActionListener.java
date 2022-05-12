package tanks.event;

import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public interface DamageActionListener extends EventListener {

    void damageCaused(@NotNull DamageActionEvent event);

    void objectDestroyed(@NotNull DamageActionEvent event);
}
