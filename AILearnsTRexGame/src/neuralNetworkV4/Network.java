package neuralNetworkV4;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Network {

	Random random = new Random();
	
	List<Neuron> neurons;
	
	Integer bestScore = null;
	
	//Post epochs, this saves the progress of the run.
	String filePath = "bestEpochV1.txt";
	String filePath2 = "bestEpochV2.txt";
	
	//no save imported
	public Network() {
		
		neurons = Arrays.asList(
				new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random),//input nodes   //speed, distance1, distance2, is jumping, is crouching
				new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random),//hidden nodes 
				new Neuron(random), new Neuron(random) //output node
				);
		
	}
	
	//save imported
	public Network(Double[][] saves) {
		
		neurons = Arrays.asList(
				new Neuron(saves[0], random), new Neuron(saves[1], random), new Neuron(saves[2], random), new Neuron(saves[3], random), new Neuron(saves[4], random),//input nodes   //speed, distance1, distance2, is jumping, is crouching
				new Neuron(saves[5], random), new Neuron(saves[6], random), new Neuron(saves[7], random), new Neuron(saves[8], random), new Neuron(saves[9], random),//hidden nodes 
				new Neuron(saves[10], random), new Neuron(saves[11], random) //output node
				);
		
		bestScore = saves[saves.length - 1][0].intValue();
		
	}
	
	
	public void train(int epoch) {
		
		if(bestScore < 100) {
			neurons.get(epoch % neurons.size()).mutate(random);
			neurons.get(epoch % neurons.size()).mutate(random);
			neurons.get(epoch % neurons.size()).mutate(random);
		}else if(bestScore < 1000 && bestScore >= 100) {
			neurons.get(epoch % neurons.size()).mutate(random);
			neurons.get(epoch % neurons.size()).mutate(random);
		}else if(bestScore >= 1000) {
			neurons.get(epoch % neurons.size()).mutate(random);
		}
	}
	
	public Network deepCopy() {
	    Network copy = new Network();

	    copy.neurons = new ArrayList<>();
	    for (Neuron n : this.neurons) {
	        copy.neurons.add(new Neuron(n)); // deep copy each neuron
	    }

	    copy.bestScore = this.bestScore;

	    return copy;
	}
	
	public void evaluate(DinoGameTraining game, Network childNetwork) {
		if (bestScore == null || game.score > bestScore) {
	        bestScore = game.score;
	        System.out.println("New Best Epoch: " + bestScore);

	        this.neurons = new ArrayList<>();
	        for (Neuron n : childNetwork.neurons) {
	            this.neurons.add(new Neuron(n));
	            
	        }
		}
	}

	
	public int interpretPred(Double pred) {
		int x;
		if(pred < 0.5) {
			x = 0;
		}else {
			x = 1;}
		return x;
	}
	
	//feed forwarding function
	public Double predictJump(List<Double> in) {
		
		double i0 = neurons.get(0).compute(in);
		double i1 = neurons.get(1).compute(in);
		double i2 = neurons.get(2).compute(in);
		double i3 = neurons.get(3).compute(in);
		double i4 = neurons.get(4).compute(in);
		
		double i5 = neurons.get(5).compute5(i0, i1, i2, i3, i4);
		double i6 = neurons.get(6).compute5(i0, i1, i2, i3, i4);
		double i7 = neurons.get(7).compute5(i0, i1, i2, i3, i4);
		double i8 = neurons.get(8).compute5(i0, i1, i2, i3, i4);
		double i9 = neurons.get(9).compute5(i0, i1, i2, i3, i4);
		
		return neurons.get(10).compute5(i5, i6, i7, i8, i9);
	}
	
	public Double predictCrouch(List<Double> in) {
		
		//inputs the first three neurons (input nodes) into node 4 and 5 (the hidden nodes) and outputs a single output (node 6)
		double i0 = neurons.get(0).compute(in);
		double i1 = neurons.get(1).compute(in);
		double i2 = neurons.get(2).compute(in);
		double i3 = neurons.get(3).compute(in);
		double i4 = neurons.get(4).compute(in);
		
		double i5 = neurons.get(5).compute5(i0, i1, i2, i3, i4);
		double i6 = neurons.get(6).compute5(i0, i1, i2, i3, i4);
		double i7 = neurons.get(7).compute5(i0, i1, i2, i3, i4);
		double i8 = neurons.get(8).compute5(i0, i1, i2, i3, i4);
		double i9 = neurons.get(9).compute5(i0, i1, i2, i3, i4);
		
		return neurons.get(11).compute5(i5, i6, i7, i8, i9);
	}
	
	
	//The following function runs after we have run the max number of epochs.
	//This records the information about the best epoch data to a text file for later use
	//when the program is rerun, this text file will be overwritten. Be sure to save it
	public void recordBest() {
		// Use try-with-resources to automatically close the FileWriter
		//This version readable by humans
		try (FileWriter writer = new FileWriter(filePath)) {
			
			//best epoch loss. REMEMBER, this is dependant on what the "goal" is in the Util class.
			writer.write("Best Score: " + bestScore);
			writer.write(System.lineSeparator()); // Add a new line
			writer.write(System.lineSeparator()); // Add a new line
			
			//this cycles through the neurons and writes out their information such as bias and weight
			for(int i = 0; i < neurons.size(); i++) {
				
				writer.write("Neuron " + i + ": "); // Write string to file
				writer.write(System.lineSeparator()); // Add a new line
				
				writer.write("Bias: " + neurons.get(i).bias);
				writer.write(System.lineSeparator()); // Add a new line
				
				//cycles through the weight of each neuron and writes it out
				for(int j = 0; j < neurons.get(i).numOfInputs; j++) {

					writer.write("Weight " + j + ": " + neurons.get(i).Weights[j]);
					writer.write(System.lineSeparator()); // Add a new line
				}
				
				//in between neuron write cycles, this adds an extra line between individual neuron datas
				writer.write(System.lineSeparator()); // Add a new line
				
			}
		    System.out.println("File written successfully: " + filePath);
		} catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }

		//version readable by the SaveReader class
		try (FileWriter writer = new FileWriter(filePath2)) {
	
				//this cycles through the neurons and writes out their information such as bias and weight
				for(int i = 0; i < neurons.size(); i++) {
		
					writer.write("" + neurons.get(i).bias);
				
					//cycles through the weight of each neuron and writes it out
					for(int j = 0; j < neurons.get(i).numOfInputs; j++) {

						writer.write(" " + neurons.get(i).Weights[j]);
					}
		
					//in between neuron write cycles, this adds an extra line between individual neuron datas
					writer.write(System.lineSeparator()); // Add a new line
		
				}
				
				writer.write(System.lineSeparator());
				writer.write("" + bestScore);
				
				System.out.println("File written successfully: " + filePath2);
		} catch (IOException e) {
			System.err.println("An error occurred while writing to the file: " + e.getMessage());
		}
	} 
	
}

