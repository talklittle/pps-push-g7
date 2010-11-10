package push.g4;

import java.util.Comparator;

public class TrackPlayerComparator implements Comparator<TrackPlayer>
{

	@Override
	public int compare(TrackPlayer arg0, TrackPlayer arg1)
	{
		if (arg0.positionScore>0 && arg1.positionScore==0)
			return -1;
		else if (arg0.positionScore==0 && arg1.positionScore>0)
			return 1;
		
		if (arg0.diff>arg1.diff)
			return -1;
		else if (arg0.diff<arg1.diff)
			return 1;
		else
		{
			if (arg0.positionScore>arg1.positionScore)
				return -1;
			else if (arg0.positionScore<arg1.positionScore)
				return 1;
			else
				return 0;
		}
			
	}
}
