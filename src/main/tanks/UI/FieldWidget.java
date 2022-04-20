package tanks.UI;

import org.jetbrains.annotations.NotNull;
import tanks.AbstractCell;
import tanks.CellPosition;
import tanks.Field;

import javax.swing.*;

public class FieldWidget extends JPanel {

    private final Field _field;

    public FieldWidget(@NotNull Field field, @NotNull WidgetFactory widgetFactory) {
        this._field = field;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        fillField(widgetFactory);
    }

    private void fillField(WidgetFactory widgetFactory) {
        for (int i = 0; i < _field.getHeight(); ++i) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

            for(int j = 0; j < _field.getWidth(); ++j) {
                CellPosition cellPosition = new CellPosition(i, j);
                AbstractCell cell = _field.getCell(cellPosition);
                CellWidget cellWidget = widgetFactory.create(cell);
                row.add(cellWidget);
            }

            add(row);
        }
    }
}
