package neuralNetworkV5;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/*
  EXPLAINATION FOR HOW THIS FUNCTIONs
  Network = the neural network. Contains everything that the network needs to:
  	A. Copy itself (create a child network independant of the parent network)
  		i. ONLY IMPORTANT FOR THE PARENT COPY
  		ii. parentNetwork.deepCopy() - returns a new "Network" type object that is identical to the parentNetwork 
  	B. Mutate 
  		i. ONLY IMPORTANT FOR THE CHILD COPY
  		ii. childNetwork.train(epoch) - randomly mutates one weights/bias in one of the neurons of the childNetwork
  	C. 
  
 */

public class Util {

	public Network parentNetwork;  							//best iteration so far
	private Network childNetwork; 							//mutated, candidate for the best
	private final Robot roboGoon;							//allows model to simulate button presses
	
	SaveReader uploadSave = new SaveReader();				//uploads saved bias/weights to parent system
	Double[][] saves;

	int maxEpoch = 5000;
	int epoch = 0;
	boolean postRunFinished = false;
	
	
	public Util() {
		
		saves = uploadSave.readSave("bestEpochV2.txt", 5, 12);
		
		try {
			this.roboGoon = new Robot();
		}catch(AWTException e) {
			throw new RuntimeException("Failed to initialize Robot (possibly headless environment)", e);
		}
		
		//parentNetwork = new Network(saves);
		parentNetwork = new Network();
		
		childNetwork = parentNetwork.deepCopy();
		childNetwork.train(epoch);
		
		
	}
	
	public void connector(DinoGameTraining game) {
		if (epoch < maxEpoch) {
			//System.out.println("Hello");
			if(epoch == maxEpoch - 2) {
				game.FPS = 60;
			}
			
			if(!game.gameOver) {
			
				List<Double> dataNorm = normalization(game);
				
				if (childNetwork.interpretPred(childNetwork.predictJump(dataNorm)) == 1) {
					roboGoon.keyPress(KeyEvent.VK_SPACE);
				}else {
					roboGoon.keyRelease(KeyEvent.VK_SPACE);}
			
				if (childNetwork.interpretPred(childNetwork.predictCrouch(dataNorm)) == 1) {
					roboGoon.keyPress(KeyEvent.VK_S);
				}else {
					roboGoon.keyRelease(KeyEvent.VK_S);}
			}else if(game.gameOver) {
				
				parentNetwork.evaluate(game, childNetwork);   
				childNetwork = parentNetwork.deepCopy();      
				childNetwork.train(epoch);                    
				
				epoch++;
				System.out.println("-------------------------------------");
				System.out.println("Iteration: " + epoch);
				
				game.restartGame();
			
			}
		} else if (!postRunFinished){
			System.out.println("Total Epochs Run: " + maxEpoch);
			System.out.println("Best Epoch: " + parentNetwork.bestScore);
			parentNetwork.recordBest();
			postRunFinished = true;
		}
	}
	
	public List<Double> normalization(DinoGameTraining game) {
		List<Double> b = new ArrayList<Double>();
		
		b.add(game.data.get(0) / 20.0); //veloX
		
		double veloY = Math.max(-15, Math.min(game.data.get(1),  15)); //clamps between -15 and 15
		b.add(veloY / 15); //veloY
		
		b.add(game.data.get(2) / 800); //dist1
		b.add(game.data.get(3) / 800); //dist2
		b.add((game.data.get(4) - game.OBSTACLE_HEIGHT) / game.maxObsHeight); //height1
		b.add((game.data.get(5) - game.OBSTACLE_HEIGHT) / game.maxObsHeight); //height2
		
		double dinoY = Math.min(game.data.get(6) / 17.7, 1.0);
		b.add(dinoY); //dinoY Max dinoY (considering jump velocity and gravity) is 17.6471
		
		double tti = game.data.get(2) / Math.max(game.data.get(0), 0.1); //raw time to impact. math insures velocity > 0.1 at all times (not 0 or negative)
		tti = Math.min(tti, 200); //caps time to impact at 200. The system doesn't need the exact distance. Just needs to know when its about to hit
		tti = tti / 200; //normalize tti by our chosen cap
		b.add(tti); //time to impact
		
		return b;
	}	
	
	public static double tanh(double in) {
		return Math.tanh(in);
	}
	
}
