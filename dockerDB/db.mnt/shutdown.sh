# clear entrypoint script to prevent reinitializing database on mysqld startup
echo > /docker-entrypoint-initdb.d/db_init.sql

# execute 'shutdown;' in SQL to shutdown mysqld server process
echo "shutdown;" | mysql --user=root --password=password
