package de.freerider.data_jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import de.freerider.datamodel.Vehicle;
import de.freerider.datamodel.DataFactory;


/**
 * Non-public implementation class of Vehicle DataAccess interface.
 */
@Component
class DataAccessVehiclesImpl implements DataAccessVehicles {

    /**
     * Datafactory is a component that creates datamodel objects.
     */
    @Autowired
    private DataFactory dataFactory;

    /*
     * JdbcTemplate is the central class in the JDBC core package for SQL
     * database access.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * Run query that returns the number of Vehicles in the database:
     * - query: SELECT COUNT(ID) FROM VEHICLE;
     * - returns number extracted from ResultSet.
     * 
     * @return number of Vehicle records in the database.
     */
    @Override
    public long countVehicles() {
        List<Object> result = jdbcTemplate.query(
            "SELECT COUNT(ID) FROM VEHICLE",
            (rs, rowNum) -> {
                long count = rs.getInt(1);  // index[1]
                return count;
            }
        );
        return result.size() > 0? (long)(result.get(0)) : 0;
    }


    /**
     * Run query that returns all Vehicles in the database.
     * - query: SELECT * FROM VEHICLE;
     * - returns Vehicle objects created from ResultSet rows.
     * 
     * @return all Vehicles in the database.
     */
    @Override
    public Iterable<Vehicle> findAllVehicles() {
        //
        return jdbcTemplate.queryForStream(
            "SELECT * FROM VEHICLE",
            (rs, rowNum) -> createVehicle(rs)
        )
        .filter(opt -> opt.isPresent())
        .map(opt -> opt.get())
        .collect(Collectors.toList());
    }


    /**
     * Run query that returns one Vehicles with a given id.
     * - query: SELECT * FROM VEHICLE WHERE ID = 10;
     * - returns Vehicle object created from ResultSet row.
     * 
     * @param id Vehicle id (WHERE ID = ?id)
     * @return Optional with Vehicle or empty if not found.
     */
    @Override
    public Optional<Vehicle> findVehicleById(long id) {
            List<Optional<Vehicle>> result = jdbcTemplate.query(
            "SELECT * FROM VEHICLE WHERE ID = ?",
            ps -> {
                ps.setInt(1, (int)id);
            },
            (rs, rowNum) -> createVehicle(rs)
        );
        return result.size() > 0? result.get(0) : Optional.empty();
    }


    /**
     * Run query that returns all Vehicles with matching id in ids.
     * - query: SELECT * FROM VEHICLE WHERE ID IN (10, 20, 30000, 40);
     * - returns Vehicle objects created from ResultSet rows.
     * 
     * @param ids Vehicle ids (WHERE IN (?ids))
     * @return Vehicles with matching ids.
     */
    @Override
    public Iterable<Vehicle> findAllVehiclesById(Iterable<Long> ids) {
        /*
         * Map ids (23, 48, 96) to idsStr: "23, 48, 96"
         */
        String idsStr = StreamSupport.stream(ids.spliterator(), false)
            .map(id -> String.valueOf(id))
            .collect(Collectors.joining(", "));
        //
        return jdbcTemplate.queryForStream(
            //
            String.format("SELECT * FROM VEHICLE WHERE ID IN (%s)", idsStr),
            //
            (rs, rowNum) -> createVehicle(rs)
        )
        .filter(opt -> opt.isPresent())
        .map(opt -> opt.get())
        .collect(Collectors.toList());
    }


    /**
     * Create Vehicle object from data in ResultSet.
     * 
     * @param rs SQL ResultSet containing all columns for a row.
     * @return Optional<Vehicle>.
     */
    private Optional<Vehicle> createVehicle(ResultSet rs) {
        try {
            long id = rs.getInt("ID");
            String make = rs.getString("MAKE");
            String model = rs.getString("MODEL");
            int seats = rs.getInt("SEATS");
            String category = rs.getString("CATEGORY");
            String power = rs.getString("POWER");
            String status = rs.getString("STATUS");
            //
            return dataFactory.createVehicle(id, make, model, seats, category, power, status);
        //
        } catch(SQLException e) { }
        //
        return Optional.empty();
    }
}
