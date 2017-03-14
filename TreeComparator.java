import java.util.Comparator;

/**
 * This class is used to compare two BinaryTree objects that hold CharFreq objects.
 * @author Raunak Bhojwani - February 20 2015
 */
public class TreeComparator implements Comparator<BinaryTree<CharFreq>> {
	
	 // Compares the character frequency counts of two BinaryTree nodes
	 // return -1 if first node charFrequency is less than second node charFrequency,
	 // 0 if the charFrequencys are equal, 1 if second node charFrequency is less than first node charFrequency
	public int compare(BinaryTree<CharFreq> a, BinaryTree<CharFreq> b) {
		if (a.getData().getCharFrequency() < b.getData().getCharFrequency()) {
			return -1;
		}
		else if (a.getData().getCharFrequency() == b.getData().getCharFrequency()) {
			return 0;
		}
		else {
			return 1;
		}
	}
}