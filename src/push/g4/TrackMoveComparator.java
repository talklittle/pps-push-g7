package push.g4;

import java.util.Comparator;

public class TrackMoveComparator implements Comparator<TrackMove>
{
	public int compare(TrackMove arg0, TrackMove arg1)
	{
		if (arg0.gain>arg1.gain)
			return -1;
		else if (arg0.gain<arg1.gain)
			return 1;
		else
			return 0;
	}
}
