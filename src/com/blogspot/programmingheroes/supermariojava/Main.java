
// PlatformGame.java -------------------------------------------------

package com.blogspot.programmingheroes.supermariojava;


import java.awt.*;
import java.awt.event.*;

import com.blogspot.programmingheroes.supermariojava.loaders.ImagesLoader;
import com.blogspot.programmingheroes.supermariojava.loaders.Map;
import com.blogspot.programmingheroes.supermariojava.loaders.SoundsLoader;


/**
 *
 */
public class Main extends Stage {

	// For load images, sounds and maps.
	private ImagesLoader loader;
	private SoundsLoader sounds;
	private Map map;

	// Gravedad del escenario. Defecto 0.2
	private float gravity = 0.2F;

	Point pointCursor = new Point(-1,-1);

	public Main(boolean applet) {
		super(CANVAS);
		setFPS(80);
		setSize(960, 640);
		// Creamos el mapa en el mundo=1 nivel=1.
		map = new Map(this, 1, 1);
		// Creamos los cargadores pero de momento
		// no cargamos nada.
		loader = new ImagesLoader("res/img", "loader");
		sounds = new SoundsLoader("res/sounds", "loader");
		// Añadimos los cargadores de sonido y de
		// imagen a el objeto Stage (superclase).
		setImagesLoader(loader);
		setSoundsLoader(sounds);
	}

	public Main() {
		super(JFRAME);
		setFPS(80);
		setSize(960-6, 640-6);
		window.setResizable(false);
		// Creamos el mapa en el mundo=1 nivel=1.
		map = new Map(this, 1, 1);
		// Creamos los cargadores pero de momento
		// no cargamos nada.
		loader = new ImagesLoader("res/img", "loader");
		sounds = new SoundsLoader("res/sounds", "loader");
		// Añadimos los cargadores de sonido y de
		// imagen a el objeto Stage (superclase).
		setImagesLoader(loader);
		setSoundsLoader(sounds);
	}
	
	public synchronized void initStage() {
		// Cargamos las imágenes y los sonidos
		// que están indicados en el archivo externo.
		loader.startLoader();
		sounds.startLoader();
		
		// Iniciamos el mapa.
		map.initMap();

		// Creamos un jugador.
		Mario m = new Mario(this);
		map.addPlayer(m);
	}

	public synchronized void updateStage() {
		map.act();
		if (!gameOver && Coin.N_COINS == Coin.COINS_CATCHED) {
			gameOver();
			final Stage s = this;
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (Exception e) {}
					Coin.N_COINS = 0;
					gameOver = false;
					map.nextLevel();
					map.addPlayer(new Mario(s));
					Coin.COINS_CATCHED = 0;
				}
			}).start();
		}
	}

	public synchronized void renderStage(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0,0,WIDTH,HEIGHT);
		map.paint(g);
		// Indicamos que se ha llegado al final
		// del juego si es necesario.
		if (gameOver) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.WHITE);
			g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			g2.setFont(new Font(Font.MONOSPACED,
				Font.BOLD, 30));
			g2.drawString("Fin del juego",
				WIDTH/2-100, HEIGHT/2-10);
		}
		// PARA SIMULAR OSCURIDAD EN EL MAPA
		//BufferedImage b =
		//	new BufferedImage(WIDTH, HEIGHT, 2);
		//Graphics2D gg = (Graphics2D)b.getGraphics();
		//gg.setColor(Color.BLACK);
		//gg.setPaint(new GradientPaint(WIDTH/2, 0, Color.BLACK,
		//	WIDTH/2, HEIGHT, Color.WHITE)); 
		//gg.fillRect(0,0,WIDTH,HEIGHT);
		//b = imgEffects.returnAlphaImg(b, 0.6F);
		//g.drawImage(b, 0,0,null);
	}

	public synchronized void mouseMoved(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		for (int i=0; i<map.players.size(); i++) {
			map.players.get(i).keyPressed(e);
		}
	}

	public void keyReleased(KeyEvent e) {
		for (int i=0; i<map.players.size(); i++) {
			map.players.get(i).keyReleased(e);
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public float getGravity() {
		return gravity;
	}

	public Map getCurrentMap() {
		return map;
	}

	public static void main(String[] args) {
		Main p = new Main();
		p.getWindow().setVisible(true);
		p.startGame();
	}
}  // fin de la clase PlatformGame

// PlatformGame.java ------------------------------------------------