package tanks.UI;

import javax.swing.*;
import java.awt.*;

public abstract class CellWidget extends JPanel {

    CellItemWidget _cellItemWidget;

    private static final int CELL_SIZE = 64;

    public CellWidget() {
        setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
    }

    public void addItem(CellItemWidget item) {
        _cellItemWidget = item;
        add(item);
    }

    public void removeItem() {
        remove(0);
        _cellItemWidget = null;
        repaint();
    }

}
