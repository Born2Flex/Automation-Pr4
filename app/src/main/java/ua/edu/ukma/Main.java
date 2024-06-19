package ua.edu.ukma;

import ua.edu.ukma.validator.Validator;
import ua.edu.ukma.validator.Violation;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Runtime
        Validator validator = new Validator();
        Person validPerson = new Person("John", "john@gmail.com", LocalDate.of(1999, 1, 1));
        Person invalidPerson = new Person(null, "johnemail", LocalDate.of(2077, 1, 1));
        Person invalidPerson2 = new Person("Bob", "bob@gmail@", null);

        System.out.println("Valid " + validPerson);
        printViolations(validator.validate(validPerson));
        System.out.println();

        System.out.println("Invalid " + invalidPerson);
        printViolations(validator.validate(invalidPerson));
        System.out.println();

        System.out.println("Invalid " + invalidPerson2);
        printViolations(validator.validate(invalidPerson2));
        System.out.println();

        Person person = new PersonBuilder()
                .name("Paul")
                .email("macscskacmkackacm")
                .birthDate(LocalDate.now())
                .build();
        System.out.println("Created with Builder " + person);
        printViolations(validator.validate(person));
    }

    private static void printViolations(List<Violation> violations) {
        if (violations.isEmpty()) {
            System.out.println("No violations found");
            return;
        }
        for (Violation violation : violations) {
            System.out.println(violation);
        }
    }
}