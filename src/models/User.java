
package models;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class User
{
	public String firstName, lastName, occupation, username, password;
	public int age;
	public long userId; 
	public char gender;
	
	
	public Map<Long, Rating> ratedMovies = new HashMap<>();
	
	public User(long userId, String firstName, String lastName, 
							int age, char gender, String occupation, String username, String password)
	{
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.gender = gender;
		this.occupation = occupation;
		this.username = username;
		this.password = password;
	}

	
	public void addRatedMovies(Long movieId, Rating rating)
	{
		ratedMovies.put(movieId, rating);
	}
	
  public String toString()
  {
    return toStringHelper(this).addValue(userId)
    		                   		 .addValue(firstName)
                               .addValue(lastName)
                               .addValue(age)
                               .addValue(gender)
                               .addValue(occupation)
                               .toString();
  }
  
  @Override  
  public int hashCode()  
  {  
     return Objects.hashCode(this.userId, this.firstName, this.lastName, this.age, this.gender,
    		 											this.occupation, this.username, this.password);  
  }  
  
  @Override
  public boolean equals(final Object obj)
  {
    if (obj instanceof User)
    {
      final User other = (User) obj;
      return Objects.equal(userId, other.userId) 
          && Objects.equal(firstName,  other.firstName)
          && Objects.equal(lastName,  other.lastName)
          && Objects.equal(age,  other.age)
          && Objects.equal(gender,  other.gender)
          && Objects.equal(occupation,  other.occupation)
          && Objects.equal(ratedMovies,  other.ratedMovies)
          && Objects.equal(username,     other.username)
          && Objects.equal(password,  other.password);
    }
    else
    {
      return false;
    }
  }
}
