package ua.edu.ukma.validator;

import ua.edu.ukma.annotations.runtime.InPast;
import ua.edu.ukma.annotations.runtime.NotNull;
import ua.edu.ukma.annotations.runtime.ValidEmail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    public List<Violation> validate(Object object) {
        List<Violation> violations = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
            for (Annotation annotation : declaredAnnotations) {
                try {
                    if (annotation.annotationType().equals(NotNull.class)) {
                        processNotNull(field, object, violations);
                    } else if (annotation.annotationType().equals(ValidEmail.class)) {
                        processEmail(field, object, violations);
                    } else if (annotation.annotationType().equals(InPast.class)) {
                        processInPast(field, object, violations);
                    }
                } catch (IllegalAccessException e) {
                    System.err.println("Field " + field.getName() + " is not accessible");
                    throw new RuntimeException(e);
                }
            }
        }
        return violations;
    }

    private void processNotNull(Field field, Object object, List<Violation> violations) throws IllegalAccessException {
        if (field.get(object) == null) {
            violations.add(new Violation(field.getName(), "Field value shouldn't be null"));
        }
    }

    private void processInPast(Field field, Object object, List<Violation> violations) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (!fieldType.equals(LocalDate.class)) {
            violations.add(new Violation(field.getName(), "Field type should be LocalDate.class"));
            return;
        }
        LocalDate date = (LocalDate) field.get(object);
        if (date == null) {
            violations.add(new Violation(field.getName(), "Date shouldn't be null"));
            return;
        }
        if (date.isAfter(LocalDate.now())) {
            violations.add(new Violation(field.getName(), "Date should be in the past"));
        }
    }

    private void processEmail(Field field, Object object, List<Violation> violations) throws IllegalAccessException {
        if (!field.getType().equals(String.class)) {
            violations.add(new Violation(field.getName(), "Field type should be String.class"));
            return;
        }
        String email = (String) field.get(object);
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            violations.add(new Violation(field.getName(), "Email should be valid"));
        }
    }
}