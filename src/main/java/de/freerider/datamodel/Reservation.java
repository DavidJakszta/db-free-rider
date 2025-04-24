package de.freerider.datamodel;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Entity class for Reservation.
 *
 * Schema in database:
 * +-------------+-------------------------------------+------+-----+---------+
 * | Field       | Type                                | Null | Key | Default |
 * +-------------+-------------------------------------+------+-----+---------+
 * | ID          | int                                 | NO   | PRI | NULL    |
 * | CUSTOMER_ID | int                                 | NO   | MUL | NULL    |
 * | VEHICLE_ID  | int                                 | NO   | MUL | NULL    |
 * | BEGIN       | datetime                            | YES  |     | NULL    |
 * | END         | datetime                            | YES  |     | NULL    |
 * | PICKUP      | varchar(48)                         | YES  |     | NULL    |
 * | DROPOFF     | varchar(48)                         | YES  |     | NULL    |
 * | STATUS      | enum('Inquired','InquiryConfirmed', | YES  |     | NULL    |
 * |             |   'Booked','Cancelled')             |      |     |         |
 * +-------------+-------------------------------------+------+-----+---------+
 *
 * Reservation date/times are stored as long values counted as msec since 1970-01-01.
 * Examples of date/times:
 *
 * 0:               1970-01-01 01:00:00
 * 1577833200000:   2020-01-01 00:00:00
 * 1893452399000:   2029-12-31 23:59:59
 * 9223372036854775000 (Long.MAX_VALUE): Year: 292,278,994 Aug, 17 (08-17), 08:12:55
 *
 * @author sgra64
 */
public final class Reservation {

    /**
     * Reservation attribute: id.
     */
    private final long id;

    /**
     * Reservation attribute: customer_id, reference to Customer owning the reservation.
     */
    private final long customer_id;

    /**
     * Reservation attribute: vehicle_id, reference to the reserved Vehicle.
     */
    private final long vehicle_id;

    /**
     * Reservation attribute: begin, datetime the reservation begins.
     */
    private long begin;

    /**
     * Reservation attribute: end, datetime the reservation ends.
     */
    private long end;

    /**
     * Reservation attribute: pickup, location the Vehicle is picked up.
     */
    private String pickup;

    /**
     * Reservation attribute: dropoff, location the Vehicle is returned.
     */
    private String dropoff;

    /**
     * Reservation attribute: status (Inquired, InquiryConfirmed, Booked, Cancelled).
     */
    private Status status;

    /**
     * The Lifecycle type Status and the status attribute of a Reservation object
     * implement the 3-stage business logic of making a reservation:
     *  //
     *  1st stage:
     *      Customer sends inquiry (Resveration object in state: Inquired). The
     *      reservation system probes wether the reservation can be fulfilled
     *      or not.
     *  //
     *  2nd stage: if the reservation can be fulfilled, the system records the
     *      Reservation object in the database in state: InquiryConfirmed for
     *      10 minutes (timeout) and returns the Reservation object with this
     *      state to the Customer.
     *      If the reservation request cannot be met, the system does not record
     *      the Reservation object in the database and returns it to the Customer
     *      with status: Cancelled.
     *  //
     *  3rd stage: if the Customer resubmitts a Resveration object within the
     *      timeout period in state: Inquiry-Confirmed, the Reservation held in
     *      the database becomes permanent transitioning to state: Booked.
     *      If the Customer resubmits the Reservation object with another status
     *      or does not resubmit within the timeout period, the Reservation
     *      object is removed from the database (if it was not in state Booked).
     *
     * A Reservation can be cancelled from any state and is removed from the
     * database, if the Reservation was not in a Booked state before. When
     * cancelled, Booked reservations remain in the database in state: Cancelled.
     *
     */
    public enum Status {

        /**
         * 1st stage: initial request looking up reservation is possible.
         */
        Inquired,

        /**
         * 2nd stage: holding a inquired reservation for 10 minutes (timeout).
         */
        InquiryConfirmed,

        /**
         * 3rd stage: Customer confirmed inquired reservation within timeout.
         */
        Booked,

        /**
         * Reservation cancelled from any other state.
         */
        Cancelled
    };

    /**
     * Date/Time format used by SQL for external representation, "yyyy-MM-dd HH:mm:ss".
     * Example for 28th Dec, 2022, 16:30: "2022-12-28 16:30:00"
     */
    public static final SimpleDateFormat reservationDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Lower bound of past reservation dates (all reservations must be later than: 01/01/2020).
     */
    private static final long reservationLowerBoundDateTime = 1577833200000L; // 2020-01-01 00:00:00

    /**
     * Upper bound of acceptable reservation dates ((all reservations must be before: 12/31/2029)
     */
    private static final long reservationUpperBoundDateTime = 1893452399000L; // 2029-12-31 23:59:59

    /**
     * Non-public constructor that prevents creating instances outside
     * this package. Instances can only be created through DataFactory.
     *
     * @param id unique identifier, PRIMARY KEY in database.
     * @param customer reference to Customer, FOREIGN KEY in database.
     * @param vehicle reference to Vehicle, FOREIGN KEY in database.
     * @param begin date/time the reservation begins.
     * @param end date/time the reservation ends.
     * @param pickup pickup location.
     * @param dropoff drop-off location.
     * @param status status of Reservation.
     * @throws IllegalArgumentException for illegal parameters.
     */
    Reservation(long id, long customer_id, long vehicle_id,
            String begin, String end, String pickup, String dropoff, String status) {
        if (id < 0)
            throw new IllegalArgumentException(String.format("id: %d, id < 0", id));
        if (customer_id < 0)
            throw new IllegalArgumentException(String.format("customer_id: %d, customer_id < 0", customer_id));
        if (vehicle_id < 0)
            throw new IllegalArgumentException(String.format("vehicle_id: %d, vehicle_id < 0", vehicle_id));
        //
        this.id = id;
        this.customer_id = customer_id;
        this.vehicle_id = vehicle_id;
        setBegin(dateTimeStrToLong(begin));
        setEnd(dateTimeStrToLong(end));
        setPickup(pickup);
        setDropoff(dropoff);
        setStatus(strToStatusEnum(status));
    }

    /**
     * Public id attribute getter.
     *
     * @return unique Reservation identifier, PRIMARY KEY in database.
     */
    public long getId() {
        return id;
    }

    /**
     * Public customer_id attribute getter.
     *
     * @return reference to Customer, FOREIGN KEY in database.
     */
    public long getCustomerId() {
        return customer_id;
    }

    /**
     * Public vehicle_id attribute getter.
     *
     * @return reference to Vehicle, FOREIGN KEY in database.
     */
    public long getVehicleId() {
        return vehicle_id;
    }

    /**
     * Public begin attribute getter.
     *
     * @return Reservation begin date/time.
     */
    public long getBegin() {
        return begin;
    }

    /**
     * Public begin attribute setter.
     *
     * @param begin Reservation begin date/time.
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal Reservation begin parameter.
     */
    public Reservation setBegin(long begin) {
        if (begin < reservationLowerBoundDateTime || begin > reservationUpperBoundDateTime)
            throw new IllegalArgumentException(
                    String.format("begin: %d, begin outside [lower_bound_DateTime, upper_bound_DateTime]", begin));
        //
        this.begin = begin;
        return this;
    }

    /**
     * Public end attribute getter.
     *
     * @return Reservation end date/time.
     */
    public long getEnd() {
        return end;
    }

    /**
     * Public end attribute setter.
     *
     * @param end Reservation end date/time.
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal Reservation end parameter.
     */
    public Reservation setEnd(long end) {
        if (begin < reservationLowerBoundDateTime || begin > reservationUpperBoundDateTime)
            throw new IllegalArgumentException(
                    String.format("end: %d, end outside [lower_bound_DateTime, upper_bound_DateTime]", end));
        //
        this.end = end;
        return this;
    }

    /**
     * Public pickup attribute getter.
     *
     * @return Pickup location.
     */
    public String getPickup() {
        return pickup;
    }

    /**
     * Public pickup attribute setter.
     *
     * @param pickup Pickup location.
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal name parameter.
     */
    public Reservation setPickup(String pickup) {
        if (pickup == null || pickup.length() == 0)
            throw new IllegalArgumentException("pickup is null or empty");
        //
        this.pickup = pickup;
        return this;
    }

    /**
     * Public dropoff attribute getter.
     *
     * @return Drop-off location.
     */
    public String getDropoff() {
        return dropoff;
    }

    /**
     * Public dropoff attribute setter.
     *
     * @param dropoff Drop-off location.
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal name parameter.
     */
    public Reservation setDropoff(String dropoff) {
        if (dropoff == null || dropoff.length() == 0)
            throw new IllegalArgumentException("dropoff is null or empty");
        //
        this.dropoff = dropoff;
        return this;
    }

    /**
     * Public status attribute getter.
     *
     * @return status of Reservation.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Public status attribute setter.
     *
     * @param status status of Reservation.
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal status parameter.
     */
    public Reservation setStatus(Status status) {
        if (status == null)
            throw new IllegalArgumentException("status is null");
        //
        this.status = status;
        return this;
    }

    /**
     * Convert datetime from String format into long time.
     * Example: {@code "2020-01-01 00:00:00"} to {@code 1577833200000}.
     *
     * @param datetime date and time as String.
     * @return long dateTime value.
     * @throws IllegalArgumentException for unparsable datetime String.
     */
    public static String dateTimeToStr(long datetime) {
        if (datetime < 0)
            throw new IllegalArgumentException("datetime is < 0>");
        //
        return reservationDateFormat.format(new Date(datetime));
    }

    /**
     * Convert datetime from String format into long time.
     * Example: {@code "2020-01-01 00:00:00"} to {@code 1577833200000}.
     *
     * @param datetime date and time as String.
     * @return long dateTime value.
     * @throws IllegalArgumentException for unparsable datetime String.
     */
    public static long dateTimeStrToLong(String datetime) {
        if (datetime == null)
            throw new IllegalArgumentException("datetime is null");
        //
        try {
            return reservationDateFormat.parse(datetime).getTime();
            //
        } catch (ParseException e) {
            throw new IllegalArgumentException("datetime ParseException, " + e.getMessage());
        }
    }

    /**
     * Convert status from String to enum value.
     * Example: {@code "Inquired"} to {@code Status.Inquired}.
     *
     * @param status status as String.
     * @return status as enum value.
     * @throws IllegalArgumentException for unparsable or not matching status String.
     */
    private Status strToStatusEnum(String status) {
        if (status == null)
            throw new IllegalArgumentException("status is null");
        //
        for (Status s : Status.values()) { // ignore cases
            if (s.name().compareToIgnoreCase(status) == 0) {
                return s;
            }
        }
        throw new IllegalArgumentException(String.format("can't parse Status from: \"%s\"", status));
    }
}
