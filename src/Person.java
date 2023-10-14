import java.time.LocalDate;

public class Person {
String socialSecurityNumber;
String name;
LocalDate memberShipPaidDate;

    public Person(String socialSecurityNumber, String name, LocalDate memberShipPaidDate) {
        this.socialSecurityNumber = socialSecurityNumber;
        this.name = name;
        this.memberShipPaidDate = memberShipPaidDate;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getMemberShipPaidDate() {
        return memberShipPaidDate;
    }

    public void setMemberShipPaidDate(LocalDate memberShipPaidDate) {
        this.memberShipPaidDate = memberShipPaidDate;
    }
}