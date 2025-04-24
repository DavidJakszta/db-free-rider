package de.freerider.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Non-public implementation class of DataFactory interface that is not visible
 * outside this package.
 *
 * Factory create() methods return type Optional<T> that only contain objects
 * of type T when valid objects could be created from valid parameters.
 * Otherwise, the returned Optional is empty.
 *
 * The singleton instance of this class is created by the Spring Container as
 * effect of the @Component annotation.
 *
 * References to DataFactory instance can be injected into constructors in other
 * classes or @Autowired to variables of type DataFactory.
 *
 * @author sgra64
 */
@Component
final class DataFactoryImpl implements DataFactory {

    /**
     * Local logger instance registered under class name.
     */
    private final Logger logger = LoggerFactory.getLogger(DataFactoryImpl.class);

    /**
     * Non-public constructor invoked by Spring.
     */
    DataFactoryImpl() {
        logger.info("DataFactoryImpl() constructor: instance created");
    }

    /**
     * Create new Customer object from parameters.
     *
     * @param id unique identifier, PRIMARY KEY in database.
     * @param name Customer name (as single String).
     * @param contacts Customer contact information.
     * @param status status of Customer (String must match Customer.Status enum).
     * @return Optional with object or empty when no object could be created from parameters.
     */
    @Override
    public Optional<Customer> createCustomer(long id, String name, String contacts, String status) {
        try {
            return Optional.of(
                    new Customer(id, name, contacts, Customer.Status.valueOf(status)));
            //
        } catch (IllegalArgumentException iax) {
            logger.error(String.format(
                    "Customer(id: %d), IllegalArgumentException: %s\nCustomer(id: %d), dropped",
                    id, iax.getMessage(), id));
        }
        return Optional.empty();
    }

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
    @Override
    public Optional<Vehicle> createVehicle(long id, String make, String model, int seats,
            String category, String power, String status) {
        try {
            return Optional.of(
                    new Vehicle(id, make, model, seats, category, power, status));
            //
        } catch (IllegalArgumentException iax) {
            logger.error(String.format(
                    "Vehicle(id: %d), IllegalArgumentException: %s\nVehicle(id: %d), dropped",
                    id, iax.getMessage(), id));
        }
        return Optional.empty();
    }

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
    @Override
    public Optional<Reservation> createReservation(long id, long customer_id, long vehicle_id,
            String begin, String end, String pickup, String dropoff, String status) {
        try {
            return Optional.of(
                    new Reservation(id, customer_id, vehicle_id, begin, end,
                            pickup, dropoff, status));
            //
        } catch (IllegalArgumentException iax) {
            logger.error(String.format(
                    "Reservation(id: %d), IllegalArgumentException: %s\nReservation(id: %d), dropped",
                    id, iax.getMessage(), id));
        }
        return Optional.empty();
    }

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
    @Override
    public <T> Iterable<T> create(List<T> collector, Object[][] args, Function<Object[], Optional<T>> creator) {
        final List<T> col = collector == null ? new ArrayList<T>() : collector;
        if (args != null && creator != null) {
            Arrays.stream(args)
                    .map(arg -> creator.apply(arg)) // -> returns Optional<Customer>
                    .filter(opt -> opt.isPresent()) // remove empty Optionals of failed object creations
                    .map(opt -> opt.get()) // unpack object from Optional
                    .collect(Collectors.toCollection(() -> col)); // collect resulting objects
        }
        return col;
    }
}
