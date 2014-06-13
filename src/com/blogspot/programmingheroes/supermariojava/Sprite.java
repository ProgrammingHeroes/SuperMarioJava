
// Sprite.java -------------------------------------------------------

package com.blogspot.programmingheroes.supermariojava;


import java.awt.*;
import java.awt.image.*;
import java.util.*;


/**
 * Sprite class util for games.
 */
public class Sprite {

	/**
	 * Indica si se van a dibujar los bordes detecta
	 * colisiones de los Sprites.
	 */
	public static boolean drawBounds = false;

	/**
	 * Coordenadas que marcan la posicion del objeto o personaje.
	 * Se utilizan estas coordenadas de tipo float porque
	 * permiten realizar movimientos con mayor precisión.
	 */
	protected float x, y;

	/**
	 * Indica el ancho y algo de la imagen actual del sprite.
	 */
	protected int width, height;

	/**
	 * Profundidad del objeto, utilizada en las colisiones.
	 * Por defecto es 0, pero puede tomar cualquier valor.
	 */
	protected int z;

	/**
	 * Conjunto de rectángulos que sirven para
	 * controlar las colisiones.
	 */
	protected ArrayList<Rectangle> bounds;

	/**
	 * Velocidad representada por un objeto de la clase Vector2D.
	 */
	protected Vector2D speed;

	/**
	 * Nombre de las imágenes cargadas con anterioridad desde un
	 * ImagesLoader.
	 */
	protected String[] imgNames;

	/**
	 * Imagen actual del Sprite. Su nombre se encuentra en el
	 * array imgNames.
	 */
	protected BufferedImage img;

	/**
	 * Indica el índice en el que se encuentra la imagen actual
	 * del Sprite.
	 */
	protected int imgIndex;

	/**
	 * Referencia al escenario principal.
	 */
	protected Stage stage;

	/**
	 * Indica si es visible el componente. Si no lo es,
	 * el método paint(Graphics) no hará nada.
	 */
	protected boolean visible;

	/**
	 * Indica si está activo. Si no lo está, no habrá cambio
	 * entre las imágenes del Sprite.
	 */
	protected boolean active;

	/**
	 * Evita que cada vez que se cargue una imagen
	 * se cambie el valor del acho y del alto por el
	 * de la nueva imagen.
	 */
	protected boolean preferredSize;

	/**
	 * Indica cuando es posible borrar el objeto.
	 */
	protected boolean delete;



	public Sprite(Stage s) {
		stage = s;
		x = 0;
		y = 0;
		z = 0;
		visible = true;
		active = true;
		preferredSize = false;
		delete = false;
		imgIndex = 0;
		speed = new Vector2D();
		bounds = new ArrayList<Rectangle>();
	}



	/**
	 * Actualiza la posición del objeto dependidendo del valor del
	 * vector velocidad.
	 */
	public void move() {
		x += speed.getAccurateX();
		y -= speed.getAccurateY();
	}

	/**
	 * Actualiza la posición X del objeto dependidendo
	 * del valor de la X del vector velocidad.
	 */
	public void moveX() {
		x += speed.getAccurateX();
	}

	/**
	 * Actualiza la posición Y del objeto dependidendo
	 * del valor de la Y del vector velocidad.
	 */
	public void moveY() {
		y -= speed.getAccurateY();
	}

	/**
	 * Cambia la lista de nombres de las imágenes.
	 * Establece la primera imagen de ese array como la
	 * actual.
	 */
	public void setImages(String[] imgNames) {
		setImages(imgNames, 0);
	}

	/**
	 * Cambia la lista de nombres de las imágenes.
	 * La imagen inicial va indicada por <code>initIndex</code>.
	 */
	public void setImages(String[] imgNames, int initIndex) {
		this.imgNames = imgNames;
		imgIndex = initIndex;
		this.setImage(imgNames[initIndex]);
	}

	/**
	 * Cambia la imagen por la siguiente del array. Si la siguiente
	 * imagen corresponde a la última del array devuelve un true,
	 * si no, false.
	 * El Sprite debe de encontrarse activo.
	 */
	public boolean nextImg() {
		// Actualizamos en índice para que apunte a la
		// siguiente imagen.
		imgIndex = (imgIndex+1)%imgNames.length;
		this.setImage(imgNames[imgIndex]);
		// Si es la última imagen del array imgNames...
		if (imgIndex == imgNames.length-1) {
			return true;
		}
		return false;
	}

	/**
	 * Cambia la imagen por la anterior del array. Si la anterior
	 * imagen corresponde a la primera del array devuelve un true,
	 * si no, false.
	 * El Sprite debe de encontrarse activo.
	 */
	public boolean previousImg() {
		// Actualizamos en índice para que apunte a la
		// anterior imagen.
		imgIndex = (imgIndex-1)%imgNames.length;
		this.setImage(imgNames[imgIndex]);
		// Si es la última imagen del array imgNames...
		if (imgIndex == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Cambia la imagen que representa al Sprite.
	 * Devuelve si es cargada con éxito.
	 * El Sprite debe de encontrarse activo.
	 */
	public boolean setImage(String name) {
		return setImage(stage.getImagesLoader().getImage(name));
	}

	/**
	 * Cambia la images del Sprite por la que indica
	 * el índice pasado como argumento.
	 */
	public boolean setImage(int index) {
		return setImage(imgNames[index]);
	}

	/**
	 * Carga un conjunto de imágenes numeradas.
	 * 
	 * @param name El nombre de la imágen que debe de
	 * contener el asterisco '*'.
	 * @param n1 Representa en inicio del contador. Número
	 * incluido en la carga
	 * @param n2 El número de imágenes que hay que cargar
	 * a partir del índice n1. El número n1+n2 tmb está
	 * incluido en la lista de nombres de imágenes.
	 */
	public boolean setImages(String name, int n1, int n2) {
		int wildcard = name.indexOf("*");
		if (wildcard != -1 && n2 > 1) {
			imgNames = new String[n2];
			// Contador
			int i = 0;
			// stores the enumerated different names
			String fullName;
			while (i<n2) {
				fullName =
					name.substring(0,wildcard)+(n1+i)
					+name.substring(wildcard+1);
				imgNames[i++] = fullName;
			}
			setImages(imgNames);
			return true;
		} else {
			return setImage(name);
		}
	}

	/**
	 * Cambia la imagen que representa al Sprite.
	 * Devuelve si es cargada con éxito.
	 * El Sprite debe de encontrarse activo.
	 */
	public boolean setImage(BufferedImage img) {
		this.img = img;
		if (img == null || !active) {
			return false;
		}
		if (!preferredSize) {
			width = img.getWidth();
			height = img.getHeight();
		}
		return true;
	}

	/**
	 * Cambia el tamaño de la imagen al especificado por
	 * el programador y evita que se cambie el tamaño
	 * cada vez que se seleccione otra imagen distinta.
	 * Para cambiar a los valores predeterminados de la
	 * imagen basta con introducir valores negativos.
	 */
	public void setPreferredSize(int w, int h) {
		preferredSize = (w <= 0 && h <= 0)?false:true;
		if (preferredSize) {
			width = w;
			height = h;
		} else {
			width = img.getWidth();
			height = img.getHeight();
		}
	}

	/**
	 * Pinta la imagen en las coordenadas x e y,
	 * en caso de que sea visible, y sin ningún efecto.
	 */
	public void paint(Graphics g) {
		if (visible) {
			g.drawImage(img, (int)x, (int)y, width, height, null);
		}
	}

	/**
	 * Pinta la imagen en las coordenadas indicadas,
	 * en caso de que sea visible, y sin ningún efecto.
	 */
	public void paint(Graphics g, double x, double y) {
		if (visible) {
			g.drawImage(img, (int)x, (int)y, width, height, null);
		}
	}

	/**
	 * Pinta la imagen en las coordenadas indicadas y
	 * con las dimensiones especificadas, siempre
	 * en caso de que sea visible.
	 */
	public void paint(Graphics g, double x, double y,
										int w, int h) {
		if (visible) {
			g.drawImage(img, (int)x, (int)y, w, h, null);
			if (drawBounds) {
				g.setColor(Color.RED);
				for (int i=0; i<bounds.size(); i++) {
					Rectangle r = bounds.get(i);
					g.drawRect((int)(x+r.getX()), (int)(y+r.getY()),
						(int)(r.getWidth()), (int)(r.getHeight()));
				}
			}
		}
	}

	/**
	 * Debe implementarse este método para definir el comportamiento
	 * del objeto.
	 */
	public void act() {
	}

	/**
	 * Método que debe implementarse para definir el comportamiento
	 * del objeto cuando este colisiona con el Sprite pasado como
	 * argumento.
	 */
	public void collision(Sprite o) {
	}

	/**
	 * Método que comprueba si este Sprite colisiona o no con el otro
	 * pasado por el argumento.</ br>
	 * Sólo funciona en el caso que los dos Sprites se encuentren
	 * en la misma profundidad (z).</ br>
	 * <code>imgBounds</code> indica si las colisiones deben
	 * de detectarse utilizando el rectángulo que envuelve
	 * a la imagen actual del Sprite o utilizando los rectágulos
	 * creados para dicha misión. De esta segunda forma se consigue
	 * más precisión en la detección de colisiones.
	 */
	public boolean collidesWith(Sprite o, boolean imgBounds) {
		if (z == o.getZ()) {
			if (imgBounds) {
				Rectangle r1 = new Rectangle(
					(int)x, (int)y, width, height);
				Rectangle r2 = new Rectangle(
					(int)o.getX(), (int)o.getY(),
					o.getWidth(), o.getHeight());
				return r1.intersects(r2);
			} else {
				for (int j=0; j<o.getBounds().size(); j++) {
					Rectangle r = o.getBounds().get(j);
					if (collidesWith(new Rectangle(
						(int)(o.getX()+r.getX()), (int)(o.getY()+r.getY()),
						(int)(r.getWidth()), (int)(r.getHeight())), false)) {
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Método que comprueba si este Sprite colisiona o no con
	 * el rectángulo pasado como argumento.</ br>
	 * <code>imgBounds</code> debe de ser true si
	 * se quiere que las colisiones sean detectadas
	 * con respecto al rectángulo que ocupa la imagen
	 * actual del sprite.
	 */
	public boolean collidesWith(Rectangle r, boolean imgBounds) {
		if (imgBounds) {
			Rectangle bound = new Rectangle(
				(int)x, (int)y, width, height);
			if (r.intersects(bound)) {
				return true;
			}
		} else {
			for (int i=0; i<bounds.size(); i++) {
				Rectangle bound = bounds.get(i);
				if (new Rectangle((int)(x+bound.getX()),
					(int)(y+bound.getY()), (int)(bound.getWidth()),
					(int)(bound.getHeight())).intersects(r)) {
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Método que comprueba si este Sprite contiene o no
	 * el punto pasado como argumento.</ br>
	 * <code>imgBounds</code> debe de ser true si
	 * se quiere que las colisiones sean detectadas
	 * con respecto al rectángulo que ocupa la imagen
	 * actual del sprite.
	 */
	public boolean collidesWith(Point p, boolean imgBounds) {
		if (imgBounds) {
			Rectangle bound = new Rectangle(
				(int)x, (int)y, width, height);
			if (bound.contains(p)) {
				return true;
			}
		} else {
			for (int i=0; i<bounds.size(); i++) {
				Rectangle bound = bounds.get(i);
				if (new Rectangle((int)(x+bound.getX()),
					(int)(y+bound.getY()), (int)(bound.getWidth()),
					(int)(bound.getHeight())).contains(p)) {
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Comprueba si la colisión entre el objeto pasado como
	 * argumento y el Sprite actual ha dado lugar en
	 * la parte superior de el Sprite actual.
	 * Es importante tener en cuenta que los dos Sprites
	 * deben de colisionar para que el resultado sea
	 * correcto.
	 */int jope = 0;
	public boolean collideTop(Sprite s) {
		if (Math.abs(s.getY()+s.getHeight()-y) <= Math.abs(
			speed.getAccurateY()+s.getSpeed().getAccurateY()+1)) {
				return true;
		}
		return false;
	}

	/**
	 * Comprueba si la colisión entre el objeto pasado como
	 * argumento y el Sprite actual ha dado lugar en
	 * la parte superior de el Sprite actual.
	 * Es importante tener en cuenta que los dos Sprites
	 * deben de colisionar para que el resultado sea
	 * correcto.
	 */
	public boolean collideBottom(Sprite s) {
		if (Math.abs(s.getY()-y-height) <= Math.abs(
			speed.getAccurateY()+s.getSpeed().getAccurateY())) {
				return true;
		}
		return false;
	}

	/**
	 * Comprueba si la colisión entre el objeto pasado como
	 * argumento y el Sprite actual ha dado lugar en
	 * la parte derecha de el Sprite actual.
	 * Es importante tener en cuenta que los dos Sprites
	 * deben de colisionar para que el resultado sea
	 * correcto.
	 */
	public boolean collideRight(Sprite s) {
		if (Math.abs(x+width-s.getX()) <= Math.abs(
			speed.getAccurateY()+s.getSpeed().getAccurateY())) {
				return true;
		}
		return false;
	}

	/**
	 * Comprueba si la colisión entre el objeto pasado como
	 * argumento y el Sprite actual ha dado lugar en
	 * la parte izquierda de el Sprite actual.
	 * Es importante tener en cuenta que los dos Sprites
	 * deben de colisionar para que el resultado sea
	 * correcto.
	 */
	public boolean collideLeft(Sprite s) {
		if (Math.abs(s.getX()+s.getWidth()-x) <= Math.abs(
			speed.getAccurateY()+s.getSpeed().getAccurateY())) {
				return true;
		}
		return false;
	}

	// SET methods --------------------------------------------------
	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setPosition(Point p) {
		this.x = Float.parseFloat(""+p.getX());
		this.y = Float.parseFloat(""+p.getY());
		System.out.println(x+" "+y);
	}
	//  end of SET methods ------------------------------------------


	// GET methods --------------------------------------------------
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	public ArrayList<Rectangle> getBounds() {
		return bounds;
	}
	
	public Vector2D getSpeed() {
		return speed;
	}

	// grosor del rectangulo
	int c = 13;
	// inicio del rectángulo
	int gap = 6;
	public Rectangle getFoot() {
		return new Rectangle((int)(x+gap),
			(int)(y+height-1),	width-gap*2, c);
	}

	public Rectangle getHead() {
		return new Rectangle((int)(x+gap), (int)(y-1),
			width-gap*2, c);
	}

	public Rectangle getLeft() {
		return new Rectangle((int)(x), (int)(y+gap),
			c, height-gap*2);
	}

	public Rectangle getRight() {
		return new Rectangle((int)(x+width-c), (int)(y+gap),
			c, height-gap*2);
	}
	//  end of GET methods ------------------------------------------


	// BOOLEAN methods ----------------------------------------------
	public boolean isToDelete() {
		return delete;
	}

	public boolean isVisible() {
		return visible;
	}
	//  end of BOOLEAN methods --------------------------------------


}  // end of Objects class



//  end of Objects.java
