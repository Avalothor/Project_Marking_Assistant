import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Project {
    private String studentID;
    private String studentName;

    private ArrayList<String> marks = new ArrayList<>();
    private int total;

    private static ArrayList<String> criteriaNames = new ArrayList<>();
    private static ArrayList<Integer> criteriaWeight = new ArrayList<>();

    //Constructors
    public Project(String content){
        String[] items = content.split(",");

        studentID = items[0].trim();
        studentName = items[1].trim();

        //if mark is not present store a "-" icon else store mark and add to the total
        for (int i = 0; i < criteriaNames.size(); i++){
            if (items[i+2].equals("")){
                marks.add("-");
            }else {
                marks.add(items[i+2]);
                total = total + Integer.parseInt(items[i+2]);
            }
        }
    }

    public Project(String studentID, String studentName){

        this.studentID = studentID;
        this.studentName = studentName;

        for (int i = 0; i < criteriaNames.size(); i++){
            marks.add("-");
        }

        this.total = 0;
    }


    //Getters
    public static ArrayList<String> getCriteriaNames() {
        return criteriaNames;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public ArrayList<String> getMarks() {
        return marks;
    }

    public int getTotal() {
        return total;
    }

    public static ArrayList<Integer> getCriteriaWeight() {
        return criteriaWeight;
    }



    public void calculateTotal(){
        total = 0;
        for (String mark : marks) {
            total += Integer.parseInt(mark);
        }
    }


    //Splitting marking criteria and controlling marking weight
    public static void adjustCriteria(String content){
        String[] items = content.split(",");

        //Separate marking names and marking weights
        for (int j = 2; j < items.length-1; j++){
            Pattern string = Pattern.compile("[a-zA-Z]+");
            Matcher name = string.matcher(items[j].trim());
            Pattern num = Pattern.compile("\\d+");
            Matcher weight = num.matcher(items[j].trim());

            //Add to related ArrayList
            while(name.find()) {
                criteriaNames.add(name.group());
                if (weight.find()){
                    criteriaWeight.add(Integer.parseInt(weight.group()));
                }
            }
        }

        //Control and adjust weight
        adjustWeight();
    }


    //Control and adjust weight
    public static void adjustWeight(){
        Scanner input = new Scanner(System.in);
        int total;

        do {
            total = 0;
            //Calculate total
            for (int weight : criteriaWeight) {
                total += weight;
            }

            //Take weights manually
            if (total != 100){
                System.out.println("Error on marking weight, please enter values manually:");
                criteriaWeight.clear();
                for (String name : criteriaNames) {
                    System.out.print(name + ": ");
                    criteriaWeight.add(Integer.parseInt(input.nextLine()));
                }
            }

        //Repeat until total is 100
        }while (total != 100);

    }
}
