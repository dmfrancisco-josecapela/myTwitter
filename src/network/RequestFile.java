package network;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import utils.FicheiroDeObjectos;
import domain.UserCredentials;


/**
 * Classe que permite gerir ficheiros de pedidos pendentes, ainda não enviados ao
 * servidor.
 */
public class RequestFile
{
	private static final String FILENAME_PREFIX = "pendingtweets_";
	private static final String FILENAME_SUFIX = ".bin";

	@SuppressWarnings("unchecked")
	public static ArrayList<Request> read(String username) throws IOException
	{
		FicheiroDeObjectos fp = new FicheiroDeObjectos();

		try {
			fp.abreLeitura(FILENAME_PREFIX + username + FILENAME_SUFIX);
			ArrayList<Request> requests = (ArrayList<Request>) fp.leObjecto();
			fp.fechaLeitura();
			return requests;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	public static boolean write(String username, ArrayList<Request> requests)
	{
		FicheiroDeObjectos fp = new FicheiroDeObjectos();
		try {
			fp.abreEscrita(FILENAME_PREFIX + username + FILENAME_SUFIX);
			fp.escreveObjecto(requests);
			fp.fechaEscrita();

			return true;

		} catch (IOException e) { return false; }
	}

	public static boolean append(String username, Request request) throws IOException
	{
		ArrayList<Request> requests = new ArrayList<Request>();
		if (exists(username))
			requests = read(username);
		requests.add(request);
		return write(username, requests);
	}

	public static boolean delete(String username)
	{
		return new File(FILENAME_PREFIX + username + FILENAME_SUFIX).delete();
	}

	public static boolean exists(String username)
	{
		return new File(FILENAME_PREFIX + username + FILENAME_SUFIX).exists();
	}
}

/**
 * Classe auxiliar que permite a passagem de um HttpUriRequest para um objecto
 * serializável (e vice-versa).
 */
class Request implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String url;
	private String type; // Post ou Get
	private List<NameValuePair> params; // Para o caso de ser um pedido Post

	Request(HttpUriRequest request, List<NameValuePair> params)
	{
		this.url = "http://" + AbstractConnection.HOSTNAME + request.getURI().getPath();
		this.type = request.getMethod();
		this.params = params;
	}

	public HttpUriRequest convert2HttpRequest(UserCredentials author)
	{
		HttpUriRequest request;
		if (type.equals(HttpPost.METHOD_NAME))
		{
			request = new HttpPost(url);
			AbstractConnection.useExpectContinue(request);

			try {
				HttpEntity entity = new UrlEncodedFormEntity(params, AbstractConnection.ENCODING);
				((HttpPost) request).setEntity(entity);
			} catch (UnsupportedEncodingException e) { }
		}
		else // if (r.type.equals(HttpGet.METHOD_NAME))
			request = new HttpGet(url);

		// O pedido deve ser autenticado no fim de se chamar este método
		return request;
	}
}
