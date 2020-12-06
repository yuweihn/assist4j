package com.yuweix.assist4j.core.io;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;


/**
 * 生成验证码图片
 * @author yuwei
 */
public abstract class VcodeImgUtil {
	private static final String DEFAULT_IMAGE_TYPE = "jpg";
	

	public static final byte[] createImage(String code) {
		return createImage(code, DEFAULT_IMAGE_TYPE);
	}
	
	public static final byte[] createImage(String code, String imgType) {
		RenderedImage image = createImage0(code);
		ByteArrayOutputStream output = null;
		ImageOutputStream imageOut = null;
		try {
			output = new ByteArrayOutputStream();
			imageOut = ImageIO.createImageOutputStream(output);
			ImageIO.write(image, imgType, imageOut);
			return output.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (imageOut != null) {
				try {
					imageOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static RenderedImage createImage0(String code){
		int width = 72;
		int height = 20;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		Random random = new Random();
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		for (int i = 0; i < code.length(); i++) {
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(code.substring(i, i + 1), 13 * i + 6, 16);
		}
		
		g.dispose();
		return image;
	}

	private static Color getRandColor(int fc, int bc) {
		Random random = new Random();
		int fc0 = fc;
		if (fc0 > 255) {
			fc0 = 255;
		}
		
		int bc0 = bc;
		if (bc0 > 255) {
			bc0 = 255;
		}
		
		int r = fc0 + random.nextInt(bc0 - fc0);
		int g = fc0 + random.nextInt(bc0 - fc0);
		int b = fc0 + random.nextInt(bc0 - fc0);
		return new Color(r, g, b);
	}
}
