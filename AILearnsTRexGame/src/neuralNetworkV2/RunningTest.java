package neuralNetworkV2;

/* V2 - Change log:
 * This model try's to convert my prediction network into a real time neural network that can play the dino game
 * Doesn't work all too well, but its a start
 * 
 * Instead of dino game and the neural network running independently and for separate purposes, this new model has both run in unison and- 
 * makes some changes for that purpose:
 * 		- Running test is the all purpose main function now.
 * 			i. this starts the game loop in dinoGame and starts the neural network running.
 * 		- DinoGame no longer has a main function
 * 
 * Also changed the purpose of the Util class. Initially, it was simply a spare math function holder. Now it is used as a connector between the network and the game.
 * 
 * In Running Test:
 * 		- RunningTest serves a different purpose than V1
 * 			i. RunningTest V1
 * 				a. Creates and hold data set for prediction model
 * 				b. creates and trains the network object
 * 				c. Runs the program and returns prediction based on new input
 * 			ii. RunningTest V2
 * 				a. Creates dinoGame, network, and Util objects to get them started and ready
 * 				b. Steals the functionality of DinoGameV1 main function
 * 				c. creates and sets up JFrame and adds the game it it
 * 				d. starts game loop.
 * 
 * In Network:
 * 		- Increased the size of the network system (more neurons)
 * 		- Added an extra output neuron.
 * 			i. Outputs "jump" and "crouch" for the 2 output neurons
 * 		- Separated the training, prediction, and errCalculation into 3 separate functions so I can choose when in the gaming process that they trigger
 * 			i. training sets the neurons bias and weights
 * 				a. runs before the game before at the beginning of each epoch
 * 			ii. prediction runs the games data every single frame and returns the values of "jump" or "crouch" or both or neither
 * 				a. runs during the game consistently.
 * 			iii. evaluates the new mutations after the game and decides "keep new bias and weights or keep oldBias and oldWeights
 * 				a. runs after the game ends at the end of each epoch
 * 			iv. This is different from V1 because V1 just ran until it had over and over and made mutations every time until it was close to the expected outcome.
 * 			v. This version is running off of an separate a game and doesn't really have an expected outcome. 
 * 			vi. Thus, these functionalities need to be separated so that they can run when they are intended to be run in sync with the game.
 * 		- Added a fileWriter to record what the best bias and weights were after hitting max epochs
 * 
 * In Neurons:
 * 		- Created neuron constructor and moved bias/weights instantiation to it.
 * 		- Created a neuron compute function that imports a list for the data
 * 			i. This is a test on how to allow the neuron list to take dynamic input sizes in the future
 * 			ii. I plan to implement this into all neuron layers in the future once I can get the network to work first
 * 		- Updated remember and forget functions to use for loops for the weights
 * 		- Condensed the mutate function for the weights
 * 		
 * In Util:
 * 		- Added constructor to instantiate RoboGoon (robot) allow the network to interact the game
 * 		- Created connector function. This is the big important function for the game. This connects the game to the network
 * 			i. Imports data from dinoGame (check change log from dinoGame)
 * 			ii. ports data into predict functions
 * 			iii. takes predictions and tells robot to do actions based on the result
 * 			iv. trains the network in between epochs
 * 			v. evaluates networks in between epochs.
 * 
 * In dinoGame:
 * 		- Added variables to pass to the network (dist1, dist2)
 * 		- calculates the new variables
 * 			i. distance between player and the first and second obstacle respectively
 * 			ii. also coded to know whether the first obstacle is behind the player and to use the 2nd and 3rd obstacles instead
 * 		- Added Util object to create the connector and thus also create the network
 * 		- Added call to the Util connector in the game loop
 * 		- Removed Main function (check RunningTest change log)
 * 
 */

import javax.swing.JFrame;

public class RunningTest{
	
	DinoGame game = new DinoGame();
	Util util = new Util();
	Network network = new Network();
	
	public void main(String[] args) {
		JFrame frame = new JFrame("Chrome T-Rex Game");
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
			
		game.startGameThread();
	}
}
	
	
	
	
		

		
		

	


