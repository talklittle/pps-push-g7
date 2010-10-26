package push.sim;

public class MoveResult {
	private Move m;
	private boolean success;
	private int player_id;
	public int getPlayerId() {
		return player_id;
	}
	public Move getMove() {
		return m;
	}
	void setSuccess(boolean success) {
		this.success = success;
	}
	public boolean isSuccess() {
		return success;
	}
	public MoveResult(Move m, int id)
	{
		this.m=m;
		this.player_id=id;
	}
}
