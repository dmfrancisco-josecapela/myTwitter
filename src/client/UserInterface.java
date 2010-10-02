package client;

import java.io.IOException;
import java.util.ArrayList;

import network.AbstractConnection;

import utils.InputTweet;
import utils.UserInput;
import utils.Utils;
import domain.Tweet;
import domain.User;
import domain.UserCredentials;
import exceptions.NonOkResponseException;


/**
 * Assegura a interacção com a consola, garantindo responsividade ao utilizador
 * independentemente do estado da rede.
 */
public class UserInterface
{
	private static final long serialVersionUID = 1L;

	private AbstractConnection conn; // Métodos de comunicação com a API do Twitter
	private UserCredentials user; // Utilizador autenticado

	// Constructor
	UserInterface()
	{
	}

	public void start(UserCredentials user, AbstractConnection conn)
	{
		System.out.println(
		"-------------------------------------------------------------------------\n"
		+ "  Welcome " + user.getName() + "!");

		boolean signedOut = false;
		this.conn = conn;
		this.user = user;

		while (!signedOut)
		{
			switch (showMenu())
			{
			case 0: // Mostrar tweets de todas as pessoas que segue
				displayAllTweets();
				break;
			case 1: // Publica um novo tweet
				newTweet();
				break;
			case 2: // Procura se um dado utilizador existe
				searchUser();
				break;
			case 3: // Seguir um novo utilizador
				followUser();
				break;
			case 4: // Deixar de seguir um utilizador
				unfollowUser();
				break;
			case 5: // Mostrar os utilizadores que seguem determinado utilizador
				displayFollowers();
				break;
			case 6: // Mostrar os utilizadores que determinado utilizador segue
				displayFollowing();
				break;
			case 7: // Mostrar o profile de um utilizador
				displayUserProfile();
				break;
			case 8: // Mostrar os tweets de um utilizador
				displayUserTweets();
				break;
			case 9: // Termina a ligação
				signOut();
				signedOut = true;
			}
		}
	}

	/** Mostra o menu com as várias opções existentes e retorna a opção escolhida */
	private int showMenu()
	{
		int opcao;

		do { System.out.print(
		 "-------------------------------------------------------------------------\n" +
			"  0. Display list of all tweets I am following\n" +
			"  1. Send a Tweet\n" +
			"  2. Search users\n" +
			"  3. Follow a user\n" +
			"  4. Unfollow a user\n" +
			"  5. Display list of followers\n" +
			"  6. Display list of friends\n" +
			"  7. Display user profile\n" +
			"  8. Display tweets from a user\n" +
			"  9. Sign out\n" +
			"  C> ");
			opcao = UserInput.readInt();
		} while (opcao < 0 || opcao > 9);

		return opcao;
	}

	/** Imprime um aviso de que a ligação está lenta ou o servidor não responde */
	private void printConnectionProblem()
	{
		System.out.println("Your Internet connection is slow or unavailable.");
	}

	/////////////////////// Implementação das opções do menu ////////////////////////

	/**
	 * Mostra uma lista ordenada cronologicamente de todos os tweets dos utilizadores
	 * que segue, incluindo os tweets enviados pelo próprio utilizador. Tweets não
	 * são ordenados pelo remetente mas sim pela data de envio.
	 * Quando existe grande número de tweets são mostrados apenas os 20 mais recentes.
	 */
	private void displayAllTweets()
	{
		try {
			ArrayList<Tweet> tweets = conn.getTweets(user);

			String str = Utils.array2String(tweets, "There are no tweets to show.",
			"\n_________________________________________________________________________\n\n");
			System.out.println("\n" + str);

		} catch (NonOkResponseException e) {
			System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			printConnectionProblem();
		}
	}

	/**
	 * Envia um tweet (máximo 140 caracteres; é feita a truncagem) que poderá ser
	 * lido pelo próprio e por todos os que seguem este utilizador. Cada tweet tem
	 * a data em que foi publicado.
	 */
	private void newTweet()
	{
		System.out.println("What are you doing?");
		String msg = InputTweet.readTweet();
		if (msg.isEmpty()) {
			System.out.println("Empty tweets are not cool."); return;
		}

		try {
			conn.sendTweet(user, msg);
			// O tweet foi adicionado no servidor
			System.out.println("Tweet sent successfully.");

		} catch (NonOkResponseException e) {
			System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			printConnectionProblem();
			System.out.println("Your tweet has been saved for sending.");
		}
	}

	/**
	 * Realiza, na lista de todos os utilizadores (existente no servidor), uma
	 * pesquisa em busca de uma correspondência total ou parcial do nome introduzido.
	 */
	private void searchUser()
	{
		System.out.print("Who are you looking for? ");
		String searchUser = UserInput.readString();
		if (searchUser.isEmpty()) {
			System.out.println("Invalid query."); return;
		}

		try {
			ArrayList<User> res = conn.searchUser(user, searchUser);

			System.out.println("Name results: ");
			for (User user : res)
				System.out.println("  "+ user);

		} catch (NonOkResponseException e) {
			System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			printConnectionProblem();
		}
	}

	/** Pede o 'screen name' do utilizador cujos tweets pretendemos seguir */
	private void followUser()
	{
		System.out.print("Username: ");
		String username = UserInput.readString();
		if (username.isEmpty()) {
			System.out.println("Invalid username."); return;
		}

		if (username.equalsIgnoreCase(user.getName())) {
			System.out.println("You can't follow yourself!");
			return;
		}
		try {
			conn.followUser(user, username);
			System.out.println("You are now following " + username + "!");

		} catch (NonOkResponseException e) {
			if (e.getFriendlyMessage().equals("Not found"))
				System.out.println("The inserted user does not exist.");
			else System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			printConnectionProblem();
		}
	}

	/** Pede o 'screen name' do utilizador cujos tweets pretendemos deixar de seguir */
	private void unfollowUser()
	{
		System.out.print("Username: ");
		String username = UserInput.readString();
		if (username.isEmpty()) {
			System.out.println("Invalid username."); return;
		}

		try {
			conn.unfollowUser(user, username);
			System.out.println("You are not following " + username + " anymore!");

		} catch (NonOkResponseException e) {
			if (e.getFriendlyMessage().equals("Not found"))
				System.out.println("The inserted user does not exist.");
			else System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			printConnectionProblem();
		}
	}

	/** Mostra os utilizadores que seguem determinado utilizador */
	private void displayFollowers()
	{
		System.out.print("Whose followers list do you want to check ['Enter' if it's yours]: ");
		String username = UserInput.readString();
		if (username.isEmpty()) username = user.getName();

		try {
			ArrayList<User> followers = conn.getFollowers(user, username);
			String names = Utils.array2String(followers, "no one", "\n  ");
			System.out.println("User "+ username +" is being followed by: \n  " + names);

		} catch (NonOkResponseException e) {
			if (e.getFriendlyMessage().equals("Not found"))
				System.out.println("The inserted user does not exist.");
			else System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			printConnectionProblem();
		}
	}

	/** Mostra os utilizadores que determinado utilizador segue */
	private void displayFollowing()
	{
		System.out.print("Whose friends list do you want to check ['Enter' if it's yours]: ");
		String username = UserInput.readString();
		if (username.isEmpty()) username = user.getName();

		try {
			ArrayList<User> following = conn.getFollowing(user, username);
			String names = Utils.array2String(following, "no one", "\n  ");
			System.out.println("User "+ username +" is following: \n  " + names);

		} catch (NonOkResponseException e) {
			if (e.getFriendlyMessage().equals("Not found"))
				System.out.println("The inserted user does not exist.");
			else System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			printConnectionProblem();
		}
	}

	/** Mostra o profile de um utilizador */
	private void displayUserProfile()
	{
		System.out.print("Who do you want to check? ");
		String username = UserInput.readString();

		try {
			User res = conn.getUserProfile(user, username);
			System.out.println(res.getProfile());

		} catch (NonOkResponseException e) {
			if (e.getFriendlyMessage().equals("Not found"))
				System.out.println("The inserted user does not exist.");
			else System.out.println(e.getFriendlyMessage());
		} catch (IOException e) {
			printConnectionProblem();
		}
	}

	/** Mostra os tweets de um utilizador */
	private void displayUserTweets()
	{
		System.out.print("Whose tweets do you want to check ['Enter' if it's yours]: ");
		String username = UserInput.readString();

		try {
			ArrayList<Tweet> tweets = conn.getUserTweets(user, username);

			String str = Utils.array2String(tweets, "There are no tweets to show.",
			"\n_________________________________________________________________________\n\n");
			System.out.println("\n" + str);

		} catch (NonOkResponseException e) {
			System.out.println("The inserted user does not exist or has protected their tweets.");
		} catch (IOException e) {
			printConnectionProblem();
		}
	}

	/** Faz o logout da aplicação. Volta para a classe "TwitterClient" */
	private void signOut()
	{
		conn.signOut(user);
	}
}