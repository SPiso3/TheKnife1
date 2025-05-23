module it.uninsubria.common {
    exports it.uninsubria.dto;
    exports it.uninsubria.services;
    exports it.uninsubria.exceptions;

    requires java.rmi;
    requires java.sql;  // For Remote interfaces
}