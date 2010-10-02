package domain;


/**
 * Esta classe representa um utilizador.
 * Retirado do tutorial colocado no WoC.
 */
public class User
{
	private String id;
	private String name;
	private String screenName;
	private String description;
	private String url;
	private boolean protectedProfile;
	private int followers;
	private int following;
	private String createdAt;
	private int tweets;
	private Tweet lastTweet;

	public User() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isProtectedProfile() {
		return protectedProfile;
	}

	public void setProtectedProfile(boolean protectedProfile) {
		this.protectedProfile = protectedProfile;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getTweets() {
		return tweets;
	}

	public void setTweets(int tweets) {
		this.tweets = tweets;
	}

	public Tweet getLastTweet() {
		return lastTweet;
	}

	public void setLastTweet(Tweet lastTweet) {
		this.lastTweet = lastTweet;
	}

	public String getProfile()
	{
		String profile =
			"  Full Name \t"    + name        + "\n" +
			"  Screen Name \t " + screenName  + "\n" +
			"  Description \t"  + description + "\n" +
			"  URL \t\t"        + url         + "\n" +
			"  Protected \t"    + protectedProfile + "\n" +
			"  Nº Followers \t" + followers   + "\n" +
			"  Nº Following \t" + following   + "\n" +
			"  Created at \t"   + createdAt   + "\n" +
			"  Tweets \t"       + tweets      + "\n";
		return profile;
	}

	@Override
	public String toString() {
		return screenName +" ("+ name +")";
	}
}
