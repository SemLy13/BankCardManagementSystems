package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;

    private ErrorDetails error;

    public ErrorResponse() {}

    public ErrorResponse(boolean success, ErrorDetails error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {
        private boolean success;
        private ErrorDetails error;

        public ErrorResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public ErrorResponseBuilder error(ErrorDetails error) {
            this.error = error;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(success, error);
        }
    }

    public static class ErrorDetails {

        private String code;

        private String message;

        private String details;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime timestamp;

        private String path;

        private Map<String, String> validationErrors;

        public ErrorDetails() {}

        public ErrorDetails(String code, String message, String details, LocalDateTime timestamp, String path, Map<String, String> validationErrors) {
            this.code = code;
            this.message = message;
            this.details = details;
            this.timestamp = timestamp;
            this.path = path;
            this.validationErrors = validationErrors;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Map<String, String> getValidationErrors() {
            return validationErrors;
        }

        public void setValidationErrors(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
        }

        public static ErrorDetailsBuilder builder() {
            return new ErrorDetailsBuilder();
        }

        public static class ErrorDetailsBuilder {
            private String code;
            private String message;
            private String details;
            private LocalDateTime timestamp;
            private String path;
            private Map<String, String> validationErrors;

            public ErrorDetailsBuilder code(String code) {
                this.code = code;
                return this;
            }

            public ErrorDetailsBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ErrorDetailsBuilder details(String details) {
                this.details = details;
                return this;
            }

            public ErrorDetailsBuilder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public ErrorDetailsBuilder path(String path) {
                this.path = path;
                return this;
            }

            public ErrorDetailsBuilder validationErrors(Map<String, String> validationErrors) {
                this.validationErrors = validationErrors;
                return this;
            }

            public ErrorDetails build() {
                return new ErrorDetails(code, message, details, timestamp, path, validationErrors);
            }
        }
    }
}
