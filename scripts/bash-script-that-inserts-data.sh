#!/usr/bin/env bash

DB_USER=$1
DB_HOST=$2
DB_PORT=$3
DB_NAME=$4
DB_PASSWORD=$5

echo "Hello, I'm a script"

PGPASSWORD=$DB_PASSWORD psql -qtAX -U "$DB_USER" -h "$DB_HOST" -p "$DB_PORT" "$DB_NAME" -c "INSERT INTO items (name) VALUES ('item 1');"

echo "Script done"