package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputPassword
{
	/** Permite esconder uma password inserida na consola. Não funciona no eclipse */
	public static String readPassword()
	{
		EraserThread et = new EraserThread();
		Thread mask = new Thread(et);
		mask.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String password = "";

		try {
			password = in.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		// stop masking
		et.stopMasking();
		// return the password entered by the user
		return password;
	}
}

/** @see http://java.sun.com/developer/technicalArticles/Security/pwordmask/ */
class EraserThread implements Runnable
{
	private boolean stop;

	/** Begin masking...display asterisks (*) */
	@SuppressWarnings("static-access")
	public void run () {
		stop = true;
		while (stop) {
			System.out.print("\010*");
			try {
				Thread.currentThread().sleep(1);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	/** Instruct the thread to stop masking */
	public void stopMasking() {
		this.stop = false;
	}
}
