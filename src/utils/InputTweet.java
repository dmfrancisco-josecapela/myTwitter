package utils;

import java.io.IOException;

import domain.Tweet;

public class InputTweet
{
	/** Permite o mecanismo de contagem dos caracteres já escritos */
	public static String readTweet()
	{
		String msg = "", temp = "";
		char c;

		try
		{
			do {
				msg += temp; c = 0; temp = "";
				msg = msg.trim();
				System.out.print((Tweet.MAX_LENGTH - msg.length()) + " | " + msg);

				while (c != '\n') {
					c = (char) System.in.read();
					temp += c;
				}
			} while (!temp.trim().isEmpty());
		}
		catch (IOException e) { }

		return msg;
	}
}
