package de.freerider.datamodel;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Public interface of a factory that creates objects for datamodel classes <T>.
 *
 * Factory create() methods return type Optional<T> that only contain objects
 * of type T when valid objects could be created from valid parameters.
 * Otherwise, the returned Optional is empty.
 *
 * @author sgra64
 */
public interface DataFactory {

    /**
     * Create new Customer object from parameters.
     *
     * @param id unique identifier, PRIMARY KEY in database.
     * @param name Customer name (as single String).
     * @param contacts Customer contact information.
     * @param status status of Customer (String must match Customer.Status enum).
     * @return Optional with object or empty when no object could be created from parameters.
     */
    Optional<Customer> createCustomer(long id, String name, String contacts, String status);

    /**
     * Create new Vehicle object from parameters.
     *
     * @param id unique identifier, PRIMARY KEY in database.
     * @param make brand name of Vehicle, e.g. "VW" or "Tesla"
     * @param model model name of Vehicle, e.g. "ID.4"
     * @param seats number of seats in Vehicle.
     * @param category category of Vehicle (String must match Vehicle.Category enum).
     * @param power power source of Vehicle (String must match Vehicle.Power enum).
     * @param status status of Vehicle (String must match Vehicle.Status enum).
     * @return Optional with object or empty when no object could be created from parameters.
     */
    Optional<Vehicle> createVehicle(
            long id, String make, String model, int seats,
            String category, String power, String status);

    /**
     * Create new Reservation object from parameters.
     *
     * @param id unique identifier, PRIMARY KEY in database.
     * @param customer reference to Customer, FOREIGN KEY relation in database.
     * @param vehicle reference to Vehicle, FOREIGN KEY relation in database.
     * @param begin date/time the reservation begins.
     * @param end date/time the reservation ends.
     * @param pickup pickup location.
     * @param dropoff drop-off location.
     * @param status status of Reservation (String must match Reservation.Status enum).
     * @return Optional with object or empty when no object could be created from parameters.
     */
    Optional<Reservation> createReservation(
            long id, long customer_id, long vehicle_id,
            String begin, String end, String pickup, String dropoff, String status);

    /**
     * Generic method to create list of objects of type <T> from list of arguments.
     * Invalid arguments are logged as errors and prevent object creation (dropped
     * from resulting list).
     *
     * Example use:
     * <pre>
     * final Object[][] customerArgs = {
     *     {1, "Meyer, Eric", "eme22@gmail.com", "Active"},
     *     {2, "Sommer, Tina", "030 22458 29425", "Active"},
     *     {3, "Schulze, Tim", "+49 171 2358124", "Active"},
     * };
     * Iterable<Customer> customers = buildObjects(new ArrayList<Customer>(),
     *     customerArgs,
     *     args -> dataFactory.createCustomer(  // create object from args[]
     *         dataFactory.toLong(args[0]),     // customer id
     *         (String)args[1],                 // name
     *         (String)args[2],                 // contacts
     *         (String)args[3]                  // status
     * ));
     * </pre>
     *
     * @param collector to collect created objects of type <T> (null will create collector).
     * @param args array of arguments passed in order to create(...) function.
     * @param creator lambda callout passing args to create each object.
     * @return created objects of type <T>.
     */
    <T> Iterable<T> create(
            List<T> collector, Object[][] args,
            Function<Object[], Optional<T>> creator);

}
