package Interpreter;

import java.util.Scanner;

public class Debugger 
{

	public static boolean debug = false;

	public static void startDebug(){
		System.out.println("DEBUGGER STARTED");
		debug = true;
		Scanner debugCom = new Scanner(System.in);
		String comm = "";
		while(debug)
		{
			comm = debugCom.nextLine();
			if(comm.matches("h(elp)?"))
				printHelp();
			else if(comm.matches("q(uit)?"))
			{
				System.out.println("DEBUGGER EXITED");
				debug = false;
			}
			else if(comm.matches("p(rint)? .*"))
				printTape(comm);
			else if(comm.matches("i(ndex)?"))
				printIndex();
			else if(comm.matches("c(omm)? .+"))
				executeCommand(comm);
			else
				System.out.println("Type 'h' or 'help' for usage.");
		}
	}

	private static void printHelp()
	{
		//Print a list of all commands and usage
		System.out.println("h(elp)");
		System.out.println("p(rint) x");
		System.out.println("p(rint) x-x");
		System.out.println("p(rint) x y");		
		System.out.println("i(ndex)");
		System.out.println("q(uit)");
		System.out.println("c(omm) [><+-.,[]]+");

	}

	private static void printTape(String comm)
	{
		//print 1
		//print 0 1
		//print 0-0
		//TODO merge first and last case
		String[] args = comm.split(" ");
		int index = Interpreter.getIndex();
		if(args[1].matches("[\\d+\\*]") && args.length == 2)	//print 1
		{
			int i;
			if(args[1].equals("*"))
				i = Interpreter.getIndex();
			else
				i = Integer.parseInt(args[1]);
			System.out.println("Cell " + i + ": " + Interpreter.getTape()[i] + (index == i ? "*" : ""));
		}
		else if(args[1].matches("[\\d+\\*]-[\\d+\\*]")) //print 1-2
		{
			String[] ints = args[1].split("-");
			int start;
			if(ints[0].equals("*"))
				start = Interpreter.getIndex();
			else
				start = Integer.parseInt(ints[0]);
			int end;
			if(ints[1].equals("*"))
				end = Interpreter.getIndex();
			else
				end = Integer.parseInt(ints[1]);
			if(start > end)
			{
				int temp = start;
				start = end;
				end = temp;
			}
			for(int i = start; i <= end && i < Interpreter.SIZE; i++)
				System.out.println("Cell " + i + ": " + Interpreter.getTape()[i] + (index == i ? "*" : ""));
		}
		else if(args[1].matches("[\\d+\\*]") && args[2].matches("\\d+")) //print 1 2
		{
			int i;
			if(args[1].equals("*"))
				i = Interpreter.getIndex();
			else
				i = Integer.parseInt(args[1]);
			int num = Integer.parseInt(args[2]);
			for(int count = 0; count < num && count + i < Interpreter.SIZE; count++)
				System.out.println("Cell " + (i + count) + ": " + Interpreter.getTape()[i + count] + (index == (i+count) ? "*" : ""));
		}
	}

	public static void printIndex()
	{
		System.out.println("Pointer is at Cell: " + Interpreter.getIndex());
	}

	public static void executeCommand(String comm)
	{
		String[] act = comm.split(" ");
		String command = "";
		int bc = 0;
		for(int i = 1; i < act.length; i++)
			command += act[i];
		for(int i = 0; i < command.length(); i++)
		{
			char c = command.charAt(i);
			if(c == '[')
				bc++;
			else if(c == ']')
				bc--;
			if(bc < 0)
			{
				System.out.println("SYNTAX ERROR: Unmatched ']'!");
				return;
			}
		}
		if(bc > 0)
		{
			System.out.println("SYNTAX ERROR: One or more unmatched '['!");
			return;
		}

		boolean skip = false;
		int bflag = 0;
		for(int i = 0; i < command.length(); i++)
		{
			System.out.println(i);
			char c = command.charAt(i);
			if(c != '[' && c != ']')
				Interpreter.doSomething(c);
			else if(c == '[')
			{
				bc++;
				if(Interpreter.getTape()[Interpreter.getIndex()] == 0)
					skip = true;
			}
			else if(c == ']')
			{
				bc--;
				if(skip == true && bc == 0)
					skip = false;
				else if(Interpreter.getTape()[Interpreter.getIndex()] != 0)
				{
					bflag = bc;
					for(int j = i-1; j >= 0; j--)
					{
						System.out.println("Going back: char = " + command.charAt(j));
						if(command.charAt(j) == '[')
						{
							if(bc == bflag)
							{
								i = j-1;
								break;
							}
							else
								bc++;
						}
						else if(command.charAt(j) == ']')
							bc--;
					}
				}
			}
		}
	}
}
