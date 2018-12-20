
package utils;

import java.util.Comparator;

import models.Movie;


public class MovieAverageRatingComparator implements Comparator<Movie>
{

	@Override
	public int compare(Movie m1, Movie m2)
	{
		if (m1.getAverageRating() < m2.getAverageRating()) 
			 return +1;
		 else if (m1.getAverageRating() > m2.getAverageRating()) 
			 return -1;
		 else return  0;
	}
}


