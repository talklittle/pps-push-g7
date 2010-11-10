package push.g4;

import push.sim.Move;

public class TrackMove
{
	public Move move;
	public double gain;
	
	public TrackMove(Move move, double gain)
	{
		this.move=move;
		this.gain=gain;
	}
}
