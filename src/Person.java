public class Person {
String socialSecurityNumber;
String name;
String memberShipPaidDate;

    public Person(String socialSecurityNumber, String name, String memberShipPaidDate) {
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

    public String getMemberShipPaidDate() {
        return memberShipPaidDate;
    }

    public void setMemberShipPaidDate(String memberShipPaidDate) {
        this.memberShipPaidDate = memberShipPaidDate;
    }
}