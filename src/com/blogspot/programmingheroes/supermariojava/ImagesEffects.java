
// ImagesEfects.java ------------------------------------------------

package com.blogspot.programmingheroes.supermariojava;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import com.blogspot.programmingheroes.supermariojava.loaders.ImagesLoader;


/**
 *
 */
public class ImagesEffects {

	protected ImagesLoader imgLoader;

	/**
	 *
	 */
	public ImagesEffects(ImagesLoader loader) {
		this.imgLoader = loader;
	}

	public void paintAlphaImg(Graphics g,
		String name, int x, int y, int w,
		int h, float alpha) {
			this.paintAlphaImg(g,
				imgLoader.getImage(name), x, y,
				w, h, alpha);
	}

	public void paintAlphaImg(Graphics g,
		BufferedImage img, int x, int y,
		int w, int h, float alpha) {
			Graphics2D g2 = (Graphics2D)g;
			Composite c = g2.getComposite();
			g2.setComposite(
				AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, alpha));
			g2.drawImage(img, x, y, w, h, null);
			g2.setComposite(c);
	}

	public void paintAlphaImg(Graphics g,
		BufferedImage img, int dx1, int dy1,
		int dx2, int dy2, int sx1, int sy1,
		int sx2, int sy2, float alpha) {
			Graphics2D g2 = (Graphics2D)g;
			Composite c = g2.getComposite();
			g2.setComposite(
				AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, alpha));
			g2.drawImage(img, dx1, dy1, dx2, dy2,
				sx1, sy1, sx2, sy2, null);
			g2.setComposite(c);
	}

	public BufferedImage returnAlphaImg(BufferedImage img, float alpha) {
		BufferedImage image = new BufferedImage(
			img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g2 = (Graphics2D)image.getGraphics();
		g2.setComposite(
			AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, alpha));
		g2.drawImage(img, 0, 0, image.getWidth(),
			img.getHeight(), null);
		return image;
	}

	public BufferedImage returnRescaleImg(
		BufferedImage img, int w, int h) {
			System.out.println (img.getType());
			BufferedImage image = new BufferedImage(
				w, h, img.getType());
			Graphics g = image.getGraphics();
			g.drawImage(img, 0, 0, w, h, null);
			return image;
	}

	public BufferedImage returnRotatedImg(
		BufferedImage img, int degrees) {
			return returnRotatedImg(img, Math.toRadians(degrees));
	}

	public void paintShearImg(Graphics g,
		String name, int x, int y, int w, int h,
		double shx, double shy) {
			Graphics2D g2 = (Graphics2D)g;
			g2.shear(shx, shy);
			g2.drawImage(imgLoader.getImage(name),
				 x, y, w, h, null);
			g2.dispose();
	}

	public void paintRotatedImg(Graphics g,
		String name, int x, int y, int w,
		int h, int degrees) {
			Graphics2D g2 = (Graphics2D)g;
			/*RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHints(rh);*/
			AffineTransform original = g2.getTransform();
			AffineTransform rotate = new AffineTransform();
			rotate.rotate(Math.toRadians(degrees), x+w/2, y+h/2);
			g2.transform(rotate);
			g2.drawImage(imgLoader.getImage(name), x, y, w, h, null);
			g2.transform(original);
			g2.dispose();
	}

	public BufferedImage returnRotatedImg(
		BufferedImage img, double radians) {
			BufferedImage image = new BufferedImage(
				img.getWidth(), img.getHeight(),
				img.getColorModel().getTransparency());
			Graphics2D g2 = (Graphics2D)image.getGraphics();
			AffineTransform rotation = new AffineTransform();
			rotation.rotate(radians, 
				img.getWidth()/2, img.getHeight()/2);
			g2.transform(rotation);
			return image;
	}

}  // fin de la clase ImagesEfects.

//  fin de ImagesEfects.java ----------------------------------------