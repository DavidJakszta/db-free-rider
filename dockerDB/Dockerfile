# based on mysql:8.0 base image: https://hub.docker.com/_/mysql, v8:0
# Mac with M1-Chip use: FROM --platform=linux/amd64 mysql:8.0
FROM mysql:8.0

## Dockerfile is used to create an image built on a base image:
## docker build -t "${image_name}" --no-cache . ;

# Logic to later pass ARGs during 'docker run' into container:
# docker run --name=${container_name} \
#     --env MYSQL_DATABASE='FREERIDER_DB' \
#     --env MYSQL_USER='freerider' \
#     --env MYSQL_PASSWORD='free.ride' \
#     --env MYSQL_ROOT_PASSWORD='password' \
#     ... -d ${image_name}
#
ARG MYSQL_DATABASE
ARG MYSQL_USER
ARG MYSQL_PASSWORD
ARG MYSQL_ROOT_PASSWORD

# Push ARGs into container as ENV variables.
# mysql:8.0 entryscript.sh will pickup ENV variables to create
# database and user from MYSQL_DATABASE and MYSQL_USER.
#
ENV MYSQL_DATABASE=$MYSQL_DATABASE
ENV MYSQL_USER=$MYSQL_USER
ENV MYSQL_PASSWORD=$MYSQL_PASSWORD
ENV MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD

# Link files from 'db.mnt' host-directory mounted at /mnt inside the
# container to locations expected by mysqld.
# Link mysqld config file to where mysqld expects it.
RUN ln -s /mnt/my.cnf /etc/mysql/conf.d/my.cnf

# Create/add db_init file where mysql:8.0 docker-entrypoint.sh expects it
ARG DB_INIT_FILE=/docker-entrypoint-initdb.d/db_init.sql
RUN touch $DB_INIT_FILE
ADD ./db.mnt/init_freerider_schema.sql /tmp/init_schema.sql
ADD ./db.mnt/init_freerider_data.sql /tmp/init_data.sql
RUN cat /tmp/init_schema.sql >> $DB_INIT_FILE
RUN cat /tmp/init_data.sql >> $DB_INIT_FILE

# Link mysqld data and log to mount points outside the container
RUN ln -s /mnt/db_data /var/lib/mysql
RUN ln -s /mnt/db_logs /var/log/mysql

#
# Tail of original base mysql:8.0 Dockerfile (for reference),
# https://hub.docker.com/_/mysql:
#
# VOLUME /var/lib/mysql
# COPY docker-entrypoint.sh /usr/local/bin/
# RUN ln -s usr/local/bin/docker-entrypoint.sh /entrypoint.sh
# ENTRYPOINT ["docker-entrypoint.sh"]
# EXPOSE 3306 33060
# CMD ["mysqld"]
#