package ua.edu.ukma.validator;

public record Violation(String fieldName, String cause) {
        @Override
        public String toString() {
            return "Field: "  + fieldName + ", Violation = " + cause;
        }
}