package neuralNetworkV2;

import java.util.List;
import java.util.Random;

public class Neuron {
	
	public final int numOfInputs = 5;
	public double oldBias, bias;	
	public double[] oldWeights, Weights;
	
	public Neuron(Random random) {
		
		oldBias = random.nextDouble(-1,1);
		bias = random.nextDouble(-1,1);
		
		oldWeights = new double[numOfInputs];
		Weights = new double[numOfInputs];
		
		for(int i = 0; i < 5; i++) {
			this.oldWeights[i]=(random.nextDouble(-1,1));
			this.Weights[i]=(random.nextDouble(-1,1));
		}
	}
	
	
	public double compute(List<Double> in) {
		double preActivation = 0; 
		
        for(int i = 0; i < in.size(); i++) {
        	preActivation += this.Weights[i] * in.get(i);
        }
		
		preActivation += this.bias;
		double output = Util.sigmoid(preActivation);
		
		return output;
	}
	
	
	public double compute5(double input1, double input2, double input3, double input4, double input5 ) {
		
			double preActivation = (this.Weights[0] * input1) + (this.Weights[1] * input2) + (this.Weights[2] * input3) + (this.Weights[3] * input4) + (this.Weights[4] * input5) + this.bias;
			double output = Util.sigmoid(preActivation);
		
			return output;
	}
	
	
	public void mutate(Random random) {
		int propertyToChange = random.nextInt(0,6);
		Double changeFactor =  random.nextDouble(-1,1);
		
		if(propertyToChange == 0) {
			this.bias += changeFactor;
		}else {
			this.Weights[propertyToChange - 1] += changeFactor;
		}
	}
	
	
	public void forget() {
		bias = oldBias;
		for (int i = 0; i < Weights.length; i++){
			Weights[i] = oldWeights[i];
		}
	}
	
	public void remember() {
		oldBias = bias;
		for (int i = 0; i < Weights.length; i++){ 
			oldWeights[i] = Weights[i];
		} 
	}
}
