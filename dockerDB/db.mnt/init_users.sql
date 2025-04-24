-- Add users: 'root' and 'freerider'

-- grant user 'root' all privileges on all databases
CREATE USER 'root'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';

-- grant user 'freerider' all privileges on database FREERIDER_DB
CREATE USER 'freerider'@'%' IDENTIFIED BY 'free.ride';
GRANT ALL PRIVILEGES ON FREERIDER_DB.* to 'freerider'@'%';


-- SELECT host, user FROM mysql.user;
-- +-----------+------------------+
-- | host      | user             |
-- +-----------+------------------+
-- | %         | freerider        | <-- added user 'freerider'
-- | %         | root             | <-- added user 'root'
-- | localhost | mysql.infoschema |
-- | localhost | mysql.session    |
-- | localhost | mysql.sys        |
-- | localhost | root             |
-- +-----------+------------------+
-- 6 rows in set (0.00 sec)
-- 
-- references:
-- https://dev.mysql.com/doc/refman/8.0/en/creating-accounts.html
-- https://github.com/docker-library/mysql/issues/275
