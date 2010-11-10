package push.g5;

public class Cooperator {
	public int index;
	public double ratio;
	public boolean goodIndirectCoop;
	public int lastRoundTried;
	
	public Cooperator()
	{}
	
	public Cooperator(int index, double ratio, boolean goodIndirectCoop)
	{
		this.index = index;
		this.ratio = ratio;
		this.goodIndirectCoop = goodIndirectCoop;
		lastRoundTried = -2;
	}
}
