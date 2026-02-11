package neuralNetworkV1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunningTest {

	public static void main(String[] args) {
		
		Network network = new Network();
		Double prediction;
	
		List<List<Integer>> data = new ArrayList<List<Integer>>();
		data.add(Arrays.asList(115, 66));
		data.add(Arrays.asList(175, 78));
		data.add(Arrays.asList(205, 72));
		data.add(Arrays.asList(120, 67));
		
		List<Double> answers = Arrays.asList(1.0, 0.0, 0.0, 1.0);
		
		try {
	
		network.train(data, answers);
		
		prediction = network.predict(120, 66);
		System.out.println("output: "+ prediction);
		
		}catch(Exception e) {
			e.printStackTrace();
			};

	}

}
