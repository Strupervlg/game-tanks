package tanks.UI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BushWidget extends CellItemWidget implements StorageUnitWidget {

    private static final File FILE_IMG_BUSH = new File("images/Bush.png");

    public BushWidget() {
        super();
        setLayout(new BorderLayout());
    }

    private TankWidget _tankWidget;

    @Override
    public void addTankWidget(TankWidget tankWidget) {
        _tankWidget = tankWidget;
        ((CellWidget)getParent()).addItem(this._tankWidget);
    }

    @Override
    public void removeTankWidget() {
        _tankWidget = null;
        ((CellWidget)getParent()).removeItem();
    }

    private BulletWidget _bulletWidget;

    @Override
    public void addBulletWidget(BulletWidget bulletWidget) {
        _bulletWidget = bulletWidget;
        add(this._bulletWidget);
    }

    @Override
    public void removeBulletWidget() {
        remove(0);
        _bulletWidget = null;
        repaint();
    }

    @Override
    protected BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(FILE_IMG_BUSH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
