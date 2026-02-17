package neuralNetworkVT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DinoGameTraining extends JPanel implements KeyListener, Runnable { 

    // Game constants
    public static final int WIDTH = 800;
    public static final int HEIGHT = 300;
    public static final int GROUND_Y = 250;
    public static final int DINO_WIDTH = 40;
    public static final int DINO_HEIGHT = 40;
    public static final int OBSTACLE_WIDTH = 15;
    public static final int OBSTACLE_HEIGHT = 40;
    public static final int AI_FPS = 1000000;
    public static final int PLAYER_FPS = 60;
    public boolean humanPlayer;
    public int FPS;
    
    // Universal Dino variables
    private double velocityX = 4;
    private double dinoX = 50;
    public int totalPopulation;
    
    //Independent Dino storage
    List<dinoPlayer> dinoStorage = new ArrayList<>();

    // Game state
    public int livingCount;
    public boolean gameOver = false;
    public int realScore = 0;
    public volatile int drawCount = 0;
    public int syncCount = 1;
    private double runTime = 0;

    // Obstacles
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private Random rand = new Random();
    private double maxObsDist = 200;
	private double distVariation = 0.01;
	private double distTimeAdjust = distVariation * runTime;
    private double dist1 = 800;
    private double dist2 = 800;
    public double maxObsHeight = 19;
    private double heightVariation = 0.002;
	private double heightTimeAdjust = heightVariation * runTime;
    private double height1 = 40;
    private double height2 = 40;

    //Data storage for passing
    public List<Double> data = Arrays.asList(velocityX, dist1, dist2, height1, height2); //This MUST be before Util object creation
    //Additional Variables within dinoStorage: velocityY, dinoY
    
  //Objects
    Thread gameThread;
    Util util;
    
    //Constructor. Must be done before Util Object creation
    public DinoGameTraining(int childPopulation, boolean humanPlayer, int playerPopulation) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        
        this.humanPlayer = humanPlayer;
        
        if(humanPlayer == false) {
        	totalPopulation = childPopulation;
        	livingCount = childPopulation;
        	FPS = AI_FPS;
        	for(int i = 0; i < totalPopulation; i++) {
            	dinoPlayer dino = new dinoPlayer();
            	dinoStorage.add(dino);
            }
        }else if (humanPlayer == true){
        	totalPopulation = playerPopulation;
        	livingCount = playerPopulation;
        	FPS = PLAYER_FPS;
        	for(int i = 0; i < totalPopulation; i++) {
            	dinoPlayer dino = new dinoPlayer();
            	dinoStorage.add(dino);
            }
        }
        
        util = new Util(this);
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
					
					data = Arrays.asList(velocityX, dist1, dist2, height1, height2);
				}
				
				
				if(humanPlayer == true) {
					// 2. DRAW - replaces all on screen visuals with new ones to reflect the updated changes
					
					repaint();
				}else {
					// 2. AI - connects to network if no human is playing
					util.connector(this);
				}
				
				
				delta--;
				drawCount++; //counts how many times we draw
			}	
			
			if(timer>= 1000000000) { //when we have passed 1 second, display how many times we have redrawn and updated the screen in the last second
				//System.out.println("FPS: " + drawCount);
				//increase speed
		    	velocityX += 0.005;
		    	runTime++;
				//System.out.println("Speed:" + velocityX);
				timer = 0;
				drawCount = 0;//then we reset the counters. drawCount should = FPS when it is displayed
			}
		}
	}
    
	//
    public void updateGame() {
    	
    	//fitness score increment
    	//this will stop the system from stalling as it would if i relied off of score alone
    	for(dinoPlayer p : dinoStorage) {
        	if(p.isAlive == true) p.score++;
        }
    	
        // Move obstacles and check collision
        Iterator<Rectangle> it = obstacles.iterator();
        while (it.hasNext()) {
            Rectangle obs = it.next();
            obs.x -= velocityX;
            if (obs.x + OBSTACLE_WIDTH < 0) {
                it.remove();
                realScore++;
            }
            
            // Collision detection 
            for(dinoPlayer d : dinoStorage) {
            	if (obs.intersects(new Rectangle((int) dinoX, (int) d.dinoY, DINO_WIDTH, DINO_HEIGHT))) {
                    d.isAlive = false;
                    livingCount--;
            	}
            }
        }

        //check if all players are dead
        if(livingCount <= 0) gameOver = true;
        
        // Spawn new obstacles
        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < WIDTH - 175) {
        	
        	if(distTimeAdjust < maxObsDist) {
        		distTimeAdjust = distVariation * runTime;
        	}
        	
        	if(heightTimeAdjust < maxObsHeight) {
        		heightTimeAdjust = heightVariation * runTime;
        	}
        	
        	double edist = rand.nextDouble(0 ,distTimeAdjust + 1); //201
            double height = OBSTACLE_HEIGHT + rand.nextDouble(heightTimeAdjust + 1); //20
            obstacles.add(new Rectangle(WIDTH + (int) edist, GROUND_Y - (int) height, OBSTACLE_WIDTH, (int) height));
        }
        
        //checking distance, chooses dist# and height# values
        if (obstacles.size() >= 3) {
        	if (obstacles.get(0).x >= dinoX && obstacles.get(1).x >= dinoX) {
        		dist1 = obstacles.get(0).x - dinoX;
        		height1 = obstacles.get(0).height;
        		dist2 = obstacles.get(1).x - dinoX;
        		height2 = obstacles.get(1).height;
        	}else if (obstacles.get(0).x < dinoX && obstacles.get(1).x >= dinoX) {
        		dist1 = obstacles.get(1).x - dinoX;
        		height1 = obstacles.get(1).height;
        		dist2 = obstacles.get(2).x - dinoX;
        		height2 = obstacles.get(2).height;
        	}else if (obstacles.get(0).x < dinoX && obstacles.get(1).x < dinoX) {
        		dist1 = obstacles.get(2).x - dinoX;
        		height1 = obstacles.get(2).height;
        		dist2 = obstacles.get(3).x - dinoX;
        		height2 = obstacles.get(3).height;
        	}	
        }
        
     // Fallback if something goes wrong
        if (dist1 <= 0 || dist2 <= 0) {
            dist1 = 800;
            dist2 = 800;
        }
     
     // Apply gravity
       for(dinoPlayer d : dinoStorage) {
    	   if(d.isAlive) d.gravity();
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
        g.fillRect((int) dinoX,(int) dinoStorage.get(0).dinoY, DINO_WIDTH, DINO_HEIGHT); 
        
        // Draw obstacles
        g.setColor(Color.RED);
        for (Rectangle obs : obstacles) {
            g.fillRect(obs.x, obs.y, obs.width, obs.height);
        }

        // Draw score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + realScore, 10, 20);

        // Game over text
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("GAME OVER", WIDTH / 2 - 100, HEIGHT / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    	if (!gameOver) {
    		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
    			dinoStorage.get(0).jump();
    		} 
    		if (e.getKeyCode() == KeyEvent.VK_S) {
    			dinoStorage.get(0).crouch();
    		}else {
    			dinoStorage.get(0).uncrouch();
    		}
    	}else if(gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame();
		}
    }

    public void restartGame() {
        gameOver = false;
        realScore = 0;
        obstacles.clear();
        velocityX = 4;
        runTime = 0;
        livingCount = totalPopulation;
        for(dinoPlayer d : dinoStorage) {
        	d.resetValues();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    
  }