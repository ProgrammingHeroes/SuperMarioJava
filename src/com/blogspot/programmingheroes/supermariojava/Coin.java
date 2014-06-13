
// Coin.java ---------------------------------------------------------

package com.blogspot.programmingheroes.supermariojava;


import java.applet.*;


/**
 *
 */
public class Coin extends WorldObject {

	/* Permite que todos los objetos de esta misma clase
	 * puedan pasar de una imagen a otra todos sincronizados.
	 */
	protected static int indexClass = 0;

	/* Indica que es necesario actualizar el indexClass. */
	protected static boolean changeImg = false;

	/* Número de imágenes que representan al sprite. */
	public static int LENGHT_IMAGES = 4;

	/* Permite actualizar las imágenes para todos los objetos
	 * de esa clase. */
	public static void actClass() {
		if (changeImg) {
			indexClass = (indexClass+1)%LENGHT_IMAGES;
			changeImg = false;
		}
	}

	/* Cuenta el número de monedas que se han creado. */
	public static int N_COINS = 0;

	/* Cuenta las monedas cogidas. */
	public static int COINS_CATCHED = 0;

	public String imgNormal = "coin*_0";
	
	public String imgAnimation = "coinEfect*_0";

	// Indica si la moneda está reproduciendo
	// el efecto que le va a hacer desaparecer.
	public boolean effect = false;

	public static AudioClip[] audio;
	public static int indexAudio;
	public static boolean first = true;

	public Coin(Stage s) {
		super(s);
		setPreferredSize(map.tileXSize, map.tileYSize);
		setImages(imgNormal, 0, LENGHT_IMAGES);
		// Rectángulo para las colisiones
		bounds.add(new java.awt.Rectangle(
					2, 2, width-4, height-4));
		N_COINS++;
		if (first) {
			first = false;
			indexAudio = 0;
			audio = new AudioClip[5];
			for (int i=0; i<audio.length; i++) {
				audio[i] = stage.getSoundsLoader()
					.getAudio("coin.wav", true, true);
			}
		}
	}

	public void act() {
		move();
		if (effect) {
			int frameFrec = (int)(stage.getFPS()/20);
			if (frameFrec==0 
				|| stage.getTotalUpdates()%frameFrec == 0) {
					if (nextImg()) {
						delete = true;
						COINS_CATCHED++;
					}
			}
		} else {
			int frameFrec = (int)(stage.getFPS()/10);
			if (frameFrec==0 
				|| stage.getTotalUpdates()%frameFrec == 0) {
					setImage(indexClass);
					changeImg = true;
			}
		}
	}

	public void collision(Sprite s) {
		// prueba la diferencia entre
		// utilizar un mismo sonido
		// y utilizar copias de ese sonido
		if (!effect) {
			stage.getSoundsLoader().play(
				"coin.wav", false);
			audio[indexAudio].play();
			indexAudio = (indexAudio+1)%audio.length;
			setImages(imgAnimation, 0, 7);
			effect = true;
			speed.setY(2);
		}
	}

}  // fin de la clase Coin

//  fin de Coin.java ------------------------------------------------