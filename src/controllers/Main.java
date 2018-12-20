package controllers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import com.google.common.base.Optional;

import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.StdOut;
import mahoutRec.MenuDriven;
import models.Movie;
import models.Rating;
import models.User;
import utils.Serializer;
import utils.XMLSerializer;


public class Main
{
	RecommenderAPI recommenderAPI;
	User loggedInUser;			 //Stores the current logged in user
	boolean authenticated = false;		//Checks if a user has been authenticated 
	Scanner input = new Scanner(System.in);


	public Main() throws Exception
	{
		File datastore = new File("datastore.xml"); //The main datastore

		if (datastore.isFile()) 
		{
			StdOut.println("File present");
			Serializer serializer = new XMLSerializer(datastore);
			recommenderAPI = new RecommenderAPI(serializer); 
			recommenderAPI.load();
		}
		else
		{
			StdOut.println("File not present");
			Serializer newSerializer = new XMLSerializer(new File("datastore.xml")); 
			recommenderAPI = new RecommenderAPI(newSerializer); 
			recommenderAPI.loadRawData(); //Load and store raw data into datastore.xml
			recommenderAPI.load();
		}
	}

	public static void main(String[] args) throws Exception
	{
		Main main = new Main();
		main.loginRun();
	}

	
	public int loginMenu()
	{
		boolean errorFree = false; //Ensures options entered are correct
		int option = 0;

		while (!errorFree) 
		{
			option = 0;

			try
			{
				StdOut.println("\n======Login Options======\n");

				StdOut.println("Please select the numerical options below\n");
				StdOut.println("1) Login (for users with an account)");
				StdOut.println("2) Signup");
				StdOut.println("\n0) Exit system");


				option = StdIn.readInt();

				errorFree = true;
			}
			catch (Exception e)
			{
				StdIn.readLine();
				StdOut.println("Your selection is incorrect or not available, please try again");	
			}
		}
		return option;
	}

	public int mainMenu()
	{
		boolean errorFree = false;
		int option = 0;
		while (!errorFree) 
		{
			option = 0;

			try
			{
				StdOut.println("Welcome " + loggedInUser.firstName + "!");
				StdOut.println("You have currently rated " + loggedInUser.ratedMovies.size() + " movies\n");
				StdOut.println("1) Add a new movie");
				StdOut.println("2) Rate a movie");
				StdOut.println("3) Rate random movies");
				StdOut.println("4) Search movies");
				StdOut.println("5) Top 10 movies of all time");
				StdOut.println("6) Get personalised movie suggestions");
				StdOut.println("7) Get mahout based movie suggestions");
				StdOut.println("\n99) Delete account");

				StdOut.println("\n0) Log out");


				option = StdIn.readInt();
				errorFree = true;
			}
			catch (Exception e)
			{
				StdIn.readLine();
				StdOut.println("Input a valid option, please try again");	
			}
		}
		return option;

	}

	public void loginRun() throws Exception
	{
		StdOut.println("\nWelcome to MRSYS!"
				+ "\nPress enter to continue");
		StdIn.readLine();

		int loginOption = loginMenu();

		while (loginOption != 0)
		{
			switch(loginOption)
			{
			case 1:
				authenticate(); //login
				break;

			case 2:
				addUser(); //signup
				break;

			default:
				StdOut.println("Input a valid option, please try again");
			}
			if (!authenticated) 
			{
				loginOption = loginMenu();
			}
			else 
			{
				loginOption = 0;
				menuRun();
				break;
			}
		}
		StdOut.println("Thanks for using our app");
	}

	public void menuRun() throws Exception
	{
		int mainMenuOption = mainMenu();

		while (mainMenuOption != 0)
		{
			switch(mainMenuOption)
			{
			case 1:
				addMovie(); //adding a new movie
				break;

			case 2:
				addARating(); //adding a new rating
				break;

			case 3:
				addRandomRatings(); //rate random movies
				break;

			case 4:
				searchMovies(); //search movies
				break;

			case 5:
				getTop10(); //get all time top 10
				break;

			case 6:
				personalRec(); //get personalised movie recommendations
				break;
			
			case 7:
				new MenuDriven().mahoutMain(); //get mahout based movie recommendations
				break;

			case 99:
				removeUser(); //delete account
				break;
			default:
				StdOut.println("Input a valid option, please try again");
			}
			if (loggedInUser != null) //Display menu again if account still exists or not logged out
			{
				mainMenuOption = mainMenu();
			}
			else
			{
				mainMenuOption = 0;
				break;
			}
		}
	}

	public void searchMovies() throws IOException, ParseException
	{
		StdOut.println("Enter 1 for Lucene Based Search and 2 for Java Stream search: ");
		String choice = StdIn.readString();
		if(choice.equals("1")) {
			StdOut.println("Enter your search: ");
			String prefix = StdIn.readString();
			recommenderAPI.searchLucene(prefix);
			
		}else if(choice.equals("2")) {
			StdOut.println("Enter your search: ");
			String prefix = StdIn.readString();
			System.out.println(recommenderAPI.searchStream(prefix));
			
		}else {
			StdOut.println("Enter either 1 or 2");
			searchMovies();
		}

	}

	public void personalRec()
	{
		if (loggedInUser.ratedMovies.size() > 0)
		{
			StdOut.println(recommenderAPI.getUserRecommendations(loggedInUser.userId));
		}
		else
		{
			StdOut.println("No recommendations for you at the moment. We suggest you to rate more movies");
		}

	}

	public void getTop10()
	{
		System.out.println(recommenderAPI.getTopTenMovies());
	}

	public void addUser() throws Exception
	{

		StdOut.println("Please key in your details as prompted\n");

		StdOut.println("Your first name? ");
		String firstName = StdIn.readString();

		StdOut.println("Enter your last name? ");
		String lastName = StdIn.readString();

		
		boolean logicalAge = false;
		int age = 0;
		while (!logicalAge) 
		{
			try 
			{
				StdOut.println("Your age? (It's confidential ;) )");
				age = StdIn.readInt();
				if (age >=0 && age <=99)
				{
					logicalAge = true;
				}
				else
				{
					StdOut.println("You cannot possibly be negative aged!");
				}
			} 
			catch (Exception e) 
			{
				StdIn.readString();
				StdOut.println("Positive numerical inputs only!");
			}
		}

		StdOut.println("Your gender? (M/F/O)");
		char gender = StdIn.readChar();
		StdIn.readString();



		StdOut.println("Your occupation? ");
		String occupation = StdIn.readString();

		//Loops to check that the username is unique
		boolean unique = false;
		String username = null;
		while (!unique) 
		{
			StdOut.println("Enter a username: ");
			username = StdIn.readString();
			if (!recommenderAPI.usersLogin.containsKey(username))
			{
				unique = true;
			}
			else
			{
				StdOut.println("Username chosen is not unique");
			}
		}

		StdOut.println("Enter a password: ");
		String password = StdIn.readString();

		User addedUser = recommenderAPI.addUser(firstName, lastName, age, gender, occupation, username, password);
		recommenderAPI.store();
		StdOut.println("Your details have been logged!");
		addLoginRating(addedUser); 
	}

	public void authenticate()
	{
		String username;
		String password;
		StdOut.println("Authentication process");
		StdOut.println("Enter your username: ");
		username = StdIn.readString();
		StdOut.println("Enter your password: ");
		password = StdIn.readString();

		if (recommenderAPI.authenticate(username, password))
		{
			StdOut.println("Logged in successfully!");
			loggedInUser = recommenderAPI.getUserByUsername(username);
			authenticated = true;
		}
		else
		{
			StdOut.println("Log in failed");
		}
	}


	public void addARating()
	{
		long mvId = 0L;
		int rating = 0;

		boolean number = false;
		while (!number) 
		{
			try 
			{
				StdOut.println("Enter movie Id of movie you want to add rating: ");
				mvId = StdIn.readInt();
				//				StdIn.readString();

				if(recommenderAPI.movies.containsKey(mvId))
				{
					StdOut.println(recommenderAPI.getMovieById(mvId));
					number = true;
				}
				else 
				{
					StdOut.println("Movie id does not exist");
				}
			}
			catch (Exception e) 
			{
				StdIn.readString();
				StdOut.println("Numerical values only");					
			}
		}


		boolean number2 = false;
		while (!number2) 
		{
			try 
			{
				StdOut.println("Rate this movie:  (-5 to 5 in increments of 1)");
				rating = StdIn.readInt();
				if(rating >= -5 && rating <= 5)
				{
					number2 = true;
				}
				else 
				{
					StdOut.println("Numerical values only");
				}
			}
			catch (Exception e) 
			{
				StdIn.readString();
				StdOut.println("Numerical values only");					
			}
		}
		StdOut.println("Successfully rated!");
		recommenderAPI.addRating(loggedInUser.userId, mvId, rating);
	}

	
	public void addLoginRating(User addedUser) throws Exception
	{
		int max = recommenderAPI.movies.size();
		int rating = 0;
		StdOut.println("\nYou are required to rate at least 10 movies before using this service. Enter 100 to exit when prompted for rating\n");

		//Loop for 10 ratings and as long as user does not want to exit
		while (addedUser.ratedMovies.size() < 10 && rating != 100)
		{
			Random random = new Random();
			long randomId = random.nextInt(max - 0);

			if (!addedUser.ratedMovies.containsKey(randomId))  
			{
				boolean number = false;
				while (!number) 
				{
					try 
					{
						StdOut.println(recommenderAPI.getMovieById(randomId));
						StdOut.println("Rate this movie:  (-5 to 5) Enter 100 to exit");
						rating = StdIn.readInt();
						if(rating >= -5 && rating <= 5)
						{
							number = true;
						}
						else if (rating == 100)
						{
							number = true;
							break;
						}
					}
					catch (Exception e) 
					{
						StdIn.readString();
						StdOut.println("Numerical values only");					
					}
				}		
				if (rating != 100)
				{
					recommenderAPI.addRating(addedUser.userId, randomId, rating);
				}
			}

		}
		recommenderAPI.store();
	}

	public void addRandomRatings() throws Exception
	{
		int max = recommenderAPI.movies.size(); //Maximum generated ratings
		int rating = 0;
		StdOut.println("\nMovie Rating Process. You will be shown random movies you haven't rated before. Enter 100 to exit when prompted for rating\n");

		//Loop to ensure user still has unrated movies
		while (loggedInUser.ratedMovies.size() < max && rating != 100)
		{
			Random random = new Random();
			long randomId = random.nextInt(max - 0);

			if (!loggedInUser.ratedMovies.containsKey(randomId))
			{
				boolean number = false;
				while (!number) 
				{
					try 
					{
						StdOut.println(recommenderAPI.getMovieById(randomId));
						StdOut.println("Rate this movie:  (-5 to 5) Enter 100 to exit");
						rating = StdIn.readInt();
						if(rating >= -5 && rating <= 5)
						{
							number = true;
						}
						else if (rating == 100) //Exit system
						{
							number = true;
							break;
						}
					}
					catch (Exception e) 
					{
						StdIn.readString();
						StdOut.println("Numerical values only");					
					}
				}		
				if (rating != 100)
				{
					recommenderAPI.addRating(loggedInUser.userId, randomId, rating);
				}
			}

		}
		recommenderAPI.store();
	}

	public void removeUser() throws Exception
	{
		StdOut.println("Are you sure you want to delete your account? (y/n)");
		String toDelete = StdIn.readString();

		if (toDelete.equalsIgnoreCase("y"))
		{
			Optional<User> user = Optional.fromNullable(recommenderAPI.getUserById(loggedInUser.userId));
			if (user.isPresent()) 
			{
				Set<Long> ratedMoviesId = loggedInUser.ratedMovies.keySet();
				for (Long movieId: ratedMoviesId) //Delete all the user's rated movies
				{
					Movie movie = recommenderAPI.getMovieById(movieId);
					movie.userRatings.remove(loggedInUser.userId);
				}
				recommenderAPI.removeUser(loggedInUser.userId); //Remove the user entirely

				loggedInUser = null; //Logs the user out of the system
				StdOut.println("Account deleted");
			}
		}
		recommenderAPI.store();
	}

	public void addMovie() throws Exception
	{
		StdOut.println("Adding a movie");

		boolean unique = false;
		String title = "";
		String theTitle = "";
		int year = 0;
		while (!unique) 
		{
			StdOut.println("Enter movie title: ");
			title = input.nextLine();

			boolean number = false;
			while (!number) 
			{
				try 
				{
					StdOut.println("Enter the year the movie was released:");
					year = StdIn.readInt();
					if (year >= 1500 && year <= Year.now().getValue())
					{
						number = true;
					}
					else
					{
						StdOut.println("Please enter Movies from the 1500s onwards until the present year only.");
					}
				} 
				catch (Exception e) 
				{
					StdIn.readString();
					StdOut.println("Numerical inputs only");
				}
			}

			//Checks if the movie and year are present in the system
			if (recommenderAPI.uniqueMovieCheck(title, year) == true)
			{
				unique = true;
			}
			else
			{
				StdOut.println("The movie " + title + " is already in our database");
			}
		}
		StdIn.readLine();
		StdOut.println("Enter IMDb url of the movie if available");
		String url = StdIn.readString();
		if (url.isEmpty())
		{
			url = "No URL";
		}

		int rating = 0;
		boolean number = false;
		while (!number) 
		{
			try 
			{
				StdOut.println("Enter rating for this movie: (-5 to 5)");
				rating = StdIn.readInt();
				if (rating >=-5 && rating <= 5)
				{
					number = true;
				}
				else
				{
					StdOut.println("Numerical values between -5 to 5 only");
				}
			} 
			catch (Exception e) 
			{
				StdIn.readString();
				StdOut.println("Numerical values between -5 to 5 only");
			}
		}
		Movie movie = recommenderAPI.addMovie(theTitle, year, url);
		recommenderAPI.addRating(loggedInUser.userId, movie.movieId, rating);

		StdOut.println("Movie added successfully!");
		recommenderAPI.store();
	}

}
