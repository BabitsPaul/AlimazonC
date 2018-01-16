package misc;

import java.io.Serializable;

/**
 * Represents a user with all relevant data.
 */
public class User
	implements Serializable
{
	private final long uid;

	private String name;

	public User(String name, long uid)
	{
		this.name = name;
		this.uid = uid;
	}

	public long getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}
}
