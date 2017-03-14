/**
 * This class holds the objects to be used in the Huffman tree. Each object
 * holds a character and an integer.
 * @author Raunak Bhojwani - February 20 2015
 */
public class CharFreq {
	//instance variables
	private char charToCount;
	private int charFrequency;
	

	// Creates a new CharFreq object from the given parameters
	 
	public CharFreq(char newChar, int newFrequency) {
		charToCount = newChar;
		charFrequency = newFrequency;
	}


    // Creates a new CharFreq object with only a frequency assigned to it	 
	public CharFreq(int newFrequency) {
		charFrequency = newFrequency;
	}
	
	// Return the character
	public char getCharToCount() {
		return charToCount;
	}
	
	// Return the frequency of a character
	public int getCharFrequency() {
		return charFrequency;
	}
}