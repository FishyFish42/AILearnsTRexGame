package neuralNetworkV3;

/* V3 Change log:
 * This version is the first real big bug fix of the model. 
 * Because V1 was a prediction model, it was used to having a confirmed outcome to compare the predictions to
 * This Model wants to play the game real time AND does not have an "outcome" to compare the outcomes too
 * 
 * In RunningTest:
 * 		- removed network object. The util class already does this and we only need 1 single parent network object.
 * 
 * In Network:
 * 		- Condensed down the predict functions so it was not recalculating layers more than once.
 * 		- Created a deepCopy function
 * 			- Fixes the issue of the child copy of the parent network being shallow OR the child copy changing the values of the parent class directly
 * 			- 
 * 		- Removed errCalc function and replaced it with an evaluate function
 * 			a. there is no need for a prediction confidence model here as we dont have an "expected vs real outcome" 
 * 				i. (note from Future editor, prediction confidence may become important again later, but not until THIS hill-climber model works as intended.)
 * 			b. replaced with basic evaluate function 
 * 				i. simply compares score and chooses which model is better.
 * 				ii. if better, replace old with new (remember)
 * 					- creates new neurons list for the parent class and copies the values of the child class into a new neuron (deep copy) which is added to the new list
 * 				iii. if worse, keep old (forget)
 * 			c. 
 * 				
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
	
	
	
	
		

		
		

	


