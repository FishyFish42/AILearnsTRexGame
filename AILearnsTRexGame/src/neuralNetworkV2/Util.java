package neuralNetworkV2;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Util {

	//network object. this is THE neural network. Anything in this class talking to "network" is telling the network to act
	private final Network network = new Network();
	private final Robot roboGoon;
	
	int maxEpoch = 100;
	int epoch = 0;
	int goalOfTraining = 100;
	boolean postRunFinished = false;
	
	public Util() {
		try {
			this.roboGoon = new Robot();
		}catch(AWTException e) {
			throw new RuntimeException("Failed to initialize Robot (possibly headless environment)", e);
		}
	}
	
	public void connector(DinoGame game, boolean gameOver) {
		if (epoch <= maxEpoch) {
			//System.out.println("Hello");
		
			if(gameOver) {
				System.out.println("-------------------------------------");
				System.out.println("Iteration: " + epoch);
			
				network.errCalc(goalOfTraining, game.score);
			
				network.train(epoch);
				epoch++;
				game.restartGame();
			
			}else if(!gameOver) {
			
				if (network.interpretPred(network.predictJump(game.data)) == 1) {
					roboGoon.keyPress(KeyEvent.VK_SPACE);
				}else {
					roboGoon.keyRelease(KeyEvent.VK_SPACE);}
			
				if (network.interpretPred(network.predictCrouch(game.data)) == 1) {
					roboGoon.keyPress(KeyEvent.VK_S);
				}else {
					roboGoon.keyRelease(KeyEvent.VK_S);}
			}
		} else if (!postRunFinished){
			System.out.println("Total Epochs Run: " + maxEpoch);
			System.out.println("Best Epoch: " + network.bestEpochLoss);
			network.recordBest();
			postRunFinished = true;
		}
	}
	
	public static double sigmoid(double in) {
		return 1 / (1 + Math.exp(-in));
	}
	
	public static Double meanSquareLoss(int goalOfTraining, int finalData) {
		
		double sumSquare = 0;
		
		double error =  goalOfTraining - finalData;
		sumSquare += (error * error);
		
		return sumSquare;
	}
	
	
}
