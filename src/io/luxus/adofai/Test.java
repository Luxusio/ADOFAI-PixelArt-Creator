package io.luxus.adofai;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import io.luxus.api.adofai.MapData;
import io.luxus.api.adofai.TileData;
import io.luxus.api.adofai.action.ColorTrack;
import io.luxus.api.adofai.action.PositionTrack;
import io.luxus.api.adofai.type.EventType;
import io.luxus.api.adofai.type.TileAngle;
import net.coobird.thumbnailator.makers.FixedSizeThumbnailMaker;
import net.coobird.thumbnailator.resizers.DefaultResizerFactory;
import net.coobird.thumbnailator.resizers.Resizer;

public class Test {
	
	public static void main(String[] args) throws Throwable {

		String path = "echidna.png";
		int toWidth = 200;
		int toHeight = 100;

		BufferedImage image = ImageIO.read(new File(path));

		image = cropMiddle(image, toWidth, toHeight);
		image = resize(image, toWidth, toHeight * 2);

		MapData mapData = new MapData();
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int lastColor = -1;
		int floor = 1;
		for (int row = 0; row < height; row++) {
			TileData tileData = null;
			
			for (int col = 0; col < width; col++) {
				int color  = image.getRGB(col, row);
				tileData = new TileData(floor, TileAngle._0);
				if(col == 0) {
					tileData.getActionList(EventType.MOVE_TRACK).add(new PositionTrack(Arrays.asList((double) -width, -0.5), "Disabled"));
				}
				if(lastColor != color) {
					String hexColorString = Integer.toHexString(color).substring(2);
					tileData.getActionList(EventType.COLOR_TRACK).add(
							new ColorTrack("Single", hexColorString, "ffffff", 0.0, "None", 0L, "Standard"));
				}
				
				if(floor != 0) {
					mapData.getTileDataList().add(tileData);
				}
				
				floor++;
				lastColor = color;
			}
		}

		int idx = path.lastIndexOf('.');
		String saveStr = (idx == -1 ? path : path.substring(0, idx)) + ".adofai";
		System.out.println(saveStr);
		mapData.save(saveStr);
		
		System.out.println("end");
	}

	private static BufferedImage cropMiddle(BufferedImage image, int widthRatio, int heightRatio) {
		int height = image.getHeight();
		int width = image.getWidth();

		if (height * widthRatio < width * heightRatio) {
			// cut left / right
			double newWidth = height * widthRatio / heightRatio;
			return image.getSubimage((int) (width - newWidth) / 2, 0, (int) newWidth, height);
		} else {
			// cut top / bottom
			double newHeight = width * heightRatio / widthRatio;
			return image.getSubimage(0, (int) (height - newHeight) / 2, width, (int) newHeight);
		}
	}

	public static BufferedImage resize(BufferedImage image, int width, int height) throws IOException {
		Resizer resizer = DefaultResizerFactory.getInstance()
				.getResizer(new Dimension(image.getWidth(), image.getHeight()), new Dimension(width, height));

		return new FixedSizeThumbnailMaker(width, height, false, true).resizer(resizer)
				.make(image);
	}

}