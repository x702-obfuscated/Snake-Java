package game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
//import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener{
	//Settings
	private static final int SCREEN_WIDTH = 600;
	private static final int SCREEN_HEIGHT = 600;
	private static final int UNIT_SIZE = 25;
	private static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	private static final int DELAY = 100; //higher the number slower the game. 
	private static final boolean edgeCollisions = false;
	
	private static final Color snakeHeadColor = new Color(0,255,0);
	private static final Color snakeBodyColor = new Color(0,255,0);//ex Color.red or new Color(255,255,255);
	private final int x[] = new int[GAME_UNITS];
	private final int y[] = new int[GAME_UNITS];
	private int bodyParts = 6;
	
	private static final Color appleColor = new Color(255,0,0);//ex Color.red or new Color(255,255,255);
	private int applesEaten;
	private int appleX;
	private int appleY;
	
	char direction = 'R';// R L U D
	boolean running = false;
	
	Timer timer;
	Random random; 


	
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));;
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();

	}
	
	
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY,this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		if (running) {
			showGridLines(g);//comment to remove gridlines

			g.setColor(appleColor);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			

			for (int i = 0; i < bodyParts; i++) {
				if (0 == i) {
					g.setColor(snakeHeadColor);

				} else {
					g.setColor(snakeBodyColor);
				}

				g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
			}
			
			displayScore(g);
		} else {
			gameOver(g);
		}
		
	}
	
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
		
	}
	
	public void move() {
		for(int i = bodyParts; i > 0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
			}
	}
	
	public void checkApple() {
		if((x[0] == appleX) && ( y[0] == appleY)){
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions() {
		//check if head collides with body
		for(int i = bodyParts; i > 0; i--) {
			if(x[0] == x[i] && y[0] == y[i]) {
				running = false;
			}
		}
		
		if(edgeCollisions) {
			checkEdgeCollisions();
		}
		else {
			wrapAroundEdges();
		}
		
		
		
		if (!running) {
			timer.stop();
		}
	}
	
	public void checkEdgeCollisions() {
		// checks if head collides with left border
		if (x[0] < 0) {
			running = false;
		}

		// checks if head collides with right border
		if (x[0] > SCREEN_WIDTH) {
			running = false;
		}

		// checks if head collides with upper border
		if (y[0] < 0) {
			running = false;
		}

		// checks if head collides with lower border
		if (y[0] > SCREEN_HEIGHT) {
			running = false;
		}
	}
	
	public void wrapAroundEdges() {
		// checks if head collides with left border
		if (x[0] < 0) {
			x[0] = SCREEN_WIDTH;
		}

		// checks if head collides with right border
		if (x[0] > SCREEN_WIDTH) {
			x[0] = 0;
		}

		// checks if head collides with upper border
		if (y[0] < 0) {
			y[0] = SCREEN_HEIGHT;
		}

		// checks if head collides with lower border
		if (y[0] > SCREEN_HEIGHT) {
			y[0] = 0;
		}
	}

	public void showGridLines(Graphics g) {
		for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
			g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
			g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
		}
	}
	public void displayScore(Graphics g) {
		g.setColor(Color.red);
		g.setFont( new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten,(SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
	}
	
	public void gameOver(Graphics g) {
		displayScore(g);
		g.setColor(Color.red);
		g.setFont( new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("GAME OVER",(SCREEN_WIDTH - metrics.stringWidth("GAME OVER"))/2, SCREEN_HEIGHT/2);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(running) {
			move();
			checkApple();
			checkCollisions();
			
		}
		repaint();
		
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			}
			
		}
	}

}
