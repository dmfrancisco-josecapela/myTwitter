package network;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import domain.Tweet;
import domain.User;
import domain.UserCredentials;
import exceptions.NonOkResponseException;
import utils.XmlParsingUtils;


/**
 * Classe que utiliza Apache HttpClient para comunicar pedidos e receber respostas
 * através da API de REST da plataforma twitter.com.
 */
public abstract class AbstractConnection
{
	/** Tipo de codificação usada para os pares de parâmetros name-value */
	public static final String ENCODING = "UTF-8";
	public static final String HOSTNAME = "twitter.com";
	protected static final int N_ATTEMPT = 5; // Número de retransmissões
	protected static final int WAIT_TRY = 500; // Intervalo de tempo entre elas

	protected final String url_statuses    = "http://" + HOSTNAME + "/statuses/";
	protected final String url_users       = "http://" + HOSTNAME + "/users/";
	protected final String url_friendships = "http://" + HOSTNAME + "/friendships/";
	protected final String url_account     = "http://" + HOSTNAME + "/account/";

	protected HttpHost host;
	protected HttpClient httpClient;

	// Constructor
	public AbstractConnection() { init(); }

	public void init() {
		host = new HttpHost(HOSTNAME);
		httpClient = new DefaultHttpClient();
	}

	public void terminate() {
		httpClient.getConnectionManager().shutdown();
	}

	/**
	 * Realiza um pedido (GET ou POST) e devolve a resposta obtida
	 * @throws IOException, NonOkResponseException
	 */
	public HttpResponse doRequest(HttpUriRequest request) throws IOException
	{
		HttpResponse response = httpClient.execute(host, request);
		StatusLine statusLine = response.getStatusLine();

		if (statusLine.getStatusCode() != HttpStatus.SC_OK)
		{
			String twitterErrorMsg = "";
			try { // O twitter possui mensagens de erro específicas que vêm no XML
				Document d = new SAXBuilder().build(response.getEntity().getContent());
				twitterErrorMsg = d.getRootElement().getChildText("error");
			}
			catch (IllegalStateException e) { }
			catch (JDOMException e) { }

			throw new NonOkResponseException(statusLine, twitterErrorMsg);
		}
		return response;
	}

	/**
	 * Realiza um pedido (GET ou POST) e devolve a resposta obtida. Se 'pending'
	 * for 'true', envia também os pedidos que estejam num ficheiro.
	 * @throws IOException, NonOkResponseException
	 */
	public HttpResponse doRequest(HttpUriRequest request, UserCredentials author,
		boolean pending) throws IOException
	{
		/* De notar que este método deve ser utilizado somente para enviar pedidos
		 * armazenados cuja resposta não é importante (como por exemplo, enviar um
		 * tweet) pois a única resposta devolvida é a do pedido recebido por
		 * parâmetro */

		// Se se pretende enviar também pedidos que estejam armazenados
		if (pending && RequestFile.exists(author.getName()))
		{
			ArrayList<Request> requests = RequestFile.read(author.getName());

			for (Request r : requests) {
				HttpUriRequest oldRequest = r.convert2HttpRequest(author);
				authenticate(oldRequest, author);
				HttpResponse response = doRequest(oldRequest);
				response.getEntity().consumeContent();
			}
			RequestFile.delete(author.getName());

			System.out.println("Your pending tweets have been sent.");
		}
		return doRequest(request);
	}

	/**
	 * Realiza um pedido (GET ou POST) e tenta a sua retransmissão em caso de falha.
	 * Se 'pending' for 'true', envia também os pedidos que estejam num ficheiro.
	 * @throws IOException, NonOkResponseException
	 */
	public HttpResponse doRequest(HttpUriRequest request, UserCredentials author,
		int nattempt, int waitry, boolean pending) throws IOException
	{
		int i = 1;
		while (true) // Tenta reenviar o pedido 'nattempt' vezes
		{
			try { return doRequest(request, author, pending); } // Tenta enviar pedido à API
			catch (NonOkResponseException e) { throw e; } // Ocorreu erro, mas não de ligação
			catch (IOException e) { if (i == nattempt) throw e; }
			i++;
			try { Thread.sleep(waitry); }
			catch (InterruptedException e) { }
		}
	}

	/**
	 * Realiza um pedido (GET ou POST) e tenta a sua retransmissão em caso de falha.
	 * Se não conseguir enviar o pedido, este é armazenado num ficheiro.
	 * @throws IOException, NonOkResponseException
	 */
	public HttpResponse doPersistentRequest(HttpUriRequest request,
		List<NameValuePair> params, UserCredentials author, int nattempt, int waitry)
		throws IOException
	{
		try {
			return doRequest(request, author, nattempt, waitry, true);
		}
		catch (IOException e) {
			RequestFile.append(author.getName(), new Request(request, params));
			throw e;
		}
	}

	/** Realiza autenticação do pedido passado por parâmetro (BASIC ou OAUTH) */
	public abstract void authenticate(HttpUriRequest request, UserCredentials user);

	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * Inicia a sessão no servidor de Twitter.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method:-account%C2%A0verify_credentials
	 */
	public abstract boolean signIn(UserCredentials user) throws IOException;

	/**
	 * Negocia com o servidor um nome de utilizador válido e regista-o.
	 * @see Esta operação não existe na API do twitter.com.
	 */
	public abstract boolean register(UserCredentials user);

	/**
	 * Mostra uma lista ordenada cronologicamente de todos os tweets dos utilizadores
	 * que segue, incluindo os tweets enviados pelo próprio utilizador.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline
	 */
	public ArrayList<Tweet> getTweets(UserCredentials user) throws IOException
	{
		String url = url_statuses + "friends_timeline.xml";
		HttpGet request = new HttpGet(url);
		authenticate(request, user);

		HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
		return XmlParsingUtils.parseTweets(response);
	}

	/**
	 * Envia um tweet (máximo 140 caracteres; é feita a truncagem) que poderá ser
	 * lido pelo próprio e por todos os que seguem este utilizador.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0update
	 */
	public void sendTweet(UserCredentials user, String tweet) throws IOException
	{
		String url = url_statuses + "update.xml";
		HttpPost request = new HttpPost(url);
		useExpectContinue(request);
		List<NameValuePair> p = addParameters(request, new BasicNameValuePair("status", tweet));

		authenticate(request, user);
		HttpResponse response = doPersistentRequest(request, p, user, N_ATTEMPT, WAIT_TRY);
		response.getEntity().consumeContent(); /* Para evitar erro "Invalid use of
		SingleClientConnManager: connection still allocated. Make sure to release
		the connection before allocating another one"?) */
	}

	/**
	 * Realiza, na lista de todos os utilizadores (existente no servidor), uma
	 * pesquisa em busca de uma correspondência total ou parcial do nome introduzido.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-users-search
	 */
	public ArrayList<User> searchUser(UserCredentials user, String query) throws IOException
	{
		String url = url_users + "search.xml?q=" + query.trim();
		HttpGet request = new HttpGet(url);
		authenticate(request, user);

		HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
		return XmlParsingUtils.parseUsers(response);
	}

	/**
	 * Segue um utilizador com determinado 'screen name'.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships%C2%A0create
	 */
	public void followUser(UserCredentials user, String follow) throws IOException
	{
		String url = url_friendships + "create/" + follow.trim() + ".xml";
		HttpPost request = new HttpPost(url);
		authenticate(request, user);

		HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
		response.getEntity().consumeContent();
	}

	/**
	 * Pede o 'screen name' do utilizador cujos tweets pretendemos deixar de seguir.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships%C2%A0create
	 */
	public void unfollowUser(UserCredentials user, String unfollow) throws IOException
	{
		String url = url_friendships + "destroy/" + unfollow.trim() + ".xml";
		HttpPost request = new HttpPost(url);
		authenticate(request, user);

		HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
		response.getEntity().consumeContent();
	}

	/**
	 * Mostra os utilizadores que seguem determinado utilizador.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0followers
	 */
	public ArrayList<User> getFollowers(UserCredentials user, String username) throws IOException
	{
		String url = url_statuses + "followers/" + username + ".xml";
		HttpGet request = new HttpGet(url);
		authenticate(request, user);

		HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
		return XmlParsingUtils.parseUsers(response);
	}

	/**
	 * Mostra os utilizadores que um determinado utilizador segue.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0friends
	 */
	public ArrayList<User> getFollowing(UserCredentials user, String username) throws IOException
	{
		String url = url_statuses + "friends/" + username+  ".xml";
		HttpGet request = new HttpGet(url);
		authenticate(request, user);

		HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
		return XmlParsingUtils.parseUsers(response);
	}

	/**
	 * Mostra o profile de um utilizador.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0friends
	 */
	public User getUserProfile(UserCredentials user, String username) throws IOException
	{
		String url = url_users + "show.xml?screen_name=" + username.trim();
		HttpGet request = new HttpGet(url);
		authenticate(request, user);

		HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
		return XmlParsingUtils.parseUser(response);
	}

	/**
	 * Mostra os tweets de um utilizador.
	 * @see apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-user_timeline
	 */
	public ArrayList<Tweet> getUserTweets(UserCredentials user, String username) throws IOException
	{
		String url = url_statuses + "user_timeline.xml?screen_name=" + username.trim();
		HttpGet request = new HttpGet(url);
		authenticate(request, user);

		HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
		return XmlParsingUtils.parseTweets(response);
	}

	/** Faz o logout da aplicação */
	public void signOut(UserCredentials user)
	{
	}

	///////////////////////////////////// Utils /////////////////////////////////////

	/** Assegurar a reutilização do socket */
	public static void useExpectContinue(HttpUriRequest request)
	{
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setUseExpectContinue(params, false);
		request.setParams(params);
	}

	/** Adicionar parâmetros a um pedido do tipo POST */
	public static List<NameValuePair> addParameters(HttpEntityEnclosingRequestBase request,
		BasicNameValuePair ... pairs)
	{
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (BasicNameValuePair pair : pairs)
			formparams.add(pair);

		try { request.setEntity(new UrlEncodedFormEntity(formparams, ENCODING)); }
		catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		return formparams;
	}
}
