package de.freerider.data_jdbc;


public class DataAccessException extends Exception {

    public enum Code {
        BadRequest(400),
        NotFound(404),
        Conflict(409);

        public final int code;
        private Code(int code) { this.code=code; }
    };

    public final Code code;

    DataAccessException(Code code, String msg) {
        super(msg);
        this.code = code;
    }


    public static class BadRequest extends DataAccessException {
        BadRequest(String msg) {
            super(Code.BadRequest, msg);
        }
    }

    public static class NotFound extends DataAccessException {
        NotFound(String msg) { super(Code.NotFound, msg); }
    }

    public static class Conflict extends DataAccessException {
        Conflict(String msg) { super(Code.Conflict, msg); }
    }

}
