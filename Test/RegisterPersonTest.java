import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

class RegisterPersonTest {

    RegisterPerson rp = new RegisterPerson();
    String testCustomersFilePath = "Test/Paying customers Test.txt";
    String testWorkoutFilePath = "Test/Workout sheet Test.txt";
    Person p1 = new Person("9006161234", "Daniel Isaksson",
            LocalDate.of(2023, 6, 16));
    Person p2 = new Person("9403021234", "Sarah Wrengler",
            LocalDate.of(2022, 12, 2));
    Person p3 = new Person("7608081234", "Malin Isaksson",
            LocalDate.of(2021, 2, 2));
    ArrayList<Person> testList = new ArrayList<>(Arrays.asList(p1, p2, p3));


    @BeforeEach
    public void setup(){
        rp.test = true;
    }

    @Test
    void formatDateToStringTest() {
        String expected = "1990-06-16";
        LocalDate specificDate = LocalDate.of(1990, 6, 16);
        String actual = rp.formatDateToString(specificDate);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void parseDateFromStringTest(){
        LocalDate expected = LocalDate.of(1990, 6, 16);
        LocalDate actual = rp.parseDateFromString("1990-06-16");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createCustomerTest() {
        Person expected = p1;
        String[] actualPersonArray = new String []{"9006161234", "Daniel Isaksson", "2023-06-16"};
        Person actual = rp.createCustomer(actualPersonArray);

        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getSocialSecurityNumber(), actual.getSocialSecurityNumber());
        Assertions.assertEquals(expected.getMemberShipPaidDate(), actual.getMemberShipPaidDate());
    }

    @Test
    void createCustomersFromFileTest(){
        ArrayList<Person> expected = testList;
        ArrayList<Person> actual = rp.createCustomersFromFile(testCustomersFilePath);

        for(int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i).getName(), actual.get(i).getName());
            Assertions.assertEquals(expected.get(i).getSocialSecurityNumber(), actual.get(i).getSocialSecurityNumber());
            Assertions.assertEquals(expected.get(i).getMemberShipPaidDate(), actual.get(i).getMemberShipPaidDate());
        }
    }

    @Test
    void checkIfPersonIsCustomerTest(){
        boolean expected = true;
        boolean actual = rp.checkIfPersonIsCustomer("Daniel Isaksson", testList);
        Assertions.assertEquals(expected, actual);

        actual = rp.checkIfPersonIsCustomer("9006161234", testList);
        Assertions.assertEquals(expected, actual);

        actual = rp.checkIfPersonIsCustomer("Henrik Isaksson", testList);
        Assertions.assertNotEquals(expected,actual);

        actual = rp.checkIfPersonIsCustomer("9203021234", testList);
        Assertions.assertNotEquals(expected, actual);
    }

    @Test
    void checkIfPersonIsPayingCustomerTest(){
        boolean expected = true;

        boolean actual = rp.checkIfPersonIsPayingCustomer(p1);
        if(p1.getMemberShipPaidDate().isAfter(LocalDate.now().minusYears(1))) {
            Assertions.assertEquals(expected, actual);
        }else{
            Assertions.assertNotEquals(expected, actual);
        }

        actual = rp.checkIfPersonIsPayingCustomer(p2);
        if(p2.getMemberShipPaidDate().isAfter(LocalDate.now().minusYears(1))) {
            Assertions.assertEquals(expected, actual);
        }else{
            Assertions.assertNotEquals(expected, actual);
        }

        actual = rp.checkIfPersonIsPayingCustomer(p3);
        if(p3.getMemberShipPaidDate().isAfter(LocalDate.now().minusYears(1))) {
            Assertions.assertEquals(expected, actual);
        }else{
            Assertions.assertNotEquals(expected, actual);
        }
    }

    @Test
    void checkIfInputIsOnlyNumbersTest(){
        boolean expected = true;
        boolean actual = rp.checkIfInputIsOnlyNumbers("123");
        Assertions.assertEquals(expected, actual);

        actual = rp.checkIfInputIsOnlyNumbers("hej");
        Assertions.assertNotEquals(expected, actual);
    }

    @Test
    void checkIfSocialSecurityInputIsLongEnoughTest(){
        boolean expected = true;
        boolean actual = rp.checkIfSocialSecurityInputIsLongEnough("9006161234");
        Assertions.assertEquals(expected, actual);

        actual = rp.checkIfSocialSecurityInputIsLongEnough("900616");
        Assertions.assertNotEquals(expected, actual);
    }

    @Test
    void enterNameOrSocialSecurityNumber(){
        String expected = "Daniel Isaksson";
        String actual = rp.enterNameOrSocialSecurityNumber("Daniel Isaksson");
        Assertions.assertEquals(expected, actual);

        expected = "9006161234";
        actual = rp.enterNameOrSocialSecurityNumber("9006161234");
        Assertions.assertEquals(expected, actual);

        expected = "invalid number";
        actual = rp.enterNameOrSocialSecurityNumber("900616");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void validateInput(){
        String expected = "expected";
        String actual = rp.validateInput("expected");
        Assertions.assertEquals(expected, actual);

        expected = "invalid number";
        actual = rp.validateInput("123");
        Assertions.assertEquals(expected, actual);

        expected = "input is empty";
        actual = rp.validateInput("");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void print(){
        String expected = "Daniel Isaksson is a paying customer";
        String actual = rp.print("Daniel Isaksson", true, true);
        Assertions.assertEquals(expected, actual);

        expected = "Daniel Isaksson is not a customer";
        actual = rp.print("Daniel Isaksson", false, false);
        Assertions.assertEquals(expected, actual);

        expected = "Daniel Isaksson is a customer, but has not paid their fee";
        actual = rp.print("Daniel Isaksson", true, false);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createFile() throws IOException {
        Path path = Paths.get("Test/New file");
        boolean expected = true;
        boolean actual;

        if(!Files.exists(path)){
            Files.delete(path);
        }
        actual = Files.exists(path);
        Assertions.assertNotEquals(expected,actual);

        rp.createFile(path.toString());
        actual = Files.exists(path);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createWorkoutForPayingCustomerTest() {
        String expected;
        String actual;
        StringBuilder expectedBuilder = new StringBuilder();

        try {
            Path path = Paths.get(testWorkoutFilePath);
            if(!Files.exists(path)){
                rp.createWorkoutForPayingCustomers(testWorkoutFilePath, p1);
            }

            expectedBuilder.append(new String(Files.readAllBytes(path)));
            expectedBuilder.append("\n9006161234, Daniel Isaksson\n").append(rp.formatDateToString(LocalDate.now()));
            expected = expectedBuilder.toString().trim();

            rp.createWorkoutForPayingCustomers(testWorkoutFilePath, p1);

            actual = new String(Files.readAllBytes(path)).trim();
            Assertions.assertEquals(expected, actual);
        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while reading from file: "+ testWorkoutFilePath);
        }

    }

}