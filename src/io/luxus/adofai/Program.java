package io.luxus.adofai;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

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

public class Program {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			Program.program(scanner);
			System.out.println("계속하시려면 엔터키를 눌러주세요.");
			System.in.read();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	private static void program(Scanner scanner) throws IllegalArgumentException, IllegalAccessException, IOException {
		System.out.println("A Dance of Fire and Ice 픽셀아트 제작기");
		System.out.println("ver 1.0.0");
		System.out.println("개발자 : Luxus io");
		System.out.println("YouTube : https://www.youtube.com/c/Luxusio");
		System.out.println("Github : https://github.com/Luxusio/ADOFAI-Map-Converter");
		System.out.println();
		
		System.out.println();
		System.out.print("이미지 경로(확장자 포함) : ");
		String path = scanner.nextLine();
		

		System.out.print("높이 : ");
		int toHeight = scanner.nextInt();
		scanner.nextLine();
		
		System.out.print("너비 : ");
		int toWidth = scanner.nextInt();
		scanner.nextLine();
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch(IOException e) {
			System.out.println("E> 잘못된 이미지이거나 없는 이미지입니다.");
			e.printStackTrace();
		}
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
				int color = image.getRGB(col, row);
				tileData = new TileData(floor, TileAngle._0);
				if(col == 0) {
					tileData.getActionList(EventType.MOVE_TRACK).add(new PositionTrack(Arrays.asList((double) -width, -0.5), "Disabled"));
				}
				if(lastColor != color) {
					String hexColorString = Integer.toHexString(color).substring(2);
					tileData.getActionList(EventType.COLOR_TRACK).add(
							new ColorTrack("Single", hexColorString, "ffffff", 0.0, "None", 0L, "Standard"));
				}
				
				if(floor == 1) {
					//MoveCamera(Double duration, String relativeTo, List<Double> position, Double rotation, Long zoom,
					//		Double angleOffset, String ease, String eventTag)
					//tileData.getActionList(EventType.MOVE_CAMERA)
					//.add(new MoveCamera());
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
		
		System.out.println("complete");
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
