package tanks.UI;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BarrelOfFuelWidget extends CellItemWidget {

    private static final File FILE_IMG_BARREL_OF_FUEL = new File("images/BarrelOfFuel.png");
    private static final File FILE_IMG_EXPLOSION = new File("images/Explosion.png");

    public BarrelOfFuelWidget() {
        super();
    }

    private File getBarrelOfFuelImageFile(State state) {
        File file = null;
        if (state == State.ALIVE)  {
            file = FILE_IMG_BARREL_OF_FUEL;
        }
        else if (state == State.DAMAGED) {
            file = FILE_IMG_EXPLOSION;
        }
        return file;
    }

    @Override
    protected BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getBarrelOfFuelImageFile(this.getState()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
