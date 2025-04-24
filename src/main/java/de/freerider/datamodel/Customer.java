package de.freerider.datamodel;

/**
 * Entity class for Customer.
 *
 * Schema in database:
 * +---------+-----------------------------------------+------+-----+---------+
 * | Field   | Type                                    | Null | Key | Default |
 * +---------+-----------------------------------------+------+-----+---------+
 * | ID      | int                                     | NO   | PRI | NULL    |
 * | NAME    | varchar(60)                             | YES  |     | NULL    |
 * | CONTACT | varchar(60)                             | YES  |     | NULL    |
 * | STATUS  | enum('Active','InRegistration',         | YES  |     | NULL    |
 * |         |   'Terminated')                         |      |     |         |
 * +---------+-----------------------------------------+------+-----+---------+
 *
 * @author sgra64
 */
public final class Customer {

    /**
     * Customer attribute: id.
     */
    private final long id;

    /**
     * Customer attribute: name (single-String).
     */
    private String name;

    /**
     * Customer attribute: contacts (single-String).
     */
    private String contact;

    /**
     * Customer attribute: status (Active, InRegistration, Terminated).
     */
    private Status status;

    /**
     * Lifecycle and Status information of a Customer.
     */
    public enum Status {
        Active, // Active Customer, can make reservations.

        InRegistration, // New Customer in registration,
        // cannot make reservations, yet.
        Terminated // Customer has ended business relation,
    }; // can no longer make reservations.

    /**
     * Non-public constructor that prevents creating instances outside
     * this package. Instances can only be created through DataFactory.
     *
     * @param id unique identifier, PRIMARY KEY in database.
     * @param name Customer name (single String).
     * @param contacts Customer contact information.
     * @param status status of Customer.
     * @throws IllegalArgumentException for illegal parameters.
     */
    Customer(long id, String name, String contacts, Status status) {
        if (id < 0)
            throw new IllegalArgumentException(String.format("id: %d, id < 0", id));
        //
        this.id = id;
        setName(name);
        setContact(contacts);
        setStatus(status);
    }

    /**
     * Public id attribute getter.
     *
     * @return unique Customer identifier, PRIMARY KEY in database.
     */
    public long getId() {
        return id;
    }

    /**
     * Public name attribute getter.
     *
     * @return Customer name (single String).
     */
    public String getName() {
        return name;
    }

    /**
     * Public name attribute setter.
     *
     * @param name Customer name (single String).
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal name parameter.
     */
    public Customer setName(String name) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("name is null or empty");
        //
        this.name = name;
        return this;
    }

    /**
     * Public contacts attribute getter.
     *
     * @return Customer contact information.
     */
    public String getContact() {
        return contact;
    }

    /**
     * Public contacts attribute setter.
     *
     * @param contacts Customer contact information (may be empty).
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal contacts parameter.
     */
    public Customer setContact(String contacts) {
        if (contacts == null)
            throw new IllegalArgumentException("contacts is null");
        //
        this.contact = contacts;
        return this;
    }

    /**
     * Public status attribute getter.
     *
     * @return status of Customer.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Public status attribute setter.
     *
     * @param status status of Customer.
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal status parameter.
     */
    public Customer setStatus(Status status) {
        if (status == null)
            throw new IllegalArgumentException("status is null");
        //
        this.status = status;
        return this;
    }
}
