package com.yuweix.assist4j.core.io;


import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


/**
 * 二维码工具类
 * @author yuwei
 */
public abstract class QrCodeUtil {
	private static final String CHARSET = "utf-8";
	private static final String DEFAULT_IMAGE_TYPE = "jpg";
	/**
	 * 二维码尺寸
	 */
	private static final int QRCODE_SIZE = 300;
	/**
	 * Logo宽度
	 */
	private static final int WIDTH = 60;
	/**
	 * Logo高度
	 */
	private static final int HEIGHT = 60;



	private static BufferedImage createImage(String content, int qrcodeSize, ErrorCorrectionLevel ecLevel, byte[] logoData, boolean compressLogo) {
		if (qrcodeSize <= 0) {
			qrcodeSize = QRCODE_SIZE;
		}
		if (ecLevel == null) {
			ecLevel = ErrorCorrectionLevel.H;
		}
		Map<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ecLevel);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 0);
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrcodeSize, qrcodeSize, hints);
			int width = bitMatrix.getWidth();
			int height = bitMatrix.getHeight();
			BufferedImage qrcodeImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					qrcodeImg.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
				}
			}
			if (logoData == null || logoData.length <= 0) {
				return qrcodeImg;
			}
			/**
			 * 插入Logo
			 */
			insertLogo(qrcodeImg, logoData, compressLogo);
			return qrcodeImg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 插入Logo
	 * @param qrcodeImg 二维码图片
	 * @param logoData Logo图片
	 * @param compressLogo 是否压缩
	 */
	private static void insertLogo(BufferedImage qrcodeImg, byte[] logoData, boolean compressLogo) {
		ByteArrayInputStream logoBais = null;
		try {
			logoBais = new ByteArrayInputStream(logoData);
			Image logoImg = ImageIO.read(logoBais);
			int width = logoImg.getWidth(null);
			int height = logoImg.getHeight(null);

			/**
			 * 压缩Logo
			 */
			if (compressLogo) {
				if (width > WIDTH) {
					width = WIDTH;
				}
				if (height > HEIGHT) {
					height = HEIGHT;
				}
				Image scaledLogoImg = logoImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = tag.getGraphics();
				/**
				 * 绘制缩小后的图
				 */
				g.drawImage(scaledLogoImg, 0, 0, null);
				g.dispose();
				logoImg = scaledLogoImg;
			}
			/**
			 * 插入Logo
			 */
			Graphics2D graph = qrcodeImg.createGraphics();
			int x = (QRCODE_SIZE - width) / 2;
			int y = (QRCODE_SIZE - height) / 2;
			graph.drawImage(logoImg, x, y, width, height, null);
			Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
			graph.setStroke(new BasicStroke(3f));
			graph.draw(shape);
			graph.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (logoBais != null) {
				try {
					logoBais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 生成二维码
	 * @param content 内容
	 */
	public static byte[] encode(String content) {
		return encode(content, 0);
	}

	public static byte[] encode(String content, int qrcodeSize) {
		return encode(content, qrcodeSize, DEFAULT_IMAGE_TYPE, null, false);
	}

	/**
	 * 生成二维码(内嵌Logo)
	 * @param content 内容
	 * @param logoData Logo图片
	 */
	public static byte[] encode(String content, byte[] logoData) {
		return encode(content, logoData, false);
	}

	public static byte[] encode(String content, String imgType) {
		return encode(content, 0, imgType, null, false);
	}

	/**
	 * 生成二维码(内嵌Logo)
	 * @param content 内容
	 * @param logoData Logo图片
	 * @param compressLogo 是否压缩Logo
	 * 返回二维码图片的二进制数据
	 */
	public static byte[] encode(String content, byte[] logoData, boolean compressLogo) {
		return encode(content, 0, DEFAULT_IMAGE_TYPE, logoData, compressLogo);
	}

	public static byte[] encode(String content, String imgType, byte[] logoData, boolean compressLogo) {
		return encode(content, 0, imgType, logoData, compressLogo);
	}

	public static byte[] encode(String content, int qrcodeSize, String imgType, byte[] logoData, boolean compressLogo) {
		return encode(content, qrcodeSize, imgType, ErrorCorrectionLevel.H, logoData, compressLogo);
	}

	public static byte[] encode(String content, int qrcodeSize, String imgType, ErrorCorrectionLevel ecLevel, byte[] logoData, boolean compressLogo) {
		BufferedImage bufImg = createImage(content, qrcodeSize, ecLevel, logoData, compressLogo);
		ByteArrayOutputStream baos = null;
		byte[] qrcodeData = null;
		try {
			baos = new ByteArrayOutputStream();
			ImageIO.write(bufImg, imgType, baos);
			qrcodeData = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return qrcodeData;
	}

	/**
	 * 解析二维码
	 * 返回二维码中嵌入的内容
	 */
	public static String decode(byte[] qrcode) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(qrcode);
			BufferedImage image = ImageIO.read(bais);
			if (image == null) {
				return null;
			}
			BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
			Result result = new MultiFormatReader().decode(bitmap, hints);
			return result.getText();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
