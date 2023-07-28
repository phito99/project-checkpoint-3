import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application.
 */
public class BTreeMain {

    public static void main(String[] args) {

        /** Read the input file -- input.txt */
        Scanner scan = null;
        try {
            scan = new Scanner(new File("src/input.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        /** Read the minimum degree of B+Tree first */

        int degree = scan.nextInt();

        BTree bTree = new BTree(degree);

        /** Reading the database student.csv into B+Tree Node */
        List<Student> studentsDB = getStudents();

        for (Student s : studentsDB) {
            bTree.insert(s);
        }

        /** Start reading the operations now from input file */
        try {
            while (scan.hasNextLine()) {
                Scanner s2 = new Scanner(scan.nextLine());

                while (s2.hasNext()) {

                    String operation = s2.next();

                    switch (operation) {
                        case "insert": {

                            long studentId = Long.parseLong(s2.next());
                            String studentName = s2.next() + " " + s2.next();
                            String major = s2.next();
                            String level = s2.next();
                            int age = Integer.parseInt(s2.next());
                            /** TODO: Write a logic to generate recordID */
                            long recordID = generateNextId(bTree);

                            Student s = new Student(studentId, age, studentName, major, level, recordID);
                            bTree.insert(s);

                            break;
                        }
                        case "delete": {
                            long studentId = Long.parseLong(s2.next());
                            boolean result = bTree.delete(studentId);
                            if (result)
                                System.out.println("Student deleted successfully.");
                            else
                                System.out.println("Student deletion failed.");

                            break;
                        }
                        case "search": {
                            long studentId = Long.parseLong(s2.next());
                            long recordID = bTree.search(studentId);
                            if (recordID != -1)
                                System.out.println("Student exists in the database at " + recordID);
                            else
                                System.out.println("Student does not exist.");
                            break;
                        }
                        case "print": {
                            List<Long> listOfRecordID = new ArrayList<>();
                            listOfRecordID = bTree.print();
                            System.out.println("List of recordIDs in B+Tree " + listOfRecordID.toString());
                        }
                        default:
                            System.out.println("Wrong Operation");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Student> getStudents() {

        List<Student> studentList = new ArrayList<>();
        try {
            // setup classes to read in the csv file
            FileReader fReader = new FileReader("student.csv");
            BufferedReader bReader = new BufferedReader(fReader);
            String line = "";

            // loop through the file and break out the data fields, then create Student
            // objects
            while ((line = bReader.readLine()) != null) {
                String[] pieces = line.split(",");
                Student student = new Student(
                        (long) Long.valueOf(pieces[0]),
                        (int) Integer.valueOf(pieces[1]),
                        pieces[2],
                        pieces[3],
                        pieces[4],
                        (long) Long.valueOf(pieces[5]));
                studentList.add(student);
            }
            bReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return studentList;
    }

    public static long generateNextId(BTree bTree) {
        long output = 0;
        BTreeNode currNode;
        // TODO implement search to find max record id and increment by 1 and return it
        // as output

        // If root has not been defined, return 1
        // TODO implement getter for root and update to getRoot
        if (bTree.root == null)
            output = 1;
        else {
            currNode = bTree.root;

            // while current node is not a leaf change the current node to the branch from
            // the largest key
            while (!currNode.leaf) {

                for (int i = currNode.keys.length - 1; i >= 0; i--) {
                    if (currNode.keys[i] != 0) {
                        currNode = currNode.children[i + 1];
                        break;
                    }
                    // else if(i == 0 && currNode.children[i+1] is not defined)
                    // currNode=currNode.children[0]
                }
            }

            // find the largest value and set output=value+1
            for (k = currNode.values.length - 1; k >= 0; k--) {
                if (currNode.values[k] != 0) {
                    output = currNode.values[k] + 1;
                    break;
                }
            }

        }
        return output;
    }
}
