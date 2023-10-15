import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class RegisterPerson {

    private static final String FILE_PATH_CUSTOMERS = "src/Paying customers.txt.txt";
    private static final String FILE_PATH_WORKOUT_SHEET = "src/Workout sheet.txt.txt";
    private static final String INVALID_NUMBER = "invalid number";
    private static final String INPUT_IS_EMPTY = "input is empty";
    private static final String QUIT = "quit";
    private boolean programIsRunning = true;
    private Scanner scan;
    public boolean test = false;

    /**
     * Main method to run the program
     */
    void run() {
        ArrayList<Person> customers = createCustomerListFromFile(FILE_PATH_CUSTOMERS);

        while (programIsRunning) {
            String input = enterNameOrSocialSecurityNumber(null);

            if (!input.equals(INVALID_NUMBER) && !input.equals(INPUT_IS_EMPTY)) {
                boolean isCustomer = checkIfPersonIsCustomer(input, customers);
                boolean isPayingCustomer = false;
                Person person = null;

                if (isCustomer) {
                    for (Person p : customers) {
                        if (input.equalsIgnoreCase(p.getName()) ||
                                input.equals(p.getSocialSecurityNumber())) {
                            person = p;
                            break;
                        }
                    }
                    if (person != null && checkIfPersonIsPayingCustomer(person)) {
                        isPayingCustomer = true;
                        addWorkoutInFileForPayingCustomers(FILE_PATH_WORKOUT_SHEET, person);
                    }
                }
                if (programIsRunning) {
                    System.out.println(printIfPersonIsCustomer(input, isCustomer, isPayingCustomer));
                }
            }
        }
    }

    /**
     * Formats a given LocalDate object as a string.
     *
     * @param date The LocalDate object to format.
     * @return A formatted string representation of the date.
     */
    public String formatDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    /**
     * Parses a formatted date string into a LocalDate object.
     *
     * @param date The formatted date string.
     * @return A LocalDate object representing the parsed date.
     */
    public LocalDate parseDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    /**
     * Create a Person object from an array of customer information.
     *
     * @param customerInfo An array containing customer information. {SSN, name, memberShipPaidDate}.
     * @return A Person object created from the information.
     */
    public Person createCustomer(String[] customerInfo) {
        return new Person(customerInfo[0], customerInfo[1], parseDateFromString(customerInfo[2]));
    }

    /**
     * Reads customer information from a file and creates a list of Person objects.
     *
     * @param filePath The path to the file containing customer information.
     * @return A list of Person objects created from the file data.
     */
    public ArrayList<Person> createCustomerListFromFile(String filePath) {
        ArrayList<Person> payingCustomersTemp = new ArrayList<>();

        try (BufferedReader bf = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = bf.readLine()) != null) {
                String[] customerSocialSecurityNumberAndName = line.trim().split(",");
                String SSN = customerSocialSecurityNumberAndName[0].trim();
                String name = customerSocialSecurityNumberAndName[1].trim();
                String memberShipPaidDate = bf.readLine().trim();

                Person p = createCustomer(new String[]{SSN, name, memberShipPaidDate});
                payingCustomersTemp.add(p);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File '" + filePath + "' could not be found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Unknown error with file '" + filePath + "' occurred");
            e.printStackTrace();
        }
        return payingCustomersTemp;
    }

    /**
     * Checks if a given name or social security number corresponds to a customer in the list.
     *
     * @param nameOrSocialSecurityNumber The name or social security number to check.
     * @param payingCustomers A list of Person objects representing customers.
     * @return True if the name or SSN matches a customer; false otherwise.
     */
    public boolean checkIfPersonIsCustomer(String nameOrSocialSecurityNumber, ArrayList<Person> payingCustomers) {
        boolean check = false;
        for (Person p : payingCustomers) {
            if (nameOrSocialSecurityNumber.equalsIgnoreCase(p.getName())
                    || nameOrSocialSecurityNumber.equals(p.getSocialSecurityNumber())) {
                check = true;
                break;
            }
        }
        return check;
    }

    /**
     * Checks if a person is a paying customer based on their membership payment date.
     *
     * @param customer The Person object to check.
     * @return True if the customer is a paying customer; false otherwise.
     */
    public boolean checkIfPersonIsPayingCustomer(Person customer) {
        return (customer.getMemberShipPaidDate().isAfter(LocalDate.now().minusYears(1)));
    }

    /**
     * Checks if the input consists only of numeric characters.
     *
     * @param input The input string to check.
     * @return True if the input contains only numeric characters; false otherwise.
     */
    public boolean checkIfInputIsOnlyNumbers(String input) {
        return input.matches("\\d+");
    }

    /**
     * Checks if a social security number input has the correct length (10 characters).
     *
     * @param socialSecurityNumber The social security number to check.
     * @return True if the SSN has the correct length; false otherwise.
     */
    public boolean checkIfSocialSecurityInputIsLongEnough(String socialSecurityNumber) {
        return socialSecurityNumber.length() == 10;
    }

    /**
     * Reads a name or social security number from the user and performs validation.
     *
     * @param testString A test input string (for testing purposes) or null for user input.
     * @return The validated name or social security number or special constants (e.g., QUIT, INPUT_IS_EMPTY).
     */
    public String enterNameOrSocialSecurityNumber(String testString) {
        String input;

        if (!test) {
            scan = new Scanner(System.in);
            System.out.println("Please enter the name or SSN (10 numbers) of the person who just entered the gym." +
                    "\n[Type '" + QUIT + "' to exit program]: ");
        } else {
            scan = new Scanner(testString);
        }

        input = scan.nextLine();

        if (input.equalsIgnoreCase(QUIT)) {
            System.out.println("Good bye!");
            programIsRunning = false;
        } else {
            input = validateInput(input);
        }

        return input.trim();
    }

    /**
     * Validates user input, ensuring it is not empty or SSN is incorrectly formatted.
     *
     * @param input The input to validate.
     * @return The validated input or a constant indicating an error (e.g., INPUT_IS_EMPTY, INVALID_NUMBER).
     */
    public String validateInput(String input) {
        if (input.isEmpty()) {
            if (!test) {
                System.out.println("Person or SSN input cannot be empty");
            }
            return INPUT_IS_EMPTY;
        } else if (checkIfInputIsOnlyNumbers(input)) {
            if (!checkIfSocialSecurityInputIsLongEnough(input)) {
                if (!test) {
                    System.out.println("The SSN you have entered is not long enough. 10 numbers are required");
                }
                return INVALID_NUMBER;
            }
        }
        return input;
    }

    /**
     * Generates a message indicating the customer's status (e.g., not a customer, paying customer).
     *
     * @param input The name or SSN of the person.
     * @param isCurrentCustomer True if the person is a customer; false otherwise.
     * @param payingCustomer True if the person is a paying customer; false otherwise.
     * @return A message describing the customer's status.
     */
    public String printIfPersonIsCustomer(String input, boolean isCurrentCustomer, boolean payingCustomer) {
        if (!isCurrentCustomer) {
            return input + " is not a customer";
        } else {
            if (!payingCustomer) {
                return input + " is a customer, but has not paid their fee";
            } else {
                return input + " is a paying customer";
            }
        }
    }

    /**
     * Creates a file.
     *
     * @param filePath The path to where the file will be created
     */
    public void createFile(String filePath){
        Path path = Paths.get(filePath);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create the file " + filePath);
        }
    }

    /**
     * Creates a workout entry for a paying customer and appends it to the workout sheet file.
     *
     * @param workOutFilePath The path to the workout sheet file.
     * @param person The paying customer for whom the workout entry is created.
     */
    public void addWorkoutInFileForPayingCustomers(String workOutFilePath, Person person) {
        Path path = Paths.get(workOutFilePath);

        if (!Files.exists(path)){
        createFile(workOutFilePath);
            try (BufferedWriter firstLineWriter = Files.newBufferedWriter(path)) {
                firstLineWriter.write("Workout sheet:");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            writer.write("\n" + person.getSocialSecurityNumber() + ", " + person.getName() +
                    "\n" + formatDateToString(LocalDate.now()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while handling file: " + workOutFilePath);
        }

    }
}
