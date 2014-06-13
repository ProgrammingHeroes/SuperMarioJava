
// ImagesLoader.java -------------------------------------------------

package com.blogspot.programmingheroes.supermariojava.loaders;


import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;


/**
 * Easy way to load images.
 */
public class ImagesLoader extends Loader {

	/**
	 * To create compatible images.
	 */
	private GraphicsConfiguration gc;


	/**
	 * ImagesLoader with the path of the package directory
	 * and with those directory <code>loader</code>.
	 */
	public ImagesLoader() {
		this(".", "");
	}  // end of ImagesLoader();

	/**
	 * ImagesLoader with the indicated path and a
	 * directory loader.
	 *
	 * @param path Directory with the images.
	 */
	public ImagesLoader(String path) {
		this(path, "");
	} // end of ImagesLoader(String);

	/**
	 * ImageLoader with the indicated path and the
	 * indicated directory.
	 *
	 * @param path Directory with the images.
	 * @param loader File with the images to load.
	 */
	public ImagesLoader(String path, String loader) {
		super(path, loader);
		loaded = new HashMap<String, Object>();
		GraphicsEnvironment ge = GraphicsEnvironment
			.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice()
			.getDefaultConfiguration();
	}  // end of ImagesLoader(String, String);



	/**
	 * Returns the default GraphicsConfiguration
	 * to create compatible images.
	 */
	public GraphicsConfiguration getGraphicsConfiguration() {
		return gc;
	}

	/**
	 * Analysing a line of text that contains a
	 * load information.
	 *
	 * @param line Information to load an image/s.
	 */
	public void loadLine(String line) {
		super.loadLine(line);
		if (line.startsWith("3 ")) {
			loadSprites(line);
		} 
		/* TODO Posible implementación: Carga por separado las
		 * imágenes de un gif animado. *
		else if (line.startsWith("4 ")) {
			animatedGif(line);
		} */
	}

	private boolean loadSprites(String line) {
		boolean error = false;
		StringTokenizer st = new StringTokenizer(line, " ");
		
		if (st.countTokens() >= 4) {
			st.nextToken();
			String name = st.nextToken();
			String nameFile = "";
			int equals = name.indexOf("=");
			int row = 0, col = 0;
			try {
				row = Integer.parseInt(st.nextToken());
				col = Integer.parseInt(st.nextToken());
			} catch (NumberFormatException e) {
				error = true;
				//e.printStackTrace();
			}
			if (!error) {
				if (equals == -1) { // if no =
					if (!super.load(name)) {
						return false;
					}
				} else { // if =
					nameFile = name.substring(equals+1);
					name = name.substring(0, equals);
					if (!super.load(nameFile, name)) {
							return false;
					}
				}
				BufferedImage bi = getImage(name);
				int w = (int)bi.getWidth()/col;
				int h = bi.getHeight()/row;
				int transparency = bi
						.getColorModel().getTransparency();
				
				BufferedImage img;
				Graphics g;
				String nextName = null;
				equals = -1;
				for (int i=0; i<row; i++)
					for (int j=0; j<col; j++) {
						img = gc.createCompatibleImage(
							w, h, transparency);
						g = img.getGraphics();
						g.drawImage(bi, 0, 0, w, h,
							j*w, i*h, (j*w)+w, (i*h)+h, null);
						if (nextName == null) {
							nextName = "";
							while (st.hasMoreTokens()) {
								nextName = st.nextToken();
								equals=nextName.indexOf("=");
								if (equals != -1) {
									break;
								}
							}
						}
						if (nextName.startsWith(i+"_"+j)) {
							loaded.put(nextName.substring(
								equals+1), img);
							System.out.print(nextName.substring(
								equals+1)+" -> "); // TODELETE
							nextName = null;
						} else {
							loaded.put(name+i+"_"+j, img);
						}
						g.dispose();
						System.out.println(name+i+"_"+j);
					}
				removeObject(name);
			}
		} else {
		// if no enough arguments
			error = true;
		}
		if (error) {
			System.err.println("Error format in line: "+line);
		}
		return error;
	}  // end of loadSprites(String);

	public boolean load(File f, String name, boolean rewrite) {
		if (name == null) {
			name = f.getName();
		}
		if (!rewrite && loaded.containsKey(name)) {
			return false;
		}
		try {
			BufferedImage bi = ImageIO.read(f);
			int transparency = bi.getColorModel()
				.getTransparency();
			BufferedImage img = gc.createCompatibleImage(
				bi.getWidth(), bi.getHeight(), transparency);
			
			Graphics2D g = img.createGraphics();
			g.drawImage(bi, 0, 0, null);
			g.dispose();
			loaded.put(name, img);
		} catch (IOException e) {
			System.err.println("Error loanding image "
				+f.getPath());
			e.printStackTrace();
			return false;
		}
		System.out.println("Loaded "+name+" from "+f.getName());
		return true;
	}  // end of load(File, String, boolean);

	/**
	 * Returns a previous loaded image.
	 */
	public BufferedImage getImage(String name) {
		return (BufferedImage)loaded.get(name);
	}

	/**
	 * Returns a image. If <code>load</code> is
	 * <code>true</code> and if is necessary it's loaded.
	 */
	public BufferedImage getImage(String name, boolean load, boolean rewrite) {
		Object o = super.getObject(name, load, rewrite);
		if (o == null) {
			return null;
		}
		return (BufferedImage)o;
	}  // end of getImage(String, boolean, boolean);

}  // end of ImagesLoader class.


//  end of ImagesLoader.java
