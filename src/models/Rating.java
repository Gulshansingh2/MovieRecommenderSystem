
package models;

import static com.google.common.base.MoreObjects.toStringHelper;

import com.google.common.base.Objects;

public class Rating
{
	public long userId, movieId, timestamp;
	public Integer rating;
	public double averageRating; //Calculated based on the average user ratings of a movie Calculated in the movie class

	public Rating(long userId, long movieId, Integer rating, long timestamp)
	{
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
		this.timestamp = timestamp;
	}

	public Rating(long userId, long movieId, int rating)
	{
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
	}
	
	public Rating(long movieId, double averageRating)
	{
		this.movieId = movieId;
		this.averageRating = averageRating;
	}
	public String toString()
	{
		return toStringHelper(this).addValue(userId)
				.addValue(movieId)
				.addValue(rating)
				.toString();
	}

	@Override  
	public int hashCode()  
	{  
		return Objects.hashCode(this.userId, this.movieId, this.rating, this.timestamp);  
	}  

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof Rating)
		{
			final Rating other = (Rating) obj;
			return Objects.equal(userId, other.userId) 
					&& Objects.equal(movieId,  other.movieId)
					&& Objects.equal(rating,  other.rating)
					&& Objects.equal(timestamp,  other.timestamp);
		}
		else
		{
			return false;
		}
	}

}
