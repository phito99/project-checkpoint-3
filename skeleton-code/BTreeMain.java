import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
        long max = 0;
        try {
            // setup classes to read in the csv file
            FileReader fReader = new FileReader("skeleton-code/Student.csv");
            BufferedReader bReader = new BufferedReader(fReader);
            String line = "";

            // loop through the file and break out the data fields, then create Student
            // objects
            while ((line = bReader.readLine()) != null) {
                String[] pieces = line.split(",");
                Student student = new Student(
                        (long) Long.valueOf(pieces[0]),
                        (int) Integer.valueOf(pieces[4]),
                        pieces[1],
                        pieces[2],
                        pieces[3],
                        (long) Long.valueOf(pieces[5]));
                studentList.add(student);
                long rId = (long) Long.valueOf(pieces[5]);
                if (rId > max) {
                    max = rId;
                }

            }
            bReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // setup the convention to write the current recordID to disk for generateNextId
        // logic
        try {
            FileWriter fWriter = new FileWriter("recordID.txt");
            fWriter.write(String.valueOf(max));
            fWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return studentList;
    }

    public static long generateNextId(BTree bTree) {
       // variable declaration
       int ch;
       String stream = "";
       int recID;

       // check if File exists or not
       try
       {
            FileReader fReader = new FileReader("recordID.txt");
            // read from FileReader till the end of file
            while ((ch=fReader.read())!=-1)
                stream = stream + (char)ch;

            // close the file
            fReader.close();
       }
       catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

       recID = Integer.parseInt(stream);

       recID++;

       try{
        FileWriter fWriter = new FileWriter("recordID.txt");
        fWriter.write((int)(recID));
        fWriter.close();
       }
        catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return recID;
    }
}
