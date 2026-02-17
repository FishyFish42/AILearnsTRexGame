package neuralNetworkV7;

/* V7 - Changelog:
 * No big changes to the neural network inner workings this time. This update is paving the way for and implementing a child network population
 * This updates changes the way that the network interacts with the dinoGame as well as changes how the back end dinoGame functions to make this work 
 * Of course, this version will not perfect this model, but it gets it to a functional state where it "works" but needs some fine tuning to work well
 * 			(that means next update is the fine tuning update)
 * 
 * My intent with this update is to keep the dinoGame playable by me while simultaneously allowing me to implement a population of networks AND make it run faster
 * 	
 * Goals:
 * 		- Update the method that user inputs are taken and ran (back end updates within dinoGame)
 * 		- Ditching the RoboGoon object from the Util class
 * 			i. this will allow the program to run exponentially faster than before as robots eat computer resources faster than David can bulk
 * 		- Allow dinoGame to handle multiple childNetworks at once (will also allow human controlled multi-player later if wanted i guess)
 * 		- create and implement multiple child networks
 * 		- allow toggle between playing the game in person and running the network (this one isn't necessary for the AI but allows me to mess around if i'm showing off to friends
 *  	
 * Firstly, back end dinoGame input updates.
 * Part 1 edits:
 * 		- Added functions "jump", "crouch", and "uncrouch" in dinoGame
 * 			i. these functions will be what's called to actually make the player jump or crouch
 * 		- Updated keyPressed function (user input) to call new Jump and Crouch functions instead of manually doing the actions
 * 			i. this allows key presses to still work while allowing the network to call functions instead of having to press keystrokes
 * 		- Updated Util connector logic
 * 			i. updated the predict function interpreter to call on jump or crouch functions rather than robot key presses
 * 			ii. removed all other instances of robots and key-presses from Util class 
 * 				a. constructor
 * 				b. prediction
 * 				c. instantiation and declaration
 * 				d. import headers java.awt.AWTException, import java.awt.Robot, and java.awt.event.KeyEvent.
 * 
 * Secondly, enabling multiple child classes to play the game at once
 * I have 2 ideas on how to do this:
 * 		- Create multiple instances of the game and have each child class play their respective game simultaneously
 * 			i. Resource intensive
 * 			ii. Would require that I move the main deepCopying/evaluation logic from Util to RunningTest to ensure some logic remains outside of the training loop
 * 				a. I.E. repurpose RunningTest and Util to serve slightly different roles
 * 		- Allow multiple players to play the same dinoGame at the same time
 * 			i. likely less resource intensive
 * 			ii. requires restructuring of dinoGame variable logic to support multiple players
 * 			iii. allow util to support and connect dinoGame to multiple child classes
 * 			iv. will likely require me to resort to object oriented programming for the dino game to distinguish between players
 * 
 * I am going to do the second route. Mostly because it would be fun to do multiplayer dinoGame with a friend lol
 * also because its nicer on my sub-par computer
 * 
 * Part 2 edits: this one is gonna be tough
 * 		- Created dinoPlayer class to individualize each player
 * 		- Moved a variaty of functions and variables to the dinoPlayer class and added some extra stuff in the process
 * 			i. Variables - dinoY, velocityY, isJumping, isCrouching
 * 				a. added score to dinoPlayer and changed DinoGame.score to realScore
 * 				b. changed references to the score into realScore within the evaluation function in the Network class
 * 				c. created an "isAlive" variable to track the gameOver Status of each player individually
 * 			ii. Functions - gravity (from updateGame), jump, crouch, uncrouch
 * 			iii. added resetValues function in dinoPlayer. Will replace value reseting in the restartGame function.
 * 		- Added "childPopulationSize" variable to RunningTest for users to choose size of population
 * 		- Added a few local variables within DinoGame to help keep track of game and player status'
 * 			i. totalPopulation
 * 				a. locally stores the values of population size from RunningTest in the dinoGame class (in constructor, read below notes)
 * 			ii. livingCount - keeps track of how many players are still alive (for gameOver logic)
 * 			iii. dinoStorage - List<dinoPlayer> to store dinoPlayer objects.
 * 		- Updated dinoGame constructor to create the players equal to the user chosen population
 * 			i. dinoGame constructor now imports childPopulationSize and locally assigns the value to totalPopulation
 * 			ii. uses a for loop to create a totalPopulationSize number of dinoPlayer and stores them within dinoStorage
 * 		- Updated various dinoClass functions to reference dinoStorage players rather than the previously hard coded player values
 * 			i. these all run on a for each loop to run through each player
 * 			ii. Collision detection within gameUpdate()
 * 				a. also updates livingCount as collisions are detected
 * 				b. if player currently being check is alive AND is colliding, set it to dead and livingCount--
 * 			    c. also added check within gameUpdate to check if all players are still alive (checks livingCount <= 0)
 * 			iv. gravity logic within gameUpdate, now references dinoPlayer.gravity on a for each loop
 * 			v. restart game function now calls dinoPlayer.resetValues in a for each loop.
 * 		- Added score increment within the update game function
 * 			i. score/bestScore are now calculated in pixels and not in score
 * 			ii. this helps the system choose the best child model of the ten based off of "time survived" rather than obsticles passed
 * 				a. Maybe a model jumped and hit the top of a obstacle rather than ran straight into the side of it
 * 				b. would live slightly longer and technically a step in the right direction
 * 			iii. only increments score if the player is alive
 * 		
 * Now, I am fixing the Util class to allow it properly connect between the game and the network and to properly integrate a child population.	
 * Part 3:
 * 		-  fixed the new data passing bugs between dinoGame and Util
 * 			i. some data(velocityY and dinoY) are different for each player.
 * 			ii. connector now runs on a for loop while game is running to compute each player
 * 				a. creates list, fills list with normalized values, pass values into predict function, forget the list, repeat											
 * 			iii. normalized data is still stored in a List<Double> within the connector, but the normalization function is now called once for each individual player
 * 				a. Normalization function now imports an int storing the current player number that the game is processing
 * 				b. normalization function now takes that data directly from the dinoStorage that is specific for each player (dinoY, velocityY)
 * 				c. references to game.data now reference the new correct data values
 * 					- data is now 5 values long instead of 7, adjusted the references to reflect this
 * 					- dinoY and velocityY were removed from the list and are now taken straight from dinoStorage
 * 			iv. This now properly connects the two classes
 * 				a. each of the players are controlled by the Util class connector
 * 				b. HOWEVER, they are all controlled by the same childNetwork (read below for the fix)
 * 					- program has only created a single child network thus far
 * 		- added the creation and recreation of the child network population between child populations.
 * 			i. created a list "eipsteinsIsland" to store various child networks as they are created
 * 				a. initialized in constructor			
 * 				b. list is recreated each epoch in gameOver segment of the connector function
 * 			ii. for loops to fill eipsteinsIsland with child population.
 * 				a. runs game.childPopulation number of times (number is decided in RunningTest class by the user)
 * 			iii. removed the old "Network childNetwork;" declaration from the top of the class that was used in the previous version
 * 		- updated the connector function to connect the correct childNetwork to the appropriate dinoPlayer
 * 			i. now references eipsteinsIsland for the child networks when using predict functions
 * 			ii. Added conditional statement before the predictor functions to check if the network's corresponding player is alive or dead
 * 				a. only predicts if the player is alive
 * 		- edited the evaluation call in Util to evaluate each child and choose the best model of all those evaluated
 * 			i. evaluates them one at a time
 * 			ii. only keeps a model if it does better than the original/last saved model evaluated before it
 * 			iii. Edited evaluation function within Network class to pull the best scores from their respective dinoPlayer stored in dinoStorage
 * 
 * Lastly, adding the toggle to allow humans to play instead of hooking it up to ai.
 * 		- added variables to RunningTestClass 
 * 			i. HUMAN_PLAYER - boolean asking user if they are playing the game or not
 * 			ii. PLAYER_POPULATION_SIZE - how many human players are there
 * 				a. This variable will be kept equal to 1 indefinitely until a future update to dino game if I decide to integrate human multiplayer
 * 		- Updated dinoGame constructor and the initiation within RunningTest to include importing these two variables
 * 			i. while it might be beneficial to condense player and child populations into one variable and change the value when I change the HUMAN_PLAYER value, 
 * 				this allows the user (me) the convenience of not having to change those numbers for now
 * 				
 * 
 * Additional Bug fixes with this updates
 * 		- fixed Network.train to no longer import "epoch" from Util as it wasn't needed in the previous or current version
 * 			i. also updated the call to the function within the Util class
 * 		- fixed issue with if statements assigning values rather than comparing values (= vs ==)
 * 		- Moved filePath variable creation to be within the save reader within the network class so each child network does not also 
 * 			create these variables when they are not needed
 * 		- changed the thresholds of the numberOfMutations within the Network class
 * 			i. <1000 score for 6 mutations
 * 			ii. <5000 score for 3 mutations
 * 			iii. >=5000 score for 1 mutation
 * 			iv. this is simply to help the system make its first big jumps on this program. It is unlikely that it will ever surpass 5000 score any time soon
 * 				a. I can barely get past 50 obstacles (around 5000ish pixels i guess) and I have timing, but as a computer it could definitely do better than me once trained
 * 			v. remember that score is now based off of pixels and not obstacles.
 * 		- Removed dist1 and 2 calculation fallback, its already built into the calculations of the variables
 * 		- Fixed Network deepCopy function to also copy the numOfInputs variable value 
 * 			i. took me 4 hours to find this bug between last version and this. This bug COMPLETELY broke the system
 * 			ii. remember kids, if you ain't copying your parents action perfectly, you ain't gonna go far lol
 * 
 */

import javax.swing.JFrame;

public class RunningTest{
	
	static final int CHILD_POPULATION_SIZE = 10;
	static final boolean HUMAN_PLAYER = false;
	static final int PLAYER_POPULATION_SIZE = 1; //keep this as 1 for now (multiplayer MIIIGHT be added at a later date)
	
	static DinoGameTraining game = new DinoGameTraining(CHILD_POPULATION_SIZE, HUMAN_PLAYER, PLAYER_POPULATION_SIZE);
	//static DinoGameTraining game = new DinoGameTraining();
	
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
	
	
	
	
		

		
		

	


