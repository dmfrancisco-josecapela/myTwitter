package utils;

import java.io.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Simple class to read data from the standard input/graphic windows.
 * @version 1.2 - 05 Mar 1999
 * @author Paulo Marques (pmarques@dei.uc.pt)
 */
public class UserInput
{
	protected static final int BUF_SIZE = 1024;

	// Disable default constructor
	protected UserInput()
	{
	}

	/**
	* Reads an integer from the standard input.
	* The input is terminated by a return. If the input isn't a valid number,
	* "!!! Not an integer!!!" is displayed and the user can try to write the number again.
	*
	* @return the number read
	*/
	public static int readInt()
	{
  		while(true)
      	{
        	try
         	{
         		return Integer.valueOf(readString().trim()).intValue();
         	}
      		catch(Exception e)
         	{
         		System.out.println("!!! Not an integer !!!");
         	}
         }
	}

	/**
	* Reads a double from the standard input.
	* The input is terminated by a return. If the input isn't a valid number,
	* "!!! Not a double!!!" is displayed and the user can try to write the number again.
	*
	* @return the number read
	*/
	public static double readDouble()
	{
  		while(true)
      	{
        	try
         	{
         		return Double.valueOf(readString().trim()).doubleValue();
         	}
      		catch(Exception e)
         	{
         		System.out.println("!!! Not a double !!!");
         	}
         }
	}

	/**
	* Reads a float from the standard input.
	* The input is terminated by a return. If the input isn't a valid number,
	* "!!! Not a float!!!" is displayed and the user can try to write the number again.
	*
	* @return the number read
	*/
	public static float readFloat()
	{
  		while(true)
      	{
        	try
         	{
         		return Float.valueOf(readString().trim()).floatValue();
         	}
      		catch(Exception e)
         	{
         		System.out.println("!!! Not a float !!!");
         	}
         }
	}


	/**
	* Reads a string from the standard input.
	* The input is terminated by a return.
	* @return the string read, without the final '\n\r'
	*/
	public static String readString()
	{
		String s = "";

		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in), 1);
			s = in.readLine();
		}
		catch (IOException e)
		{
        	System.out.println("Error reading from the input stream.");
		}

		return s;
	}

	/**
	* Reads an integer using a separate graphic window
	* The input is terminated when Ok is pressed.
	* @return the integer read.
	*/
	public static int graphicsReadInt(String msg)
	{
		ReadIntFrame intFrame = new ReadIntFrame(msg);
		return intFrame.getValue();
	}

	/**
	* Reads an integer using a separate graphic window
	* The input is terminated when Ok is pressed.
	* @return the integer read.
	*/
	public static double graphicsReadDouble(String msg)
	{
		ReadDoubleFrame doubleFrame = new ReadDoubleFrame(msg);
		return doubleFrame.getValue();
	}

	/**
	* Reads a string using a separate graphic window
	* The input is terminated when Ok is pressed.
	* @return the string read.
	*/
	public static String graphicsReadString(String msg)
	{
		ReadStringFrame stringFrame = new ReadStringFrame(msg);
		return stringFrame.getValue();
	}
}

/**
 * Auxiliary class that reads integers in a window.
 * @version 1.0 - 13 Jan 1999
 * @author Paulo Marques (pmarques@dei.uc.pt)
 */
class ReadIntFrame
	implements ActionListener
{
	/**
	 * Class constructor.
	 * @param msg - the message to appear in the title.
	 */
	public ReadIntFrame(String msg)
	{
		this.msg = new String(msg);
		valid    = false;

		window = new Frame();
		window.setTitle(msg);
		window.setLayout(new FlowLayout());

		valueArea = new TextField("", 15);
		Button done = new Button("  Ok  ");
		done.addActionListener(this);

		window.add(valueArea);
		window.add(done);
		window.setSize(320, 100);
		window.setBackground(Color.gray);
		window.setLocation(100, 100);
		window.setResizable(false);
		window.setVisible(true);
	}

	/**
	 * Method called when Ok is pressed.
	 */
	public synchronized void actionPerformed(ActionEvent e)
	{
		try
		{
			value = Integer.valueOf(valueArea.getText().trim()).intValue();
			valid = true;

			window.dispose();
			notify();
		}
		catch (Exception exc)
		{
			window.setTitle("!!! Not an integer !!! --- " + msg);
		}
	}

	/**
	 * Returns the value inputed by the used.
	 */
	public synchronized int getValue()
	{
		try
		{
			if (!valid)
				wait();
		}
		catch (Exception e)
		{
		}

		return value;
	}

	private boolean   valid;
	private int       value;
	private String    msg;
	private Frame     window;
	private TextField valueArea;
}

/**
 * Auxiliary class that reads doubles in a window.
 * @version 1.0 - 13 Jan 1999
 * @author Paulo Marques (pmarques@dei.uc.pt)
 */
class ReadDoubleFrame
	implements ActionListener
{
	/**
	 * Class constructor.
	 * @param msg - the message to appear in the title.
	 */
	public ReadDoubleFrame(String msg)
	{
		this.msg = new String(msg);
		valid    = false;

		window = new Frame();
		window.setTitle(msg);
		window.setLayout(new FlowLayout());

		valueArea = new TextField("", 15);
		Button done = new Button("  Ok  ");
		done.addActionListener(this);

		window.add(valueArea);
		window.add(done);
		window.setSize(320, 100);
		window.setBackground(Color.gray);
		window.setLocation(100, 100);
		window.setResizable(false);
		window.setVisible(true);
	}

	/**
	 * Method called when Ok is pressed.
	 */
	public synchronized void actionPerformed(ActionEvent e)
	{
		try
		{
			value = Double.valueOf(valueArea.getText().trim()).doubleValue();
			valid = true;

			window.dispose();
			notify();
		}
		catch (Exception exc)
		{
			window.setTitle("!!! Not a double !!! --- " + msg);
		}
	}

	/**
	 * Returns the value inputed by the used.
	 */
	public synchronized double getValue()
	{
		try
		{
			if (!valid)
				wait();
		}
		catch (Exception e)
		{
		}

		return value;
	}

	private boolean   valid;
	private double    value;
	private String    msg;
	private Frame     window;
	private TextField valueArea;
}

/**
 * Auxiliary class that reads strings in a window.
 * @version 1.0 - 05 Mar 1999
 * @author Paulo Marques (pmarques@dei.uc.pt)
 */
class ReadStringFrame
	implements ActionListener
{
	/**
	 * Class constructor.
	 * @param msg - the message to appear in the title.
	 */
	public ReadStringFrame(String msg)
	{
		valid    = false;

		window = new Frame();
		window.setTitle(msg);
		window.setLayout(new FlowLayout());

		valueArea = new TextField("", 15);
		Button done = new Button("  Ok  ");
		done.addActionListener(this);

		window.add(valueArea);
		window.add(done);
		window.setSize(320, 100);
		window.setBackground(Color.gray);
		window.setLocation(100, 100);
		window.setResizable(false);
		window.setVisible(true);
	}

	/**
	 * Method called when Ok is pressed.
	 */
	public synchronized void actionPerformed(ActionEvent e)
	{
		value = valueArea.getText();
		valid = true;

		window.dispose();
		notify();
	}

	/**
	 * Returns the value inputed by the used.
	 */
	public synchronized String getValue()
	{
		try
		{
			if (!valid)
				wait();
		}
		catch (Exception e)
		{
		}

		return value;
	}

	private boolean   valid;
	private String    value;
	private Frame     window;
	private TextField valueArea;
}
