package neuralNetworkVT;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Util {

	private Network parentNetwork;  								//best iteration so far
	private Network childNetwork; 									//mutated, candidate for the best
	private final Robot roboGoon;									//allows model to simulate button presses
	//private final SaveReader uploadSave = new SaveReader();		//uploads saved bias/weights to parent system

	//USER CHANGEABLE VARIABLES
	//ALWAYS double check these before starting.
	int maxEpoch = 100;
	int[] layerSizes = {8, 8, 2}; 							//Last number must ALWAYS be the number of outputs (2)(jump and crouch). Also be aware of the number of inputs into the system.
	
	//String saveFilePath = "bestEpochV2.txt"; 				//Only needed if going off of a save file. This is broken in this version
	
	//Internal counters/info storage 
	Double[][] saves;
	public int numOfInputs;
	int epoch = 0;
	boolean postRunFinished = false;
	
	
	public Util(DinoGameTraining dino) {
		
		//Finds total number of inputs
		numOfInputs = normalization(dino).size();
		
		//saves = uploadSave.readSave(saveFilePath, 5, 12); //this is broken in this version
		
		//robot allows network to interact with the game
		try {
			this.roboGoon = new Robot();
		}catch(AWTException e) {
			throw new RuntimeException("Failed to initialize Robot (possibly headless environment)", e);
		}
		
		//creates parent network
		//parentNetwork = new Network(saves); //this is broken in this version
		parentNetwork = new Network(numOfInputs, layerSizes);
		
		//creates and mutates(trains) a child copy of the parent
		childNetwork = parentNetwork.deepCopy();
		childNetwork.train(epoch);
		
		
	}
	
	
	//connects the game to the network
	public void connector(DinoGameTraining game) {
		
		//while training is not finished
		if (epoch < maxEpoch) {
			
			//while game is still going
			if(!game.gameOver) {
			
				//data normalization
				List<Double> dataNorm = normalization(game);
				
				//child network chooses whether to jump, then this relays it to the game.
				if (childNetwork.interpretPred(childNetwork.predictJump(dataNorm, layerSizes)) == 1) {
					roboGoon.keyPress(KeyEvent.VK_SPACE);
				}else {
					roboGoon.keyRelease(KeyEvent.VK_SPACE);}
			
				//child network chooses whether to crouch, then this relays it to the game
				if (childNetwork.interpretPred(childNetwork.predictCrouch(dataNorm, layerSizes)) == 1) {
					roboGoon.keyPress(KeyEvent.VK_S);
				}else {
					roboGoon.keyRelease(KeyEvent.VK_S);}
				
			//if the player has lost the game
			}else if(game.gameOver) {
				
				//If childScore > parentScore, replace parent with child
				parentNetwork.evaluate(game, childNetwork);   
				
				//creates new child copy of parent and mutates(trains) it for next run.
				childNetwork = parentNetwork.deepCopy();      
				childNetwork.train(epoch);                    
				
				//moves to next epoch, then outputs the current iteration/epoch
				epoch++;
				System.out.println("-------------------------------------");
				System.out.println("Iteration: " + epoch);
				
				//Restart game and test the new child network
				game.restartGame();
			
			}
			
		//after all iterations are finished (epoch >= maxEpoch) AND this has not run before (!postRunFinished)
		} else if (!postRunFinished){
			
			//displays important info into the console
			System.out.println("Total Epochs Run: " + maxEpoch);
			System.out.println("Best Epoch: " + parentNetwork.bestScore);
			
			//records the neurons' weights and bias' and best sores. Passes in layerSizes for formating
			parentNetwork.recordBest(layerSizes);
			
			//prevents this if statement from looping
			postRunFinished = true;
		}
	}
	
	//data normalization. scales the input numbers to a size interpretable to the system (between 0 and 1)
	public List<Double> normalization(DinoGameTraining game) {
		List<Double> b = new ArrayList<Double>();
		
		b.add(game.data.get(0) / 20.0); //veloX
		
		double veloY = Math.max(-15, Math.min(game.data.get(1),  15)); //clamps veloY between -15 and 15 (theoretical max and min of veloY)
		b.add(veloY / 15); //veloY
		
		b.add(game.data.get(2) / 800); //dist1 (800 is theoretical max distance from player)
		b.add(game.data.get(3) / 800); //dist2 (800 is theoretical max distance from player)
		b.add((game.data.get(4) - game.OBSTACLE_HEIGHT) / game.maxObsHeight); //height1 (the model just looks at the additional "randomized" height of the cactus)
		b.add((game.data.get(5) - game.OBSTACLE_HEIGHT) / game.maxObsHeight); //height2	(maxObsHeight is the range/max value of the randomized portion of height)
		
		double dinoY = Math.min(game.data.get(6) / 17.7, 1.0);
		b.add(dinoY); //dinoY. Max dinoY (considering jump velocity and non-crouching-gravity (minimum gravity) is 17.6471. rounded to 17.7 for safety)
		
		double tti = game.data.get(2) / Math.max(game.data.get(0), 0.1); //raw time to impact. math insures velocity > 0.1 at all times (not 0 or negative)
		tti = Math.min(tti, 200); //caps time to impact at 200. The system doesn't need the exact distance. Just needs to know when its about to hit (clamps data)
		tti = tti / 200; //normalize tti by our chosen range
		b.add(tti); //time to impact
		
		return b; //returns clean and normalized list of inputs
	}	
	
	//Activation function
	public static double tanh(double in) {
		return Math.tanh(in);
	}
	
}
