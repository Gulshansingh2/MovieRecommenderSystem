
package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.introcs.In;
import models.Movie;
import models.Rating;
import models.User;


public class Parser
{
	Map<Long, User> users = new HashMap<>(); //Stores all the User objects
	Map<Long, Movie> movies = new HashMap<>(); //Stores all the Movie objects 
	List<Rating> ratings = new ArrayList<>(); //Stores all the Rating objects 
	Map<String, Rating> ratingsMap = new HashMap<>(); //Map to ensure unique ratings


	RatingByTimeComparator comparator = new RatingByTimeComparator(); 


	public Parser()
	{

	}

	
	public Map<Long, User> parseUserData(String path) throws Exception
	{
		File usersFile = new File(path);
		In inUsers = new In(usersFile);
		String delims = "[|]";

		while (!inUsers.isEmpty())
		{
			String userDetails = inUsers.readLine();
			String[] userTokens = userDetails.split(delims);

			if (userTokens.length == 7) 
			{
				long userId = Long.parseLong(userTokens[0]);
				String firstName = userTokens[1];
				String lastName = userTokens[2];
				int age = Integer.parseInt(userTokens[3]);
				char gender = userTokens[4].charAt(0);
				String occupation = userTokens[5];
				String username = "";
				String password = "";
				User user = new User(userId, firstName, lastName, age, gender, occupation, username, password);
				users.put(userId, user);

			}
			else
			{
				throw new Exception("Invalid member length: "+ userTokens.length);
			}
		}
		inUsers.close();
		return users;
	}

	
	public Map<Long, Movie> parseMovieData(String path) throws Exception 
	{
		File moviesFile = new File(path);
		In inMovies = new In(moviesFile);
		String delims = "[|]";

		while (!inMovies.isEmpty())
		{
			String movieDetails = inMovies.readLine();
			String[] movieTokens = movieDetails.split(delims);

			if (movieTokens.length == 23) 
			{
				long movieId = Long.parseLong(movieTokens[0]);
				String title = movieTokens[1];
				String theYear = movieTokens[2];
				int year = 0;
				if (theYear != null && theYear.length() > 0)
				{
					year = Integer.parseInt(theYear.substring(7));
				}
				else
				{
					year = 1996;
				}
				String url = movieTokens[3];

				Movie movie = new Movie(movieId, title, year, url);
				movies.put(movieId, movie);
			}
			else
			{
				throw new Exception("Invalid member length: "+ movieTokens.length);
			}
		}
		inMovies.close();
		return movies;
	}

	public List<Rating> parseRatingData(String path) throws Exception
	{
		File ratingsFile = new File(path);
		In inRatings = new In(ratingsFile);
		String delims = "[|]";

		while (!inRatings.isEmpty())
		{
			String ratingDetails = inRatings.readLine();
			String[] ratingTokens = ratingDetails.split(delims);

			if (ratingTokens.length == 4) 
			{
				long userId = Long.parseLong(ratingTokens[0]);
				long movieId = Long.parseLong(ratingTokens[1]);
				Integer rating = Integer.parseInt(ratingTokens[2]);
				long timestamp = Long.parseLong(ratingTokens[3]);
				Rating r = new Rating(userId, movieId, rating, timestamp);

				ratings.add(r);

			}
			else
			{
				throw new Exception("Invalid member length: "+ ratingTokens.length);
			}
		}
		inRatings.close();
		
			
		Collections.sort(ratings, comparator);

		
		for (Rating r: ratings)
		{
			ratingsMap.put(r.userId + "u" + r.movieId + "m", r);
		}

		
		List<Rating> ratingsFiltered = new ArrayList<>(ratingsMap.values());
		Collections.sort(ratingsFiltered, comparator);



		//Adds respective ratings to users and movies
		for (Rating rating: ratingsFiltered)
		{
			User user = getUser(rating.userId);
			Movie movie = getMovie(rating.movieId);
			Rating r = new Rating(user.userId, movie.movieId, rating.rating);

			user.addRatedMovies(movie.movieId, r);
			movie.addUserRatings(user.userId, r);
		}
		return ratingsFiltered;
	}

	public User getUser(long l)
	{
		return users.get(l);
	}

	public Movie getMovie(long i)
	{
		return movies.get(i);
	}
	
	public int getMoviesSize()
	{
		return movies.size();
	}

}
