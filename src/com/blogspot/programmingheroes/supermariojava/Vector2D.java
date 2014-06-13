
// Vector2D.java -----------------------------------------------------
// TODO Traducir todos los comentarios al inglés!!!

package com.blogspot.programmingheroes.supermariojava;


/**
 * Representa un vector matemático sobre los ejes X e Y (2
 * dimensiones). Es de gran utilidad para representar el vector
 * velocidad de objetos o personajes en toda clase de juegos.
 * Hay que tener en cuenta la diferencia que existe entre los
 * ejes matemáticos y los ejes gráficos de la pantalla. El eje X
 * es igual para los dos, pero, en el eje Y hay diferencias.
 * En el caso matemático el eje Y aumenta conforme subimos en la
 * gráfica, es decir todo lo contrario a el eje gráfico de la
 * pantalla. El Vector2D utiliza un eje matemático.</ br>
 * Por ejemplo, un vector con las coordenadas (2,3) se encuentra en
 * el 1º cuadrante, por lo que debería ir arriba a la derecha,
 * pero sobre la pantalla ese vector se encuentra en el 4º cuadrante,
 * abajo a la izquierda.
 */
public class Vector2D {

	// Vector properties
	protected double x, y, module, angle;

	/**
	 * Defauld constructor that init all the
	 * properties to 0.</ br>
	 * Module = 0;</ br>
	 * Angle = 0;</ br>
	 * X = 0;</ br>
	 * Y = 0;</ br>
	 */
	public Vector2D() {
		this(0, 0);
	}

	/**
	 * Vector2D with the specificated module.</ br>
	 * Angulo = 0;</ br>
	 * X = module;</ br>
	 * Y = 0;</ br>
	 */
	public Vector2D(double module) {
		this(module, 0);
	}

	/**
	 * Vector2D with the specificated module and angle.
	 * Las coordenadas son obtenidas por trigonometría.</ br>
	 * X = cos(ángulo)*módulo;</ br>
	 * Y = sen(ángulo)*módulo;</ br>
	 */
	public Vector2D(double module, double angle) {
		this.setModule(module);
		this.setAngle(angle);
	}

	/**
	 * Crea un nuevo Vector2D a partir de uno ya existente. Se puede
	 * decir que crea una copia de éste.
	 */
	public Vector2D(Vector2D vector) {
		this.module = vector.getModule();
		this.angle = vector.getAngle();
		this.x = vector.getAccurateX();
		this.y = vector.getAccurateY();
	}


	// SET methods --------------------------------------------------
	public void setModule(double module) {
		this.module = module;
		updateXY();
	}

	/**
	 * Cambia el ángulo del vector y actualiza las coordenadas para
	 * que el módulo siga siendo el mismo.
	 *
	 * @param angle El ángulo en radianes
	 */
	public void setAngle(double angle) {
		this.angle = angle % (2*Math.PI);
		if (this.angle > Math.PI) {
			this.angle = -angle % Math.PI;
		} else if (this.angle < -Math.PI) {
			this.angle = 2*Math.PI+angle;
		} else if (this.angle < 0) {
			this.angle = angle % Math.PI;
		}
		updateXY();
	}

	/**
	 * Cambia el ángulo utilizando grados.
	 *
	 * @param angle El ángulo en grados.
	 */
	public void setAngle(int angle) {
		this.setAngle(Math.toRadians(angle));
	}

	public void setX(double x) {
		this.x = x;
		updateModule();
		updateAngle();
	}

	public void setY(double y) {
		this.y = y;
		updateModule();
		updateAngle();
	}
	//  end of SET methods ------------------------------------------


	// GET methods --------------------------------------------------
	public int getX() {
		return ((int)x);
	}

	public int getY() {
		return ((int)y);
	}

	public double getAccurateX() {
		return x;
	}

	public double getAccurateY() {
		return y;
	}

	public double getAngle() {
		return angle;
	}

	public double getModule() {
		return module;
	}
	//  end of GET methods ------------------------------------------


	// AXIS methods -------------------------------------------------
	/**
	 * Invierte el eje X y realiza los cambios oportunos
	 * en el ángulo.
	 */
	public void invertX() {
		if (Math.abs(angle) > Math.PI/2) {
			angle -= Math.PI/2;
		} else {
			angle += Math.PI/2;
		}
		x = -x;
	}

	/**
	 * Invierte el eje Y, por lo tanto también se invierte en ángulo.
	 */
	public void invertY() {
		angle = -angle;
		y = -y;
	}
	//  end of AXIS methods -----------------------------------------

	// PRIVATE methods ----------------------------------------------
	/* Update the X and Y trigonometry.
	 * Used when the module or angle is changed.
	 */
	private void updateXY() {
		x = Math.cos(angle)*module;
		y = Math.sin(angle)*module;
	}

	/* Update the module by the Pitagoras' theorem.
	 * Used when X or Y are changed by setX(double) or setY(double).
	 */
	private void updateModule() {
		// by Pitagoras
		module = Math.sqrt(x*x+y*y);
	}

	/* Update the angle by trigonometry.
	 * Used when X or Y are changed by setX(double) or setY(double).
	 */
	private void updateAngle() {
		// by trigonometry
		if (x == 0 && y == 0) {
			angle = 0;
		} else if (x == 0) {
			angle = (y > 0) ? Math.PI/2: -Math.PI/2;
		} else if (y == 0) {
			angle = (x > 0) ? 0 : Math.PI;
		} else {
			angle = Math.atan(y/x);
		}
	}
	//  end of PRIVATE methods --------------------------------------

}  // end of Vector2D class

//  end of Vector2D.java
