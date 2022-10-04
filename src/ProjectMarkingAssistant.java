import java.io.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ProjectMarkingAssistant {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Scanner fileIn;

        ArrayList<Project> projects = new ArrayList<>();

        int i = 0;
        System.out.println("Please enter your file name:");
        String fileName = input.nextLine();

        try {
            fileIn = new Scanner(new FileReader(fileName));
            System.out.println("File read successfully.");

            while (fileIn.hasNextLine()){
                String content = fileIn.nextLine();
                if (i == 0){
                    Project.adjustCriteria(content);
                    i++;
                }else{
                    projects.add(new Project(content));
                }
            }


            boolean isExit = false;

            //Body of our program
            do{
                //Prints project table and menu, then takes menu input
                print(projects);
                printMenu();
                int choice = takeMenuInput();

                //Navigates to relative function
                switch (choice){
                    case 1:
                        enterProjectMarks(projects,takeStudentID());
                        break;
                    case 2:
                        createNewProject(projects);
                        break;
                    case 3:
                        deleteProject(projects);
                        break;
                    case 4:
                        save(projects,fileName);
                        isExit = true;
                        break;
                    case 5:
                        isExit = true;
                        break;
                }
            }while(!isExit);


            fileIn.close();
        }catch (FileNotFoundException e){
            System.out.println("The system can not find specified the file named: \"" + fileName + "\"");
        }
    }


    //Printing current state of marking table
    private static void print(ArrayList<Project> projects){
        ArrayList<String> criteria = Project.getCriteriaNames();
        ArrayList<Integer> weight = Project.getCriteriaWeight();

        //Divide from previous prints
        System.out.print("=========================================================================================");
        System.out.print("========================================================================================");
        System.out.println();

        //Printing first row
        System.out.printf("> %-18s ","Student ID");
        System.out.printf("| %-18s ","Student Name");
        for (int i = 0; i < criteria.size(); i++){
            System.out.printf("| %-18s ",criteria.get(i) + " (" + weight.get(i) + ")");
        }
        System.out.printf("| %-5s","Total");
        System.out.println();

        //Printing Projects
        for (Project project : projects) {
            ArrayList<String> marks = project.getMarks();
            System.out.printf("> %-19s",project.getStudentID());
            System.out.printf("| %-19s",project.getStudentName());
            for (String mark : marks) {
                System.out.printf("| %-19s",mark);
            }
            System.out.printf("| %-4s",project.getTotal());
            System.out.println();
        }
        System.out.print("=========================================================================================");
        System.out.print("========================================================================================");
        System.out.println();
    }


    //Print Menu
    private static void printMenu(){
        System.out.println("1.Enter project marks.");
        System.out.println("2.Add new project.");
        System.out.println("3.Delete project.");
        System.out.println("4.Save and Exit");
        System.out.println("5.Exit without saving.");
    }


    //Taking and controlling menu choice input
    private static int takeMenuInput(){
        Scanner in = new Scanner(System.in);
        boolean isValid;
        int choice;

        do {
            choice = in.nextInt();
            isValid = controlMenuInput(choice);
            if (!isValid) System.out.println("You entered invalid input, please try again.");
        }while (!isValid);

        return choice;
    }

    private static boolean controlMenuInput(int choice){
        if (choice == 1 || choice == 2 || choice == 3 || choice == 4 || choice == 5){
            return true;
        }else{
            return false;
        }
    }


    //Takes student ID from console
    private static String takeStudentID(){
        Scanner in = new Scanner(System.in);

        System.out.println("Enter student ID:");
        return in.nextLine();
    }

    //Takes student name from console
    private static String takeStudentName(){
        Scanner in = new Scanner(System.in);

        System.out.println("Enter student name:");
        return in.nextLine();
    }


    //Enter marks to an existing project
    private static void enterProjectMarks(ArrayList<Project> projects, String stuID){
        Scanner in = new Scanner(System.in);

        //Storage for index of particular student
        int index = -1;

        //Find if particular student exist in table
        for (Project project : projects) {
            if (project.getStudentID().equalsIgnoreCase(stuID)){
                index = projects.indexOf(project);
                break;
            }
        }

        //If student found, execute; else return to menu
        if (index >= 0){
            for (int i = 0; i < Project.getCriteriaNames().size(); i++){
                do {
                    System.out.printf("%s 0-%d",Project.getCriteriaNames().get(i),Project.getCriteriaWeight().get(i));
                    System.out.println();
                    int mark = in.nextInt();
                    if (!controlMark(mark,Project.getCriteriaWeight().get(i))){

                        //Inform user and take input again
                        System.out.println("Invalid input, try again.");
                    }else{

                        //Set marks and leave the loop
                        projects.get(index).getMarks().set(i, Integer.toString(mark));
                        break;
                    }
                }while(true);
            }

            //Calculate new total
            projects.get(index).calculateTotal();

            //Print total to screen
            System.out.println("TOTAL: " + projects.get(index).getTotal());
            System.out.println();
        }else {
            System.out.println("===Student ID cannot found. Please press Enter to return to the Main Menu. ===");
            in.nextLine();
        }

    }


    //Controls if mark value is valid
    private static boolean controlMark(int mark, int maxLim){
        if (0 <= mark && mark <= maxLim) return true;
        else return false;
    }


    //Create a new project
    private static void createNewProject(ArrayList<Project> projects){
        Scanner in = new Scanner(System.in);
        String stuID;
        boolean isValid;

        //Check for the student ID if it exists
        do {
            isValid = true;
            stuID = takeStudentID();
            for (Project project : projects){
                if (project.getStudentID().equalsIgnoreCase(stuID) || !controlStudentID(stuID)) {
                    System.out.println("Id exists or in different format, please try again.");
                    isValid = false;
                    break;
                }
            }
        }while(!isValid);

        String stuName = takeStudentName();

        //Create a new empty project with ID and name
        projects.add(new Project(stuID.toUpperCase(),stuName));

        //If user wants to enter marks, navigate to enterProjectMarks method
        System.out.println("Do you want to mark the project as well? (Y/N)");
        String ans = in.nextLine();
        if (ans.equalsIgnoreCase("y")){
            enterProjectMarks(projects,stuID);
        }
    }


    //Checks if the student ID is correct format
    private static boolean controlStudentID(String stuID){
        if (Pattern.matches("[pfPF][0-9]{6,7}",stuID)){
            return true;
        }else {
            return false;
        }
    }


    //Deletes existing project
    private static void deleteProject(ArrayList<Project> projects){

        String stuID = takeStudentID();
        ListIterator<Project> projectIterator = projects.listIterator();

        while (projectIterator.hasNext()){
            Project tmp = projectIterator.next();
            if (tmp.getStudentID().equalsIgnoreCase(stuID)){
                projectIterator.remove();
                System.out.println("Project deleted successfully.");
                break;
            }
        }
    }


    //Save current state of marking table
    private static void save(ArrayList<Project> projects, String filename){
        PrintWriter pw;
        ArrayList<String> formattedTable = new ArrayList<>();

        //Add first row to ArrayList
        formattedTable.add(formatInfo());

        //Add projects separately to ArrayList
        for (Project project : projects) {
            formattedTable.add(formatProject(project));
        }

        try {
            pw = new PrintWriter(new FileWriter(filename));

            for (String str : formattedTable) {
                pw.println(str);
            }

            pw.close();
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }

    }

    //Formats info row to String
    private static String formatInfo(){

        String formattedStr = "Student ID,Student Name,";
        for (int i = 0; i < Project.getCriteriaNames().size(); i++) {
            formattedStr += Project.getCriteriaNames().get(i) + " (" + Project.getCriteriaWeight().get(i) + "),";
        }
        formattedStr += "Total";
        return formattedStr;
    }

    //Formats particular project to string
    private static String formatProject(Project project){

        String formattedProject = project.getStudentID() + "," + project.getStudentName() + ",";
        for (String mark : project.getMarks()) {
            if (mark.equals("-")){
                formattedProject += ",";
            }else{
                formattedProject += mark + ",";
            }
        }
        formattedProject += project.getTotal();

        return formattedProject;
    }
}
