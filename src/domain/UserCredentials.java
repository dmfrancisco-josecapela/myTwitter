package domain;

import java.io.Serializable;

/**
 * Representa informação confidencial de um utilizador do Twitter, e é utilizada no
 * processo de autenticação.
 */
public class UserCredentials implements Serializable
{
	private static final long serialVersionUID = 1L;

	protected String name;
	protected String password;
	protected String accessToken;
	protected String tokenSecret;

	/**
	 * Representa informação confidencial de um utilizador do Twitter
	 * @param username (identificador único, sem espaços)
	 * @param password (palavra chave do utilizador no caso da autenticação BASIC
	 *                  ou PinCode no caso da autenticação OAUTH)
	 */
	public UserCredentials(String name, String password)
	{
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/** Compara password de um utilizador com a de outro */
	public boolean passIsValid(UserCredentials user)
	{
		return this.password.equals(user.password);
	}

	public boolean equals(UserCredentials user)
	{
		return passIsValid(user) && name.equals(user.getName());
	}

	///////////////////////////// OAuth authentication //////////////////////////////
	// Alguns métodos relacionados com a autenticação do tipo OAuth

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
}
