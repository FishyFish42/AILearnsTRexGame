package neuralNetworkV5;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Network {

	Random random = new Random();
	
	List<Neuron> neurons;
	List<Double> layerPasser;
	
	Integer bestScore = 0;
	
	//Post epochs, this saves the progress of the run.
	String filePath = "bestEpochV1.txt";
	String filePath2 = "bestEpochV2.txt";
	
	//no save imported
	public Network() {
		
		neurons = Arrays.asList(
				new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random),//input nodes   //speed, distance1, distance2, height1, height2, timeToImpact
				new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random), new Neuron(random),//hidden nodes 
				new Neuron(random), new Neuron(random) //output node
				);
		
	}
	
	//save imported
	public Network(Double[][] saves) {
		
		neurons = Arrays.asList(
				new Neuron(saves[0], random), new Neuron(saves[1], random), new Neuron(saves[2], random), new Neuron(saves[3], random), new Neuron(saves[4], random), new Neuron(saves[5], random), new Neuron(saves[6], random), new Neuron(saves[7], random),//input nodes   //speed, distance1, distance2, height1, height2, timeToImpact
				new Neuron(saves[8], random), new Neuron(saves[9], random), new Neuron(saves[10], random), new Neuron(saves[11], random), new Neuron(saves[12], random), new Neuron(saves[13], random), new Neuron(saves[14], random), new Neuron(saves[15], random),//hidden nodes 
				new Neuron(saves[16], random), new Neuron(saves[17], random) //output node
				);
		
		bestScore = saves[saves.length - 1][0].intValue();
		
	}
	
	//deepCopy constructor
	//boolean empty will not be used. Exist simply to give unique function identifier
	private Network(boolean empty) {
		neurons = new ArrayList<>();
	}
	
	public void train(int epoch) {

	    int mutationsThisEpoch;

	    if (bestScore == null || bestScore < 100) {
	        mutationsThisEpoch = 6;
	    } else if (bestScore < 1000) {
	        mutationsThisEpoch = 3;
	    } else {
	        mutationsThisEpoch = 1;
	    }

	    for (int i = 0; i < mutationsThisEpoch; i++) {
	        int neuronIndex = random.nextInt(neurons.size());
	        neurons.get(neuronIndex).mutate(random);
	    }
	}
	
	public Network deepCopy() {
	    Network copy = new Network(true);

	    for (Neuron n : this.neurons) {
	        copy.neurons.add(new Neuron(n)); // deep copy each neuron
	    }

	    copy.bestScore = this.bestScore;

	    return copy;
	}
	
	public void evaluate(DinoGameTraining game, Network childNetwork) {
		if (game.score > bestScore) {
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
		
		//activation function (tanh) outputs value between -1 and 1. This translate to a boolean like value.
		if(pred < 0) x = 0;
		else x = 1;
		
		return x;
	}
	
	//feed forwarding function
	private List<Double> computeHidden(List<Double> in) {
	    double i0 = neurons.get(0).compute(in);
	    double i1 = neurons.get(1).compute(in);
	    double i2 = neurons.get(2).compute(in);
	    double i3 = neurons.get(3).compute(in);
	    double i4 = neurons.get(4).compute(in);
	    double i5 = neurons.get(5).compute(in);
	    double i6 = neurons.get(6).compute(in);
	    double i7 = neurons.get(7).compute(in);
	    
	    layerPasser = Arrays.asList(i0, i1, i2, i3, i4, i5, i6, i7);
	    
	    double h0 = neurons.get(8).compute(layerPasser);
	    double h1 = neurons.get(9).compute(layerPasser);
	    double h2 = neurons.get(10).compute(layerPasser);
	    double h3 = neurons.get(11).compute(layerPasser);
	    double h4 = neurons.get(12).compute(layerPasser);
	    double h5 = neurons.get(13).compute(layerPasser);
	    double h6 = neurons.get(14).compute(layerPasser);
	    double h7 = neurons.get(15).compute(layerPasser);
	    
	    layerPasser = Arrays.asList(h0, h1, h2, h3, h4, h5, h6, h7);

	    return layerPasser;
	}

	public Double predictJump(List<Double> in) {
	    List<Double> h = computeHidden(in);
	    return neurons.get(16).compute(h);
	}

	public Double predictCrouch(List<Double> in) {
		List<Double> h = computeHidden(in);
	    return neurons.get(17).compute(h);
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

