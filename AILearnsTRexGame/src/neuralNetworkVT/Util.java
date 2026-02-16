package neuralNetworkVT;

import java.util.ArrayList;
import java.util.List;

public class Util {

	private Network parentNetwork;  								//best iteration so far
	private List<Network> eipsteinsIsland;							//stores the child network population. initialized in constructor and recreated in "gameOver" code
	//private final SaveReader uploadSave = new SaveReader();		//uploads saved bias/weights to parent system

	//USER CHANGEABLE VARIABLES
	//ALWAYS double check these before starting.
	int maxEpoch = 1000;
	int[] layerSizes = {8, 8, 2}; 							//Last number must ALWAYS be the number of outputs (2)(jump and crouch). Also be aware of the number of inputs into the system.
	
	//String saveFilePath = "bestEpochV2.txt"; 				//Only needed if going off of a save file. This is broken in this version
	
	//Internal counters/info storage 
	Double[][] saves;
	public int numOfInputs;
	int epoch = 0;
	boolean postRunFinished = false;
	int childPopulationSize;
	
	
	public Util(DinoGameTraining game) {
		
		//Takes constructor arguments and finds the data needed from them
		numOfInputs = normalization(game, 0).size();
		
		//saves = uploadSave.readSave(saveFilePath, 5, 12); //this is broken in this version
		
		//creates parent network
		//parentNetwork = new Network(saves); //this is broken in this version
		parentNetwork = new Network(numOfInputs, layerSizes);
		
		//creates and mutates(trains) many child copies of the parent equal to the childPopulationSize from the dinoGame
		eipsteinsIsland = new ArrayList<>();
		for(int i = 0; i < game.totalPopulation; i++) {
			Network childNetwork = parentNetwork.deepCopy();
			childNetwork.train();
			
			eipsteinsIsland.add(childNetwork);
		}
		
	}
	
	
	//connects the game to the network
	public void connector(DinoGameTraining game) {
		
		//while training is not finished
		if (epoch < maxEpoch) {
			
			//while game is still going
			if(!game.gameOver) {
			
				for(int i = 0; i < game.dinoStorage.size(); i++) {
					//data normalization
					List<Double> dataNorm = normalization(game, i);
					
					//child network chooses whether to jump, then this relays it to the game.
					if (eipsteinsIsland.get(i).interpretPred(eipsteinsIsland.get(i).predictJump(dataNorm, layerSizes)) == 1) {
						game.dinoStorage.get(i).jump();}
				
					//child network chooses whether to crouch, then this relays it to the game
					if (eipsteinsIsland.get(i).interpretPred(eipsteinsIsland.get(i).predictCrouch(dataNorm, layerSizes)) == 1) {
						game.dinoStorage.get(i).crouch();
					}else {
						game.dinoStorage.get(i).uncrouch();}
				}
				
			//if the player has lost the game
			}else if(game.gameOver) {
				
				
				//If childScore > parentScore, replace parent with child
				for(int i = 0; i < eipsteinsIsland.size(); i++) {
					parentNetwork.evaluate(i, game, eipsteinsIsland.get(i));   
				}
				
				//creates new child copies of parent and mutates(trains) it for next run.
				eipsteinsIsland = new ArrayList<>();
				for(int i = 0; i < game.totalPopulation; i++) {
					Network childNetwork = parentNetwork.deepCopy();
					childNetwork.train();
					
					eipsteinsIsland.add(childNetwork);
				}
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
	public List<Double> normalization(DinoGameTraining game, int goon) {
		
		List<Double> b = new ArrayList<Double>();
		
		b.add(game.data.get(0) / 20.0); //veloX
		
		double veloY = Math.max(-15, Math.min(game.dinoStorage.get(goon).velocityY,  15)); //clamps veloY between -15 and 15 (theoretical max and min of veloY)
		b.add(veloY / 15); //veloY
		
		b.add(game.data.get(1) / 800); //dist1 (800 is theoretical max distance from player)
		b.add(game.data.get(2) / 800); //dist2 (800 is theoretical max distance from player)
		b.add((game.data.get(3) - game.OBSTACLE_HEIGHT) / game.maxObsHeight); //height1 (the model just looks at the additional "randomized" height of the cactus)
		b.add((game.data.get(4) - game.OBSTACLE_HEIGHT) / game.maxObsHeight); //height2	(maxObsHeight is the range/max value of the randomized portion of height)
		
		double dinoY = Math.min(game.dinoStorage.get(goon).dinoY / 17.7, 1.0);
		b.add(dinoY); //dinoY. Max dinoY (considering jump velocity and non-crouching-gravity (minimum gravity) is 17.6471. rounded to 17.7 for safety)
		
		double tti = game.data.get(1) / Math.max(game.data.get(0), 0.1); //raw time to impact. math insures velocity > 0.1 at all times (not 0 or negative)
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
