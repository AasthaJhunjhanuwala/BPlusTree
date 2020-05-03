import java.util.*;

/**
 * The Class Node contains the data structure of each node of the B+ tree.
 */
public class Node{
    /**
     * flag: 0 = internal node
     *       1 = leaf node
     */
    public int flag;

    /**Store the number of keys in the node */
    public int numOfKeys = 0;

    /**Store the type of node */
    public int nodeType;

    /**Stores a pointer to the parent node */
    public Node parent;

    /**Store the list of keys at the node */
    public List<Integer> keys;

    //Only Leaf node attributes
    /**Only leaf node attribute. Store values at the data node */
    public List<Double> values;

    /** Only leaf node attribute. Stores the previous node in the doubly linked list*/
    public Node previous;

    /** Only leaf node attribute. Stores the next node in the doubly linked list*/
    public Node next;

    //Only Internal Node attributes
    /** Only internal node attribute. Stores the children of the node*/
    public List<Node> children;   

    /**
	 * Instantiates a new node of a B+ tree.
	 */
    public Node(){
        keys = new ArrayList<>();
        values = new ArrayList<>();
        children = new ArrayList<>();
        previous = null;
        next = null;
      //  children = null;
    }

    /**
	 * Create new leaf node in the B+ tree.
     * 
     * @param key
     *          the key to be inserted in the new leaf node
     * @param value
     *          the corresponding value to be inserted at the new leaf node
	 */
    public void createLeafNode(int key, double value){
        
        keys.add(key);
        values.add(value);

        numOfKeys = 1;
        flag = 1;
    }

    /**
     * Create a new internal node and set flag.
     */
    public void createInternalNode(){
        flag = 0;
    }
}
