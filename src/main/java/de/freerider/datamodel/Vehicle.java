package de.freerider.datamodel;


/**
 * Entity class for Vehicle.
 * 
 * Schema in database:
 * +----------+----------------------------------------+------+-----+---------+
 * | Field    | Type                                   | Null | Key | Default |
 * +----------+----------------------------------------+------+-----+---------+
 * | ID       | int                                    | NO   | PRI | NULL    |
 * | MAKE     | varchar(60)                            | YES  |     | NULL    |
 * | MODEL    | varchar(60)                            | YES  |     | NULL    |
 * | SEATS    | int                                    | YES  |     | 4       |
 * | CATEGORY | enum('Sedan','SUV','Convertible',      | YES  |     | NULL    |
 * |          |   'Van', 'Bike')                       |      |     |         |
 * | POWER    | enum('Gasoline','Diesel','Electric',   | YES  |     | NULL    |
 * |          |   'Hybrid','Hydrogen')                 | YES  |     | NULL    |
 * | STATUS   | enum('Active','Serviced','Terminated') | YES  |     | NULL    |
 * +----------+----------------------------------------+------+-----+---------+
 * 
 * @author sgra64
 */
public final class Vehicle {

    /*
     * Vehicle attribute: id.
     */
    private final long id;

    /*
     * Vehicle attribute: make, brand of a manufacturer, e.g. "VW".
     */
    private final String make;

    /*
     * Vehicle attribute: model, vehicle model, e.g. "Golf".
     */
    private final String model;

    /*
     * ...more attributes
     * 
     */
    private int seats;

    private Category category;

    private Power power;

    private Status status;



    /**
     * Vehicle category.
     */
    public enum Category {
        Sedan, SUV, Convertible, Van, Bike
    };

    /**
     * Main power source of a vehicle.
     */
    public enum Power {
        Gasoline, Diesel, Electric, Hybrid, Hydrogen
    };

    /**
     * Lifecycle and Status information of a Vehicle.
     */
    public enum Status {
        Active, Serviced, Terminated
    };


    /**
     * Non-public constructor that prevents creating instances outside
     * this package. Instances can only be created through DataFactory.
     * 
     * @param id unique identifier, PRIMARY KEY in database.
     * @param make brand name of Vehicle, e.g. "VW" or "Tesla"
     * @param model model name of Vehicle, e.g. "ID.4"
     * @param seats number of seats in Vehicle.
     * @param category category of Vehicle.
     * @param power power source of Vehicle.
     * @param status status of Vehicle.
     * @throws IllegalArgumentException for illegal parameters.
     */
    Vehicle(long id, String make, String model, int seats, String category, String power, String status) {
        if(id < 0)
            throw new IllegalArgumentException(String.format("id: %d, id < 0", id));
        if(make==null || make.length()==0)
            throw new IllegalArgumentException("make is null or empty");
        if(model==null || model.length()==0)
            throw new IllegalArgumentException("model is null or empty");
        if(seats <= 0 || seats > 100)
            throw new IllegalArgumentException(String.format("seats: %d, seats <= 0 || seats > 100", seats));
        if(category==null)
            throw new IllegalArgumentException("category is null");
        if(power==null)
            throw new IllegalArgumentException("power is null");
        //
        this.id = id;
        this.make = make;
        this.model = model;
    }


    /**
     * Public id attribute getter.
     * 
     * @return unique Vehicle identifier, PRIMARY KEY in database.
     */
    public long getId() {
        return id;
    }


    /**
     * Public make attribute getter.
     * 
     * @return brand name of Vehicle, e.g. "VW" or "Tesla".
     */
    public String getMake() {
        return make;
    }


    /**
     * Public model attribute getter.
     * 
     * @return model name of Vehicle, e.g. "ID.4"
     */
    public String getModel() {
        return model;
    }


    /**
     * Public seats attribute getter.
     * 
     * @return number of seats in Vehicle.
     */
    public int getSeats() { 
        return seats;
    }


    /**
     * Public category attribute getter.
     * 
     * @return category of Vehicle.
     */
    public Category getCategory() { 
        return category;
    }


    /**
     * Public power attribute getter.
     * 
     * @return power source of Vehicle.
     */
    public Power getPower() { 
        return power;
    }


    /**
     * Public status attribute getter.
     * 
     * @return status of Vehicle.
     */
    public Status getStatus() { 
        return status;
    }


    /**
     * Public status attribute setter.
     * 
     * @param status status of Vehicle.
     * @return chainable self-reference.
     * @throws IllegalArgumentException for illegal status parameter.
     */
    public Vehicle setStatus(Status status) { 
        // if(status != Vehicle.Status.Active || status != Vehicle.Status.Serviced || status != Vehicle.Status.Terminated )
        if(status==null)
			throw new IllegalArgumentException("Wrong Status");
        this.status = status;
	    return this;
    }
}
