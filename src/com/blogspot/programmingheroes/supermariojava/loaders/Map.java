
// Map.java ---------------------------------------------------------

package com.blogspot.programmingheroes.supermariojava.loaders;


import java.awt.*;
import java.io.*;
import java.util.*;

import com.blogspot.programmingheroes.supermariojava.Background;
import com.blogspot.programmingheroes.supermariojava.Brick;
import com.blogspot.programmingheroes.supermariojava.Coin;
import com.blogspot.programmingheroes.supermariojava.CoinBox;
import com.blogspot.programmingheroes.supermariojava.Player;
import com.blogspot.programmingheroes.supermariojava.Sprite;
import com.blogspot.programmingheroes.supermariojava.Stage;
import com.blogspot.programmingheroes.supermariojava.WorldObject;


/**
 *
 */
public class Map {

	public static final String MAP_PATH = "res/maps/";
	public static final String FILE_PREFIX = "level*_*";

	public static final String TAG_BACKGROUND = "back:";
	public static final String TAG_FRONTGROUND = "front:";
	public static final String TAG_MUSIC = "music:";
	public static final String TAG_TIME = "time:";

	public static final int TILE_X_SIZE = 32;
	public static final int TILE_Y_SIZE = 32;

	public static final int MAX_SIZE_X = 5000;
	public static final int MAX_SIZE_Y = 5000;

	public static final int DISPLAY_X = 30;
	public static final int DISPLAY_Y = 20;

	public float xMap = 0, yMap = 0;
	public double xSpeed = 0, ySpeed = 0;
	public int tileXSize = TILE_X_SIZE, tileYSize = TILE_Y_SIZE;
	public int sizeX = 0, sizeY = 0;
	public int displayX = DISPLAY_X, displayY = DISPLAY_Y;
	public int tileX = 0, tileY = 0, accurateX = 0, accurateY = 0;
	public boolean movingX = false, movingY = false;

	public int world, level;

	public String fileName;

	public WorldObject[][] spriteMap;

	public StringBuffer[][] stringMap;

	public ArrayList<Background> backs;
	public ArrayList<Background> fronts;
	public ArrayList<String> music;
	public ArrayList<Player> players;

	/**
	 *  Lugares desde los cuales es posible que empieze
	 * un jugador. El número de puntos marca el máximo
	 * de jugadores que pueden jugar este nivel.
	 * Por lo tanto, como mínimo, debe haber uno.
	 */
	public ArrayList<Point> startingPlaces;

	public Stage stage;


	/**
	 *
	 */
	public Map(Stage s, String fileName) {
		this.stage = s;
		this.fileName = fileName;
		this.world = -1;
		this.level = -1;
	}

	/**
	 *
	 */
	public Map(Stage s, int world, int level) {
		StringBuffer sb = new StringBuffer(FILE_PREFIX);
		int first = sb.indexOf("*");
		int last = sb.lastIndexOf("*");
		if (first != last) {
			sb.replace(first, first+1, ""+world);
			sb.replace(last, last+1, ""+level);
			this.stage = s;
			this.fileName = sb.toString();
			this.world = world;
			this.level = level;
		} else {
			System.err.println("FILE_PREFIX is incorrect.");
			System.exit(-1);
		}
	}

	public void initMap() {
		spriteMap = new WorldObject[MAX_SIZE_Y][MAX_SIZE_X];
		backs = new ArrayList<Background>();
		fronts = new ArrayList<Background>();
		music = new ArrayList<String>();
		players = new ArrayList<Player>();
		startingPlaces = new ArrayList<Point>();
		readMapFile();
		WorldObject[][] s = new WorldObject[sizeY][sizeX];
		for (int i=0; i<sizeY; i++) {
			for (int j=0; j<sizeX; j++) {
				s[i][j] = spriteMap[i][j];
			}
		}
		spriteMap = s;
		startMusic();
	}

	private boolean readMapFile() {
		BufferedReader br;
		try {
			String packageDirectory = getClass()
					.getClassLoader().getResource("")
					.getPath();
			packageDirectory = packageDirectory.substring(0,
				packageDirectory.lastIndexOf("bin/"));
			InputStream is = new FileInputStream(packageDirectory + MAP_PATH + "/"+fileName);
			InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			System.out.println("-- Reading Map File "+fileName+"--");
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				System.out.println(line);
				// Si es una linea vacía o un comentario...
				if (line.length() == 0 ||
					line.startsWith("//")) {
						continue;
				} else if (line.startsWith(TAG_BACKGROUND)) {
					addBackground(createBackground(line.substring(
						TAG_BACKGROUND.length(), line.length())));
					continue;
				}  else if (line.startsWith(TAG_FRONTGROUND)) {
					addFrontground(createBackground(line.substring(
						TAG_FRONTGROUND.length(), line.length())));
					continue;
				} else if (line.startsWith(TAG_MUSIC)) {
					addMusic(line.substring(
						TAG_MUSIC.length(), line.length()));
					continue;
				} // si no... analizamos la linea.
				if (sizeX < line.length()) {
					sizeX = line.length();
				} 
				readMapLine(line, sizeY);
				sizeY++;
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error reading map file");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void readMapLine(String line, int y) {
		for (int i=0; i<line.length(); i++) {
			readChar(line.charAt(i), i, y);
		}
	}

	private void readChar(char c, int x, int y) {
		WorldObject o = null;
		switch (c) {
			case 'B':
				o = new Brick(stage);
				break;
			case 'b':
				o = new Brick(stage);
				o.setSupportPlayer(false);
				break;
			case 'c':
				o = new Coin(stage);
				break;
			case '?':
				o = new CoinBox(stage);
				break;
			case '*':
				startingPlaces.add(new Point(
					x*tileXSize, y*tileYSize));
				break;
		}
		if (o != null) {
			o.setX(x*tileXSize);
			o.setY(y*tileYSize);
			spriteMap[y][x] = o;
		} else {
			spriteMap[y][x] = null;
		}
	}
public boolean endlevel = false;
	public void nextLevel() {
		endlevel = true;
		try{Thread.sleep(500);}catch(Exception e){}
		StringBuffer s = new StringBuffer(fileName);
		s.replace(fileName.lastIndexOf(""+level),
		fileName.lastIndexOf(""+level)+1, ""+(++level));
		fileName = s.toString();
		initMap();
		endlevel = false;	
	}

	public Background createBackground(String info) {
		StringTokenizer st = new StringTokenizer(info);
		if (st.countTokens() == 1) {
			Background b = new Background(stage, info);
			return b;
		} else if (st.countTokens() != 8) {
			System.err.println("Error creating Background.\n"
				+"Format: name factorSpeed defauldSpeed "
				+"x y width height alpha");
			return null;
		}
		String name = st.nextToken();
		double speedFactor = 0, defSpeed = 0;
		int x = 0, y = 0, width = 0, height = 0;
		float alpha = 0;
		try {
			speedFactor = Double.parseDouble(
				st.nextToken());
			defSpeed = Double.parseDouble(
				st.nextToken());
			String s = st.nextToken();
			x = Integer.parseInt(s);
			s = st.nextToken();
			y = Integer.parseInt(s);
			s = st.nextToken();
			if (s.startsWith("-")) {
				width = Background.IMG_DIMENSIONS;
			} else if (s.toLowerCase().startsWith("w")) {
				width = getDisplayableWidth();
			} else {
				width = Integer.parseInt(s);
			}
			s = st.nextToken();
			if (s.startsWith("-")) {
				height = Background.IMG_DIMENSIONS;
			} else if (s.toLowerCase().startsWith("h")) {
				height = getDisplayableHeight();
			} else {
				height = Integer.parseInt(s);
			}
			s = st.nextToken();
			if (s.startsWith("-")) {
				alpha = 1.0F;
			} else {
				alpha = Float.parseFloat(s);
			}
		} catch (NumberFormatException e) {
			System.err.println("Error adding Frontground.");
			e.printStackTrace();
			return null;
		}
		Background b = new Background(stage, name, speedFactor,
							 defSpeed, width, height, alpha);
		b.setX(x);
		b.setY(y);
		return b;
	}

	public void addBackground(Background b) {
		backs.add(b);
	}

	public void addFrontground(Background f) {
		fronts.add(f);
	}

	public void addPlayer(Player player) {
		if (players.size() == startingPlaces.size()) {
			System.err.println("No more players allowed in"
				+" this map. Only "+players.size()+".");
		} else {
			Point point = startingPlaces.get(
				players.size());
			player.setPosition(point);
			players.add(player);
			// TODO hacer que el mapa muestre al jugador
			xMap = Float.parseFloat(""+point.getX());
			yMap = Float.parseFloat(""+point.getY())
				-getDisplayableHeight()+64;
		}
	}

	public void addMusic(String name) {
		music.add(name);
	}

	public void startMusic() {
		for (int i=0; i<music.size(); i++) {
			String songName = music.get(i);
			if (songName != null) {
				stage.getSoundsLoader().play(
					music.get(i), true);
			}
		}
	}

	public boolean readyRightXMap(Player p) {
		return !movingX && (p.getX()-xMap >= getDisplayableWidth()/2)
			&& (xMap < getWidth()-getDisplayableWidth());
	}

	public boolean readyLeftXMap(Player p) {
		return !movingX && (p.getX()-xMap <= getDisplayableWidth()/2)
			&& (xMap > 0);
	}

	public boolean readyUpYMap(Player p) {
		return !movingY && (p.getY()-yMap <= getDisplayableHeight()/4)
			&& (yMap > 0);
	}

	public boolean readyDownYMap(Player p) {
		return !movingY && (p.getY()-yMap >= getDisplayableHeight()/2)
			&& (yMap < getHeight()-getDisplayableHeight());
	}

	public void setSpeedX(double s) {
		if (s < 0 && -xSpeed > s) {
			xSpeed = s;
		}  else if (s > 0 && xSpeed < s) {
			xSpeed = s;
		}
	}

	public void setSpeedY(double s) {
		if (s < 0 && -ySpeed > s) {
			ySpeed = s;
		} else if (s > 0 && ySpeed < s) {
			ySpeed = s;
		}
	}

	public void act() {
		if (endlevel) return;
		// Actualizamos las clases de manera estática.
		// De esta forma hacemos que las imágenes sean
		// las mismas para todos los WorldObjects(Sprites).
		Coin.actClass();
		Brick.actClass();
		CoinBox.actClass();
		// Movemos el mapa si es necesario.
		xMap += xSpeed;
		if (xMap < 0) {
			xSpeed = xSpeed-xMap;
			xMap = 0;
		} else if (xMap > getWidth()-getDisplayableWidth()) {
			xMap = getWidth()-getDisplayableWidth();
		}
		yMap += ySpeed;
		if (yMap < 0) {
			ySpeed = ySpeed-yMap;
			yMap = 0;
		} else if (yMap > getHeight()-getDisplayableHeight()) {
			yMap = getHeight()-getDisplayableHeight();
		}
		// Movemos los fondos.
		for (int i=0; i<backs.size(); i++) {
			Background b = backs.get(i);
			b.setSpeedX(-xSpeed);
			//b.setSpeedY(-ySpeed);
			b.act();
		}
		for (int i=0; i<fronts.size(); i++) {
			Background b = fronts.get(i);
			b.setSpeedX(-xSpeed);
			//b.setSpeedY(-ySpeed);
			b.act();
		}
		// Quitamos la posible velocidad si no es constante.
		if (!movingX) {
			xSpeed = 0;
		}
		if (!movingY) {
			ySpeed = 0;
		}
		// Actualizamos los sprites estáticos.
		tileX = (int)xMap/tileXSize;
		tileY = (int)yMap/tileYSize;
		accurateX = -(int)xMap%tileXSize;
		accurateY = -(int)yMap%tileYSize;
		for (int i=tileX; i<tileX+displayX+1 && i<sizeX; i++) {
			for (int j=tileY; j<tileY+displayY+1 && j<sizeY; j++) {
				Sprite s = spriteMap[j][i];
				if (s != null) {
					s.act();
					// Colisones con los objetos de al lado
					if (inMap(i+1,j) && spriteMap[j][i+1] != null) {
						if (s.collidesWith(spriteMap[j][i+1], false)) {
							s.collision(spriteMap[j][i+1]);
							spriteMap[j][i+1].collision(s);
						}
					}
					if (inMap(i,j+1) && spriteMap[j+1][i] != null) {
						if (s.collidesWith(spriteMap[j+1][i], false)) {
							s.collision(spriteMap[j+1][i]);
							spriteMap[j+1][i].collision(s);
						}
					}
					if (inMap(i+1,j+1) && spriteMap[j+1][i+1] != null) {
						if (s.collidesWith(spriteMap[j+1][i+1], false)) {
							s.collision(spriteMap[j+1][i+1]);
							spriteMap[j+1][i+1].collision(s);
						}
					}
					if (s.isToDelete()) {
						spriteMap[j][i] = null;
					}
				}
			}
		}
		// Actualizamos los jugadores y detectamos colisiones.
		for (int i=0; i<players.size(); i++) {
			players.get(i).act();
			checkCollisions(players.get(i));
		}
	}int jope = 0;

	public void paint(Graphics g) {
		if (endlevel) return;
		// Pintamos los fondos.
		for (int i=0; i<backs.size(); i++) {
			Background b = backs.get(i);
			b.paint(g);
		}
		// Pintamos los sprites estáticos.
		for (int i=tileX; i<tileX+displayX+1 && i<sizeX; i++) {
			for (int j=tileY; j<tileY+displayY+1 && j<sizeY; j++) {
				Sprite s = spriteMap[j][i];
				if (s != null) {
					s.paint(g, s.getX()-xMap,
						s.getY()-yMap,
						tileXSize, tileYSize);
				}
			}
		}
		// Pintamos los jugadores.
		for (int i=0; i<players.size(); i++) {
			Player p = players.get(i);
			p.paint(g, p.getX()-xMap, p.getY()-yMap,
					tileXSize, tileYSize);
		}
		// Pintamos los frontground
		for (int i=0; i<fronts.size(); i++) {
			Background b = fronts.get(i);
			b.paint(g);
		}
	}

	public void checkCollisions(Player p) {
		double colP = p.getX()/tileXSize;
		double filP = p.getY()/tileYSize;
		checkTile(Math.floor(colP), Math.floor(filP), p);
		checkTile(Math.ceil(colP), Math.ceil(filP), p);
		checkTile(Math.ceil(colP), Math.floor(filP), p);
		checkTile(Math.floor(colP), Math.ceil(filP), p);
	}

	public boolean checkTile(double x, double y, Player p) {
		if (inMap(x, y)) {
			WorldObject o = spriteMap[(int)y][(int)x];
			if (o.collidesWith(p, false)) {
				o.collision(p);
				p.collision(o);
				return true;
			}
		}
		return false;
	}

	public boolean inMap(double x, double y) {
		return (x >= 0 && y >= 0 && x < sizeX
			&& y < sizeY) && (spriteMap[(int)y][(int)x] != null);
	}

	// Métodos GET --------------------------------------------------
	public int getWidth() {
		return sizeX*tileXSize;
	}

	public int getDisplayableWidth() {
		return displayX*tileXSize;
	}

	public int getHeight() {
		return sizeY*tileYSize;
	}

	public int getDisplayableHeight() {
		return displayY*tileYSize;
	}
	//  fin de métodos GET -------------------------------------------

}  // fin de la case Map

// fin de Map.java --------------------------------------------------