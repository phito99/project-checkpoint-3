/**
 * Represents a simple student class.
 * <p>
 * You do not need to change this class.
 */

public class Student {

    long studentId;
    long recordId;
    int age;
    String studentName;
    String major;
    String level;

    public Student(long studentId, int age, String studentName, String major, String level, long recordId) {
        this.studentId = studentId;
        this.age = age;
        this.studentName = studentName;
        this.major = major;
        this.level = level;
        this.recordId = recordId;
    }

    /**
     * Function to convert a Student object to a string that can be inserted into
     * student.csv database
     * 
     * @return comma delimited String of Student attributes
     */
    public String toCSV() {
        String output = "";
        String dl = ",";
        output = "\n" + this.studentId + dl + this.studentName + dl + this.major + dl + this.level + dl + this.age
                + dl + this.recordId;
        return output;
    }
}
