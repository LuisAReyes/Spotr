/**
 * @author Aleksandr Rozenman
 * @author Adam Brakel
 * 
 * Stores static variables representing the specific challenge being shown on the screen.
 */

package com.csun.spotr.core;

/**
 * Static class. Do NOT create objects of this class.
 */
public class CurrentChallenge {
	/**
	 * Private empty constructor. Not an error.
	 */
	private CurrentChallenge() {
		// Abandon all hope, ye who enter here.
	}
	
	public static String name;
	public static float averageRating;
	public static int numRatings;        // How many people rated the challenge
	public static int points;            // The point value of the challenge
	public static byte progress;         // Valid values: -1 through 100
	                                     // -1 means not attempted
	                                     // 100 means complete
	                                     // Displayed as a percentage.
	public static String description;
	public static int numStarted;        // How many people have attempted the challenge
	public static int numCompleted;      // How many people have completed the challenge
	public static String[] category;     // Multiple categories allowed, hence array
	public static String location;
	public static boolean reviewFlag;    // True if the challenge has been flagged for
	                                     // moderator review; false otherwise.
}
