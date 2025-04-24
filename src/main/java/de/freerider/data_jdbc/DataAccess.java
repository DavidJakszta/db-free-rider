package de.freerider.data_jdbc;

import java.util.Map;
import java.util.Optional;

import de.freerider.datamodel.Customer;
import de.freerider.datamodel.Reservation;


/**
 * Public data access interface with methods defining SQL operations (queries,
 * executions, updates) in the database.
 * 
 * The singleton object implementing that interface is sometimes also called
 * "Data Access Object" or "DAO".
 * 
 * @author sgra64
 */
public interface DataAccess {

    /**
     * Run query that returns the number of Customers in the database:
     * - query: SELECT COUNT(ID) FROM CUSTOMER;
     * - returns number extracted from ResultSet.
     * 
     * @return number of Customer records in the database.
     */
    long countCustomers();


    /**
     * Run query that returns all Customers in the database.
     * - query: SELECT * FROM CUSTOMER;
     * - returns Customer objects created from ResultSet rows.
     * 
     * @return all Customers in the database.
     */
    Iterable<Customer> findAllCustomers();


    /**
     * Run query that returns one Customers with a given id.
     * - query: SELECT * FROM CUSTOMER WHERE ID = 10;
     * - returns Customer object created from ResultSet row.
     * 
     * @param id Customer id (WHERE ID = ?id)
     * @return Optional with Customer or empty if not found.
     */
    Optional<Customer> findCustomerById(long id);


    /**
     * Run query that returns all Customers with matching id in ids.
     * - query: SELECT * FROM CUSTOMER WHERE ID IN (10, 20, 30000, 40);
     * - returns Customer objects created from ResultSet rows.
     * 
     * @param ids Customer ids (WHERE IN (?ids))
     * @return Customers with matching ids.
     */
    Iterable<Customer> findAllCustomersById(Iterable<Long> ids);


    /**
     * Run query that returns all reservations held by a customer.
     * This is a JOIN-query between Reservation and Customer:
     * - query:
     *     SELECT RESERVATION.* FROM CUSTOMER
     *     JOIN RESERVATION ON RESERVATION.CUSTOMER_ID = CUSTOMER.ID
     *     WHERE CUSTOMER.ID = ?"
     * 
     * @param customer_id id of owning Customer.
     * @return Reservations with matching customer_id.
     */
    Iterable<Reservation> findReservationsByCustomerId(long customer_id);


    /**
     * Generic method to return number of elements in Iterable<T>.
     * 
     * @param <T> Generic type of objects contained in Iterable.
     * @param iter Iterable with objects of type T.
     * @return number of elements.
     */
    <T> long count(Iterable<T> iter);


    /**
     * Attempt to INSERT new record into CUSTOMER table from attributes
     * provided by name-value pairs, e.g.:
     * <pre>
     * [
     *   "id": 1,
     *   "name": "Meyer, Eric",
     *   "contact": "eme22@gmail.com",
     *   "status", "Active"
     * ]
     * </pre>
     * If customer data could be inserted into the database, a Customer
     * object is returned. An exception is thrown otherwise with error
     * code: 400 bad request (invalid attributes), 409 conflict (id exists).
     * 
     * @param map name-value pairs of Customer data.
     * @return created Customer object.
     * @throws DataAccessException with error code (400 bad request, 409 conflict).
     */
    Customer createCustomer(Map<String, Object> map) throws DataAccessException;


    /**
     * Attempt to UPDATE existing record into CUSTOMER table from attributes
     * provided by name-value pairs, e.g.:
     * <pre>
     * [
     *   "id": 1,                               <-- must be present
     *   "contact": "pojo388@supermail.com",    <-- updated data element
     * ]
     * </pre>
     * The update Customer object is returned with success. An exception is
     * thrown otherwise with error code: 400 bad request (invalid attributes),
     * 404 not found (id not found).
     * 
     * @param map name-value pairs of Customer data.
     * @return true if customer was updated sucessfully.
     * @throws DataAccessException with error code (400 bad request, 404 not found).
     */
    boolean updateCustomer(Map<String, Object> map) throws DataAccessException;


    /**
     * Delete Customer record with id from CUSTOMER table. An exception is
     * thrown with error code: 404 not found (id not found), 409 conflict
     * (foreign key violation).
     * 
     * @param id Customer id.
     * @return true if customer was deleted sucessfully.
     * @throws DataAccessException with error code (404 not found, 409 conflict).
     */
    boolean deleteCustomer(long id) throws DataAccessException;

}
