package neuralNetworkV1;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Network {

	List<Neuron> neurons = Arrays.asList(
			new Neuron(), new Neuron(), new Neuron(),//input nodes
			new Neuron(), new Neuron(), //hidden nodes
			new Neuron() //output node
			);
	
	
	//feedforwarding function
	public Double predict(Integer input1, Integer input2) {
		
		//inputs the first three neurons (input nodes) into node 4 and 5 (the hidden nodes) and outputs a single output (node 6)
		
		return neurons.get(5).compute2(
				neurons.get(3).compute3(
						neurons.get(0).compute2(input1, input2),
						neurons.get(1).compute2(input1, input2),
						neurons.get(2).compute2(input1, input2)
						), 
				neurons.get(4).compute3(
						neurons.get(0).compute2(input1, input2),
						neurons.get(1).compute2(input1, input2),
						neurons.get(2).compute2(input1, input2)
						)
				);
	}
	
	public void train(List<List<Integer>> data, List<Double> answers) {
		Double bestEpochLoss = null;
		for(int epoch = 0; epoch < 1000; epoch++) {
			//adapt neuron
			Neuron epochNeuron = neurons.get(epoch % 6);
			
			epochNeuron.mutate();
			
			List<Double> predictions = new ArrayList<Double>();
			for(int i = 0; i < data.size(); i++) {
				predictions.add(i, this.predict(data.get(i).get(0), data.get(i).get(1)));
			}
			
			Double thisEpochLoss = Util.meanSquareLoss(answers, predictions);
			
			if(bestEpochLoss == null) {
				bestEpochLoss = thisEpochLoss;
				epochNeuron.remember();
			}else {
				if(thisEpochLoss < bestEpochLoss) {
					bestEpochLoss = thisEpochLoss;
					epochNeuron.remember();
				}else {
					epochNeuron.forget();
				}
			}
		};
	}
	
	
}
