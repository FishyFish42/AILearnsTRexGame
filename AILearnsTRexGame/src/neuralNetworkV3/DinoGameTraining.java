package neuralNetworkV3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
//import java.util.Random;


public class DinoGameTraining extends JPanel implements KeyListener, Runnable { 

    // Game constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 300;
    private static final int GROUND_Y = 250;
    private static final int DINO_WIDTH = 40;
    private static int DINO_HEIGHT = 40;
    private static final int OBSTACLE_WIDTH = 15;
    private static final int OBSTACLE_HEIGHT = 40;
    public int FPS = 700;
    
    // Dino position and velocity
    private double dinoX = 50;
    private double dinoY = GROUND_Y - DINO_HEIGHT;
    private double velocityY = 0;
    private double velocityX = 4;
    private double isJumping = 0;
    private double isCrouching = 0;

    // Game state
    public boolean gameOver = false;
    public int score = 0;
    public volatile int drawCount = 0;
    public int syncCount = 1;

    // Obstacles
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    //private Random rand = new Random();
    private double dist1;
    private double dist2;

    //Objects
    Thread gameThread;
    Util util = new Util();
    
    
    //Data storage for passing
    public List<Double> data = Arrays.asList(velocityX, dist1, dist2); 
    
    
    public DinoGameTraining() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    }

    
    public void startGameThread() {
    	gameThread = new Thread(this);
		gameThread.start();
	}
    
    public void run() {//when gameThread is started, this is the method that is called
		
		//1 second divided by the FPS
		//System measures time in nanoseconds, so we divide 1 second worth of nanoseconds by FPS
		double drawInterval = 1000000000/FPS; //0.0166666 seconds
		double delta = 0;
		long lastTime =  System.nanoTime();
		long currentTime;
		long timer = 0;
		
		while(gameThread != null) {
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime); //just counts time
			lastTime = currentTime;
			
			if(delta>= 1) {	
				// 1. UPDATES - updates values such as character posistion, map visuals, etc
				if(!gameOver) {
					updateGame(); 
					data = Arrays.asList(velocityX, dist1, dist2);
				}
				util.connector(this);
				// 2. DRAW - replaces all on screen visuals with new ones to reflect the updated changes
				repaint();
				delta--;
				drawCount++; //counts how many times we draw
			}	
			
			if(timer>= 1000000000) { //when we have passed 1 second, display how many times we have redrawn and updated the screen in the last second
				//System.out.println("FPS: " + drawCount);
				//increase speed
		    	velocityX += 0.005;
				//System.out.println("Speed:" + velocityX);
				timer = 0;
				drawCount = 0;//then we reset the counters. drawCount should = FPS when it is displayed
			}
		}
	}
    
	//
    public void updateGame() {
    	
        // Move obstacles
        Iterator<Rectangle> it = obstacles.iterator();
        while (it.hasNext()) {
            Rectangle obs = it.next();
            obs.x -= velocityX;
            if (obs.x + OBSTACLE_WIDTH < 0) {
                it.remove();
                score++;
            }
            // Collision detection
            if (obs.intersects(new Rectangle((int) dinoX, (int) dinoY, DINO_WIDTH, DINO_HEIGHT))) {
                gameOver = true;
            }			
        }

        
        // Spawn new obstacles
        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < WIDTH - 250) {
        	//int edist = rand.nextInt(0,201);
            int height = OBSTACLE_HEIGHT + 10; // + rand.nextInt(20);
            obstacles.add(new Rectangle(WIDTH, GROUND_Y - height, OBSTACLE_WIDTH, height));
        }
         
        
        //checking distance
        if (obstacles.size() >= 3) {
        	if (obstacles.get(0).x >= dinoX && obstacles.get(1).x >= dinoX) {
        		dist1 = obstacles.get(0).x - dinoX;
        		dist2 = obstacles.get(1).x - dinoX;
        	}else if (obstacles.get(0).x < dinoX && obstacles.get(1).x >= dinoX) {
        		dist1 = obstacles.get(1).x - dinoX;
        		dist2 = obstacles.get(2).x - dinoX;
        	}else if (obstacles.get(0).x < dinoX && obstacles.get(1).x < dinoX) {
        		dist1 = obstacles.get(2).x - dinoX;
        		dist2 = obstacles.get(3).x - dinoX;
        	}
        }
     
        
     // Apply gravity
        if (isJumping == 1) {
        	if(isCrouching == 1) {
        		velocityY += 3;
        	}else {velocityY += 0.85;}
            dinoY += velocityY;
            
            if (dinoY >= GROUND_Y - DINO_HEIGHT) {
                dinoY = GROUND_Y - DINO_HEIGHT;
                isJumping = 0;
                velocityY = 0;}
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw ground
        g.setColor(Color.GRAY);
        g.fillRect(0, GROUND_Y, WIDTH, 5);

        // Draw dino
        g.setColor(Color.GREEN);
        g.fillRect((int) dinoX,(int) dinoY, DINO_WIDTH, DINO_HEIGHT); 
        
        // Draw obstacles
        g.setColor(Color.RED);
        for (Rectangle obs : obstacles) {
            g.fillRect(obs.x, obs.y, obs.width, obs.height);
        }

        // Draw score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);

        // Game over text
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("GAME OVER", WIDTH / 2 - 100, HEIGHT / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    	if (!gameOver) {
    		if (e.getKeyCode() == KeyEvent.VK_SPACE && isJumping == 0) {
    			isJumping = 1;
    			velocityY = -15; // jump strength
    		} 
    		if (e.getKeyCode() == KeyEvent.VK_S) {
    			isCrouching = 1;
    		}else {
    			isCrouching = 0;
    			}
    		
    	}else if(gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame();
		}
        
        
    }

    public void restartGame() {
        gameOver = false;
        score = 0;
        dinoY = GROUND_Y - DINO_HEIGHT;
        velocityY = 0;
        isJumping = 0;
        isCrouching = 0;
        obstacles.clear();
        velocityX = 4;
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    
  }