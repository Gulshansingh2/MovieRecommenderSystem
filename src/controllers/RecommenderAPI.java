package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;

import models.Movie;
import models.Rating;
import models.User;
import utils.MovieAverageRatingComparator;
import utils.Parser;
import utils.Serializer;

public class RecommenderAPI
{
	private Serializer serializer;

	Map<Long, User> users = new HashMap<>();
	Map<String, User> usersLogin = new HashMap<>();
	Map<Long, Movie> movies = new HashMap<>();
	List<Rating> ratings = new ArrayList<>();
	Parser parser = new Parser();
	MovieAverageRatingComparator movieAvgComparator;

	public RecommenderAPI()
	{}



	public RecommenderAPI(Serializer serializer) throws Exception
	{
		this.serializer = serializer;
	}


	public void loadRawData() throws Exception
	{
		String userDataPath = ("././data/data_movieLens/users.dat");
		String movieDataPath = ("././data/data_movieLens/items.dat");
		String ratingDataPath = ("././data/data_movieLens/ratings.dat");

		users = parser.parseUserData(userDataPath);
		movies = parser.parseMovieData(movieDataPath);
		ratings = parser.parseRatingData(ratingDataPath);
		store();
	}

	@SuppressWarnings("unchecked")
	public void load() throws Exception
	{
		serializer.read();
		ratings = (List<Rating>) serializer.pop();
		movies = (Map<Long, Movie>) serializer.pop();
		usersLogin = (Map<String, User>) serializer.pop();
		users = (Map<Long, User>) serializer.pop();
	}

	public void store() throws Exception
	{
		serializer.push(users);
		serializer.push(usersLogin);
		serializer.push(movies);
		serializer.push(ratings);
		serializer.write(); 
	}

	public User addUser(String firstName, String lastName, int age, char gender, String occupation, String username, String password)
	{
		Preconditions.checkNotNull(firstName);
		Preconditions.checkNotNull(lastName);
		Preconditions.checkNotNull(age);
		Preconditions.checkNotNull(occupation);
		Preconditions.checkNotNull(username);
		Preconditions.checkNotNull(password);
		
		//Cheap fix to the counter problem
		long highest = 0L;
		for (Long key: users.keySet())
		{
			if (key > highest)
			{
				highest = key;
			}
		}
		
		long userId = highest + 1;
		User user = new User(userId, firstName, lastName, age, gender, occupation, username, password);
		users.put(user.userId, user);
		usersLogin.put(user.username, user);
		return user;
	}

	public void removeUser(long userId)
	{
		Preconditions.checkNotNull(userId);
		User user = getUserById(userId);
		users.remove(user.userId);
		usersLogin.remove(user.username);
	}

	public Movie addMovie(String title, int year, String url)
	{
		Preconditions.checkNotNull(title);
		Preconditions.checkNotNull(year);
		Preconditions.checkNotNull(url);

		long movieId = movies.size() + 1;
		Movie movie = new Movie(movieId, title, year, url);
		movies.put(movieId, movie);
		return movie;
	}

	public Rating addRating(long userId, long movieId, int rating)
	{
		Preconditions.checkNotNull(userId);
		Preconditions.checkNotNull(movieId);
		Preconditions.checkNotNull(rating);

		Rating r = new Rating(userId, movieId, rating);
		User user = getUserById(r.userId);
		Movie movie = getMovieById(r.movieId);

		user.addRatedMovies(movie.movieId, r);
		movie.addUserRatings(user.userId, r);
		ratings.add(r);

		return r;
	}

	public List<Movie> getTopTenMovies()
	{
		List<Movie> topTen = new ArrayList<>(movies.values());
		Preconditions.checkArgument(topTen.size() > 0);
		Preconditions.checkNotNull(topTen);
		movieAvgComparator = new MovieAverageRatingComparator();
		Collections.sort(topTen, movieAvgComparator);
		if (topTen.size() > 10)
		{
			return topTen.subList(0, 10);
		}
		else
		{
			return topTen;
		}
	}
	public List<Movie> getUserRecommendations(long userId)
	{
		Preconditions.checkNotNull(userId);

		User currentUser = getUserById(userId);
		if (currentUser.ratedMovies.size() > 0) 
		{
			Set<Long> targetUsers = new HashSet<>();
			
			List<Rating> currentUserRatings = new ArrayList<>(currentUser.ratedMovies.values());

			for (Rating currentUserRating: currentUserRatings)
			{
				Movie movie = getMovieById(currentUserRating.movieId);
				if (movie.userRatings.size() > 1)
				{
					targetUsers.addAll(movie.userRatings.keySet());
				}
			}
			
			Long mostSimilar = 0L;
			int totalSimilarity = 0;
			targetUsers.remove(currentUser.userId);
//			System.out.println(targetUsers);
			if (targetUsers != null && targetUsers.size() > 0) 
			{
				for (Long targetId: targetUsers)
				{
					User targetUser = getUserById(targetId);
					int currentSimilarity = 0;
					int targetRating = 0;
					int currentRating = 0;
					
					for (Rating targetUserRating: targetUser.ratedMovies.values())
					{
						Movie movie = getMovieById(targetUserRating.movieId);			
						
						if (currentUser.ratedMovies.containsKey(targetUserRating.movieId))
						{
							targetRating = targetUserRating.rating;
							currentRating = currentUser.ratedMovies.get(movie.movieId).rating;
							currentSimilarity += (currentRating * targetRating);
						}
					}
					
					if (currentSimilarity > totalSimilarity)
					{
						totalSimilarity = currentSimilarity;
						mostSimilar = targetId;
					}						
				}
			}
			
			User mostSimilarUser = getUserById(mostSimilar);
			System.out.println(mostSimilarUser);
			List<Movie> recommendedMoviesList = new ArrayList<>();
			if (mostSimilarUser != null) 
			{
				for (Rating rating: mostSimilarUser.ratedMovies.values())
				{
					if (rating.rating >=3 && !currentUser.ratedMovies.containsKey(rating.movieId))
					{
						recommendedMoviesList.add(getMovieById(rating.movieId));
					}
				}
			}
			movieAvgComparator = new MovieAverageRatingComparator();
			Collections.sort(recommendedMoviesList, movieAvgComparator);
			return recommendedMoviesList;
		}
		else
		{
			return null;
		}
	}

	
	
	public boolean authenticate(String username, String password)
	{
		Preconditions.checkNotNull(username);
		Preconditions.checkNotNull(password);

		if (usersLogin.containsKey(username))
		{
			User user = usersLogin.get(username);
			if (user.password.matches(password))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean uniqueMovieCheck(String title, int year)
	{		
		Preconditions.checkNotNull(title);
		Preconditions.checkNotNull(year);

		for (Movie movie: getMovies().values())
		{			
			String movieTitle = movie.title;
			movieTitle = movieTitle.replaceAll("\\s*\\([^\\)]*\\)\\s*", "");
//			System.out.println(movieTitle);
			if (title.toLowerCase().equals(movieTitle.toLowerCase()) && (movie.year == year))
			{
				return false;
			}
		}
		return true;
	}

	public List<Movie> searchStream(String prefix)
	{
		Preconditions.checkNotNull(prefix);
		List<Movie> searchMovies = new ArrayList<>(movies.values());
		return filter(searchMovies, prefix);
	}
	
	public void searchLucene(String prefix) throws IOException, ParseException
	{
		Preconditions.checkNotNull(prefix);
		List<Movie> searchMovies = new ArrayList<>(movies.values());
		LuceneSearch h = new LuceneSearch();
		h.run(searchMovies, prefix);
	}

	private static List<Movie> filter(final Collection<Movie> source, final String prefix) 
	{
		Preconditions.checkNotNull(source);
		Preconditions.checkNotNull(prefix);

		return source.stream().filter(item -> item.title.toLowerCase().startsWith(prefix.toLowerCase())).collect(Collectors.toList());
	}
	
	public List<Rating> getRatings()
	{
		return ratings;
	}

	public Map<Long, Movie> getMovies()
	{
		return movies;
	}

	public Movie getMovieById(long id)
	{
		return movies.get(id);
	}

	public Map<Long, User> getUsers()
	{
		return users;
	}

	public User getUserById(long id)
	{
		return users.get(id);
	}

	public User getUserByUsername(String username)
	{
		return usersLogin.get(username);
	}

	public Map<Long, Rating> getUserRatings(long userId)
	{
		User user = getUserById(userId);
		return user.ratedMovies;
	}
}
