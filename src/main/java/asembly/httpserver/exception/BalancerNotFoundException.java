package asembly.httpserver.exception;

import java.io.IOException;

public class BalancerNotFoundException extends IOException {
    public BalancerNotFoundException(String message) {
        super(message);
    }
}
