package neuralNetworkV6;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Network {

	Random random = new Random();
	
	List<List<Neuron>> neurons;  		//2D list of Neurons, a list of lists
	
	Integer bestScore = 0;
	int numOfInputs;
	
	//Post epochs, this saves the progress of the run.
	String filePath = "bestEpochV1.txt";
	String filePath2 = "bestEpochV2.txt";
	
	//no save imported
	public Network(int numOfInputs, int[] layerSizes) {
		
		this.numOfInputs = numOfInputs;
		neurons = new ArrayList<>();
		
		for(int i = 0; i < layerSizes.length; i++) { 				//rows for loop
			
			List<Neuron> layer = new ArrayList<>();
			
			if(i == 0) {
				for(int j = 0; j < layerSizes[i]; j++) {			//columns for loop V1
					layer.add(new Neuron(random, numOfInputs));
				}
			}else {
				for(int j = 0; j < layerSizes[i]; j++) {
					layer.add(new Neuron(random, layerSizes[i-1]));	//columns for loop V2
				}
			}
			
			neurons.add(layer);
		}
		
	}
	
	//save imported
/*	public Network(Double[][] saves) {
		
		neurons = Arrays.asList(
				new Neuron(saves[0], random), new Neuron(saves[1], random), new Neuron(saves[2], random), new Neuron(saves[3], random), new Neuron(saves[4], random), new Neuron(saves[5], random), new Neuron(saves[6], random), new Neuron(saves[7], random),//input nodes   //speed, distance1, distance2, height1, height2, timeToImpact
				new Neuron(saves[8], random), new Neuron(saves[9], random), new Neuron(saves[10], random), new Neuron(saves[11], random), new Neuron(saves[12], random), new Neuron(saves[13], random), new Neuron(saves[14], random), new Neuron(saves[15], random),//hidden nodes 
				new Neuron(saves[16], random), new Neuron(saves[17], random) //output node
				);
		
		bestScore = saves[saves.length - 1][0].intValue();
		
	}*/
	
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
	        int interNeuronIndex = random.nextInt(neurons.get(neuronIndex).size());
	        neurons.get(neuronIndex).get(interNeuronIndex).mutate(random);
	    }
	}
	
	public Network deepCopy() {
	    Network copy = new Network(true);
	    
	    for(int i = 0; i < neurons.size(); i++) { 				//rows for loop
			
			List<Neuron> layer = new ArrayList<>();
			
			if(i == 0) {
				for(int j = 0; j < neurons.get(i).size(); j++) {			//columns for loop V1
					layer.add(new Neuron(neurons.get(i).get(j)));
				}
			}else {
				for(int j = 0; j < neurons.get(i).size(); j++) {
					layer.add(new Neuron(neurons.get(i).get(j)));	//columns for loop V2
				}
			}
			
			copy.neurons.add(layer); // deep copy each neuron
		}

	    copy.bestScore = this.bestScore;

	    return copy;
	}
	
	public void evaluate(DinoGameTraining game, Network childNetwork) {
		if (game.score > bestScore) {
	        bestScore = game.score;
	        System.out.println("New Best Epoch: " + bestScore);

	        this.neurons = new ArrayList<>();
	        for (List<Neuron> n : childNetwork.neurons) {
	        	
	        	List<Neuron> layer = new ArrayList<>();
	        	
	        	for(int i = 0; i < n.size(); i++) {
	        		layer.add(new Neuron(n.get(i)));
	        	}
	        	
	        	this.neurons.add(layer);
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
	private List<Double> computeLayer(int i, List<Double> in, int layerSize) {
		
		List<Double> layerPasser = new ArrayList<>();									
		
			for(int j = 0; j < layerSize; j++) {
				layerPasser.add(neurons.get(i).get(j).compute(in));
			}
	    return layerPasser;
	}

	public Double predictJump(List<Double> in, int[] layerSizes) {
	    
		List<Double> layerResults;
		
		layerResults = computeLayer(0, in, layerSizes[0]);
		
		for(int i = 1; i < layerSizes.length; i++) {
			List<Double> h = computeLayer(i, layerResults, layerSizes[i]);
			layerResults = h;
		}
				
	    return layerResults.get(0);
	}

	public Double predictCrouch(List<Double> in, int[] layerSizes) {
		
		List<Double> layerResults;
		
		layerResults = computeLayer(0, in, layerSizes[0]);
		
		for(int i = 1; i < layerSizes.length; i++) {
			List<Double> h = computeLayer(i, layerResults, layerSizes[i]);
			layerResults = h;
		}
		
	    return layerResults.get(1);
	}
	
	
	//The following function runs after we have run the max number of epochs.
	//This records the information about the best epoch data to a text file for later use
	//when the program is rerun, this text file will be overwritten. Be sure to save it
	public void recordBest(int[] layerSizes) {
		// Use try-with-resources to automatically close the FileWriter
		//This version readable by humans
		try (FileWriter writer = new FileWriter(filePath)) {
			
			//best epoch loss. REMEMBER, this is dependant on what the "goal" is in the Util class.
			writer.write("Best Score: " + bestScore);
			writer.write(System.lineSeparator()); // Add a new line
			writer.write(System.lineSeparator()); // Add a new line
			
			//this cycles through the neurons and writes out their information such as bias and weight
			for(int i = 0; i < layerSizes.length; i++) {
				writer.write("LAYER " + i + ": "); // Write string to file
				writer.write(System.lineSeparator()); // Add a new line
				writer.write("-------------------"); // Write string to file
				writer.write(System.lineSeparator()); // Add a new line
				
				for(int j = 0; j < layerSizes[i]; j++) {
				
					writer.write("Neuron " + j + ": "); // Write string to file
					writer.write(System.lineSeparator()); // Add a new line
				
					writer.write("Bias: " + neurons.get(i).get(j).bias);
					writer.write(System.lineSeparator()); // Add a new line
				
					//cycles through the weight of each neuron and writes it out
					if(i == 0) {
						for(int k = 0; k < numOfInputs; k++) {

							writer.write("Weight " + k + ": " + neurons.get(i).get(j).Weights[k]);
							writer.write(System.lineSeparator()); // Add a new line
						}
					}else {
						for(int k = 0; k < layerSizes[i - 1]; k++) {

							writer.write("Weight " + k + ": " + neurons.get(i).get(j).Weights[k]);
							writer.write(System.lineSeparator()); // Add a new line
						}
					}
				
				//in between neuron write cycles, this adds an extra line between individual neuron datas
				writer.write(System.lineSeparator()); // Add a new line
				
				}
			}
			
		    System.out.println("File written successfully: " + filePath);
		} catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }

		//version readable by the SaveReader class
		try (FileWriter writer = new FileWriter(filePath2)) {
	
				//this cycles through the neurons and writes out their information such as bias and weight
				for(int i = 0; i < layerSizes.length; i++) {
					for(int j = 0; j < layerSizes[i]; j++) {
		
						writer.write("" + neurons.get(i).get(j).bias);
				
					//cycles through the weight of each neuron and writes it out
						if(i == 0) {
							for(int k = 0; k < numOfInputs; k++) {
							
								writer.write(" " + neurons.get(i).get(j).Weights[k]);
							}
						}else {
							for(int k = 0; k < layerSizes[i - 1]; k++) {
								
								writer.write(" " + neurons.get(i).get(j).Weights[k]);
							}
						}
					//in between neuron write cycles, this adds an extra line between individual neuron datas
						writer.write(System.lineSeparator()); // Add a new line
		
					}
				}
				
				writer.write(System.lineSeparator());
				writer.write("" + bestScore);
				
				System.out.println("File written successfully: " + filePath2);
		} catch (IOException e) {
			System.err.println("An error occurred while writing to the file: " + e.getMessage());
		}
	} 
	
}

