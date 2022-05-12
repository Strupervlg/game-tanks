package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.event.DamageActionEvent;
import tanks.event.DamageActionListener;

public class DamageObserverTest implements DamageActionListener {
    @Override
    public void damageCaused(@NotNull DamageActionEvent event) {

    }

    @Override
    public void objectDestroyed(@NotNull DamageActionEvent event) {

    }
}
