
// Loader.java -------------------------------------------------------

package com.blogspot.programmingheroes.supermariojava.loaders;


import java.util.*;
import java.io.*;


/**
 * Easy way to load any object of any archive.
 */
public abstract class Loader {

	/**
	 * List of valid file extensions that can be loaded.
	 * The Strings aren't case sensitive. The dot '.' is
	 * not allowed. The empty String "" allows any file extension.
	 */
	public static ArrayList<String> validExtensions;

	/* Path of the package directory.
	 */
	private String packageDirectory;
	
	/* Path where the resources are.
	 * Relative to package directory.
	 */
	private String relativePath;

	/* File which contains the files to load.
	 */
	private File loader;

	/**
	 * Stores all the Object's loaded.
	 */
	protected HashMap<String, Object> loaded;


	/**
	 * Loader with the path of the package directory
	 * and with those directory <code>loader</code>.
	 */
	public Loader() {
		this(".", "");
	}  // end of Loader();

	/**
	 * Loader with the indicated path and a
	 * directory loader.
	 *
	 * @param path Directory with the images.
	 */
	public Loader(String path) {
		this(path, "");
	} // end of Loader(String);

	/**
	 * Loader with the indicated path and the
	 * indicated directory.
	 *
	 * @param path Directory with the images.
	 * @param loader File with the images to load.
	 */
	public Loader(String path, String loader) {
		validExtensions = new ArrayList<String>();
		validExtensions.add("");
		// Obtenemos el directorio de una forma enrevesada
		// para poder utilizar el cargador en Apples.
		packageDirectory = getClass()
			.getClassLoader().getResource("")
			.getPath();
		packageDirectory = packageDirectory.substring(0,
			packageDirectory.lastIndexOf("bin/"));
		setPath(path);
		setLoader(loader);
		
		System.out.println("Package Directory "
			+packageDirectory);
		System.out.println("Relative Path: "+relativePath);
		System.out.println("Loader: "+loader+" exists-> "
			+existsLoader());
	}  // end of ImagesLoader(String, String);



	/**
	 * Set the path of the files directories or
	 * the loader file.<br />
	 * The <code>path</code> is relative the main
	 * directory of the package.<br />
	 * The default path is <code>"."</code>
	 * (directory of the package).<br />
	 * Don't use <code>".."</code>
	 */
	public void setPath(String path) {
		try {
			path = path.trim();
			StringBuffer s = new StringBuffer(path);
			for (int i=0; i<s.length(); i++) {
				if (s.charAt(i) == '\\') {
					s.replace(i, i+1, "/");
				}
			}
			path = s.toString();
			if (path.charAt(path.length()-1) != '/') {
				path += "/";
			}
			this.relativePath = path;
		} catch (Exception e) {
			System.err.println("Error by setting the resource directory.");
			e.printStackTrace();
		}
	}  // end of setPath(String);

	/**
	 * A loader is a comfortable way to upload files. A
	 * loader can be a File (if <code>name</code> is a
	 * valid file name) or directory of the <code>path</code>
	 * (if <code>name</code> equals "").
	 *
	 * @param name Valid file name or <code>""</code> (all
	 * images of the <code>path</code> directory)
	 * @return <code>true</code> if the file or directory
	 * exists or <code>false</code> if not.
	 */
	public boolean setLoader(String name) {
		if (name == null) return false;
		loader = getFile(name);
		if (loader == null) return false;
		return loader.exists();
	}  // end of setLoader(String);

	/**
	 * Load all the files of the loader.
	 *
	 * @return <code>true</code> if the loader (file or
	 * directory) exists and if no errors occur.<br />
	 * On the otherwise <code>false</code>.
	 */
	public boolean startLoader() {
		if (loader != null && loader.exists()) {
			if (loader.isDirectory()) {
			// load all the valid images in the directory
				return loadDirectory(loader);
			} else if (loader.isFile()) {
				if (loader.canRead()) {
					return readLoaderFile();
				}
			}
		}
		return false;
	}  // end of startLoader();

	private boolean readLoaderFile() {
		BufferedReader br;
		try {
			InputStream is = new FileInputStream(loader);
			InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			System.out.println("-- Reading loader --");
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				// if is an empty line or a commentary
				if (line.length() == 0 ||
					line.startsWith("//")) {
						continue;
				} // else... analysing the line and load
				loadLine(line);
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error reading file loader:\n"
				+loader.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Analysing a line of text that contains a
	 * load information.
	 *
	 * @param line Information to load an image/s.
	 */
	public void loadLine(String line) {
		if (line.startsWith("1 ")) {
			loadSingleFile(line);					
		} else if (line.startsWith("2 ")) {
			loadNumeratedFiles(line);
		}
	}

	private void loadSingleFile(String line) {
		int equals = line.indexOf("=");
		if (equals == -1) {
			load(line.substring(2));
		} else {
			load(line.substring(equals+1),
				line.substring(2, equals));
		}
	}

	private void loadNumeratedFiles(String line) {
		boolean error = false;
		StringTokenizer st = new StringTokenizer(line, " ");
		int tokens = st.countTokens();
		st.nextToken();
		
		String fileName = st.nextToken();
		String loadedName = null;
		int equals = fileName.indexOf("=");
		if (equals != -1) {
			loadedName = fileName.substring(0, equals);
			fileName = fileName.substring(equals+1);
		}
		int wildcard = fileName.indexOf("*");
		if (wildcard != -1) {
			int i=0;
			// stores the enumerated different names
			String fullName;
			// for different number of argumenst
			if (tokens == 2) {
				do {
					fullName = 
						fileName.substring(0,wildcard)+(i++)
						+fileName.substring(wildcard+1);
				} while (load(fullName,loadedName+(i-1)));
			} else if (tokens >= 3) {
				int numFiles = 0;
				try {
					numFiles = Integer.parseInt(st.nextToken());
					if (tokens == 4) {
						i = Integer.parseInt(st.nextToken());
						numFiles += i;
					}
				} catch (NumberFormatException e) {
					error = true;
					//e.printStackTrace();
				}
				while (i<numFiles) {
					fullName =
						fileName.substring(0,wildcard)+(i++)
						+fileName.substring(wildcard+1);
					load(fullName, loadedName);
					//if (!load(fullName)) break;
				}
			}
		} else {
			error = true;
		}
		if (error) {
			System.err.println("Error format in line: "+line);
		}
	}

	/**
	 * Load all the directory images.
	 *
	 * @param d Directory with the images which will
	 * be loaded.
	 */
	public boolean loadDirectory(File d) {
		if (!d.isDirectory()) return false;
		File[] f = d.listFiles();
		for (File file : f) {
			if( file.isFile() &&
				hasValidExtension(file) ) {
					load(file);
			}
		}
		return true;
	}  // end of loadDirectory(File);

	public boolean load(File f) {
		return load(f, f.getName(), false);
	}  // end of load(File);

	/**
	 * You must to implements that method if you want to load
	 * any Object. Don't forget add the object in the 
	 * <code>loaded:ArrayList</code>.
	 * Don't load anything if return <code>false</code>.
	 */
	public abstract boolean load(File f, String name, boolean rewrite);

	public boolean load(String n) {
		return load(n, n);
	}  // end of load(String);

	public boolean load(String fileName, String name) {
		File f = new File(getPath()+fileName);
		if (f.exists()) {
			if (f.isDirectory()) {
				loadDirectory(f);
			} else if (f.isFile() && hasValidExtension(f)) {
				return load(f, name, false);
			}
		} else {
			System.err.println("No found: "+f.getPath());
		}
		return false;
	}  // end of load(String, String);

	/**
	 * Returns an Object.
	 */
	public Object getObject(String name, boolean load, boolean rewrite) {
		Object aux = loaded.get(name);
		if ((load && aux == null) || rewrite) {
			File f = new File(getPath()+name);
			if (f.exists() && f.isFile() && hasValidExtension(f)) {
				load(f, name, rewrite);
			}
		}
		return loaded.get(name);
	}

	/**
	 * Remove a previous loaded image.
	 */
	public void removeObject(String name) {
		loaded.remove(name);
	}

	/**
	 * Remove all the loaded images.
	 */
	public void removeAllObjects() {
		loaded.clear();
	}

	public File getFile(String name) {
		try {
			File file = new File(packageDirectory
					+ relativePath + name);
			return file;
		} catch (Exception e) {
			System.err.println("Error loanding a file "+name);
			//e.printStackTrace();
		}
		return null;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public String getPath() {
		return packageDirectory+relativePath;
	}

	public String getPackagePath() {
		return packageDirectory;
	}

	public File getLoader() {
		return loader;
	}

	public void changeName(String name, String newName) {
		loaded.put(newName, loaded.get(name));		
		removeObject(name);
	}

	public void putObject(String name, Object object) {
		loaded.put(name, object);
	}

	/**
	 * Check if the name of the <code>f</code> have
	 * any of the extensions indicated in the <code>ArrayList
	 * validExtensions</code>.
	 *
	 * @param f File to check.
	 * @return <code>true</code> if find a extension
	 * or <code>false</code> if not.
	 */
	public boolean hasValidExtension(File f) {
		for (int i=0; i<validExtensions.size(); i++) {
			String ext = "."+validExtensions.get(i).toLowerCase();
			if (ext.equals(".")) {
				return true;
			}
			if (f.getName().toLowerCase().lastIndexOf(ext) 
				+ext.length() == f.getName().length()) {
				// the file extension is allowed
					return true;
			}
		}
		return false;
	}  // end of hasValidExtension(File);

	/**
	 * Checks if exists the loader file or directory.
	 *
	 * @return <code>true</code> if exists
	 * or <code>false</code> if not.
	 */
	public boolean existsLoader() {
		if (loader != null)
			return loader.exists();
		return false;
	}


}  // end of Loader class.


//  end of Loader.java
