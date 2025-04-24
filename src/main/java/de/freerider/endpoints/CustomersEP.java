package de.freerider.endpoints;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import de.freerider.datamodel.Customer;


/**
 * Spring Controller interface for /customers REST endpoint to access the
 * collection of customer resources maintained in a CustomerRepository.
 * 
 * Operations provided by the endpoint:
 * 
 * - GET /customers         - return JSON data for all customer in the repository,
 *                            status: 200 OK.
 * 
 * - GET /customers/{id}    - return JSON data for customer with id,
 *                            status: 200 OK, 400 bad request (id), 404 not found.
 * 
 * - POST /customers        - create new objects in the repository from JSON objects
 *                            passed with the request,
 *                            status: 201 created, 400 bad request (json body),
 *                            409 conflict.
 * 
 * - PUT /customers         - updated existing objects in the repository from JSON
 *                            objects passed with the request,
 *                            status: 202 accepted, 400 bad request (json body),
 *                            404 not found.
 * 
 * - DELETE /customers/{id} - delete customer with id,
 *                            status: 202 accepted, 400 bad request (id),
 *                            404 not found, 409 conflict (foreign key dependency).
 * 
 * @author sgra64
 *
 */

@RequestMapping("/v1/customers")
public interface CustomersEP extends CustomersEPDoc {

    @GetMapping("")
    @Override
    Iterable<Customer> findAllCustomers();


    @GetMapping("/{id}")
    @Override
    Customer findCustomerById(@PathVariable long id);


    @PostMapping("")
    @Override
    ResponseEntity<Customer> createCustomer(@RequestBody Map<String, Object> jsonData);


    @PutMapping("")
    @Override
    ResponseEntity<?> updateCustomer(@RequestBody Map<String, Object> jsonData);


    @DeleteMapping("/{id}")
    @Override
    ResponseEntity<?> deleteCustomerById(@PathVariable long id);

}
