import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * B+Tree Structure
 * Key - StudentId
 * Leaf Node should contain [ key,recordId ]
 */
public class BTree {

    /**
     * Pointer to the root node.
     */
    private BTreeNode root;
    /**
     * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
     **/
    private int t;

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    public long search(long studentId) {
        /**
         * TODO:
         * Implement this function to search in the B+Tree.
         * Return recordID for the given StudentID.
         * Otherwise, print out a message that the given studentId has not been found in
         * the table and return -1.
         */

        BTreeNode currNode;
        boolean found = false;

        // If root is null; tree is empty and return -1
        if (this.root == null) {
            System.out.println("This is an empty tree");
            return -1;
        }

        currNode = this.root;

        // while the current node is not a leaf loop through keys and change current
        // node to appropriate branch
        while (!currNode.leaf) {

            for (int i = 0; i < currNode.keys.length; i++) {
                if (studentId <= currNode.keys[i]) { // children[0] have values < keys[0]; keys[0] < children[1] < keys
                                                     // [1]... keys[2*t-1] < children[2*t]
                    currNode = currNode.children[i];
                    break;
                } else if (i == currNode.keys.length - 1) {
                    currNode = currNode.children[i + 1];
                }
            }
        }

        for (int k = 0; k < currNode.keys.length; k++) {
            if (studentId == currNode.keys[k]) {
                return currNode.values[k];
            }
        }

        System.out.println("Could not find student with that ID");
        return -1;
    }

    public BTree insert(Student student) {

        int maxCapacity = this.t * 2; // will use this to compare to this.n
        // setup from no root
        if (this.root == null) {
            this.root = new BTreeNode(this.t, true);
            this.root.n = 0;
        }
        // start at the root node of the tree and find insert place
        BTreeNode current = this.root;
        while (!current.leaf) {
            int target = 0;
            // this loop will set target to the index of the child node we need to move to
            for (int i = 0; i < current.keys.length; i++) {
                if (student.studentId > current.keys[i]) {
                    target++;
                }
            }
            target = (target - maxCapacity) + current.n;
            current = current.children[target];

        }
        // now need to perform the insert on the leaf node we have identified
        int idx = 0;
        boolean checkParent = false; // variable to determine if need to evaluate the parent inner node
        boolean createRoot = false; // variable to determine if a new root is needed

        // first case, leaf node has space
        if (current.n < maxCapacity) {
            long[] newKeys = new long[2 * t];
            long[] newVals = new long[2 * t];

            for (int i = 0; i < current.keys.length; i++) {
                if (student.studentId > current.keys[i]) {
                    idx++;
                }
            }
            // arrays are initialized to 0 so need to normalize
            idx = (idx - (maxCapacity)) + current.n;

            for (int i = 0; i < idx; i++) {
                newKeys[i] = current.keys[i];
                newVals[i] = current.values[i];
            }

            newKeys[idx] = student.studentId;
            newVals[idx] = student.recordId;

            for (int i = idx + 1; i < current.keys.length; i++) {
                newKeys[i] = current.keys[i - 1];
                newVals[i] = current.values[i - 1];
            }

            current.keys = newKeys;
            current.values = newVals;
            current.n++;
        } else {
            // second case where leaf is full and need to split

            // identify area the new student key fits
            for (int i = 0; i < current.keys.length; i++) {
                if (student.studentId > current.keys[i]) {
                    idx++;
                }
            }
            // prepare new leaf node this gives the new arrays needed
            BTreeNode newNode = new BTreeNode(current.t, true);
            newNode.n = (current.t + 1);
            newNode.parent = current.parent; // attach to the same parent, will need to update parent.children
            newNode.next = current.next;
            current.next = newNode;

            int increment = 0; // used to help write existing "overflowing" data into new arrays
            // store the existing keys and values to populate into nodes alongside the
            // update
            long[] bufferKey = current.keys.clone();
            long[] bufferVal = current.values.clone();
            current.n = current.t; // reduce the number of keys stored in the node to post split value

            for (int i = 0; i < (maxCapacity / 2); i++) {
                if (i == idx) {
                    current.keys[i] = student.studentId;
                    current.values[i] = student.recordId;
                    increment = 1;
                } else {
                    current.keys[i] = bufferKey[i - increment];
                    current.values[i] = bufferVal[i - increment];
                }
                current.keys[maxCapacity - 1 - i] = 0;
                current.values[maxCapacity - 1 - i] = 0;
            }
            // now update the new node, need to adjust the index of where values are set
            // from the buffer
            for (int i = (maxCapacity / 2); i < maxCapacity + 1; i++) {
                if (i == idx) {
                    newNode.keys[i - (maxCapacity / 2)] = student.studentId;
                    newNode.values[i - (maxCapacity / 2)] = student.recordId;
                    increment = 1;
                } else {
                    newNode.keys[i - (maxCapacity / 2)] = bufferKey[i - increment];
                    newNode.values[i - (maxCapacity / 2)] = bufferVal[i - increment];
                }
            }
            // now determine if parent nodes need to be corrected or created
            if (current.parent == null) {
                // create a new root node and reform the tree
                createRoot = true;
                this.root = new BTreeNode(current.t, false);
                current.parent = this.root;
                newNode.parent = this.root;
                this.root.children[0] = current;
                this.root.children[1] = newNode;
                this.root.keys[0] = newNode.keys[0];
                this.root.n = 1;
            } else if (current.parent.n == maxCapacity) {
                checkParent = true;
            }

            // update the parent node if there is no overflow and parent not root
            if (!checkParent && !createRoot) {
                long pushedId = newNode.keys[0]; // key to be copied up
                idx = 0; // reset to find new area to insert
                for (int i = 0; i < current.parent.n; i++) {
                    if (pushedId > current.parent.keys[i]) {
                        idx++;
                    }
                }

                // idx = (idx - (maxCapacity)) + current.parent.n; // normalize idx

                for (int i = (current.parent.keys.length - 1); i > idx; i--) {
                    current.parent.keys[i] = current.parent.keys[i - 1]; // make space for new key
                    current.parent.children[i + 1] = current.parent.children[i]; // make space for new child node
                }
                current.parent.keys[idx] = pushedId;
                current.parent.children[idx] = current;
                current.parent.children[idx + 1] = newNode;
            }
            // continue checking and fixing upwards if parent is at capacity
            BTreeNode existing = current;
            current = current.parent;
            long pushedId = newNode.keys[0];
            while (checkParent) {
                idx = 0;
                // array of keys with overflow to help with array manipulation
                ArrayList<Long> keyArrayList = new ArrayList<>();
                ArrayList<BTreeNode> childrenArrayList = new ArrayList<>();
                // find the index to insert the new key and populate keyArrayList
                for (int i = 0; i < current.n; i++) {
                    if (pushedId > current.keys[i]) {
                        idx++;
                    }
                    keyArrayList.add(i, current.keys[i]);
                }
                keyArrayList.add(idx, pushedId);

                int childIdx = 0;
                // update the arraylist of all the children nodes to be redistributed with split
                for (int i = 0; i < current.children.length; i++) {
                    if (newNode.keys[0] > current.children[i].keys[0]) {
                        childIdx++;
                    }
                    childrenArrayList.add(current.children[i]);
                }
                childrenArrayList.add(childIdx, newNode);

                BTreeNode newInner = new BTreeNode(current.t, false);
                newInner.n = (current.t);
                newInner.parent = current.parent; // attach to the same parent, will need to update parent.children
                // populate the old inner parent node with d keys
                for (int i = 0; i < current.t; i++) {
                    current.keys[i] = keyArrayList.get(i);
                }
                // populate the newly created inner node with d keys
                for (int i = t + 1; i < maxCapacity; i++) {
                    newInner.keys[i] = keyArrayList.get(i);
                }
                pushedId = keyArrayList.get(current.t); // get the key to push into the next level for next iteration

                // update the children nodes between the old and new inner nodes
                for (int i = 0; i < childrenArrayList.size(); i++) {
                    if (i < childrenArrayList.size() / 2) {
                        current.children[i] = childrenArrayList.get(i);
                    } else {
                        newInner.children[i - (childrenArrayList.size() / 2)] = childrenArrayList.get(i);
                    }
                }

                // now determine how to handle the new pushedId (no parent, not at 2d, at 2d)
                if (current.parent == null) {
                    BTreeNode newRoot = new BTreeNode(current.t, false);
                    newRoot.children[0] = existing;
                    newRoot.children[1] = newInner;
                    newRoot.keys[0] = pushedId;
                    newRoot.n = 1;
                    current.parent = newRoot;
                    newInner.parent = newRoot;
                    checkParent = false;
                } else if (current.parent.n < maxCapacity) {
                    ArrayList<Long> parentKeys = new ArrayList<>();
                    int target = 0;
                    for (int i = 0; i < current.parent.n; i++) {
                        if (pushedId > current.parent.keys[i]) {
                            target++;
                        }
                        parentKeys.add(current.parent.keys[i]);
                    }
                    parentKeys.add(target, pushedId);
                    for (int i = current.parent.children.length; i > target + 1; i--) {
                        current.parent.children[i] = current.parent.children[i - 1];
                    }
                    current.parent.children[target + 1] = newInner;
                    checkParent = false;
                } else {
                    newNode = newInner;
                    existing = current;
                    current = current.parent;
                }
            }
        }

        // write new student into Student.csv
        try {
            FileWriter fWriter = new FileWriter("Student.csv", true);
            fWriter.write(student.toCSV());
            fWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return this;
    }

    public boolean delete(long studentId) {

        int success;
        try{
            success = this.recurseDelete(this.root,this.root,studentId,0);
        } catch(Exception e){
            System.out.println(e);
            return false;
        }

        
        return true;
    }

    int recurseDelete(BTreeNode parent, BTreeNode currNode, long entry, int currNodePos){

        //System.out.println( "Let's see how deep the error is");
        //System.out.println(entry);
        //this.print();

        //Variable to pass position of child node to delete
        //-1 if there is no child to delete
        int delChild = -1;

        //Node at same level as currNode to use when deleting branches
        BTreeNode sibling = null;

        //Array position of sibling in parent.children
        int siblingNodePos = 0;

        //boolean to track if sibling can remove an entry
        boolean hasExtras = false;

        //boolean to track if the entry has been removed from the tree;
        boolean removed = false;

        //temp variables for interchanging values
        long moveKey;
        long moveVal;
        BTreeNode moveChild;

        if (!currNode.leaf){

            //Find the child node that has a range of keys that match entry
            for (int i = 0; i < currNode.n-1; i++){
                if (entry < currNode.keys[i]){
                    delChild = recurseDelete(currNode, currNode.children[i],entry,i);
                }
                else if ( i == currNode.n-1){
                    delChild = recurseDelete(currNode, currNode.children[i+1],entry,i+1);
                }
            }

            //if there is no child to delete return
            if (delChild == -1){
                return -1;
            }

            //if there is a child node that needs to be deleted
            else{

                //delete child from node
                currNode.children[delChild] = null;

                //move values in children[] and keys[] one index forward; make last index null or 0
                for (int j = delChild+1; j <= currNode.n; j++){
                    currNode.children[j-1] = currNode.children[j];
                    if (j == currNode.n){
                        currNode.children[j] = null;
                    }
                    if (j < currNode.n){
                        currNode.keys[j-1] = currNode.keys[j];
                    }
                    if (j == currNode.n-1){
                        currNode.keys[j] = 0;
                    }
                }
                currNode.n--;

                //if the node still meets minimum entry req, return
                if (currNode.n >= currNode.t){
                    return -1;
                }

                //if node does not have enough remaining keys; get a sibling node
                else{
                    if (currNodePos != 0){
                        siblingNodePos=currNodePos-1;
                        sibling = parent.children[currNodePos-1];
                        hasExtras = sibling.n - 1 >= parent.t;
                    }
                    if (currNodePos == 0 || (!hasExtras && currNodePos != parent.t * 2)){
                        siblingNodePos=currNodePos+1;
                        sibling = parent.children[currNodePos + 1];
                        hasExtras = sibling.n - 1 >= parent.t;
                    }
                    
                    //if sibling node can spare a key; move it to currNode
                    if(hasExtras){
                        
                        //insert key and child from back of sibling to front of currNode
                        if (siblingNodePos < currNodePos){
                            moveKey = sibling.keys[sibling.n-1];
                            moveChild = sibling.children[sibling.n];
                            sibling.keys[sibling.n-1]=0;
                            sibling.children[sibling.n]=null;
                            sibling.n--;

                            for(int j = currNode.n-1; j <= 0; j--){
                                currNode.children[j+1] = currNode.children[j];
                                if (j > 0){
                                    currNode.keys[j] = currNode.keys[j-1];
                                }
                                if(j == 0){
                                    currNode.children[j] = moveChild;
                                    currNode.keys[j] = moveKey;
                                }
                            }
                            currNode.n++;

                            //set parent key to first key in currNode
                            parent.keys[currNodePos] = currNode.keys[0];
                        }

                        //insert key and child from front of sibling to back of currNode
                        else{
                            moveKey = sibling.keys[0];
                            moveChild = sibling.children[0];
                            currNode.keys[currNode.n-1] = moveKey;
                            currNode.children[currNode.n] = moveChild;
                            currNode.n++;
                            //set parent key to second key in sibling
                            parent.keys[currNodePos] = sibling.keys[1];

                            for (int j = 1; j <= sibling.n; j++){
                                sibling.children[j-1] = sibling.children[j];
                                if (j == sibling.n){
                                    sibling.children[j] = null;
                                }
                                if (j < sibling.n){
                                    sibling.keys[j-1] = sibling.keys[j];
                                }
                                if (j == sibling.n-1){
                                    sibling.keys[j] = 0;
                                }
                            }
                            sibling.n--;

                        }
                        
                        parent.children[currNodePos] = currNode;
                        parent.children[siblingNodePos] = sibling;
                        return -1;
                    }

                    //if the sibling node would not meet minimum reqs; merge currNode and sibling
                    else{
                        BTreeNode[] childCombo = new BTreeNode[2 * t + 1];
                        long[] keysCombo = new long[2 * t];

                        if (siblingNodePos < currNodePos){
                            combineChildren(sibling.children,currNode.children,childCombo);
                            combineKeys(sibling.keys,currNode.keys,keysCombo);
                            sibling.children = childCombo;
                            sibling.keys = keysCombo;
                            sibling.n = sibling.n + currNode.n;

                            long tempKey = parent.keys[currNodePos];
                            parent.keys[currNodePos] = sibling.keys[sibling.n - 1];
                            sibling.keys[sibling.n-1] = tempKey;

                            parent.children[siblingNodePos] = sibling;
                            return currNodePos;
                        }
                        else{
                            combineChildren(currNode.children,sibling.children,childCombo);
                            combineKeys(currNode.keys,sibling.keys,keysCombo);
                            sibling.children = childCombo;
                            sibling.keys = keysCombo;
                            sibling.n = sibling.n + currNode.n;

                            long tempKey = parent.keys[siblingNodePos];
                            parent.keys[siblingNodePos] = sibling.keys[sibling.n - 1];
                            sibling.keys[sibling.n-1] = tempKey;

                            parent.children[currNodePos] = sibling;
                            return siblingNodePos;
                        }
                    }

                }

            }
        }
        else{

            for (int i = 0; i < currNode.n; i++){
                if(currNode.keys[i] == entry){
                    currNode.keys[i] = 0;
                    currNode.values[i] = 0;
                    removed = true;
                }
                if(removed){
                    if (i + 1 < currNode.n){
                        currNode.keys[i] = currNode.keys[i+1];
                        currNode.values[i] = currNode.keys[i+1];
                    }
                }
            }
            currNode.n--;

            //if node meets minimum reqs, return
            if (currNode.n-1 >= t){
                return -1;
            }

            //if leaf cannot spare an entry
            else{
                //get a sibling entry
                if (currNodePos != 0){
                    siblingNodePos=currNodePos-1;
                    sibling = parent.children[currNodePos-1];
                    hasExtras = sibling.n - 1 >= parent.t;
                }
                if ((currNodePos == 0 || !hasExtras) && currNodePos != parent.t * 2){
                    siblingNodePos=currNodePos+1;
                    sibling = parent.children[currNodePos + 1];
                    //System.out.println("Sibling Node Post: "+ siblingNodePos +"\nParent.t: "+parent.n);
                    //System.out.println(sibling);
                    hasExtras = sibling.n - 1 >= parent.t;
                }

                //if sibling still meets minimum reqs after dontating one value; donate value
                if (hasExtras){

                    //insert key and value from back of sibling to front of currNode
                    if (siblingNodePos < currNodePos){
                        moveKey = sibling.keys[sibling.n-1];
                        moveVal = sibling.values[sibling.n-1];
                        sibling.n--;

                        for(int j = currNode.n-1; j > 0; j--){

                            currNode.keys[j] = currNode.keys[j-1];
                            currNode.values[j] = currNode.values[j-1];

                            if(j == 0){
                                currNode.values[j] = moveVal;
                                currNode.keys[j] = moveKey;
                            }
                        }
                        currNode.n++;
                        //set parent key to first key in currNode
                        parent.keys[currNodePos] = currNode.keys[0];
                    }

                    //insert key and value from front of sibling to back of currNode
                    else{
                        moveKey = sibling.keys[0];
                        moveVal = sibling.values[0];

                        currNode.keys[currNode.n-1] = moveKey;
                        currNode.values[currNode.n-1] = moveVal;
                        currNode.n++;

                        //set parent key to second key in sibling
                        parent.keys[currNodePos] = sibling.keys[1];

                        for (int j = 1; j < sibling.n; j++){

                            sibling.keys[j-1] = sibling.keys[j];
                            sibling.values[j-1] = sibling.values[j];

                            if (j == sibling.n-1){
                                sibling.keys[j] = 0;
                            }
                        }
                        sibling.n--;

                    }

                    parent.children[currNodePos] = currNode;
                    parent.children[siblingNodePos] = sibling;
                    return -1;
                }

                //if sibling does not meet minimum reqs, merge nodes
                else{
                    long[] valsCombo = new long[2 * t];
                    long[] keysCombo = new long[2 * t];

                    if (siblingNodePos < currNodePos){
                        combineKeys(sibling.values,currNode.values,valsCombo);
                        combineKeys(sibling.keys,currNode.keys,keysCombo);
                        sibling.values = valsCombo;
                        sibling.keys = keysCombo;
                        sibling.n = sibling.n + currNode.n;

                        long tempKey = parent.keys[currNodePos];
                        parent.keys[currNodePos] = sibling.keys[sibling.n - 1];
                        sibling.keys[sibling.n-1] = tempKey;

                        parent.children[siblingNodePos] = sibling;
                        return currNodePos;
                    }
                    else{
                        combineKeys(currNode.values,sibling.values,valsCombo);
                        combineKeys(currNode.keys,sibling.keys,keysCombo);
                        sibling.values = valsCombo;
                        sibling.keys = keysCombo;
                        sibling.n = sibling.n + currNode.n;

                        long tempKey = parent.keys[siblingNodePos];
                        parent.keys[siblingNodePos] = sibling.keys[sibling.n - 1];
                        sibling.keys[sibling.n-1] = tempKey;

                        parent.children[currNodePos] = sibling;
                        return siblingNodePos;
                    }
                }

            }
        }

    }

    static void combineChildren(BTreeNode[] a, BTreeNode[] b, BTreeNode[] c){

        for (int i = 0; i < a.length; i++){
            c[i] = a[i];
        }
        for (int i = a.length; i < c.length; i++){
            c[i] = b[i];
        }

        return;
    }

    static void combineKeys(long[] a, long[] b, long[] c){

        for (int i = 0; i < a.length; i++){
            c[i] = a[i];
        }
        for (int i = a.length; i < c.length; i++){
            c[i] = b[i];
        }

        return;
    }

    public List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();

        BTreeNode current = this.root;
        // start at the root and perform a search for the left most (lowest) recordId
        while (!current.leaf) {
            current = current.children[0];
        }
        /*
         * once this loop is finished we have the left-most leaf, begin creating list
         * and printing values
         * traverse through the linked list of leaf nodes, the end node of the list will
         * have a null pointer to the next node
         */
        do {
            for (int i = 0; i < current.values.length; i++) {
                listOfRecordID.add(current.values[i]);
            }
            current = current.next;
        } while (current != null);

        return listOfRecordID;
    }

    public BTreeNode getRoot() {
        return this.getRoot();
    }
}
