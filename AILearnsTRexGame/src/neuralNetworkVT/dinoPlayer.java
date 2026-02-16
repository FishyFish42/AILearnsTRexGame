package neuralNetworkVT;

public class dinoPlayer {
	public boolean isAlive = true;
    public int score = 0;
    
	public double dinoY;
    public double velocityY;
    
    public double isJumping;
    public double isCrouching;
    
    
	
    dinoPlayer(){
    	dinoY = DinoGameTraining.GROUND_Y - DinoGameTraining.DINO_HEIGHT;
    	velocityY = 0;
    
    	isJumping = 0;
    	isCrouching = 0;
    }
    
    public void gravity(){
    	if (isJumping == 1) {
    		if(isCrouching == 1) {
    			velocityY += 3;
    		}else {velocityY += 0.85;}
    		dinoY += velocityY;
        
    		if (dinoY >= DinoGameTraining.GROUND_Y - DinoGameTraining.DINO_HEIGHT) {
    			dinoY = DinoGameTraining.GROUND_Y - DinoGameTraining.DINO_HEIGHT;
    			isJumping = 0;
    			velocityY = 0;}
    	}
    }
    
    public void jump() {
    	if(isJumping == 0) {
    		isJumping = 1;
			velocityY = -15; // jump strength
    	}
    }
    
    public void crouch() {
    	isCrouching = 1;
    }
    
    public void uncrouch() {
    	isCrouching = 0;
    }
    
    public void resetValues() {
    	isAlive = true;
    	score = 0;
        dinoY = DinoGameTraining.GROUND_Y - DinoGameTraining.DINO_HEIGHT;
        velocityY = 0;
        isJumping = 0;
        isCrouching = 0;
    }
    
}
