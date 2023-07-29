import java.util.ArrayList;
import java.util.List;

/**
 * B+Tree Structure
 * Key - StudentId
 * Leaf Node should contain [ key,recordId ]
 */
class BTree {

    /**
     * Pointer to the root node.
     */
    private BTreeNode root;
    /**
     * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
     **/
    private int t;

    BTree(int t) {
        this.root = null;
        this.t = t;
    }

    long search(long studentId) {
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

    BTree insert(Student student) {
        /**
         * TODO:
         * Implement this function to insert in the B+Tree.
         * Also, insert in student.csv after inserting in B+Tree.
         */
        int maxCapacity = this.t * 2; // will use this to compare to this.n
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
            current = current.children[target];

        }
        // now need to perform the insert on the leaf node we have identified
        int idx = 0;
        boolean checkParent = false; // variable to determine if need to evaluate the parent inner node

        // first case, leaf node has space
        if (current.n < maxCapacity) {
            long[] newKeys = new long[2 * t - 1];
            long[] newVals = new long[2 * t - 1];

            for (int i = 0; i < current.keys.length; i++) {
                if (student.studentId < current.keys[i]) {
                    idx = i;
                }
            }

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

            int increment = 0; // used to help write existing "overflowing" data into new arrays
            // store the existing keys and values to populate into nodes alongside the
            // update
            long[] bufferKey = current.keys;
            long[] bufferVal = current.values;
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
            }
            // now update the new node, need to adjust the index of where values are set
            // from the buffer
            for (int i = (maxCapacity / 2); i < maxCapacity; i++) {
                if (i == idx) {
                    newNode.keys[i - (maxCapacity / 2)] = student.studentId;
                    newNode.values[i - (maxCapacity / 2)] = student.recordId;
                    increment = 1;
                } else {
                    newNode.keys[i - (maxCapacity / 2)] = bufferKey[i - increment];
                    newNode.values[i - (maxCapacity / 2)] = bufferVal[i - increment];
                }
            }
            // now determine if parent nodes need to be corrected
            if (current.parent.n == maxCapacity) {
                checkParent = true;
            }

            // update the parent node if there is no overflow
            if (!checkParent) {
                long pushedId = newNode.keys[0]; // node to be copied up
                idx = 0; // reset to find new area to insert
                for (int i = 0; i < current.parent.n; i++) {
                    if (pushedId > current.parent.keys[i]) {
                        idx++;
                    }
                }
                for (int i = current.parent.keys.length; i >= idx; i--) {
                    current.parent.keys[i] = current.parent.keys[i - 1]; // make space for new key
                    current.parent.children[i + 1] = current.parent.children[i]; // make space for new child node
                }
                current.parent.keys[idx] = pushedId;
                current.parent.children[idx] = current;
                current.parent.children[idx + 1] = newNode;
            }

            while (checkParent) {

            }
        }

        return this;
    }

    boolean delete(long studentId) {
        /**
         * TODO:
         * Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */
        return true;
    }

    List<Long> print() {

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
                System.out.println(current.values[i]);
            }
            current = current.next;
        } while (current != null);

        return listOfRecordID;
    }

    public BTreeNode getRoot() {
        return this.getRoot();
    }
}
