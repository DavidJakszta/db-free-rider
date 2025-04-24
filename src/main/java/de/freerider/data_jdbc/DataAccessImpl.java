package de.freerider.data_jdbc;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freerider.datamodel.Customer;
import de.freerider.datamodel.DataFactory;
import de.freerider.datamodel.Reservation;


/**
 * Non-public implementation class or DataAccess interface.
 */
@Component
class DataAccessImpl implements DataAccess {

    /*
     * Logger instance for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DataAccessImpl.class);

    /**
     * Datafactory is a component that creates datamodel objects.
     */
    @Autowired
    private DataFactory dataFactory;

    /*
     * JdbcTemplate is the central class in the JDBC core package for SQL
     * database access.
     * - https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html
     * 
     * Examples:
     * - https://mkyong.com/spring-boot/spring-boot-jdbc-examples
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * Run query that returns the number of Customers in the database:
     * - query: SELECT COUNT(ID) FROM CUSTOMER;
     * - returns number extracted from ResultSet.
     * 
     * @return number of Customer records in the database.
     */
    @Override
    public long countCustomers() {
        //
        List<Object> result = jdbcTemplate.query(
            /*
             * Run SQL statement:
             */
            "SELECT COUNT(ID) FROM CUSTOMER",

            /*
             * Return ResultSet (rs) and extract COUNT value.
             */
            (rs, rowNum) -> {
                long count = rs.getInt(1);  // index[1]
                return count;
            }
        );
        //
        return result.size() > 0? (long)(result.get(0)) : 0;
    }


    /**
     * Run query that returns all Customers in the database.
     * - query: SELECT * FROM CUSTOMER;
     * - returns Customer objects created from ResultSet rows.
     * 
     * @return all Customers in the database.
     */
    @Override
    public Iterable<Customer> findAllCustomers() {
        //
        var result = jdbcTemplate.queryForStream(
            /*
             * Run SQL statement:
             */
            "SELECT * FROM CUSTOMER",

            /*
             * Return ResultSet (rs) and map each row to Optional<Customer>
             * depending on whether the object could be created from values
             * returned from the database or not (empty Optional is returned).
             */
            (rs, rowNum) -> {
                /*
                 * Extract values from ResultSet for each row.
                 */
                long id = rs.getInt("ID");
                String name = rs.getString("NAME");
                String contact = rs.getString("CONTACT");
                String status = rs.getString("STATUS");
                /*
                 * Attempt to create Customer object through dataFactory,
                 * which returns Optional<Customer>.
                 */
                return dataFactory.createCustomer(id, name, contact, status);
            }
        )
        /*
         * Remove empty results from stream of Optional<Customer>,
         * map remaining from Optional<Customer> to Customer and
         * collect result.
         */
        .filter(opt -> opt.isPresent())
        .map(opt -> opt.get())
        .collect(Collectors.toList());
        //
        return result;
    }


    /**
     * Run query that returns one Customers with a given id.
     * - query: SELECT * FROM CUSTOMER WHERE ID = ?id;
     * - returns Customer object created from ResultSet row.
     * 
     * @param id Customer id (WHERE ID = id)
     * @return Optional with Customer or empty if not found.
     */
    @Override
    public Optional<Customer> findCustomerById(long id) {
        //
        List<Optional<Customer>> result = jdbcTemplate.query(
            /*
             * Prepare statement (ps) with "?"-augmented SQL query.
             */
            "SELECT * FROM CUSTOMER WHERE ID = ?",
            ps -> {
                /*
                 * Insert id value of first occurence of "?" in SQL.
                 */
                ps.setInt(1, (int)id);
            },

            (rs, rowNum) -> {
                /*
                 * Extract values from ResultSet.
                 */
                String name = rs.getString("NAME");
                String contact = rs.getString("CONTACT");
                String status = rs.getString("STATUS");
                /*
                 * Create Optional<Customer> from values.
                 */
                return dataFactory.createCustomer(id, name, contact, status);
            }
        );
        /*
         * Probe List<Optional<Customer>> and return Optional<Customer> or
         * empty Optional for empty list.
         */
        return result.size() > 0? result.get(0) : Optional.empty();
    }


    /**
     * Run query that returns all Customers with matching id in ids.
     * - query: SELECT * FROM CUSTOMER WHERE ID IN (10, 20, 30000, 40);
     * - returns Customer objects created from ResultSet rows.
     * 
     * @param ids Customer ids (WHERE IN (?ids))
     * @return Customers with matching ids.
     */
    @Override
    public Iterable<Customer> findAllCustomersById(Iterable<Long> ids) {

        /*
         * Map ids (23, 48, 96) to idsStr: "23, 48, 96"
         */
        String idsStr = StreamSupport.stream(ids.spliterator(), false)
            .map(id -> String.valueOf(id))
            .collect(Collectors.joining(", "));
        //
        var result = jdbcTemplate.queryForStream(
            /*
             * Prepare statement (ps) with "?"-augmented SQL query.
             */
            String.format("SELECT * FROM CUSTOMER WHERE ID IN (%s)", idsStr),

            /*
             * Extract values from ResultSet for each row.
             */
            (rs, rowNum) -> {
                long id = rs.getInt("ID");
                String name = rs.getString("NAME");
                String contact = rs.getString("CONTACT");
                String status = rs.getString("STATUS");
                /*
                 * Create Optional<Customer> from values.
                 */
                return dataFactory.createCustomer(id, name, contact, status);
            }
        )
        /*
         * Remove empty results from stream of Optional<Customer>,
         * map remaining from Optional<Customer> to Customer and
         * collect result.
         */
        .filter(opt -> opt.isPresent())
        .map(opt -> opt.get())
        .collect(Collectors.toList());
        //
        return result;
    }


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
    @Override
    public Iterable<Reservation> findReservationsByCustomerId(long customer_id) {
        //
        return jdbcTemplate.queryForStream(
            /*
             * Prepare statement (ps) with "?"-augmented SQL query.
             */
            "SELECT RESERVATION.* FROM CUSTOMER " +
            "JOIN RESERVATION ON RESERVATION.CUSTOMER_ID = CUSTOMER.ID " +
            "WHERE CUSTOMER.ID = ?",

            ps -> {
                /*
                 * Insert customer_id value of first occurence of "?" in SQL.
                 */
                ps.setInt(1, (int)customer_id);
            },

            (rs, rowNum) -> {
                /*
                 * Extract values from ResultSet.
                 */
                long rid = rs.getInt("ID");   // RESERVATION.ID
                long vehicle_id = rs.getInt("VEHICLE_ID");
                String begin = rs.getString("BEGIN");
                String end = rs.getString("END");
                String pickup = rs.getString("PICKUP");
                String dropoff = rs.getString("DROPOFF");
                String status = rs.getString("STATUS");

                /*
                 * Create Optional<Reservation> from values.
                 */
                var opt = dataFactory.createReservation(
                    rid, customer_id, vehicle_id, begin, end, pickup, dropoff, status
                );

                if(opt.isEmpty()) {
                    // log warning if no valid Reservation object could be created
                    // from database result set
                    logger.warn(String.format("dropping reservation id: %d"));
                }
                return opt;
            })
            /*
            * Remove empty results from stream of Optional<Customer>,
            * map remaining from Optional<Customer> to Customer and
            * collect result.
            */
            .filter(opt -> opt.isPresent())
            .map(opt -> opt.get())
            .collect(Collectors.toList());
    }


    /**
     * Generic method to return number of elements in Iterable<T>.
     * 
     * @param <T> Generic type of objects contained in Iterable.
     * @param iter Iterable with objects of type T.
     * @return number of elements.
     */
    @Override
    public <T> long count(Iterable<T> iter) {
        return iter instanceof Collection? ((Collection<?>) iter).size() : -1;
    }


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
    @Override
    public Customer createCustomer(Map<String, Object> map) throws DataAccessException {
        //
        // placeholder for: ID, NAME, CONTACT, STATUS
        final Object[] attrs = {null, null, null, null};
        int id = -1;
        //
        // extract attributes from map
        for(String key : map.keySet()) {
            //
            switch(key.toUpperCase()) {
                //
                case "ID":
                    final Integer ID = parseNumber(map.get(key));
                    id = ID != null? ID.intValue() : -1;
                    attrs[0] = id >= 0? ID : null;
                    break;
                //
                case "NAME":    attrs[1] = (String)map.get(key); break;
                case "CONTACT": attrs[2] = (String)map.get(key); break;
                case "STATUS":  attrs[3] = (String)map.get(key); break;
            }
        }
        // probe all values have been set
        for(var a : attrs) {
            if(a==null)
                throw new DataAccessException.BadRequest("incomplete attributes");
        }
        //
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        //
        try {
            //
            int created = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                    //
                    .prepareStatement("INSERT INTO CUSTOMER (ID, NAME, CONTACT, STATUS) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
                    //
                    ps.setInt(1, ((Integer)attrs[0]).intValue());   // ID
                    ps.setString(2, (String)attrs[1]);   // NAME
                    ps.setString(3, (String)attrs[2]);   // CONTACT
                    ps.setString(4, (String)attrs[3]);   // STATUS
                    //
                    return ps;
                //
                }, keyHolder);
            //
            // id = (long)keyHolder.getKey();   // used to extract key generated by database
            if(created != 1) {
                throw new DataAccessException.BadRequest(
                    String.format("data recored not created for id: %d, %d records created", id, created)
                );
            }
        //
        } catch(org.springframework.dao.DataAccessException dax) {
            // "org.springframework.dao.DataIntegrityViolationException: PreparedStatementCallback;
            // Duplicate entry '1114' for key 'customer.PRIMARY'"
            throw new DataAccessException.Conflict("INSERT exception, id may exist: " + (int)attrs[0]);
        }
        //
        return dataFactory.createCustomer(id, (String)attrs[1], (String)attrs[1], (String)attrs[3])
            .map(c -> c)
            .orElseThrow(() ->
                new DataAccessException.Conflict("failed to create object for id: " + (int)attrs[0]));
    }


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
     * @return updated Customer object.
     * @throws DataAccessException with error code (400 bad request, 404 not found).
     */
    @Override
    public boolean updateCustomer(Map<String, Object> map) throws DataAccessException {
        //
        String cols = "";
        int id = -1;
        //
        // extract attributes from map
        for(String key : map.keySet()) {
            //
            switch(key.toUpperCase()) {
                //
                case "ID":
                    final Integer ID = parseNumber(map.get(key));
                    id = ID != null && ID >= 0? ID.intValue() : id;
                    if(id >= 0) {
                        cols += (cols.length() > 0? ", " : "") + "ID=\"" + map.get(key) + "\"";
                    }
                    break;
                //
                case "NAME":    cols += (cols.length() > 0? ", " : "") + "NAME=\"" + map.get(key) + "\""; break;
                case "CONTACT": cols += (cols.length() > 0? ", " : "") + "CONTACT=\"" + map.get(key) + "\""; break;
                case "STATUS":  cols += (cols.length() > 0? ", " : "") + "STATUS=\"" + map.get(key) + "\""; break;
            }
        }
        // probe all values have been set
        if(cols.length() > 0 && id >= 0) {
            try {
                //
                final String fcols = cols;
                final int fid = id;
                int updated = jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection
                        .prepareStatement("UPDATE CUSTOMER SET " + fcols + " WHERE ID = ?;");
                    ps.setInt(1, fid);
                    return ps;
                });
                //
                if(updated != 1) {
                    throw new DataAccessException.NotFound(
                        String.format("id not found: %d, %d records updated", id, updated)
                    );
                }
            //
            } catch(org.springframework.dao.DataAccessException dax) {
                throw new DataAccessException.BadRequest(dax.getMessage());
            }
        //
        } else {
            throw new DataAccessException.BadRequest("incomplete attributes");
        }
        return true;
    }


    /**
     * Delete Customer record with id from CUSTOMER table. An exception is
     * thrown with error code: 404 not found (id not found), 409 conflict
     * (foreign key violation).
     * 
     * @param id Customer id.
     * @return true when customer was deleted sucessfully.
     * @throws DataAccessException with error code (404 not found, 409 conflict).
     */
    @Override
    public boolean deleteCustomer(long id) throws DataAccessException {
        //
        if(id < 0)
            throw new DataAccessException.BadRequest("invalid id: " + id);
        //
        try {
            //
            int deleted = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                    .prepareStatement("DELETE FROM CUSTOMER WHERE ID = ?;");
                ps.setInt(1, (int)id);
                return ps;
            });
            //
            if(deleted != 1) {
                throw new DataAccessException.NotFound(
                    String.format("id not found: %d, %d records deleted", id, deleted)
                );
            }
        //
        } catch(org.springframework.dao.DataAccessException dax) {
            throw new DataAccessException.Conflict("conflict deleting item id: " +
                        id + ", foreign key dependency may exist");
        }
        return true;
    }


    /**
     * Attempt to parse an Integer value from an object.
     * 
     * @param obj object to parse.
     * @return Integer value or null.
     */
    private Integer parseNumber(Object obj) {
        Integer number = null;
        if(obj != null) {
            if(obj instanceof Integer)
            number = (Integer)obj;
            else if(obj instanceof String)
                try {
                    number = Integer.parseInt((String)obj);
                } catch(NumberFormatException ex) { };
            }
        return number;
    }

}
