package neuralNetworkV4;

import java.io.BufferedReader;
import java.io.FileReader;

public class SaveReader {
	
	public Double[][] readSave(String filePath, int numOfInputs, int numOfNeurons) {
		
		Double[][] saves = new Double[numOfNeurons + 1][numOfInputs + 1];
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				
				for(int row = 0; row < numOfNeurons; row++) {
					
					String line = br.readLine();
					if (line == null || line.trim().isEmpty()) break;  // file ended early

					String[] numbers = line.trim().split(" ");

					for (int col = 0; col < numOfInputs + 1; col++) {
						
						if(col < numbers.length) {
							double num = Double.parseDouble(numbers[col]); //converting the recorded string data to integer values
							saves[row][col] = num;
						}else {
							col = numOfInputs;
						}
						
					}
				}
				br.readLine();
				String line = br.readLine();
				saves[numOfNeurons][0] = Double.parseDouble(line);
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		return saves;	
		
	}
	
	
}
