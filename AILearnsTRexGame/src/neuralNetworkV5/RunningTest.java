package neuralNetworkV5;

/*	V5 - Changelog:
 * 	This is probably the first completely stable and working version
 * 	This version has a properly functioning network that does not rely on "luck" nearly as much as previous versions
 * 	
 * 	In Network class:
 * 		Features new neurons for 2 new inputs and hidden neurons (hardcoded in)
 * 		New constructor specifically for copied children networks
 * 			- No longer creates random-valued neurons on creation. Creates empty list instead.
 * 		Updated deepCopy by removing the "new ArrayList()"
 * 			- This, in conjunction with the new constructor, prevents the creation of a unused list of neurons during childNetwork initialization.
 * 			- Now only the deepCopy function will fill the empty list created in the constructor.
 * 		Updated evaluate function to not accept first model at the beginning of each run.
 * 		Updated train function to mutate different values in multiple neurons (instead of only 1 neurons) and increased total # of mutations per epoch
 * 		Updated interpretPred function to be more readable (functions identically to last version)
 * 		Updated predict functions and isolated shared components.
 * 			- created a new "computeHidden" function that computes all the neuron layers before the output layer
 * 				a. The code in computeHidden was shared by both predict functions. 
 * 				b. allows me to reuse code and condense the predict functions to improve readability
 * 			- Additionally I have all the compute functions import lists
 * 				a. Each input neuron computes the input data list from the normalization function in Util
 * 				b. These values are then collected in a second list and passed to the next layer of neurons
 * 				c. This allows all neurons to share the same compute function (i.e. no need for multiple different compute functions in the neuron class)
 * 			- Unfortunately, all neurons and their computations are still hardcoded into the system.
 * 				a. This will be converted into a dynamic system next version now that the system functions as intended.
 * 
 * In Neuron Class
 * 		Updated Neuron Save-File constructor by fixed improper indexing in the nested if statement
 * 		Removed compute5 function
 * 			- above-stated changes in the network class allows me to use compute(list) for all neurons now
 * 		Updated mutate function
 * 			- Now used Gaussian random number for "changeFactor"
 * 			- reduced most mutations to small mutations, BUT allowed the system to occasionally choose to do a larger mutation to avoid some false peaks/minimums
 * 
 * In Util Class
 * 		Updated Normalization function
 * 			- All inputs are now properly normalized now (between 0 and 1)
 * 			- added new input "timeToImpact" based on game inputs "dist1" and "velocityX" to add redundancies/different nuances to the inputs
 * 			- also imports new input from game "dinoY"
 * 
 * In DinoGame
 * 		Added fallback in case the distance calculator ever fails
 * 		commented out repaint from the main game loop
 * 			- rendering graphics is resource intensive
 * 			- without graphics rendering, the maximum fps before the my computer crashes increases from around 700fps to 500000fps
 * 		Updated some variables from private to public
 * 		Added new input "dinoY"
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
	
	
	
	
		

		
		

	


