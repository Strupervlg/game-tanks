package tanks.event;

import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public interface TankActionListener extends EventListener {

    void tankMoved(@NotNull TankActionEvent event);

    void tankSkippedMove(@NotNull TankActionEvent event);

    void tankShot(@NotNull TankActionEvent event);

    void bulletChangedCell(@NotNull TankActionEvent event);
}
