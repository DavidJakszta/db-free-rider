###########################################################################
# Project-specific environment inside MySQL container. Source with:
#   source /mnt/.env.sh
#
# inherit MYSQL_HOME from .bashrc or specify here
MYSQL_HOME="/usr/bin"
export MYSQL_DATABASE="FREERIDER_DB"
export MYSQL_USER="freerider"
export MYSQL_PASSWORD="free.ride"
export MYSQL_ROOT_PASSWORD="password"


# Overload mysql client command with project-specific settings.
function mysql() {
    "${MYSQL_HOME}/mysql" \
        --default-character-set=utf8 \
        --host=localhost --port=3306 \
        --user="${MYSQL_USER}" --password="${MYSQL_PASSWORD}" \
        "${MYSQL_DATABASE}" \
    ;
}

# mysql script for root access.
function mysql_root() {
    "${MYSQL_HOME}/mysql" \
        --default-character-set=utf8 \
        --host=localhost --port=3306 \
        --user="root" --password="${MYSQL_ROOT_PASSWORD}" \
    ;
}


# log into database
# mysql --user=root --password=password
# \\
# SELECT host, user FROM mysql.user;
# mysql> SELECT host, user FROM mysql.user;
# +-----------+------------------+
# | host      | user             |
# +-----------+------------------+
# | %         | freerider        |
# | %         | root             |
# | localhost | mysql.infoschema |
# | localhost | mysql.session    |
# | localhost | mysql.sys        |
# | localhost | root             |
# +-----------+------------------+
# 6 rows in set (0.01 sec)
