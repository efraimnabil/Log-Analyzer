package com.service_health_monitor_portal.log_analyzer;

import java.util.Objects;

public class Service {
    private static int counter = 0;
    private final int id;
    private String name;
    private int success;
    private int throttlingError;
    private int dependencyError;
    private int faultError;
    private int invalidInputError;

    public Service(String name, int success, int throttlingError, int dependencyError, int faultError,
            int invalidInputError) {
        counter++;
        this.id = counter;
        this.name = name;
        this.success = success;
        this.throttlingError = throttlingError;
        this.dependencyError = dependencyError;
        this.faultError = faultError;
        this.invalidInputError = invalidInputError;
    }

    public Service() {
        counter++;
        this.id = counter;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getSuccess() {
        return success;
    }
    public void setSuccess(int success) {
        this.success = success;
    }
    public int getThrottlingError() {
        return throttlingError;
    }
    public void setThrottlingError(int throttlingError) {
        this.throttlingError = throttlingError;
    }
    public int getDependencyError() {
        return dependencyError;
    }
    public void setDependencyError(int dependencyError) {
        this.dependencyError = dependencyError;
    }
    public int getFaultError() {
        return faultError;
    }
    public void setFaultError(int faultError) {
        this.faultError = faultError;
    }
    public int getInvalidInputError() {
        return invalidInputError;
    }
    public void setInvalidInputError(int invalidInputError) {
        this.invalidInputError = invalidInputError;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Service)) {
            return false;
        }
        Service service = (Service) o;
        return id == service.id && Objects.equals(name, service.name) && success == service.success
                && throttlingError == service.throttlingError && dependencyError == service.dependencyError
                && faultError == service.faultError && invalidInputError == service.invalidInputError;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, success, throttlingError, dependencyError, faultError, invalidInputError);
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", name='" + getName() + "'" + ", success='" + getSuccess() + "'"
                + ", throttlingError='" + getThrottlingError() + "'" + ", dependencyError='" + getDependencyError() + "'"
                + ", faultError='" + getFaultError() + "'" + ", invalidInputError='" + getInvalidInputError() + "'"
                + "}";
    }
}