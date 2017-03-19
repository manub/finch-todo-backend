#!/bin/bash

docker run --name some-postgres -e POSTGRES_PASSWORD=password -d postgres
