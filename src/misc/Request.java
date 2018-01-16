package misc;

/**
 * Should identify a specific search request (including the user who made it)
 * and date it was made on.
 */
public class Request
{
	private final User user;

	private final String request;

	public Request(User user, String request)
	{
		this.user = user;
		this.request = request;
	}

	public User getUser() {
		return user;
	}

	public String getRequest() {
		return request;
	}
}
