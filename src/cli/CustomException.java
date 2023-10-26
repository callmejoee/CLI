package cli;

public class CustomException extends Exception {
    public CustomException(String errorMsg) {
        super(errorMsg);
    }
}
