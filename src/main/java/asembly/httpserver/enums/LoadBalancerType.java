package asembly.httpserver.enums;

import java.util.Arrays;

public enum LoadBalancerType {
    ROUND_ROBIN,
    WEIGHTED;

    public static LoadBalancerType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("LoadBalancerType value is null");
        }
        try {
            return LoadBalancerType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown LoadBalancerType: " + value + ". Supported values: " +
                            Arrays.toString(LoadBalancerType.values())
            );
        }
    }

}
