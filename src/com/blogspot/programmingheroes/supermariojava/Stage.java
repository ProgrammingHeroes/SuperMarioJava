
// Stage.java --------------------------------------------------------

package com.blogspot.programmingheroes.supermariojava;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

import com.blogspot.programmingheroes.supermariojava.loaders.ImagesLoader;
import com.blogspot.programmingheroes.supermariojava.loaders.SoundsLoader;

/**
 * Stage which can be used for any game.
 *
 * @author WilyWill
 */
public abstract class Stage implements Runnable,
			KeyListener, MouseListener,
			MouseMotionListener, MouseWheelListener,
			FocusListener, ComponentListener,
			WindowStateListener, WindowFocusListener,
									 WindowListener {

	// Size of Stage
	protected static int WIDTH = 500;
	protected static int HEIGHT = 400;

	// For the animation loop
	protected Thread animator;
	// Stop the animator:Thread, stop the animation
	protected volatile boolean running = false;

	// For game termination, the animation does not stop
	protected volatile boolean gameOver = false;

	// For game pause, the animation does not stop
	protected volatile boolean pause = false;

	// Variables for off-screen rendering
	protected BufferStrategy bs;

	/* Number of frames with a delay of 0 ms before the
	 * animation thread yields to other running threads. */
	protected static int NO_SLEEPS_FOR_YIELD = 15;

	/* frames that can be skipped in any one animation loop
	 * i.e the games state is updated but not rendered (UPS)*/
	protected static int MAX_FRAME_SKIPS = 8;
	
	// Util for calculate the period
	protected static int FPS;

	/* Period that indicate the FPS. 1000(1seg en miliS)/Period = FPS
	 * This must to be represented in nanoS */
	protected static long period;

	// For check performance
	protected long totalFrames = 0;
	protected long totalUpdates = 0;
	protected long totalSleepTime = 0;
	// Stores the initial time in ns (after initStage() is call)
	protected long initTime;
	// Stores the time that the game isn't paused.
	protected long playedTime;
	/* Stores the time in each paused. For total pause time should
	 * be used: getTimeRunning()-getTimePlayed() */
	protected long pausedTime;

	/* Utility to load images.
	 * Must to be setting before use by getImagesLoader() */
	protected ImagesLoader imgLoader;

	/* For image efects. */
	protected ImagesEffects imgEffects;

	/* Utility to load sounds.
	 * Must to be setting before use by getSoundsLoader() */
	protected SoundsLoader soundsLoader;

	// MODES of the Stage
	public static final int FSEM = 0; // Full-Screen Exclusive Mode
	public static final int AFS = 1; // Almost Full-Screen
	public static final int UFS = 2; // Undecorated Full-Screen
	public static final int JFRAME = 3;
	protected JFrame window; // useful for FSEM, AFS,
							// UFS, JFRAME modes
	
	public static final int CANVAS = 4; // Paint in a Canvas class
	protected Canvas canvas; // useful for CANVAS mode
	
	public static final int JPANEL = 5; // Paint in a JPanel class
	protected JPanel panel; // useful for JPANEL mode
	
	protected int mode; // current mode
	protected Component component; // specific component of the mode
	//  end of MODES of the Stage

	// For Full-Screen Exclusive Mode
	protected GraphicsEnvironment ge;
	protected GraphicsDevice screenDevice;
	protected DisplayMode defaultDisplay;



	/**
	 * An empty Stage was created. To start the game must to
	 * call <code>startGame()</code>.
	 *
	 * @param mode Surface over that the game will show.
	 * @see startGame();
	 */
	public Stage(int mode) {
		initTime=System.currentTimeMillis();
		setFPS(80);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screenDevice = ge.getDefaultScreenDevice();
		
		switchMode(mode);
	}  // end of Stage() constructor

	/**
	 * Change the current display mode or init a display.
	 */
	public void switchMode(int mode) {
		this.mode = mode;
		
		switch(mode) {
			case FSEM: initFSEM(); break;
			case AFS: initAFS(); break;
			case UFS: initUFS(); break;
			case JFRAME: initJFrame(); break;
			case CANVAS: initCanvas(); break;
			case JPANEL: initJPanel(); break;
			default: throw new IllegalArgumentException(
						"The mode of the Stage is invalid.");
		}
		
		// Add the listeners for capture the EVENTS -------
			// listen for component mouse presses
			component.addMouseListener(this);
			// listen for component key events
			component.setFocusable(true);
			component.requestFocus();
			component.addKeyListener(this);
			// listen for componente mouse wheel
			component.addMouseWheelListener(this);
			// listen for component
			component.addComponentListener(this);
			// listen for component focus changed
			component.addFocusListener(this);
			// listen for component mouse motion
			component.addMouseMotionListener(this);
		// end of capture components EVENTS ---------------
		
	}  // end of switchMode()

	/**
	 * Init the mode Full-Screen Exclusive Mode.
	 */
	public void initFSEM() {
		if(!screenDevice.isFullScreenSupported())
			throw new IllegalArgumentException("No FSEM supported.");
		window = new JFrame("GameFrame");/* {
			// Create BufferStrategy for FSEM
			public void addNotify() {
				super.addNotify();
				window.createBufferStrategy(2);
				bs = window.getBufferStrategy();
			}
		};*/
		window.addWindowFocusListener(this);
		window.addWindowListener(this);
		window.addWindowStateListener(this);
		window.setUndecorated(true);
		window.setIgnoreRepaint(true);
		window.setResizable(false);
		
		try {
			screenDevice.setFullScreenWindow(window);
			Toolkit tk = Toolkit.getDefaultToolkit();
			WIDTH = (int)tk.getScreenSize().getWidth();
			HEIGHT = (int)tk.getScreenSize().getHeight();
		} catch(Exception e) {
			System.err.println("Error with setting FSEM.");
			e.printStackTrace();
			screenDevice.setFullScreenWindow(null);
		}
		
		// CREATE BufferStrategy for FSEM
		try {
			EventQueue.invokeAndWait( new Runnable() {
				public void run() {
					window.createBufferStrategy(2);
				}
			});
		} catch(Exception e) {
			System.out.println("Error while creating buffer"
				+" strategy (FSEM).");
			e.printStackTrace();
		}
		
		try { // sleep to give time for buffer strategy to be done
			Thread.sleep(0); // 1 sec
		} catch(InterruptedException ex) {}

		bs = window.getBufferStrategy();
		// end of CREATE BufferStrategy for FSEM		
		
		component = window;
	}
	
	/**
	 * Window unresizable that occupies the entire screen.
	 */
	public void initAFS() {
		initJFrame();
		
		//GraphicsConfiguration gc = window.getGraphicsConfiguration();
		//Rectangle screenRect = gc.getBounds();

		Toolkit tk = Toolkit.getDefaultToolkit( );
		Dimension d = tk.getScreenSize();
		window.setSize(d);
		window.setResizable(false);
		//window.setAlwaysOnTop(true);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.addComponentListener(this);
	}

	/**
	 * An Undecorated Full-Screen.
	 */
	public void initUFS() {
		initJFrame();
		
		// start with window maximized
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// start with full screen
		window.setUndecorated(true);
		WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
   		HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	/**
	 * Init a canvas.
	 */
	public void initCanvas() {
		canvas = new Canvas() {
			private static final long serialVersionUID = 1L;

			public void addNotify() {
				super.addNotify();
				canvas.createBufferStrategy(2);
				bs = canvas.getBufferStrategy();
			}
		};
		canvas.setIgnoreRepaint(true);
		try { // sleep to give time for buffer strategy to be done.
			Thread.sleep(1000); // 1 sec
		} catch(InterruptedException ex) {}
		component = canvas;
	}  // end of initCanvas();

	/**
	 * Init a JFrame.
	 */
	public void initJFrame() {
		initCanvas();
		
		window = new JFrame("GameFrame");
		window.addWindowFocusListener(this);
		window.addWindowListener(this);
		window.addWindowStateListener(this);
		window.setIgnoreRepaint(true);
		window.getContentPane().add(canvas);
	}  // end of initJFrame();

	/**
	 * Init a JPanel.
	 */
	public void initJPanel() {
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void addNotify() {
				super.addNotify();
				//panel.createBufferStrategy(2);
				//bs = panel.getBufferStrategy();
			}
		};
		panel.setIgnoreRepaint(true);
		
		component = panel;
	}  // end of initJPanel();

	/**
	 * Inicialise and start the thread with the loop of the game.
	 */
	public void startGame() {
		if(animator!=null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}  // end of startGame();

	/**
	 * Stop the animator:Thread.
	 * Called by the user to stop execution and exit of the game.
	 */
	public void stopGame() {
		running = false;
	}  // end of stopGame();

	/* Loop game: update, render, sleep.
	 * Controls the FPS and the UPS with an
	 * accurate System.nanoTime(); */
	public void run() {
		if(bs==null) throw new NullPointerException("Buffer"
			+"Stragegy is null.");
		long beforeTime, afterTime, diff, sleepTime;
		long extraSleepTime = 0L, excessTime = 0L;
		int noSleeps = 0;
		
		initStage();
		initTime = playedTime = System.nanoTime();
		
		running = true;
		while(running) {
			beforeTime = System.nanoTime();
			
			/* If frame animation is taking too long, update
			 * the game state without rendering it, to get the
			 * UPS nearer to the required FPS. */
			int skips = 0;
			while(skips<MAX_FRAME_SKIPS && excessTime>period) {
				excessTime -= period;
				updateStage(); // only update, not render
				skips++;
				totalUpdates++;
			}
			
			// LOOP game
			updateStage(); // Update game stage
			updateScreen(); // Render stage in a buffer
							// and show it in the screen
			// end LOOP game
			
			afterTime = System.nanoTime();
			diff = afterTime - beforeTime;
			sleepTime = (period - diff) - extraSleepTime;
			
			if(sleepTime>0) {
				totalSleepTime += sleepTime;
				try {
					// nanoS -> miliS
					Thread.sleep(sleepTime/1000000L);
				} catch(InterruptedException e) {
					System.err.println(e.getMessage());
				}
				extraSleepTime = System.nanoTime()-afterTime-sleepTime;
			} else {
			// sleepTime<=0 frame took longer than the period
				excessTime -= sleepTime; // store excess time value
				extraSleepTime = 0L;
				if (++noSleeps>=NO_SLEEPS_FOR_YIELD) {
					// give another thread a chance to run
					Thread.yield();
					noSleeps = 0;
				}
			}
		}  // end of game -> running = false
		
		showPerformance();
		if (mode==FSEM) closeFSEM();
		System.exit(0);
	}  // end of run()

	/**
	 * Repaint the screen whether the buffer don't lose the contents.
	 */
	public void updateScreen() {
		++totalFrames; ++totalUpdates;
		try {
			Graphics g = bs.getDrawGraphics();
			renderStage(g);
			
			if (bs.contentsLost()) {
				System.out.println("Contents of the buffer are lose.");
			} else {
				bs.show();
			}
			g.dispose();
			// sync the display on some systems
			Toolkit.getDefaultToolkit().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}  // end of updateScreen()

	public void showPerformance() {
		double totalTime = (System.nanoTime()-initTime)/1000000000.0;
		System.out.println(
			"------------------------------");
		System.out.println("Total time: "+
			totalTime+"\nFPS: "+FPS+"  Period: "+period+
			"\nTotal frames: "+totalFrames+"\nAverage FPS: "+
			((float)totalFrames/totalTime)
			+"\nTotal updates: "+totalUpdates+"\n"+
			"Average UPS: "+((float)totalUpdates/totalTime)+
			"\nSleepTime: "+totalSleepTime+
			"\nAverage SleepTime: "+((float)totalSleepTime/period));
	}  // end of showPerformance()

	public abstract void updateStage();

	public abstract void renderStage(Graphics g);

	public abstract void initStage();

	// Indicates the end of the game
	public void gameOver() {
		gameOver = true;
	}  // end of gameOver();

	public void closeFSEM() {
		try {
			// Also restores the display mode to
			// its original state
			// screenDevice.setDisplayMode(defaultDisplay);
			window.dispose();
			screenDevice.setFullScreenWindow(null);
		} catch (Exception ex) {
			System.err.println("Error closing the application.");
			ex.printStackTrace();
		}
	}  // end of closeFSEM()

	public void exit() {
		running = false;
	}  // end of exit();

	// Pause or resume the game.
	public void setPause(boolean p) {
		pause = p;
	}  // end of setPause(boolean);

	public void setWindowVisible(boolean v) {
		if(window!=null)
			window.setVisible(v);
	}  // end of setWindowVisible(boolean);

	public void setImagesLoader(ImagesLoader il) {
		this.imgLoader = il;
		this.imgEffects = new ImagesEffects(il);
	}  // end of setImagesLoader(ImagesLoader);

	public ImagesLoader getImagesLoader() {
		return imgLoader;
	}  // end of getImagesLoader();

	public ImagesEffects getImagesEffects() {
		return imgEffects;
	}  // end of getImagesEffects();

	public void setSoundsLoader(SoundsLoader sl) {
		this.soundsLoader = sl;
	}  // end of setSoundsLoader(SoundsLoader);

	public SoundsLoader getSoundsLoader() {
		return soundsLoader;
	}  // end of getSoundsLoader();

	public JFrame getWindow() {
		return window;
	}  // end of getWindow();

	/**
	 * Total time running in nanoseconds.
	 */
	public long getTimeRunning() {
		return System.nanoTime()-initTime;
	}  // end of getTimeRunning();

	/**
	 * Total time played in nanoseconds.
	 */
	public long getTimePlayed() {
		return System.nanoTime()-playedTime;
	}  // end of getTimePlayed();

	/**
	 * Return the specific game component.
	 */
	public Component getComponent() {
		return component;
	}  // end of getComponent();

	public int getFPS() {
		return FPS;
	}

	public long getTotalUpdates() {
		return totalUpdates;
	}

	public long getTotalFrames() {
		return totalFrames;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	// BOOLEAN methods ----------------------------------------------
	public boolean isPause() {
		return pause;
	}  // end of isPause();

	public boolean isGameOver() {
		return gameOver;
	}  // end of isGameOver();

	public boolean isRunning() {
		return running;
	}  // end of isRunning();
	// end of BOOLEAN methods ---------------------------------------

	public void setFPS(int fps) {
		FPS = fps;
		period = 1000000000L/FPS;
	}  // end of setFPS(int);

	public void updateSize() {
		WIDTH = component.getWidth();
		HEIGHT = component.getHeight();
	}  // end of updateSize();

	public void setSize(int w, int h) {
		component.setPreferredSize(new Dimension(
			w, h));
		if (mode==JFRAME) window.pack();
		else updateSize();
	}  // end of setSize(int, int);

	/**
	 * Checks whether the d:DisplayMode is supported.
	 */
	public boolean isDisplayModeAvailable(DisplayMode d) {
		DisplayMode[] dm = screenDevice.getDisplayModes();
		for(int i=0; i<dm.length; i++)
			if( dm[i].getWidth()==d.getWidth()
				&& dm[i].getHeight()==d.getHeight()
				&& dm[i].getBitDepth()==d.getBitDepth()
				&& dm[i].getRefreshRate()==d.getRefreshRate() )
				return true;
		return false;
	}  // end of isDisplayModeAvailable(DisplayMode);

	// Changes the DisplayModel
	public boolean setDisplayMode(DisplayMode dm) {
		if(mode!=FSEM) return false;
		if(!screenDevice.isDisplayChangeSupported()
			&& isDisplayModeAvailable(dm))
			return false;
		defaultDisplay = screenDevice.getDisplayMode();
		try {
			screenDevice.setDisplayMode(dm);
		} catch(Exception e) {
			System.err.println("Error setting DisplayMode.");
			e.printStackTrace();
			screenDevice.setDisplayMode(defaultDisplay);
		}
		return true;
	}

	// Implements all the events that you can overwrite in a subclass
	//  if you need -------------------------------------------------
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if ((keyCode == KeyEvent.VK_ESCAPE) ||
			(keyCode == KeyEvent.VK_END) ||
			((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
				exit();
		}
	}
	public void keyTyped(KeyEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseWheelMoved(MouseWheelEvent e) {}
	public void focusGained(FocusEvent e) {}
	public void focusLost(FocusEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {
		if (mode == AFS && e.getComponent() instanceof JFrame) {
				window.setLocation(0,0);
		}
	}
	public void componentResized(ComponentEvent e) {
		updateSize();
	}
	public void componentShown(ComponentEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		exit();
	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowGainedFocus(WindowEvent e) {}
	public void windowLostFocus(WindowEvent e) {}
	public void windowStateChanged(WindowEvent e) {}
	//  end of events implementation --------------------------------

}  // end of Class Stage


//  end of Stage.java -----------------------------------------------
