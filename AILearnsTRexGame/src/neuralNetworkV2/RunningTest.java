package neuralNetworkV2;

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
	
	
	
	
		

		
		

	


