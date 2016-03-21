package edu.cornell.rocketry.comm.send;

/**
 * Reprsents a command to be sent to the TEM on the Launch Vehicle
 * 
 * @author Gus
 *
 */
public class Command {
	
	/**
	 * Contains the type of action to be carried out.
	 */
	private CommandType type;
	
	/**
	 * The time when the command was issued.
	 */
	private long startTime;
	
	/**
	 * Constructs a {@code Command} with the given 
	 * {@code CommandType} and issue time {@code t}.
	 * 
	 * @param ct the type of action to be carried out
	 * @param t the time when the command was issued
	 */
	public Command (CommandType ct, long t) {
		type = ct;
		startTime = t;
	}
	
	/**
	 * @return the type of action to be carried out
	 */
	public CommandType type () { return type; }
	
	/**
	 * @return the time when the Command was issued
	 */
	public long time() { return startTime; }

}
