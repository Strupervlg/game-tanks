package tanks.event;

import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public interface BaseActionListener extends EventListener {

    void damageCaused(@NotNull BaseActionEvent event);

}
