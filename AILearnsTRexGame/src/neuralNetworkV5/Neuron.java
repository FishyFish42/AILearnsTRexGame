package neuralNetworkV5;

import java.util.List;
import java.util.Random;

public class Neuron {
	
	public final int numOfInputs = 8;
	public double bias;	
	public double[] Weights;
	
	//if no save is imported
	public Neuron(Random random) {
		
		this.bias = random.nextDouble(-1,1);
		
		this.Weights = new double[numOfInputs];
		
		for(int i = 0; i < numOfInputs; i++) {
			this.Weights[i]=(random.nextDouble(-1,1));
		}
	}
	
	//if save is imported
	public Neuron(Double[] saves, Random random) {
		
		this.bias = saves[0];
		
		this.Weights = new double[numOfInputs];
		
		for(int i = 0; i < numOfInputs; i++) {
			if(i + 1 < saves.length) {
				this.Weights[i] = saves[i + 1]; 	//weights gets i - 1 because its length is numOfInputs, thus the last viable index in weights is numOfInputs - 1
			}else {
				this.Weights[i] = random.nextDouble(-1, 1);
			}
		}
	}
	
	//Specifically used for the childNetwork
	 public Neuron(Neuron n) {
		 this.bias = n.bias;

		 this.Weights = n.Weights.clone();
	 }
	
	
	public double compute(List<Double> in) {
		double preActivation = 0; 
		
        for(int i = 0; i < numOfInputs; i++) {
        	preActivation += Weights[i] * in.get(i);
        }
		
		preActivation += bias;
		double output = Util.tanh(preActivation);
		
		return output;
	}
	
	public void mutate(Random random) {
	    int propertyToChange = random.nextInt(0, numOfInputs + 1);

	    // Small mutation most of the time
	    double changeFactor = random.nextGaussian() * 0.1; // mean 0, std dev 0.1

	    // Occasionally allow a big jump
	    if (random.nextDouble() < 0.05) { // 5 percent chance
	        changeFactor = random.nextGaussian() * 0.5;
	    }

	    if(propertyToChange == 0) {
	        bias += changeFactor;
	    }else {
	        Weights[propertyToChange - 1] += changeFactor;
	    }
	}
}
