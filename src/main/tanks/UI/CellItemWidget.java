package tanks.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class CellItemWidget extends JPanel {

    private State _state;

    private static final int CELL_ITEM_SIZE = 64;

    public CellItemWidget() {
        setState(State.ALIVE);
        setOpaque(false);
    }

    public void setState(State state) {
        this._state = state;
        setPreferredSize(new Dimension(CELL_ITEM_SIZE, CELL_ITEM_SIZE));
        repaint();
        revalidate();
    }

    public State getState() {
        return _state;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(getImage(), 0, 0, null);
    }

    protected abstract BufferedImage getImage();
}
