package neuralNetworkV3;

/* V3 Change log:
 * This version is the first real big bug fix of the model. 
 * Because V1 was a prediction model, it was used to having a confirmed outcome to compare the predictions to
 * This Model wants to play the game real time AND does not have an "outcome" to compare the outcomes too
 * 
 * Because the new model's internal neural network is object orientated while V1 was not internally object orientated, I had to overhaul the bias/weight system
 * This version introduces a true deep copy build that doesn't shallowly copy or change references within the parent class when it shouldn't
 * 
 * In RunningTest:
 * 		- removed network object. The util class already does this and we only need 1 single parent network object.
 * 
 * In Network:
 * 		- Condensed down the predict functions so it was not recalculating layers more than once.
 * 		- Created a deepCopy function
 * 			- Fixes the issue of the child copy of the parent network being shallow OR the child copy changing the values of the parent class directly
 * 		- Removed errCalc function and replaced it with an evaluate function
 * 			a. there is no need for a prediction confidence model here as we dont have an "expected vs real outcome" 
 * 				i. (note from Future editor, prediction confidence may become important again later, but not until THIS hill-climber model works as intended.)
 * 			b. replaced with basic evaluate function 
 * 				i. simply compares score and chooses which model is better.
 * 				ii. if better, replace old with new
 * 					- creates new neurons list for the parent class and copies the values of the child class into a new neuron (deep copy) which is added to the new list
 * 				iii. if worse, keep old
 * 			c. This system also replaces neuron.remember and neuron.forget 
 * 		- Train function condensed to one line
 * 
 * In Neuron:
 * 		- Removed the oldBias/oldWeights system
 * 			a. This was the reason for the huge bugs in V2
 * 			b. Because we are now changing values and copying values from separate neurons, we no longer need internal memory of previous values
 * 				i. "parent class compared to child class" vs "parent class old values vs new"
 * 		- Created new constructor for cloned neurons in the child class
 * 			a. copies values of the passed in (original) neuron from the parent class
 * 		- Updated compute functions to remove the "this" reference as it was unneeded
 * 			a. additionally updated the reference to the activation function to point towards "tanh" instead of "sigmoid" (check Util Change log)
 * 		- Updated mutate function to remove the "this" reference as it was unneeded
 * 		- removed remember and forget functions. This system gets replaced in the evaluate function within the network class (check above change log)
 * 				
 * In Util:
 * 		- Added a file writer to write out initial values on a text file to help bug fix improper deepCopying
 * 			a. this will be removed next version when I can confirm that the deepCopy handles the child class mutation and evaluation correctly
 * 			b. only purpose of this is to bug fix
 * 		- created child network object for the new deepCopy system
 * 		- updated constructor to deepCopy the parent network onto the child network and then to train the child network
 * 		- created a data normalization function so that data from the game is now meaningful to the neural network
 * 			a. puts the values between 0 and 1
 * 		- updated connector function
 * 			a. firstly, reordered it to be more readable (game still going code, THEN game over code)
 * 			b. updated the predictInterpreter to take normalized data instead of raw data
 * 			c. updated the all calls on the parent class predict/training functions to call the child class instead
 * 				i. because the child class is playing the game, not the parent class
 * 			d. in the game over segment
 * 				i. evaluates whether the child class or parent class is better and replaces parent class with the best option
 * 				ii. re-copyies and trains the child class every iteration
 * 		- Replaced the sigmoid activation function with a tanh activation function.
 * 		
 * In DinoGame:
 * 		- Fixed an issue where the data list values were not getting updated with real time values
 * 			i. needed to be reassigned every frame
 * 			ii. added the line into the game loop
 * 		- Changed the functionality of the game to make testing and training easier on the network while I build the initial network
 * 			i. velocityX increases as a slower rate (within game loop)
 * 			ii. removed the randomness of the obstacle heights and distances
 * 				- the network is neither complex enough nor built correctly enough to learn complex timings needed for this.
 * 				- this helps me insure that the base functionality of the network works before i give it the ability to learn these things
 */

import javax.swing.JFrame;

public class RunningTest{
	
	static DinoGameTraining game = new DinoGameTraining();
	Util util = new Util();
	
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
	
	
	
	
		

		
		

	


