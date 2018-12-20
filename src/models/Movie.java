
package models;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;


public class Movie
{
	public String title, url;
	public int year;
	public long movieId;
	
	public Map<Long, Rating> userRatings = new HashMap<>();

	public Movie(long movieId, String title, int year, String url)
	{
		this.movieId = movieId;
		this.title = title;
		this.year = year;
		this.url = url;
	}

	
	public void addUserRatings(Long userId, Rating rating)
	{
		userRatings.put(userId, rating);
	}

	
	public double getAverageRating()
	{
		double totalRating = 0.0; 
		double averageRating = 0.0;

		for (Rating rating: userRatings.values())
		{
			totalRating += rating.rating;
		}
		if (userRatings.size() > 0)
		{
			return averageRating = totalRating/userRatings.size();
		}
		return averageRating;
	}
	
	public String toString()
	{
		return toStringHelper(this).addValue("\n\nMovie Id: " + movieId)
				.addValue("\nTitle: " + title)
				.addValue("\nRelease year: " + year)
				.addValue("\nAverage rating: " + getAverageRating())
				.addValue("\nIMDb URL: " + url + "\n\n")
				.toString();
	}

	@Override  
	public int hashCode()  
	{  
		return Objects.hashCode(this.movieId, this.title, this.year, this.url);  
	}  

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof Movie)
		{
			final Movie other = (Movie) obj;
			return Objects.equal(movieId, other.movieId) 
					&& Objects.equal(title,  other.title)
					&& Objects.equal(year,  other.year)
					&& Objects.equal(userRatings,  other.userRatings)
					&& Objects.equal(url,  other.url);
		}
		else
		{
			return false;
		}
	}
}
