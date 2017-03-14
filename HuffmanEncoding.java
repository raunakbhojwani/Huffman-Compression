import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

import javax.swing.JFileChooser;

import java.util.PriorityQueue;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Huffman Encoding
 * @author Raunak Bhojwani - February 20 2015
 */
public class HuffmanEncoding {
	
	
	//initial map to find frequencies from
	private Map<Character, Integer> frequencyMap;
	private String fileName;
	private BinaryTree<CharFreq> HuffmanTree;
	private Map<Character, String> HuffmanTreePaths;
	
	
	/**
	 * Creates a HuffmanEncoding object containing a blank frequency map.
	 */
	public HuffmanEncoding() {
		
		frequencyMap = new HashMap<Character, Integer>();
		fileName = "";
		HuffmanTree = null;
		HuffmanTreePaths = new HashMap<Character,String>();
	}
	
	public Map<Character, Integer> getFrequencyMap() {
		return frequencyMap;
	}

	
	
	/**
	 * recursive search on the Huffman tree starting from the root and moving downwards. The string that defines the 
	 * "path" from the root to a leaf is composed of 0s and 1s - moving to the right child of a node adds a "1" to the end of it, moving 
	 * to the left child of a node adds a "0" to the end of it. The string starts off initially empty. Upon reaching a leaf, an entry is added
	 * to the instance variable hashmap HuffmanTreePaths composed of a "char" key (taken from the leaf node CharFreq's character value), with value 
	 * corresponding to the current "path" string in the recursive function.
	 * @param path, current path travelled
	 * @param huffman, current node in the Huffman tree
	 * @return string defining the most recent path travelled
	 */
	public void generateHuffmanMap(String path, BinaryTree<CharFreq> huffman) {
		if(huffman != null) { //check to see if current node is null before proceeding
			if (huffman.isLeaf()) { //if leaf, add new value to map with key (character) and value path (string)
				HuffmanTreePaths.put(huffman.getData().getCharToCount(), path);
			}
			if(huffman.hasLeft()) { //if there is a left node, travel to it and repeat the method
				generateHuffmanMap(path + "0", huffman.getLeft());
			}
			if(huffman.hasRight()) { //if there is a right node, travel to it and repeat the method
				generateHuffmanMap(path + "1", huffman.getRight());
			}
		}
	}
	
	
	/**
	 * Puts all the pieces of the process together and creates a Huffman tree from the inputted pathname of the file, 
	 * also generates a map of its nodes' character values and the path to the respective node.
	 * @param pathName, pathname of .txt file
	 * @return the final Huffman tree
	 */
	public BinaryTree<CharFreq> generateHuffmanTree(String pathName) throws FileNotFoundException, IOException {
		BinaryTree<CharFreq> Huffman = createEncoding(createPriorityQueue(generateFrequencyMap(pathName)));
		HuffmanTree = Huffman;
		this.generateHuffmanMap("", Huffman);
		return Huffman;
	}
	
	
	/**
	 * This method generates a frequency map for all the characters within the text file used as input.
	 * @param pathName, where to find the file
	 * @return map of characters and frequency of appearance
	 */
	public Map<Character, Integer> generateFrequencyMap(String pathName) throws FileNotFoundException, IOException {	
		BufferedReader inputFile = new BufferedReader(new FileReader(pathName)); //filereader to read from file at designated pathname
		try {		
			Map<Character, Integer> newFrequencyMap = new HashMap<Character, Integer>(); //initializes new map to hold char
			//loops through entire file of characters
			int cInt = inputFile.read(); //get next char in file, is returned as an integer in unicode
			while (cInt != -1) { //while there exists a next character in the file
				//checks to see if character exists in map before proceeding
				if (newFrequencyMap.containsKey((char)cInt)) {
					newFrequencyMap.put((char)cInt,newFrequencyMap.get((char)cInt) + 1);
				}
				else {
					newFrequencyMap.put((char)cInt,1);
				}
				cInt = inputFile.read(); //get next character
			}
			this.frequencyMap = newFrequencyMap; //set frequency map of HuffmanEncoding object to newly created map
			return newFrequencyMap; //returns the new frequency map
		}
		finally {
			inputFile.close(); //close BufferedReader
		}
	}

	
	/**
	 * Creates a singleton tree that holds a CharFreq object
	 * @param cf, CharFreq object to instantiate tree with
	 * @return BinaryTree node containing the CharFreq object as its data
	 */
	public BinaryTree<CharFreq> singletonTree(CharFreq cf) {
		return new BinaryTree<CharFreq>(cf);
	}
	
	
	/**
	 * This method returns the inversion of a HuffmanTreePaths map (keys are now values, values are now keys)
	 * @return inverted HuffmanTreePaths map
	 */
	public Map<String, Character> invertedHuffmanTreePaths() {
		Map<String, Character> inverted = new HashMap<String,Character>();
		Set<Character> keys = HuffmanTreePaths.keySet();
		for (char c : keys) {
			inverted.put(HuffmanTreePaths.get(c), c);
		}
		return inverted;
	}
	
	
	/**
	 * Creates a priority queue with initial size corresponding to the number of keys (characters) in the frequencyMap used as a parameter
	 * @param freqMap, frequency map for the characters within a file
	 */
	public PriorityQueue<BinaryTree<CharFreq>> createPriorityQueue(Map<Character,Integer> freqMap) {
		//creates a priority queue with initial size corresponding to the number of keys (characters) in the map
		PriorityQueue<BinaryTree<CharFreq>> createTree = new PriorityQueue<BinaryTree<CharFreq>>(freqMap.keySet().size(), new TreeComparator());
		Set<Character> keys = freqMap.keySet(); //create a set of all character keys
		//iterate through it, creating CharFreq objects with the character/charFrequency values held within freqMap
		//create a singleton tree for every CharFreq object, and add it to the priority queue
		for (char c : keys) { 
			CharFreq currentCharFreq = new CharFreq(c, freqMap.get(c)); //charfreq object holding data from map
			BinaryTree<CharFreq> CharFreqNode = singletonTree(currentCharFreq); //singleton tree
			createTree.add(CharFreqNode); //add to queue
		}
		return createTree;
	}
	
	
	/**
	 * This method takes all of the singleton tree nodes within the priority queue and builds the final Huffman tree
	 * @param createTree, priority queue to build the tree with
	 * @return BinaryTree that has sorted all of the CharFreq objects according to the requirements of Huffman sorting
	 */
	public BinaryTree<CharFreq> createEncoding(PriorityQueue<BinaryTree<CharFreq>> createTree) {
		while (createTree.size() > 1) {
			BinaryTree<CharFreq> lowFreq = createTree.poll(); //remove and return singleton tree with lowest character frequency
			BinaryTree<CharFreq> secondLowFreq = createTree.poll(); //remove and return singleton tree with 2nd lowest character frequency (technically the current lowest)
			CharFreq onlyFreq = new CharFreq(lowFreq.getData().getCharFrequency() + secondLowFreq.getData().getCharFrequency()); //create a CharFreq object with no character assigned to it
			BinaryTree<CharFreq> newNode = new BinaryTree<CharFreq>(onlyFreq, lowFreq, secondLowFreq); //create a new singleton tree using the above CharFreq object
			//set its left and right pointers to the previously removed BinaryTrees
	
			createTree.add(newNode); //add the tree back into the queue
		}
		return createTree.poll(); //return the final Huffman tree
	}

	/**
	 * Puts up a fileChooser and gets path name for file to be opened.
	 * Returns an empty string if the user clicks "cancel".
	 * @return path name of the file chosen  
	 */
	public static String getFilePath() {
	    JFileChooser fc = new JFileChooser("."); // start at current directory

	    int returnVal = fc.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	        File file = fc.getSelectedFile();
	        String pathName = file.getAbsolutePath();
	        return pathName;
	    }
	    else {
	        return "";  
	    }
	}
 
 
 /**
  * This method compresses a file by converting all of its characters into bytes assigned by using a Huffman tree
  * @param filePath, path of file to compress
  */
 	public void compressFile(String filePath) throws FileNotFoundException, IOException {
 		BufferedReader inputFile = new BufferedReader(new FileReader(filePath + ".txt")); //filereader to read from file at designated pathname
 		//bitwriter to compress the file with, creates new file that has the same filepath as the initial file but with "_compressed" added to it
 		BufferedBitWriter bitOutputFile = new BufferedBitWriter(filePath + "_compressed.txt"); 
 		try {	
	 		int cInt = inputFile.read(); //get next char in file, is returned as an integer in unicode
			while (cInt != -1) { //while there exists a next character in the file
				String nodePath = HuffmanTreePaths.get((char)cInt); //get the path to the node from the HuffmanTreePaths map
				//if this conditional statement returns false, we know the file is composed of only
				//one character that is repeated 1 or more times. This is a boundary case.
				if (this.frequencyMap.size() != 1) { 
					for (int i = 0; i < nodePath.length(); i++) { //iterate along the string containing the path from the beginning to end
						int bitToWrite = 0; //integer representing the bit that will be written
						if (nodePath.charAt(i) == '0') //character = 0, set bit to 0
							bitToWrite = 0;
						if (nodePath.charAt(i) == '1') //character = 1, set bit to 1
							bitToWrite = 1;
						bitOutputFile.writeBit(bitToWrite); //write bit to file
					}
				}
				else {
					//for files that only have one character repeated one or more times, we store that char as a 0.
					int bitToWrite = 0; 
					bitOutputFile.writeBit(bitToWrite); //write bit to file
				}
				cInt = inputFile.read(); //get next character in file	
			}
 		}
 		finally {
			inputFile.close(); //close input stream
			bitOutputFile.close(); //close output stream
 		}
 	}
 	
 	
 	/**
 	 * This method decompresses a file by converting all of its bytes into characters assigned by a Huffman tree
 	 * @param filePath, path of file to decompress
 	 */
 	public void decompressFile(String filePath) throws FileNotFoundException, IOException {
 		BufferedBitReader bitInputFile = new BufferedBitReader(filePath + "_compressed.txt"); //filereader to read bytes from file at designated pathname
 		//bitreader to decompress the file with, creates new file that has the same filepath as the initial file but instead of "_compressed" at the end it has "_decompressed" added to it
 		BufferedWriter outputFile =  new BufferedWriter(new FileWriter(filePath + "_decompressed.txt"));
 		try {
	 		int bitReturned = bitInputFile.readBit(); //get next bit in file
	 		Map<String,Character> huffmanInverted = this.invertedHuffmanTreePaths(); //invert the keys/values of the huffman tree
	 		String nodePath = ""; //create an empty string to hold the path for the character in the inverted huffman tree
		 	while (bitReturned != -1) { //while there exists a next bit in the file	
		 		if (this.frequencyMap.size() == 1) { //if file is composed of a single repeating character
			 		Set<Character> oneChar = this.getFrequencyMap().keySet();
			 		Iterator<Character> oneCharIter = oneChar.iterator();
			 		outputFile.write(oneCharIter.next()); //write that one character to the file		
			 	}
		 		else {
			 		if (bitReturned == 0) { //if 0 is returned, add 0 to the string
			 			nodePath += "0";
			 		}
			 		else if (bitReturned == 1) { //if 1 is returned, add 1 to the string
			 			nodePath += "1";
			 		}
			 		//if the path exists in the inverted huffman map, add a new character to the file associated with that path
			 		if (huffmanInverted.containsKey(nodePath)) { 
			 			outputFile.write((char)huffmanInverted.get(nodePath));
			 			nodePath = ""; //'reset' the string that holds paths
			 		}
		 		}
		 		bitReturned = bitInputFile.readBit(); //get next bit in file
		 	}	 		
 		}
 		finally {
 			bitInputFile.close(); //close input stream
 			outputFile.close(); //close output stream
 		}
 	}
 	
 	/**
 	 * Does the whole process - creates the tree, compression, decompression - the whole 9 yards.
 	 * @param pathName, name of file
 	 */
 	public void doItAll() throws FileNotFoundException, IOException {
 		if ((new File(fileName + ".txt").length()) != 0) { //check to see if file is empty
	 		this.generateHuffmanTree(fileName + ".txt"); //generate Huffman tree
	 		this.compressFile(fileName); //compress file
	 		this.decompressFile(fileName); //decompress file
//	 		//print file stats
//	 		System.out.println("File name: " + this.getFileName()); 
//	 		System.out.println("Initial file size: " + (new File(this.getFilePath() + ".txt").length()));
//	 		System.out.println("Compressed file size: " + (new File(this.getFilePath() + "_compressed.txt")).length());
//	 		System.out.println("Decompressed file size: " + (new File(this.getFilePath() + "_decompressed.txt")).length() + '\n');
	 	}
 		else { //if file is blank, don't run the the method and instead return an error message
// 			System.out.println("File name: " + this.getFileName());
 			System.out.println("This is a blank file! Why would you want to compress it?\n");
 		}
 	}
 	
 /**
  * Main method for testing.
  */
 public static void main(String [] args) { 
	 //create an individual object for each text file that is to be compressed
	 HuffmanEncoding USConstitution = new HuffmanEncoding();
	 HuffmanEncoding WarAndPeace = new HuffmanEncoding();
	 HuffmanEncoding EmptyFile = new HuffmanEncoding();
	 HuffmanEncoding OneChar = new HuffmanEncoding();
	 HuffmanEncoding OnlyOneChar = new HuffmanEncoding();
	 
	 //find the file folder directory using getFilePath(), 
	 //then creates a frequency map for each using generateFrequencyMap()
	 //checks exceptions
	 try {
//		 System.out.println("Please navigate to the MobyDick.txt file on your hard drive and select it.");
//		 String folderPath = getFolderPath(); //find pathname of folder containing text files

		 //set the filepath/filename of each HuffmanEncoding object as the folder path + its corresponding filename
//		 MobyDick.setFilePath(folderPath + "/MobyDick"); 
//		 MobyDick.setFileName("MobyDick.txt");
//		 USConstitution.setFilePath(folderPath + "/USConstitution");
//		 USConstitution.setFileName("USConstitution.txt");
//		 WarAndPeace.setFilePath(folderPath + "/WarAndPeace");
//		 WarAndPeace.setFileName("WarAndPeace.txt");
//		 EmptyFile.setFilePath(folderPath + "/blanktest");
//		 EmptyFile.setFileName("blanktest.txt");
//		 OneChar.setFilePath(folderPath + "/allOnes");
//		 OneChar.setFileName("allOnes.txt");
//		 OnlyOneChar.setFilePath(folderPath + "/oneChar");
//		 OnlyOneChar.setFileName("oneChar.txt");
		 
		 System.out.println("Please navigate to EmptyFile.txt");
			EmptyFile.fileName = getFilePath();
			int lastIndex = EmptyFile.fileName.lastIndexOf(".");
			EmptyFile.fileName = EmptyFile.fileName.substring(0, lastIndex);
			System.out.println(EmptyFile.fileName);
			
			System.out.println("Please navigate to OneChar.txt");
			OneChar.fileName = getFilePath();
			lastIndex = OneChar.fileName.lastIndexOf(".");
			OneChar.fileName = OneChar.fileName.substring(0, lastIndex);
			
			System.out.println("Please navigate to OnlyOneChar.txt");
			OnlyOneChar.fileName = getFilePath();
			lastIndex = OnlyOneChar.fileName.lastIndexOf(".");
			OnlyOneChar.fileName = OnlyOneChar.fileName.substring(0, lastIndex);
			
			System.out.println("Please navigate to WarAndPeace.txt");
			WarAndPeace.fileName = getFilePath();
			lastIndex = WarAndPeace.fileName.lastIndexOf(".");
			WarAndPeace.fileName = WarAndPeace.fileName.substring(0, lastIndex);
			
			System.out.println("Please navigate to USConstitution.txt");
			USConstitution.fileName = getFilePath();
			lastIndex = USConstitution.fileName.lastIndexOf(".");
			USConstitution.fileName = USConstitution.fileName.substring(0, lastIndex);
		 
		 //generate Huffman trees for each one, compress the file, and decompress it using doItAll()
		 //tests boundary cases too, blank file and a file filled with all one repeating character
		 USConstitution.doItAll();
		 WarAndPeace.doItAll();
		 EmptyFile.doItAll(); //empty file
		 OneChar.doItAll(); //file filled with '1's
		 OnlyOneChar.doItAll(); //file filled with 'a's
	 }
	 catch (Exception FileNotFoundException) {
		 System.out.println ("File directory not found!");
	 }	  
 }
}