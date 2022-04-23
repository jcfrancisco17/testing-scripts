#!/usr/bin/env bash

DB_USER=$1
DB_HOST=$2
DB_PORT=$3
DB_NAME=$4
DB_PASSWORD=$5

PGPASSWORD=$DB_PASSWORD psql -qtAX -U "$DB_USER" -h "$DB_HOST" -p "$DB_PORT" "$DB_NAME" -c "INSERT INTO items (name) VALUES ('item 1');"
