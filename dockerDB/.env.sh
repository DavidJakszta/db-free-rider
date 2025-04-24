###########################################################################
# Project-specific environment inside MySQL container. Source with:
# Source settings with: source .env.sh
#
# inherit MYSQL_HOME from .bashrc or specify here
#MYSQL_HOME="/c/Program Files/MySQL/MySQL Workbench 8.0 CE"
export project_path="$(pwd -LP)"
export image_name="mysql/db-freerider_img:8.0"
export container_name="db-freerider_MySQLServer"

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


# Overload docker commands with project-specific settings.
function dock() {
  select="${1}"   # first arg, arg[1]: selector
  args="${@:2}"   # remaining args
  cmd=""
  func=""

  case "${select}" in
    #    
    env)    func="dock_env"
        ;;  # show environment

    build_img) cmd="docker build -t \"${image_name}\" --no-cache . "
        ;;  # create new image from Dockerfile in local directory: .

    build)  func="dock_build_container"
        ;;  # create container from new image using function

    start)  cmd="docker start \"${container_name}\""
        ;;

    stop)   # cmd="docker stop \"${container_name}\""
            # use graceful shutdown executing shutdown.sh script
            cmd="echo \"cat /mnt/shutdown.sh | /bin/bash\" | \
                    docker exec -i \"${container_name}\" /bin/bash"
        ;;

    bash)   cmd="docker exec -it \"${container_name}\" /bin/bash"
        ;;  # attach bash to running container

    logs)   cmd="docker logs \"${container_name}\""
        ;;

    top)    cmd="docker top \"${container_name}\""
        ;;  # show processes running inside the container

    clean)  func="dock_clean"
        ;;  # clean container and image using function
    #
  esac
  #
  if [ ! -z "${cmd}${func}" ]; then
    if [ ! -z "${cmd}" ]; then
        echo "${cmd}"
        bash -c "${cmd}"
    else
        this_file="${BASH_SOURCE[0]}"
        # need to reload functions in sub-bash
        BASH_ENV=${this_file} bash -c "${func}"
        this_file=""
    fi
  else
    cmd="dock"
    echo "Usage: ${cmd} <command> <args>"
    echo " - ${cmd} env"
    echo " - ${cmd} build_img"
    echo " - ${cmd} build"
    echo " - ${cmd} start | stop"
    echo " - ${cmd} bash  | logs | top | clean"
  fi
}

if [ -z "${this_file}" ]; then
    echo "project environment sourced"
fi

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

#
# - - - - - - - - - - -
#
function test_image_exists()     { [ ! -z "$(docker images | grep db-freerider_img)" ]; }
function test_container_exists() { [ ! -z "$(docker ps -a | grep ${cont_name})" ]; }
function test_container_runs()   { [ ! -z "$(docker ps | grep ${cont_name})" ]; }

function dock_build_container() {
    #
    # create container from image
    docker run \
        --name="${container_name}" \
        \
        --env MYSQL_DATABASE="${MYSQL_DATABASE}" \
        --env MYSQL_USER="${MYSQL_USER}" \
        --env MYSQL_PASSWORD="${MYSQL_PASSWORD}" \
        --env MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}" \
        \
        --publish 3306:3306 \
        --mount type=bind,src="${project_path}/db.mnt",dst="/mnt" \
        -d "${image_name}"
    #
}

function dock_clean() {
    #
    # clear container, image and db_data and db_logs files
    docker stop "${container_name}"
    docker rm "${container_name}"
    docker rmi "${image_name}"
    docker volume prune -f
    rm -rf db.mnt/db_data/* db.mnt/db_logs/*
    touch db.mnt/db_data/.touch
    touch db.mnt/db_logs/.touch
    #
}

function dock_env() {
    # echo "project environment sourced with:"
    echo " - \${project_path}:   \"${project_path}\""
    echo " - \${image_name}:     \"${image_name}\""
    echo " - \${container_name}: \"${container_name}\""
    echo " - \${MYSQL_DATABASE}:      \"${MYSQL_DATABASE}\""
    echo " - \${MYSQL_USER}:          \"${MYSQL_USER}\""
    echo " - \${MYSQL_PASSWORD}:      \"${MYSQL_PASSWORD}\""
    echo " - \${MYSQL_ROOT_PASSWORD}: \"${MYSQL_ROOT_PASSWORD}\""
    echo " - mysql --default-character-set=utf8 --user=${MYSQL_USER} --password=${MYSQL_PASSWORD}"
}
# - - - - - - - - - - -
#
