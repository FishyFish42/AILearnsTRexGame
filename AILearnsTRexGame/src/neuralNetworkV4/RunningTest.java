package neuralNetworkV4;

/* V4 - Changelog:
 * 
 * Firstly, with the success the last model had with a predictable environment, This version introduces a new environment for the network
 * It adds randomness
 * The current project is to allow the network to learn timing as well as improve learning efficiency. 
 * 
 * This version also introduces a file saver and file reader to the network class
 * This is not usually important for ai, but it allows me to train my ai in small chunks and pause and continue training at a later date
 * Very helpful for me as I need to use this computer for college too and the ai trains at a veeeerrry slow pace currently
 * 
 * In RunningTest class
 * 		removed Util object. Useless here as its already created AND used by the dino class
 * 
 * In Network class
 * 		Increased Neuron in input layer and hidden layer from 3 to 5
 * 			- Allows 2 more inputs into the system
 * 		Updated predict functions to account for bigger input size
 * 		Updated train function to mutate multiple value at a time
 * 		New save-file constructor to import weights and bias from a save file (a text file)
 * 		New save file writer that records the values of the parent network in two separate text files.
 * 			- no longer records the starting random values in Util, but network class now utilizes both text files
 * 			- File1 is in a format that us humans can read easily
 * 			- File2 is in a format that the SaveReader class can read easily
 * 
 * In Neuron class
 * 		Updated compute3 to be compute5 (adjusted for extra inputs)
 * 		Added new save-file constructor to set values equal to the imported save-file values
 * 
 * In Util class
 * 		Removed unneeded meanSquareLoss function
 * 		Added some inputs into the normalization function
 * 		Removed initial values writer. moved file writing to Network class (check network class change log)
 * 		
 * 
 * In the DinoGame class
 * 		Added a ton of variables for randomization of environment + extra storage variables
 * 			- (runTime, maxObsDist, distVariation distTimeAdjust, maxObsHeight, heightVariation, heightTimeAdjust, height1, height2)
 * 			- randomizing obstacle distance
 * 			- randomizing obstacle height
 * 			- now exports the height of the two closest obstacles to the neural network (in the data list)
 * 		Added segments to "Spawn new variables" to randomize height of obstacles and distance between obstacles
 * 			- an if statement for both height and distance
 * 			- these statements slowly randomize the run more and more as the game advances
 * 				a. starts off predictable 
 * 				b. slowly becomes more randomized
 * 		Updated restartGame function to reflect the changes and properly reset the game
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
	
	
	
	
		

		
		

	


