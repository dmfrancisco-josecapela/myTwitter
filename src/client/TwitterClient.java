package client;

import java.io.IOException;

import network.AbstractConnection;
import network.BasicConnection;
import network.OAuthConnection;

import utils.UserInput;
import domain.UserCredentials;
import exceptions.NonOkResponseException;


/**
 * Assegura comunicação do cliente MyTwitter com a API Rest da plataforma twitter.com.
 * Permite a entrada do utilizador. Cria um objecto do tipo AbstractConnection
 * que trata da comunicação com o servidor. Assim que o processo de sign-in está
 * completo, executa o código da classe UserInterface.
 */
public class TwitterClient
{
	protected AbstractConnection conn; // Classe de comunicação com a API do twitter.com
	protected UserInterface userapi; // Classe executada após login

	protected final static int BASIC = 1;
	protected final static int OAUTH = 2;
	protected int authType;

	// Construtor
	TwitterClient(int authentication)
	{
		userapi = new UserInterface();
		authType = authentication;

		switch (authType) {
		case 1:
			conn = new BasicConnection();
			break;
		case 2:
		default:
			conn = new OAuthConnection();
		}
	}

	/** Recebe da consola o tipo de autenticação que pretende utilizar */
	public static void main(String args[])
	{
		String auth = "OAUTH";
		if (args.length > 0) auth = args[0];

		if (auth.equalsIgnoreCase("BASIC"))
			new TwitterClient(BASIC).start();
		else if (auth.equalsIgnoreCase("OAUTH"))
			new TwitterClient(OAUTH).start();
		else
			System.out.println("Invalid authentication option.");
	}

	public void start()
	{
		System.out.println(
		"  Welcome to MyTwitter!\n" +
		"  Share and discover what’s happening right now, anywhere in the world.");
		UserCredentials user = null;

		while (true)
		{
			switch (showMenu())
			{
				case 1: // Pede o username e password ao utilizador
					user = signIn();
					break;
				case 2: // Negocia com o servidor um nome e regista-o
					user = register();
					break;
				case 3: // Termina a aplicação
					conn.terminate();
					return;
			}
			if (user != null) // Se o login ou registo foi feito com sucesso
				startTwitter(user); // Ínicio da troca de mensagens (bloqueante)
		}
	}

	/** Permite utilizar as várias funcionalidades do Twitter */
	private void startTwitter(UserCredentials user)
	{
		// Interacção com o utilizador (não é uma nova thread! é a thread principal)
		userapi.start(user, conn);
	}

	/////////////////////////////////////////////////////////////////////////////////

	/** Mostra o menu com as várias opções existentes e retorna a opção escolhida */
	private int showMenu()
	{
		int opcao;

		do { System.out.print(
			"-------------------------------------------------------------------------\n" +
				"  1. Sign-in\n" +
				"  2. Register\n" +
				"  3. Exit\n" +
				"  C> ");
			opcao = UserInput.readInt();
		} while (opcao < 1 || opcao > 3);

		return opcao;
	}

	/** Pede o username e password ao utilizador */
	private UserCredentials askLogin()
	{
		System.out.print("Username: ");
		String username = UserInput.readString();
		String password;

		if (username.isEmpty()) {
			System.out.println("Invalid username.");
			return null;
		}

		switch (authType) {
		case 1: // Se for autenticação BASIC pede a password ao utilizador
			System.out.print("Password: ");
			password = UserInput.readString(); // InputPassword.readPassword();
			break;
		default: // Se for autenticação OAUTH pede o pincode gerado pelo twitter
			((OAuthConnection) conn).requestPinCode();
			System.out.print("Pin code: ");
			password = UserInput.readString();
		}

		if (password.isEmpty()) {
			System.out.println("Invalid input.");
			return null;
		}
		else return new UserCredentials(username, password);
	}

	/** Inicia a sessão no servidor de Twitter */
	private UserCredentials signIn()
	{
		UserCredentials user = askLogin(); // Pede username e password desejados
		if (user == null) return null;

		try {
			if (conn.signIn(user))
				return user;
		} catch (NonOkResponseException e) {
			System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			System.out.println("We're having problems connecting to server...\n" +
				e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * Negocia com o servidor um nome de utilizador válido e regista-o.
	 * Este método não está disponível na API do serviço twitter.com.
	 */
	private UserCredentials register()
	{
		/* UserCredentials user = askLogin(); // Pede username e password desejados
		if (user == null) return null;

		if (conn.register(user)) {
			System.out.println("Congratulations! Your account has been created.");
			return user;
		} */
		System.out.println("We are sorry but this feature is currently unavailable.");
		return null;
	}
}
