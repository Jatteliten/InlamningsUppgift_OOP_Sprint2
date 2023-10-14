import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class RegisterPerson {

    private static final String CUSTOMERS_FILE_PATH = "src/Paying customers";
    private static final String WORKOUT_SHEET = "src/Workout sheet";
    private static final String INVALID_NUMBER = "invalid number";
    private static final String INPUT_IS_EMPTY = "input is empty";
    private static final String QUIT = "quit";
    private boolean programIsRunning = true;
    private Scanner scan;
    public boolean test = false;

    void run(){
        ArrayList<Person> customers = createCustomersFromFile(CUSTOMERS_FILE_PATH);

        while(programIsRunning) {
            String input = enterNameOrSocialSecurityNumber(null);
            if (!input.equals(INVALID_NUMBER) && !input.equals(INPUT_IS_EMPTY)) {
                boolean customer = checkIfPersonIsCustomer(input, customers);
                boolean payingCustomer = false;
                Person person = null;

                if(customer){
                    for(Person p: customers){
                        if(input.equalsIgnoreCase(p.getName()) ||
                                input.equals(p.getSocialSecurityNumber())){
                            person = p;
                            break;
                        }
                    }
                    if(person != null && !checkIfPersonIsReturningCustomer(person)){
                        payingCustomer = true;
                        createWorkoutForPayingCustomers(WORKOUT_SHEET, person);
                    }
                }
                if(programIsRunning) {
                    System.out.println(print(input, customer, payingCustomer));
                }
            }
        }
    }

    public String formatDateToString (LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return date.format(formatter);
    }

    public Person createCustomer(String[] customerFromFile){
        return new Person(customerFromFile[0], customerFromFile[1], customerFromFile[2]);
    }

    public ArrayList<Person> createCustomersFromFile(String filePath){
        String reader;
        ArrayList<Person> payingCustomersTemp = new ArrayList<>();
        String[] customerSocialSecurityNumberAndName;
        String[] customerInformation = new String[3];

        try(BufferedReader bf = new BufferedReader(new FileReader(filePath))){
            while((reader = bf.readLine()) != null){
                customerSocialSecurityNumberAndName = reader.trim().split(",");
                customerInformation[0] = customerSocialSecurityNumberAndName[0];
                customerInformation[1] = customerSocialSecurityNumberAndName[1].trim();
                customerInformation[2] = bf.readLine();
                Person p = createCustomer(customerInformation);
                payingCustomersTemp.add(p);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown error occurred");
            e.printStackTrace();
        }
        return payingCustomersTemp;
    }

    public boolean checkIfPersonIsCustomer(String nameOrSocialSecurityNumber, ArrayList<Person> payingCustomers){
        boolean check = false;
        for(Person p: payingCustomers){
            if(nameOrSocialSecurityNumber.equalsIgnoreCase(p.getName())
                    || nameOrSocialSecurityNumber.equals(p.getSocialSecurityNumber())){
                check = true;
                break;
            }
        }
        return check;
    }

    public boolean checkIfPersonIsReturningCustomer(Person customer){
        LocalDate today = LocalDate.now();
        String todayString = formatDateToString(today);

        if (checkYearOfString(customer.getMemberShipPaidDate()) == (checkYearOfString(todayString) - 1)
                && checkMonthOfString(customer.getMemberShipPaidDate()) < checkMonthOfString(todayString)){
            return true;
        }else {
            return (checkYearOfString(customer.getMemberShipPaidDate()) <= (checkYearOfString(todayString) - 2));
        }
    }

    public int checkYearOfString(String date){
        return Integer.parseInt(date.substring(0, 4));
    }

    public int checkMonthOfString(String date){
        return Integer.parseInt(date.substring(5, 7));
    }

    public boolean checkIfInputIsOnlyNumbers(String input){
        return input.matches("\\d+");
    }

    public boolean checkIfSocialSecurityInputIsLongEnough(String socialSecurityNumber){
        return socialSecurityNumber.length() == 10;
    }

    public String enterNameOrSocialSecurityNumber(String testString){
        String input;

        if(!test) {
            scan = new Scanner(System.in);
            System.out.println("Please enter the name or SSN (12 numbers) of the person who just entered " +
                    "\n[Type 'quit' to exit program]: ");
        }else{
            scan = new Scanner(testString);
        }

        input = scan.nextLine();

        if(input.equalsIgnoreCase(QUIT)){
            System.out.println("Good bye!");
            programIsRunning = false;
        } else {
            input = validateInput(input);
        }

        return input;
    }

    public String validateInput(String input){
        if (input.isEmpty()){
            if(!test) {
                System.out.println("Person or SSN field cannot be empty");
            }
            return INPUT_IS_EMPTY;
        } else if(checkIfInputIsOnlyNumbers(input)){
            if(!checkIfSocialSecurityInputIsLongEnough(input)){
                if(!test) {
                    System.out.println("The SSN you have entered is not long enough");
                }
                return INVALID_NUMBER;
            }
        }
        return input;
    }

    public String print(String input, boolean currentCustomer, boolean payingCustomer){
        if(!currentCustomer){
            return input + " is not a customer";
        }else{
            if(!payingCustomer){
                return input + " is a customer, but has not paid their fee";
            }else{
                return input + " is a paying customer";
            }
        }
    }

    public void createWorkoutForPayingCustomers(String workOutFilePath, Person person){
        try {
            File file = new File(workOutFilePath);

            if (!file.exists()) {
                file.createNewFile();
                try (BufferedWriter firstLineWriter = new BufferedWriter(new FileWriter(workOutFilePath))) {
                    firstLineWriter.write("Workout sheet:");
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Error while creating file: " + workOutFilePath);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(workOutFilePath, true))) {
                writer.write("\n" + person.getSocialSecurityNumber() + ", " + person.getName() +
                        "\n" + formatDateToString(LocalDate.now()));
            }
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Error while handling file: " + workOutFilePath);
        }
    }

}