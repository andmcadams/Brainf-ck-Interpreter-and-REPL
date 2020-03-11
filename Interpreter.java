package Interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Interpreter 
{

	public static final char DEBUGCONST = '?';
	
	public static boolean DEBUGFLAG;
	public static void main(String[] args0)
	{
		if(args0.length == 1 || args0.length == 2)
		{
			String dbflag = (args0.length == 1)? "" : args0[0];
			String filename = (args0.length == 2)? args0[1] : args0[0];
			if(filename == null || !filename.matches(".+\\.bf$"))
			{
				System.out.println("FILE ERROR: " + filename + " is not a valid brainfuck file (.bf)");
				System.exit(0);
			}
			if(dbflag.equals("-d") && args0.length == 2)
					DEBUGFLAG = true;
			else 
				DEBUGFLAG = false;
			File bffile = new File(filename.trim());
			for(int i = 0; i < SIZE; i++)
				tape[i] = 0;
			interpret(bffile);
		}
		else
		{
			System.out.println("Usage: $java bfd [-d] [file.bf]");
			System.exit(0);
		}
	}

	private static int index = 0;
	public static final int SIZE = 300;
	private static byte[] tape = new byte[SIZE];
	private static String inline;
	private static int icount;
	private static String line;
	private static int brcount = 0;
	private static String log;
	private static boolean skipflag = false;
	private static int brcflag = 0;
	
	public static void interpret(File bffile)
	{

		checkSyntax(bffile);
		Scanner d;
		try {
			d = new Scanner(bffile);
			log = "";
			while(d.hasNextLine())
			{
				line = d.nextLine();
				for(int i = 0; i < line.length(); i++)
				{
					char c = line.charAt(i);
					if(logflag)
						addToLog(c);
					else
						doSomething(c);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void checkSyntax(File bffile)
	{

		int bracketcount = 0;
		int linecount = 0;
		Scanner file;
		String line;
		try {

			file = new Scanner(bffile);

			while(file.hasNextLine())
			{
				line = file.nextLine();
				for(int i = 0; i < line.length(); i++)
				{
					if(line.charAt(i) == '[')
						bracketcount++;
					else if(line.charAt(i) == ']')
						bracketcount--;
					if(bracketcount < 0)
					{
						System.out.println("SYNTAX ERROR: Unmatched ']'! Line: " + linecount + " Char: " + i + " in " + bffile + ".");
						System.exit(0);
					}
				}
				linecount++;
			}
			if(bracketcount > 0)
			{
				System.out.println("SYNTAX ERROR: One or more unmatched '[' detected in " + bffile + ".");
				System.exit(0);
			}
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void movePointerRight()
	{
		//Check for out of bounds
		if(index < SIZE-1)
			index++;
		else
			index = 0;
	}
	public static void movePointerLeft()
	{
		//Check for out of bounds
		if(index != 0)
			index--;
		else
			index = SIZE-1;
	}
	public static void incrementValue()
	{
		if(tape[index] < 127)
			tape[index] += 1;
		else
			tape[index] = -128;
	}
	public static void decrementValue()
	{
		if(tape[index] > -128)
			tape[index] -= 1;
		else
			tape[index] = 127;
	}
	public static void printValue()
	{
		System.out.print((char)tape[index]);
	}
	public static void readValue()
	{
		if(inline == null || icount >= inline.length())
		{
			Scanner s = new Scanner(System.in);
			inline = s.nextLine();
			inline += "\n";
			icount = 0;
		}
		tape[index] = (byte)inline.charAt(icount);
		icount++;
	}

	public static void leftBracket()
	{
		brcount++;
		if(tape[index] != 0)
		{
			if(brcount == 1 && log == "")
				{
					logflag = true;
					log = "[";
				}
		}
		else
		{
			brcflag = brcount;
			skipflag = true;
		}
	}
	public static void rightBracket()
	{
		brcount--;
		if(tape[index] != 0)
		{
			if(brcount == 0)
				logindex = -1;
			else
			{
				brcflag = brcount;
				for(int i = logindex-1; i >= 0; i--)
				{
					if(log.charAt(i) == '[')
						{
						if(brcount == brcflag)
								{
									logindex = i-1;
									return;
								}
						else
							brcount++;
						}
					else if(log.charAt(i) == ']')
						brcount--;
				}
			}
		}
		else if(brcount == 0)
			log = "";
	}



	public static void doSomething(char c)
	{
		if(!skipflag)
		{
			if(c == '>')
				movePointerRight();
			else if(c == '<')
				movePointerLeft();
			else if(c == '+')
				incrementValue();
			else if(c == '-')
				decrementValue();
			else if(c == '.')
				printValue();
			else if(c == ',')
				readValue();
			else if(c == '[')
				leftBracket();
			else if(c == ']')
				rightBracket();
			else if(c == DEBUGCONST && Debugger.debug == false)
				Debugger.startDebug();
			else
				return;
		}
		else if(skipflag)
		{
			if(c == '[')
				brcount++;
			else if(c == ']')
			{
				if(brcflag == brcount)
						skipflag = false;
				brcount--;
			}
		}
	}

	public static void printReport(int max, char c)
	{
		//System.out.println("Skip? " + skipflag);
		System.out.println("Character: " + c);
		System.out.println("BRCOUNT: " + brcount);
		if(!log.equals(""))
			System.out.println("LOG: " + log);
		System.out.print("TAPE: ");
		String numbers = "      ";
		for(int i = 0; i < max; i++)
		{
			if(i != index)
			{
				System.out.print(tape[i] + " ");
				numbers += i + " ";
			}
			else
			{
				System.out.print("*" + tape[i] + "* ");
				numbers += " " + i + "  ";
			}
		}
		System.out.println();
		System.out.println(numbers);

	}

	private static boolean logflag = false;
	private static int logindex = 0;
	public static void addToLog(char c)
	{
		if(c == '[' || c == ']' || c == '+' || c == '-' 
				|| c == '.' || c == ',' || c == '>' || c == '<' || c == DEBUGCONST)
			log += c;
		if(c == ']' && brcount == 1)
		{
			logflag = false;
			logindex = 1;
			while(log != "")
			{
				c = log.charAt(logindex);
				doSomething(c);
				logindex++;
			}
		}
		else if(c == '[')
		{
			brcount++;
		}
		else if(c == ']')
		{
			brcount--;
		}

	}
	public static byte[] getTape()
	{
		return tape;
	}

	public static int getIndex()
	{
	   	return index;
	}
}

