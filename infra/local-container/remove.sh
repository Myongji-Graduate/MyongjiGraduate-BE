#!/usr/bin/env sh
docker rm mg-mysql
docker rmi mysql:mysql:8.0.29

docker volume rm mg_mysql-container_config
docker volume rm mg_mysql-container_data
