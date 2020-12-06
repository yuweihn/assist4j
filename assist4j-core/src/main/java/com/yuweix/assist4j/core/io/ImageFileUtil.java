package com.yuweix.assist4j.core.io;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 图片处理工具
 * @author wei
 */
public abstract class ImageFileUtil extends FileUtil {
	private static final Logger log = LoggerFactory.getLogger(ImageFileUtil.class);


	/**
	 * 读取图片的尺寸
	 */
	public static Dimension measure(byte[] imgData) {
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(imgData);
			BufferedImage image = ImageIO.read(inputStream);
			int width = image.getWidth(null);
			int height = image.getHeight(null);
			return new Dimension(width, height);
		} catch (IOException e) {
			log.error("", e);
			return null;
		} finally {
			close(inputStream);
		}
	}

	/**
	 * 按比例压缩图片
	 */
	public static byte[] compress(byte[] imgData, double ratio) {
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(imgData);
			BufferedImage oldImage = ImageIO.read(inputStream);
			int oldWidth = oldImage.getWidth(null);
			int oldHeight = oldImage.getHeight(null);
			return compress0(oldImage, (int) (oldWidth * ratio), (int) (oldHeight * ratio), getImageType(imgData));
		} catch (IOException e) {
			log.error("", e);
			return null;
		} finally {
			close(inputStream);
		}
	}
	public static byte[] compress(byte[] imgData, int newWidth) {
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(imgData);
			BufferedImage oldImage = ImageIO.read(inputStream);
			int oldWidth = oldImage.getWidth(null);
			int oldHeight = oldImage.getHeight(null);

			if (newWidth > oldWidth) {
				newWidth = oldWidth;
			}
			int newHeight = (newWidth * oldHeight) / oldWidth;
			return compress0(oldImage, newWidth, newHeight, getImageType(imgData));
		} catch (IOException e) {
			log.error("", e);
			return null;
		} finally {
			close(inputStream);
		}
	}
	public static byte[] compress(byte[] imgData, int newWidth, int newHeight) {
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(imgData);
			BufferedImage oldImage = ImageIO.read(inputStream);
			return compress0(oldImage, newWidth, newHeight, getImageType(imgData));
		} catch (IOException e) {
			log.error("", e);
			return null;
		} finally {
			close(inputStream);
		}
	}
	private static byte[] compress0(BufferedImage oldImage, int newWidth, int newHeight, String formatName) {
		ByteArrayOutputStream outputStream = null;
		try {
			Image resizedImage = oldImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
			Image temp = new ImageIcon(resizedImage).getImage();
			BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);

			/**
			 * Clear background and paint the image.
			 */
			Graphics g = bufferedImage.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
			g.drawImage(temp, 0, 0, null);
			g.dispose();

			outputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, formatName, outputStream);
			return outputStream.toByteArray();
		} catch (IOException e) {
			log.error("", e);
			return null;
		} finally {
			close(outputStream);
		}
	}

	/**
	 * 将图片imgData嵌入bgImgData指定位置
	 * 返回合并后图片的二进制数据
	 * @param bgImgData
	 * @param imgData
	 * @param x               横坐标
	 * @param y               纵坐标
	 * @param width           imgData压缩后的宽度
	 * @param height          imgData压缩后的高度
	 * @return
	 */
	public static byte[] merge(byte[] bgImgData, byte[] imgData, int x, int y, int width, int height) {
		ByteArrayInputStream bgImgStream = null;
		ByteArrayInputStream imgStream = null;
		ByteArrayOutputStream outputStream = null;
		try {
			bgImgStream = new ByteArrayInputStream(bgImgData);
			imgStream = new ByteArrayInputStream(imgData);

			BufferedImage bg = ImageIO.read(bgImgStream);
			BufferedImage image = ImageIO.read(imgStream);
			Graphics2D g = bg.createGraphics();
			g.drawImage(image, x, y, width, height, null);
			g.dispose();

			outputStream = new ByteArrayOutputStream();
			ImageIO.write(bg, getImageType(bgImgData), outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			log.error("", e);
			return null;
		} finally {
			close(outputStream);
			close(imgStream);
			close(bgImgStream);
		}
	}

	/**
	 * 取得图片类型
	 * @param imgData
	 * @return
	 */
	public static String getImageType(byte[] imgData) {
		ByteArrayInputStream inputStream = null;
		ImageInputStream imageInputStream = null;
		try {
			inputStream = new ByteArrayInputStream(imgData);
			imageInputStream = ImageIO.createImageInputStream(inputStream);
			ImageReader imageReader = ImageIO.getImageReaders(imageInputStream).next();
			return imageReader.getFormatName();
		} catch (Exception e) {
			log.error("", e);
			return null;
		} finally {
			close(imageInputStream);
			close(inputStream);
		}
	}

	/**
	 * 图片裁剪
	 * @param imgData
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static byte[] cut(byte[] imgData, int x, int y, int width, int height) {
		ByteArrayInputStream inputStream = null;
		ImageInputStream imageInputStream = null;
		ByteArrayOutputStream outputStream = null;
		try {
			inputStream = new ByteArrayInputStream(imgData);
			imageInputStream = ImageIO.createImageInputStream(inputStream);
			ImageReader imageReader = ImageIO.getImageReaders(imageInputStream).next();
			imageReader.setInput(imageInputStream, true);
			ImageReadParam param = imageReader.getDefaultReadParam();
			Rectangle rect = new Rectangle(x, y, width, height);
			param.setSourceRegion(rect);
			BufferedImage image = imageReader.read(0, param);

			outputStream = new ByteArrayOutputStream();
			ImageIO.write(image, imageReader.getFormatName(), outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			log.error("", e);
			return null;
		} finally {
			close(outputStream);
			close(imageInputStream);
			close(inputStream);
		}
	}

	/**
	 * 图片旋转
	 * @param imgData
	 * @param degree        正数是顺时针，负数是逆时针
	 * @return
	 */
	public static byte[] rotate(byte[] imgData, int degree) {
		ByteArrayInputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;
		try {
			inputStream = new ByteArrayInputStream(imgData);
			BufferedImage image = ImageIO.read(inputStream);
			int w = image.getWidth();
			int h = image.getHeight();
			int type = image.getColorModel().getTransparency();
			BufferedImage img = new BufferedImage(w, h, type);
			Graphics2D graph = img.createGraphics();
			graph.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graph.rotate(Math.toRadians(degree), w / 2, h / 2);
			graph.drawImage(image, 0, 0, null);
			graph.dispose();

			outputStream = new ByteArrayOutputStream();
			ImageIO.write(img, getImageType(imgData), outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			log.error("", e);
			return null;
		} finally {
			close(outputStream);
			close(inputStream);
		}
	}
	private static void close(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static final class Dimension {
		private int width;
		private int height;
		private Dimension(int width, int height) {
			this.width = width;
			this.height = width;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
}
