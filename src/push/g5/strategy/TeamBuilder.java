package push.g5.strategy;

import push.g5.Cooperator;
import push.g5.g5player;

public class TeamBuilder {
	
	private g5player player;
	private final int WAIT_CYCLES = 6;
	
	public TeamBuilder( g5player player )
	{
		this.player = player;
	}
	
	public int getNextBestCooperator(g5player player)
	{
		if( player.secondCooperationEstablished )
		{
			if( player.firstNonCooperator.ratio > 0 )
				return returnIfValid( player.firstNonCooperator );
			return player.id;
		}
		else if( player.firstCooperationEstablished )
		{
			if( player.firstNonCooperator.ratio == player.secondNonCooperator.ratio && player.firstNonCooperator.ratio > 0 )
			{
				Cooperator nextCoop = ( player.firstNonCooperator.lastRoundTried < player.secondNonCooperator.lastRoundTried ) ? player.firstNonCooperator : player.secondNonCooperator;
				return returnIfValid( nextCoop );
			}
			else
			{
				Cooperator nextCoop = ( player.firstNonCooperator.ratio > player.secondNonCooperator.ratio ) ? player.firstNonCooperator : player.secondNonCooperator;
				if( nextCoop.ratio > 0 )
				{
					return returnIfValid( nextCoop );
				}
				return player.id;
			}
		}
		else{ 
			Cooperator nextCoop = null;

			if( player.firstNonCooperator.ratio == player.highestRatio )
				nextCoop = player.firstNonCooperator;
			
			if( player.secondNonCooperator.ratio == player.highestRatio )
			{
				if( nextCoop != null )
				{
					if( player.secondNonCooperator.lastRoundTried < nextCoop.lastRoundTried )
						nextCoop = player.secondNonCooperator;
				}
				else
					nextCoop = player.secondNonCooperator;
			}
			
			if( player.thirdNonCooperator.ratio == player.highestRatio )
			{
				if( nextCoop != null )
				{
					if( player.thirdNonCooperator.lastRoundTried < nextCoop.lastRoundTried )
						nextCoop = player.thirdNonCooperator;
				}
				else
					nextCoop = player.thirdNonCooperator;
			}
			
			if( nextCoop.ratio > 0 )
				return returnIfValid( nextCoop );
			
			return player.id;
		}
	}
	
	private int returnIfValid( Cooperator coop )
	{
		int lastTry = coop.lastRoundTried;
		if( lastTry == -2 || ( player.round - lastTry ) > ( WAIT_CYCLES * player.NUMRECENTMOVES ) )
		{
			coop.lastRoundTried = player.round;
			return coop.index;
		}
		else
			return player.id;
	}
}
