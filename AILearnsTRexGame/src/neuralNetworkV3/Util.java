package neuralNetworkV3;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {

	//network object. this is THE neural network. Anything in this class talking to "network" is telling the network to act
	public Network parentNetwork = new Network(); 		//best iteration so far
	private Network childNetwork; 						//mutated, candidate for the best
	private final Robot roboGoon;

	int maxEpoch = 1000;
	int epoch = 0;
	boolean postRunFinished = false;
	
	
	public Util() {
		try {
			this.roboGoon = new Robot();
		}catch(AWTException e) {
			throw new RuntimeException("Failed to initialize Robot (possibly headless environment)", e);
		}
		
		childNetwork = parentNetwork.deepCopy();
		childNetwork.train(epoch);
		
		try (FileWriter writer = new FileWriter(parentNetwork.filePath2)) {
			
			//best epoch loss. REMEMBER, this is dependant on what the "goal" is in the Util class.
			writer.write("Best Score: " + parentNetwork.bestScore);
			writer.write(System.lineSeparator()); // Add a new line
			writer.write(System.lineSeparator()); // Add a new line
			
			//this cycles through the neurons and writes out their information such as bias and weight
			for(int i = 0; i < parentNetwork.neurons.size(); i++) {
				
				writer.write("Neuron " + i + ": "); // Write string to file
				writer.write(System.lineSeparator()); // Add a new line
				
				writer.write("Bias: " + parentNetwork.neurons.get(i).bias);
				writer.write(System.lineSeparator()); // Add a new line
				
				//cycles through the weight of each neuron and writes it out
				for(int j = 0; j < parentNetwork.neurons.get(i).numOfInputs; j++) {

					writer.write("Weight " + j + ": " + parentNetwork.neurons.get(i).Weights[j]);
					writer.write(System.lineSeparator()); // Add a new line
				}
				
				//in between neuron write cycles, this adds an extra line between individual neuron datas
				writer.write(System.lineSeparator()); // Add a new line
				
			}
		    System.out.println("File written successfully: " + parentNetwork.filePath2);
		} catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
	}
	
	public void connector(DinoGameTraining game) {
		if (epoch < maxEpoch) {
			//System.out.println("Hello");
			if(epoch == maxEpoch - 1) {
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
		
		return b;
	}	
	
	public static double tanh(double in) {
		return Math.tanh(in);
	}
	
	public static Double meanSquareLoss(int goalOfTraining, int finalData) {
		
		double sumSquare = 0;
		
		double error =  goalOfTraining - finalData;
		sumSquare += (error * error);
		
		return sumSquare;
	}
	
	
}
