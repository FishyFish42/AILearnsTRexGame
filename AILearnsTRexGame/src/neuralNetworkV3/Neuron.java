package neuralNetworkV3;

import java.util.List;
import java.util.Random;

public class Neuron {
	
	public final int numOfInputs = 3;
	public double bias;	
	public double[] Weights;
	
	public Neuron(Random random) {
		
		this.bias = random.nextDouble(-1,1);
		
		this.Weights = new double[numOfInputs];
		
		for(int i = 0; i < numOfInputs; i++) {
			this.Weights[i]=(random.nextDouble(-1,1));
		}
	}
	 public Neuron(Neuron n) {
		 this.bias = n.bias;

		 this.Weights = n.Weights.clone();
	 }
	
	
	public double compute(List<Double> in) {
		double preActivation = 0; 
		
        for(int i = 0; i < in.size(); i++) {
        	preActivation += Weights[i] * in.get(i);
        }
		
		preActivation += bias;
		double output = Util.tanh(preActivation);
		
		return output;
	}
	
	
	public double compute3(double input1, double input2, double input3) {
		
			double preActivation = (Weights[0] * input1) + (Weights[1] * input2) + (Weights[2] * input3) + bias;
			double output = Util.tanh(preActivation);
		
			return output;
	}
	
	
	public void mutate(Random random) {
		int propertyToChange = random.nextInt(0,numOfInputs + 1);
		Double changeFactor =  random.nextDouble(-1,1);
		
		if(propertyToChange == 0) {
			bias += changeFactor;
		}else {
			Weights[propertyToChange - 1] += changeFactor;
		}
	}
}
