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
        return -1;
    }

    BTree insert(Student student) {
        /**
         * TODO:
         * Implement this function to insert in the B+Tree.
         * Also, insert in student.csv after inserting in B+Tree.
         */
        int maxCapacity = this.t * 2; // will use this to compare to this.keys.length
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
        // first case, leaf node has space
        if (current.keys.length < maxCapacity) {
            long[] newKeys = new long[2 * t - 1];
            long[] newVals = new long[2 * t - 1];
            int idx = 0;
            for (int i = 0; i < current.keys.length; i++) {
                if (student.recordId < current.keys[i]) {
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
            // TODO implement the splitting insert logic
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
}
