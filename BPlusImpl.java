import java.util.*;

/**
 * The Class BPlusImpl executes all the B+ tree modification functions like insert, delete, search.         
 */
public class BPlusImpl{
    /**Stores the order of the tree */
    public int m;
    
    /** Root node of the tree*/
    public Node root;

    //Initialize to set order as m and root as null.
    public void initialize(int m){
        this.m = m;
        this.root = null;
    }

    /**
	 * Insert a new key value pair in the B+ tree.
	 *
	 * @param key
	 *            the key to be inserted
	 * @param value
	 *            the value to be inserted        
	 */
    public void insert(int key, double value){
        //Insert in new tree
        if(root==null){
            root = new Node();
            root.createLeafNode(key, value);
        }

        //Insert when leaf node is root
        else if(root.flag == 1 && root.numOfKeys < m - 1){
            insertLeafNode(key, value, root);
        }

        //Insert in respective leaf node
        else{
            Node curr = new Node();
            curr = root;
            Node parent = null;
            while(curr.flag == 0){
                curr.parent = parent;
                parent = curr;
                curr = curr.children.get(searchInNode(curr, key));
            }
            insertLeafNode(key, value, curr);

            //If leaf is full
            if(curr.keys.size() == m){
                splitLeafNode(curr);
            }
        }
        
    }
    /**
	 * Insert the new key value pairs in the respective lead node location
	 *
	 * @param key
	 *            the key to be inserted in the leaf node
	 * @param value
	 *            the value to be inserted in the leaf node
     * @param node
     *            the node where the key value pair is to be inserted
	 */
    public void insertLeafNode(int key, double value, Node node){
        int index = searchInNode(node, key);
        node.keys.add(index, key);
        node.values.add(index, value);
        node.numOfKeys += 1;
    }


    /**
	 * Search exact key location in any node: binary search. Returns existing key index or the next key index.
	 *
	 * @param key
	 *            the key to be searched in the node
	 * @param node
	 *            the node where the key is to be searched
     * @return the index at which the key exists or should exist
	 */
    public int searchInNode(Node node, int key){
        List<Integer> keyList = node.keys;
        int low = 0;
        int high = keyList.size()-1;
        int index = -1;

        if(key < keyList.get(0)){
            return 0;
        }
        if(key >= keyList.get(high)){
            return keyList.size();
        }
        while(low<=high){
            int mid = (low) + (high - low)/2;
            if (key < keyList.get(mid) && key >= keyList.get(mid - 1)) {
				index = mid;
				break;
			} // Following conditions follow normal Binary Search
			else if (key >= keyList.get(mid)) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
        }
        return index;
    }

    /**
	 * If leaf node overflows during insert, this function is called where is splits the leaf node and makes the middle node as parent.
	 *
	 * @param node
	 *            the node that overflows and needs to be split
	 */
    public void splitLeafNode(Node node){

        //index to be split at
        int splitIndex = m/2;

        Node middleParent = new Node();
        Node rightSplit = new Node();

        //Insert keys, values to new leaf node
       if(node.parent!=null){
    }
        rightSplit.keys.addAll(node.keys.subList(splitIndex, node.keys.size()));
        rightSplit.values.addAll(node.values.subList(splitIndex, node.values.size()));

        //Split existing leaf node
        node.keys = node.keys.subList(0, splitIndex);
        node.values = node.values.subList(0, splitIndex);

        rightSplit.parent = middleParent;
        rightSplit.flag = 1;

        //create new parent internal node
        middleParent.keys.add(rightSplit.keys.get(0));
        middleParent.children.add(rightSplit);
        boolean leafChild = true;
        middleParent.flag = 0;
        
        splitInternalNode(middleParent, node, node.parent, leafChild);
    }

    /**
	 * Merges the parent of the new split child node into an existing internal node.
     * If internal node overflows during insert, this function is called where is splits the internal node and makes the middle node as parent.
	 *
	 * @param toBeInsertedNode
	 *            the new node to be inserted, parent of the split child nodes
     * @param childNode
     *            the right child of the toBeInsertedNode
     * @param currentNode
     *            the existing node where the toBeInsertedNode has to be merged
     * @param   leafChild
     *            stores true if the immediate child is a leaf node 
	 */
    public void splitInternalNode(Node toBeInsertedNode, Node childNode, Node currentNode, boolean leafChild){
        
        //Spliting root and creating now new root
        if(currentNode == null){
            root = toBeInsertedNode;
            int childIndex = searchInNode(toBeInsertedNode, childNode.keys.get(0));
            toBeInsertedNode.children.add(childIndex, childNode);
            childNode.parent = toBeInsertedNode;

            //If immediate split child is a leaf node
            if(leafChild){
                if(childIndex == 0){
                    toBeInsertedNode.children.get(1).previous = toBeInsertedNode.children.get(0);
                    toBeInsertedNode.children.get(0).next = toBeInsertedNode.children.get(1);
                }
                else{
                    toBeInsertedNode.children.get(childIndex-1).next = toBeInsertedNode.children.get(childIndex);
                    toBeInsertedNode.children.get(childIndex).previous = toBeInsertedNode.children.get(childIndex-1);

                    toBeInsertedNode.children.get(childIndex+1).previous = toBeInsertedNode.children.get(childIndex);
                    toBeInsertedNode.children.get(childIndex).next = toBeInsertedNode.children.get(childIndex+1);
                }
            }
        }

        //If an internal node is to be split and merged
        else{
            merge(currentNode, toBeInsertedNode);
            
            //If internal node is full split the node
            if(currentNode.keys.size() == m){
                int splitIndex = m/2;

                Node middleParent = new Node();
                Node rightSplit = new Node();

                //create the right split and split the keys
                rightSplit.keys.addAll(currentNode.keys.subList(splitIndex+1, currentNode.keys.size()));
                middleParent.keys.add(currentNode.keys.get(splitIndex));
                currentNode.keys = currentNode.keys.subList(0, splitIndex);

                //assign right split as child of middle split
                rightSplit.parent = middleParent;
                middleParent.children.add(rightSplit);
                middleParent.flag = 0;

                //split children list
                List<Node> rightChildren = new ArrayList<>();
                List<Node> existingChildren = currentNode.children;
                
                //find the nodes split to right child and left child
                int lastCurrChild = existingChildren.size();
                for(int i = existingChildren.size()-1; i>= 0; i--){
                    List<Integer> keysList = existingChildren.get(i).keys;

                    if(middleParent.keys.get(0) > keysList.get(0)){
                        break;
                    }
                    else{
                        existingChildren.get(i).parent = rightSplit;
                        rightChildren.add(0, existingChildren.get(i));
                        lastCurrChild--;
                    }
                }
                rightSplit.children = rightChildren;
                currentNode.children = currentNode.children.subList(0, lastCurrChild);
                currentNode.keys = currentNode.keys.subList(0, splitIndex);

                //recursive call to split parent
                splitInternalNode(middleParent, currentNode, currentNode.parent, false);
            }
        }
    }

    /**
	 * When any new node is to be merged with an exsiting node. 
     * If the childrent are leaf nodes, resets the previous and next nodes in the doubly linked leaf nodes.
	 *
	 * @param existingNode
	 *            the node that accepts the new node and merged into
     * @param newNode
     *            the node that is merged into the existing node
	 */
    public void merge(Node existingNode, Node newNode){

        int keyOfNewNode = newNode.keys.get(0);
        Node childOfNewNode = newNode.children.get(0);

        //search index and add new internal node
        int indexOfInsert = searchInNode(existingNode, keyOfNewNode);
        int childIndex = indexOfInsert;
        if(keyOfNewNode <= childOfNewNode.keys.get(0)){
            childIndex += 1;
        }

        childOfNewNode.parent = existingNode;
        existingNode.keys.add(indexOfInsert, keyOfNewNode);
        existingNode.children.add(childIndex, childOfNewNode);

        //update children DLL
        if(existingNode.children.get(0).flag == 1){

            //if first element is inserted
            if(existingNode.children.size()-1 != childIndex && existingNode.children.get(childIndex + 1).previous == null){
                existingNode.children.get(childIndex).next = existingNode.children.get(childIndex + 1);
                existingNode.children.get(childIndex+1).previous = existingNode.children.get(childIndex);
            }
            
            //if last element is inserted
            else if(childIndex != 0 && existingNode.children.get(childIndex-1).next == null){
        
                existingNode.children.get(childIndex-1).next = existingNode.children.get(childIndex);
                existingNode.children.get(childIndex).previous = existingNode.children.get(childIndex-1);
            }
            //anything in between
            else{
                existingNode.children.get(childIndex).next = existingNode.children.get(childIndex - 1).next;
                existingNode.children.get(childIndex).next.previous = existingNode.children.get(childIndex);

                existingNode.children.get(childIndex-1).next = existingNode.children.get(childIndex);
                existingNode.children.get(childIndex).previous = existingNode.children.get(childIndex - 1);
            }
        }
    }


    /**
	 * Search the B+ tree for the key and returns corresponding value from the tree. 
     * If the key does not exist, returns null.
	 * @param key
	 *            the key to be searched in the tree
	 */
    public void search(int key){
        double value = Double.MIN_VALUE;
        Node current = root;
        //traverse till external node reached
        while(current.flag != 1){
            int index = searchInNode(current, key);
            current = current.children.get(searchInNode(current, key));
        }

        //search key and return value
        List<Integer> keyList = current.keys;
        for(int i=0;i<keyList.size();i++){
            if(keyList.get(i) == key){
                value = current.values.get(i);
                break;
            }
        }
        if(value == Double.MIN_VALUE){
            System.out.println("Null");
        }
        else{
            System.out.println(value);
        }
    }

    //Search within range and returns list of nodes
    /**
	 * Search the B+ tree for the range of keys taken as input and returns all the values of the in-between keys from the tree. 
     * If the keys do not exist, returns null.
	 * @param keyStart
	 *            the start key of the range to be searched in the tree
     * @param keyEnd
     *            the end key of the range to be searched in the tree
	 */
    public void search(int keyStart, int keyEnd){
        List<Double> allValues = new ArrayList<>();
        Node current = root;

        //traverse till external node reached that contains start key
        while(current.flag != 1){
            current = current.children.get(searchInNode(current, keyStart));
        }

        /* Checks for all the external nodes starting from the beginning key in the range 
        till either the last key in the range or end of external nodes list */
        boolean keyEndReach = false;
        while(current != null && !keyEndReach){
            for(int i=0;i<current.keys.size();i++){
                if(current.keys.get(i) >= keyStart && current.keys.get(i) <= keyEnd){
                    allValues.add(current.values.get(i));
                }
                if(current.keys.get(i) > keyEnd){
                    keyEndReach = true;
                    break;
                }
            }
            current = current.next;
        }
        if(allValues.size()==0){
            System.out.println("Null");
        }
        else{
            for(int i=0;i<allValues.size();i++){
                System.out.print(allValues.get(i)+",");
            }
            System.out.println();
        }
    }

    /**
	 * Search the B+ tree for the key and deletes corresponding key-value pair from the tree. 
     * If the key does not exist, just returns.
	 * @param key
	 *            the key whose key-value pair to be deleted in the tree
	 */
    public void delete(int key){
        Node curr = new Node();
        int i = 0;                      
        curr = root;
        Node parent = null;

        //traverse path in tree to leaf node
        while(curr.flag == 0){
            i = searchInNode(curr, key);
            parent = curr;
            curr = curr.children.get(i);
            curr.parent = parent;
        }
        
        //Find index to delete the leaf node at
        int deleteIndex = searchInNode(curr, key);
        deleteIndex = deleteIndex==0?0:deleteIndex-1;

        //If key does not exist
        if(curr.keys.get(deleteIndex) != key){
            return;
        }

        //delete key from leaf and check for root
        curr.keys.remove(deleteIndex);
        curr.values.remove(deleteIndex);
        if(root.flag == 1 && root.keys.size()==0){
            root = null;
            return;
        }

        //if leaf node is deficient with no keys left, balance the tree
        if(curr.keys.size() == 0){
    
            //if previous sibling exists, has more than one pairs and has the same parent, borrow from previous sibling
            if(curr.previous!= null && curr.previous.keys.size() > 1 && curr.previous.parent == curr.parent){
                Node prev = curr.previous;
                
                //add to current leaf
                curr.keys.add(prev.keys.get(prev.keys.size()-1));
                curr.values.add(prev.values.get(prev.values.size()-1));

                //update parent keys
                curr.parent.keys.set(i-1, curr.keys.get(0));

                //remove from previous sibling pairs
                prev.keys.remove(prev.keys.size()-1);
                prev.values.remove(prev.keys.size()-1);
            }

            //if next sibling exists, has more than one pairs and has the same parent, borrow from next sibling
            else if(curr.next!=null && curr.next.keys.size() > 1 && curr.next.parent == curr.parent){
                Node nextSib = curr.next;

                //add to current leaf
                curr.keys.add(nextSib.keys.get(0));
                curr.values.add(nextSib.values.get(0));

                //remove from next sibling
                nextSib.keys.remove(0);
                nextSib.values.remove(0);

                //update parent keys
                curr.parent.keys.set(i, nextSib.keys.get(0));
            }

            //if children cannot be taken from siblings, merge two siblings
            else{
                //check for children of root
                if(root.children.get(0).flag==1 && curr.parent.keys.size()==0){
                    if(root.children.get(0).keys.size()==0 && root.children.get(1).keys.size()==1){
                        root = root.children.get(1);
                    }
                    else{
                        root = root.children.get(0);
                    }
                    return;
                }
                //merge two leaf nodes by calling merge leaf siblings method and check for root
                int removedKey = mergeLeafSibling(curr, i); 
                if(removedKey == Integer.MIN_VALUE){
                    return;
                }
 
                //if internal nodes are deficient, calls merge Internal to balance tree
               if(curr.parent.keys.size() == 0){
                    curr = curr.parent;
                    int indexOfNewCurrent = searchInNode(curr.parent, removedKey);
                    mergeInternalNodes(curr, indexOfNewCurrent);
               }
            }
        }
    }

    /**
	 * If a leaf node has an underflow after the delete, it merges the leafnode with a sibling. 
     * @param current
     *            the leaf node to be merged
	 * @param index
	 *            the index at which the leaf node to be merged exists
	 */
    public int mergeLeafSibling(Node current, int index){
        int key = -1; 
        //If first leaf node to be merged
        if(index == 0){
            key = current.parent.keys.remove(0);
            current.next.previous = current.previous;
            if(current.previous!=null){
                current.previous.next = current.next;
            }
        }

        //If not the first leaf nodes to be merged
        else{
            key = current.parent.keys.remove(index - 1);
            current.previous.next = current.next;
            if(current.next!=null){
                current.next.previous = current.previous;
            }
        }
    
        //checks the root condition
        if(root.children.get(0).flag==1 && root.keys.size()==0){
            if(root.children.get(0).keys.size()==0 && root.children.get(1).keys.size()==1){
                root = root.children.get(1);
            }
            else{
                root = root.children.get(0);
            }
            return Integer.MIN_VALUE;
        }
        current.parent.children.remove(index);
        return key;
    }

    /**
	 * If a internal node has an underflow after the delete, it merges the internal node with a sibling. 
     * @param current
     *            the internal node to be merged
	 * @param index
	 *            the index at which the internal node to be merged exists
	 */
    public void mergeInternalNodes(Node current, int index){

        Node parentNode = current.parent;

       //if left sibling has more than one keys 
        int removedKey = 0;
        if(index!=0 && parentNode.children.get(index-1).keys.size() > 1){
            int lastKey = parentNode.children.get(index-1).keys.size()-1;
            
            //move the in between key to the current and move left sibling key to parent
            current.keys.add(parentNode.keys.get(index-1));
            parentNode.keys.set(index-1, parentNode.children.get(index-1).keys.get(lastKey));
            parentNode.children.get(index-1).keys.remove(lastKey);

            //move the left sibling last child as current sibling first child
            current.children.add(0, parentNode.children.get(index-1).children.get(lastKey+1));
            parentNode.children.get(index-1).children.remove(lastKey+1);
        }

        //if right sibling has more than one keys
        else if(index != parentNode.children.size()-1 && parentNode.children.get(index+1).keys.size() > 1){

            //move inbetween parent key to the current and move right sibling key to the parent
            current.keys.add(parentNode.keys.get(index));
            parentNode.keys.set(index, parentNode.children.get(index+1).keys.get(0));
            parentNode.children.get(index+1).keys.remove(0);

            //move the right sibling first child to the current first child
            current.children.add(parentNode.children.get(index+1).children.get(0));
            parentNode.children.get(index+1).children.remove(0);
        }

        //if siblings have size 1, merge with parent
        else{
            
            //If index to be searched is the first node
            if(index == 0){
                
                parentNode.children.get(index+1).keys.add(0,parentNode.keys.get(index));
                parentNode.children.get(index+1).children.addAll(0, current.children);
                current.children.get(0).parent = parentNode.children.get(index+1);
                removedKey = parentNode.keys.remove(index);
                parentNode.children.remove(index);
            }

            //any other sibling except the first
            else{
                int key = parentNode.keys.get(index-1);    
                parentNode.children.get(index-1).keys.add(key);
                parentNode.children.get(index-1).children.addAll(current.children);
                current.children.get(0).parent = parentNode.children.get(index-1);
                removedKey = parentNode.keys.remove(index-1);
                parentNode.children.remove(index);
            }

            //reached root check for root
            if(parentNode.parent == null){
                if(parentNode.keys.size()==0){
                this.root = parentNode.children.get(0);
                }
                else{
                    this.root = parentNode;
                }
                return;
            }
            if(parentNode != null && parentNode.parent == null){
                this.root = parentNode;
            }
            if(parentNode.parent.keys.size()!=0){
                int parentIndex = searchInNode(parentNode.parent, removedKey);
                if(parentNode.keys.size() == 0){
                    mergeInternalNodes(parentNode, parentIndex);
                }
            }    
        }
    }
}

