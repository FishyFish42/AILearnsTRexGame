package neuralNetworkV6;

/*	V6 - Changelog:
 * 	This version functions identically to the last one with a single exception.
 * 	This version's neural network is dynamic in size as opposed to previous versions' hardcoded network sizes
 * 	i.e. we can change the size of the neural network(# of neurons, # of layers, # of outputs) 
 * 
 * 	To do this, go to the Util class and look for the "layerSizes" list variable
 * 	for each extra number you add to the list, another layer will be added
 * 	the network will create neurons equal to the number you put for that layer
 * 
 * 	The first layer will always have a number of inputs equal to the total number of data points outputed by the normalization function in Util (in this case, 8)
 * 	Each subsequent layer will have each neuron from the previous layer passed in
 * 	The last layer is always the output layer. For the sake of this model, that should ALWAYS be 2
 * 
 * 	The predictJump and predictCrouch functions in the Network class still have the output variables hardcoded. 
 * 	Ensure that the last number of the layerSizes list is always 2
 */
import javax.swing.JFrame;

public class RunningTest{
	
	static DinoGameTraining game = new DinoGameTraining();
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Chrome T-Rex Game");
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.startGameThread();
	}
}
	
	
	
	
		

		
		

	


