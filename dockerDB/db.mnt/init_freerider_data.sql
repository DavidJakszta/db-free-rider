USE FREERIDER_DB;

DELETE FROM CUSTOMER;
INSERT INTO CUSTOMER (ID, NAME, CONTACT, STATUS) VALUES
    (1, 'Meyer, Eric', 'eme22@gmail.com', 'Active'),
    (2, 'Sommer, Tina', '030 22458 29425', 'Active'),
    (3, 'Schulze, Tim', '+49 171 2358124', 'Active')
;

DELETE FROM VEHICLE;
INSERT INTO VEHICLE (ID, MAKE, MODEL, SEATS, CATEGORY, POWER, STATUS) VALUES
    (1001, 'VW', 'Golf', 4, 'Sedan', 'Gasoline', 'Active'),
    (1002, 'VW', 'Golf', 4, 'Sedan', 'Hybrid', 'Active'),
    (2000, 'BMW', '320d', 4, 'Sedan', 'Diesel', 'Active'),
    (3000, 'Mercedes', 'EQS', 4, 'Sedan', 'Electric', 'Active'),
    (1200, 'VW', 'Multivan Life', 8, 'Van', 'Gasoline', 'Active'),
    (6000, 'Tesla', 'Model 3', 4, 'Sedan', 'Electric', 'Active'),
    (6001, 'Tesla', 'Model S', 4, 'Sedan', 'Electric', 'Serviced')
;

-- https://www.w3schools.com/sql/func_mysql_now.asp
-- 
-- Date and time values can be converted from String representations
-- using formatting rules. See
-- https://www.w3schools.com/mysql/func_mysql_str_to_date.asp
-- \\
-- Example:
-- STR_TO_DATE('20/12/2022 10:00:00','%d/%m/%Y %H:%i:%s')
--
-- Alternatively, a non-linear decimal format can be used comprised of 4 digits
-- for the year followed by number pairs for month, day, hour, min and sec:
--    2022 * 10,000,000,000 - years
--    + 12 *    100,000,000 - month
--    + 31 *      1,000,000 - day
--    + 24 *         10,000 - hour
--    + 59 *            100 - minute
--    + 59 *              1 = 20221231245959 = 2022,12,31 24:59:59
-- This format cannot be used for time calculations due to irregular carry-over.
--
-- Unix 'epoch time' is a linear date/time format counting sec since 01/01/1970 UTC
-- with conversion functions, e.g. from_unixtime().
-- For example, e.g. 04.12.2022 10:00 = 1,670,148,000 (~1,6 billion seconds).
-- Use converter: https://www.epochconverter.com
-- Example:
-- 1671357600 - Sunday, December 18, 2022 10:00:00
-- 1671379200 - Sunday, December 18, 2022 16:00:00
-- = +6h = 6*60*60 sec = 21,600 = 1671379200 - 1671379200 = 21,600.
--

DELETE FROM RESERVATION;
INSERT INTO RESERVATION (ID, CUSTOMER_ID, VEHICLE_ID, BEGIN, END, PICKUP, DROPOFF, STATUS) VALUES
    (201235, 1, 1002, STR_TO_DATE('20/12/2022 10:00:00','%d/%m/%Y %H:%i:%s'), STR_TO_DATE('20/12/2022 20:00:00','%d/%m/%Y %H:%i:%s'), 'Berlin Wedding', 'Berlin Wedding', 'Booked'),
    (145373, 2, 6001, STR_TO_DATE('04/12/2022 20:00:00','%d/%m/%Y %H:%i:%s'), STR_TO_DATE('04/12/2022 23:00:00','%d/%m/%Y %H:%i:%s'), 'Berlin Wedding', 'Hamburg', 'Inquired'),
    (382565, 2, 3000, 20221218180000, 20221218181000, 'Berlin Wedding', 'Hamburg', 'Inquired'),
    (351682, 2, 6000, from_unixtime(unix_timestamp()), from_unixtime(unix_timestamp() + 2*60*60), 'Berlin Wedding', 'Hamburg', 'Inquired'),
    (682351, 2, 6000, from_unixtime(1671357600), from_unixtime(1671379200), 'Potsdam', 'Teltow', 'Inquired')
;
