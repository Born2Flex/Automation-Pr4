package ua.edu.ukma;

import ua.edu.ukma.annotations.runtime.InPast;
import ua.edu.ukma.annotations.runtime.NotNull;
import ua.edu.ukma.annotations.runtime.ValidEmail;

import java.time.LocalDate;

public class Person {
    @NotNull
    private String name;
    @ValidEmail
    private String email;
    @InPast
    private LocalDate birthDate;

    public Person(String name, String email, LocalDate birthDate) {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public String toString() {
        return "Person: name = " + name + ", email = '" + email + '\'' + ", birthDate = " + birthDate;
    }
}
