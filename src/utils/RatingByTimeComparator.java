
package utils;

import java.util.Comparator;

import models.Rating;


public class RatingByTimeComparator implements Comparator<Rating>
{
	@Override
	public int compare(Rating r1, Rating r2)
	{
		if (r1.timestamp > r2.timestamp) 
			return +1;
		else if (r1.timestamp < r2.timestamp) 
			return -1;
		else return  0;
	}

}
