package neuralNetworkV4;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;


public class Util {

	public Network parentNetwork;  							//best iteration so far
	private Network childNetwork; 							//mutated, candidate for the best
	private final Robot roboGoon;							//allows model to simulate button presses
	
	SaveReader uploadSave = new SaveReader();				//uploads saved bias/weights to parent system
	Double[][] saves;

	int maxEpoch = 500;
	int epoch = 0;
	boolean postRunFinished = false;
	
	
	public Util() {
		
		saves = uploadSave.readSave("bestEpochV2.txt", 5, 12);
		
		try {
			this.roboGoon = new Robot();
		}catch(AWTException e) {
			throw new RuntimeException("Failed to initialize Robot (possibly headless environment)", e);
		}
		
		parentNetwork = new Network(saves);
		//parentNetwork = new Network();
		
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
		
		b.add(game.data.get(0));
		b.add(game.data.get(1) / 250);
		b.add(game.data.get(2) / 500);
		b.add(game.data.get(3));
		b.add(game.data.get(4));
		
		return b;
	}	
	
	public static double tanh(double in) {
		return Math.tanh(in);
	}
	
}
