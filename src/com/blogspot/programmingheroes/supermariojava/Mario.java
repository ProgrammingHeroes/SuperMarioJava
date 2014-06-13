
// Mario.java -------------------------------------------------------

package com.blogspot.programmingheroes.supermariojava;


import java.awt.event.*;
import java.awt.*;


/**
 *
 */
public class Mario extends Player {

	public Mario(Stage s) {
		super(s);
		// Tamaño predeterminado del jugador.
		setPreferredSize(map.tileXSize, map.tileYSize);
		// Rectángulo para las colisiones
		bounds.add(new Rectangle(6, 1, width-12, height-1));
		}

	/* Asignación de las teclas que controlan al jugador. */
	public void createKeys() {
		keyLeft = KeyEvent.VK_LEFT;
		keyRight = KeyEvent.VK_RIGHT;
		keyRun = KeyEvent.VK_SHIFT;
		keyJump = KeyEvent.VK_SPACE;
		keyCrouch = KeyEvent.VK_DOWN;
	}

	/* Asignación de las imágenes que representan al jugador. */
	public void createImgs() {
		imgLStop = "marioLStop";
		imgRStop = "marioRStop";
		imgLJump = "marioLJump";
		imgRJump = "marioRJump";
		imgLFall = "marioLJump";
		imgRFall = "marioRJump";
		imgRight = "marioRWalk*";
		imgLeft = "marioLWalk*";
		imgRSlip = "marioRSlip";
		imgLSlip = "marioLSlip";
		imgRCrouch = "marioRCrouch";
		imgLCrouch = "marioLCrouch";
	}

}  // end of Mario class

//  end of Mario.java -----------------------------------------------