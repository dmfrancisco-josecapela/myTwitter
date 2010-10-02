package network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.SignatureMethod;
import utils.BrowserLaunch;

import domain.UserCredentials;


public class OAuthConnection extends AbstractConnection
{
	private static final String consumerKey = "< register your app at https://twitter.com/apps >";
	private static final String consumerSecret = "< register your app at https://twitter.com/apps >";

	protected OAuthConsumer consumer;
	protected OAuthProvider provider;

	// Construtor
	public OAuthConnection()
	{
		super();
		consumer = new DefaultOAuthConsumer(consumerKey,
			consumerSecret, SignatureMethod.HMAC_SHA1);
		provider = new DefaultOAuthProvider(consumer,
			"http://twitter.com/oauth/request_token",
			"http://twitter.com/oauth/access_token",
			"http://twitter.com/oauth/authorize");
	}

	/** Realiza autenticação de tipo OAuth ao pedido passado por parâmetro */
	public void authenticate(HttpUriRequest request, UserCredentials user)
	{
		try {
			CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey,
				consumerSecret, SignatureMethod.HMAC_SHA1);
			consumer.setTokenWithSecret(user.getAccessToken(), user.getTokenSecret());
			consumer.sign(request);

		} catch (OAuthMessageSignerException e) {
			System.out.println("We're having problems connecting to server...\n" +
				"Please, sign out and sign in again.");
			e.printStackTrace();

		} catch (OAuthExpectationFailedException e) {
			System.out.println("We're having problems connecting to server...\n" +
				"Please, sign out and sign in again.");
			e.printStackTrace();
		}
	}

	public void requestPinCode()
	{
		try {
			String authUrl = provider.retrieveRequestToken(OAuth.OUT_OF_BAND);
			BrowserLaunch.openURL("Please open this url", authUrl);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * Inicia a sessão no servidor de Twitter
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method:-account%C2%A0verify_credentials
	 */
	public boolean signIn(UserCredentials user) throws IOException
	{
		try {
			provider.retrieveAccessToken(user.getPassword());

			String accessToken = consumer.getToken();
			String tokenSecret = consumer.getTokenSecret();
			user.setAccessToken(accessToken);
			user.setTokenSecret(tokenSecret);
			consumer.setTokenWithSecret(accessToken, tokenSecret);

			URL url = new URL(url_account + "verify_credentials.xml");
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			consumer.sign(request);

			request.connect();
			if (request.getResponseCode() == HttpStatus.SC_OK)
				return true;

		} catch (OAuthNotAuthorizedException e) { // Rejeitou autenticação
			return false;
		} catch (OAuthMessageSignerException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (OAuthExpectationFailedException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (OAuthCommunicationException e) {
			System.out.println(e.getLocalizedMessage());
		}
		return false;
	}

	/**
	 * Negocia com o servidor um nome de utilizador válido e regista-o.
	 * @see Esta operação não existe na API do twitter.com.
	 */
	public boolean register(UserCredentials user)
	{
		throw new UnsupportedOperationException();
	}
}
