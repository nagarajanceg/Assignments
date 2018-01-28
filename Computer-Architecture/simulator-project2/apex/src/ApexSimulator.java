/**
 * 
 */
package apex;

import java.util.Scanner;

import apex.helpers.ApexSimulatorHelper;

/**
 * @author nagaraj
 *
 */
public class ApexSimulator {
	//PC initial location
	public static int pc = 4000;
	/**
	 * @param args
	 */

	public static void main(String[] args) {
		boolean istage = false; // determine the initialization stage passed
		boolean stageExit = false; // stop the program to execute
		String fileName = args[1]; //filename as argument
		// String fileName = "input.txt";
		Scanner commands = new Scanner(System.in); // input commands
		System.out.println("Enter the operation");
		System.out.println("Initialize:I\tSimulate<n>:S\tDisplay:D\tExit:E");
		while(true){
			if(stageExit == true){
				break;
			}
			if(istage == true){
				System.out.println("Initialize:I\tSimulate<n>:S\tDisplay:D\tExit:E");
			}
			String execInst = commands.next().toUpperCase();
			switch(execInst){
				case "I" :
					ApexSimulatorHelper.INSTANCE.initialize(); // Initialize simulator
					System.out.println("Initialize successfull");
					istage = true;
					break;
				case "S" :
					if(istage){
						ParseInstructions parseInstructions = new ParseInstructions();
						parseInstructions.readInstFromFile(Integer.parseInt(commands.next()),fileName);
					}else{
						System.out.println("Please do Initialize before simulate");
						System.out.println("Initialize:I\tSimulate<n>:S\tDisplay:D\tExit:E");
					}
					break;
				case "D":
					ApexSimulatorHelper.INSTANCE.displayStatus();
					ApexSimulatorHelper.INSTANCE.printArchState();
					break;
				case "E":
					stageExit = true;
					break;
				default:
					System.out.println("Try with listed valid commands");
					break;
			}
		}
		commands.close();
		return;
	}

}
