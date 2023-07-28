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

        //If root is null; tree is empty and return -1
        if(this.root == null){
            System.out.println("This is an empty tree");
            return -1;
        }
        
        currNode = this.root;

        //while the current node is not a leaf loop through keys and change current node to appropriate branch
        while(!currNode.leaf){

            for(int i = 0; i < currNode.keys.length; i++){
                if(studentId <= currNode.keys[i])                //children[0] have values < keys[0]; keys[0] < children[1] < keys [1]... keys[2*t-1] < children[2*t]
                    currNode=currNode.children[i];
                    break;
                }
                else if(i == currNode.keys.length - 1){
                    currNode = currNode.children[i+1];
                }
            }
        }

        for(int k = 0; k < currNode.keys.length; k++){
            if(studentId == currNode.keys[k]){
                return currNode.values.[k];
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
