package de.freerider.endpoints;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freerider.data_jdbc.DataAccess;
import de.freerider.data_jdbc.DataAccessException;
import de.freerider.datamodel.Customer;


@RestController
class CustomersRestController implements CustomersEP {

    /*
     * Logger instance for this class.
     */
    private static final Logger logger =
        LoggerFactory.getLogger(CustomersRestController.class);

    /**
     * DataAccess (object) DAO is a component to accesses data in the
     * database through SQL queries.
     */
    @Autowired
    private DataAccess dao;


    @Override
    public Iterable<Customer> findAllCustomers() {
        return dao.findAllCustomers();
    }


    @Override
    public Customer findCustomerById(@PathVariable long id) {
        //
        logger.info(String.format("--- received request: GET /customer/%d", id));
        //
        if(id < 0L)
            // throw error 400 (bad request)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Customer id: %d negative", id, HttpStatus.BAD_REQUEST.value())
            );
        //
        Customer found = dao.findCustomerById(id)
            .map(c -> c)    // return customer{id}, if found
            //
            //              // else throw error 404 (not found)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Customer id: %d not found, error %d", id, HttpStatus.NOT_FOUND.value())
            ));
        //
        logger.info(String.format("--- found: Customer(id: %d, name: %s)", found.getId(), found.getName()));
        //
        return found;
    }


    @Override
    public ResponseEntity<Customer> createCustomer(@RequestBody Map<String, Object> jsonData) {
        //
        logger.info(String.format("--- received POST (create): Customer JSON data:"));
        //
        try {
            //
            Customer customer = dao.createCustomer(jsonData);
            //
            logger.info(String.format("--- new Customer object created: [%d, \"%s\", \"%s\", \"%s\"]",
                customer.getId(), customer.getName(), customer.getContact(), customer.getStatus()
            ));
            //
            // return Customer object (serialized to JSON)
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        //
        } catch(DataAccessException dax) {
            reThrow(dax, "DataAccessException dax: " + dax.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @Override
    public ResponseEntity<?> updateCustomer(Map<String, Object> jsonData) {
        //
        logger.info(String.format("--- received PUT (update): Customer JSON data:"));
        //
        var respCode = HttpStatus.NOT_IMPLEMENTED;
        try {
            dao.updateCustomer(jsonData);
            logger.info(String.format("--- Customer object updated"));
            respCode = HttpStatus.ACCEPTED;
        //
        } catch(DataAccessException dax) {
            reThrow(dax, "DataAccessException dax: " + dax.getMessage());
        }
        return ResponseEntity.status(respCode).build();
    }


    @Override
    public ResponseEntity<?> deleteCustomerById(long id) {
        //
        logger.info(String.format("--- received request: DELETE /customer/%d", id));
        //
        var respCode = HttpStatus.NOT_IMPLEMENTED;
        try {
            dao.deleteCustomer(id);
            respCode = HttpStatus.ACCEPTED;
        //
        } catch(DataAccessException dax) {
            reThrow(dax, "DataAccessException dax: " + dax.getMessage());
        }
        return ResponseEntity.status(respCode).build();
    }


    /**
     * Map exceptions of type DataAccessException used in the data access layer
     * to HTTP ResponseStatusExceptions used in the Controller layer.
     * 
     * @param dax DataAccessException from the data access layer.
     * @param msg exception message.
     * @throws ResponseStatusException return to HTTP client.
     */
    private void reThrow(DataAccessException dax, String msg) throws ResponseStatusException {
        var respCode = HttpStatus.NOT_IMPLEMENTED;
        switch(dax.code) {
            case BadRequest: respCode = HttpStatus.BAD_REQUEST; break;
            case NotFound:   respCode = HttpStatus.NOT_FOUND; break;
            case Conflict:   respCode = HttpStatus.CONFLICT; break;
        }
        throw new ResponseStatusException(respCode, msg);
    }

}
