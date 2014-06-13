
// SoundsLoader.java -------------------------------------------------

package com.blogspot.programmingheroes.supermariojava.loaders;


import java.applet.*;
import java.net.*;
import java.io.*;
import java.util.*;


/**
 * Easy way to load sounds.
 */
public class SoundsLoader extends Loader {

	/**
	 * Stores all the AudioClips that are being
	 * playing.
	 */
	public ArrayList<String> playing;
	/**
	 * ImagesLoader with the path of the package directory
	 * and with those directory <code>loader</code>.
	 */
	public SoundsLoader() {
		this(".", "");
	}  // end of ImagesLoader();

	/**
	 * ImagesLoader with the indicated path and a
	 * directory loader.
	 *
	 * @param path Directory with the images.
	 */
	public SoundsLoader(String path) {
		this(path, "");
	} // end of ImagesLoader(String);

	/**
	 * ImageLoader with the indicated path and the
	 * indicated directory.
	 *
	 * @param path Directory with the images.
	 * @param loader File with the images to load.
	 */
	public SoundsLoader(String path, String loader) {
		super(path, loader);
		loaded = new HashMap<String, Object>();
		playing = new ArrayList<String>();
	}  // end of ImagesLoader(String, String);




	public boolean load(File f, String name, boolean rewrite) {
		if (name == null) {
			name = f.getName();
		}
		if (!rewrite && loaded.containsKey(name)) {
			return false;
		}
		try {
			URL url = f.toURI().toURL();
			AudioClip a = Applet.newAudioClip(url);
			loaded.put(name, a);
		} catch (Exception e) {
			System.err.println("Error loanding sound "
				+name+" from "+f.getPath());
			e.printStackTrace();
			return false;
		}
		System.out.println("Loaded "+name+" from "+f.getName());
		return true;
	}  // end of load(File, String, boolean);

	/**
	 * Play a previous loaded sound.
	 */
	public void play(String name, boolean loop) {
		if (loop) {
			((AudioClip)loaded.get(name)).loop();
			playing.add(name);
			return;
		}
		((AudioClip)loaded.get(name)).play();
	}

	/**
	 * Returns a previous loaded image.
	 */
	public AudioClip getAudio(String name) {
		return (AudioClip)loaded.get(name);
	}

	/**
	 * Returns a image. If <code>load</code> is
	 * <code>true</code> and if is necessary it's loaded.
	 */
	public AudioClip getAudio(String name, boolean load, boolean rewrite) {
		Object o = super.getObject(name, load, rewrite);
		if (o == null) {
			return null;
		}
		return (AudioClip)o;
	}  // end of getAudio(String, boolean, boolean);

}  // end of SoundsLoader class.


//  end of SoundsLoader.java
