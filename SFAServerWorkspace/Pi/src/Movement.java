
public enum MOVEMENT {
	    FORWARD([1,0,1,0]), 
	    BACKWARD([0,1,0,1]),
	    RIGHT([1,0,0,1]), 
	    LEFT([0,1,1,0]), 
	    STOP([0,0,0,0])

	    public final int[] signal;
	    private void MOVEMENT(signal) {
	        this.signal = signal;
	    }

	}