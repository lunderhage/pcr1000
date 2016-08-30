#!/bin/bash

MODE=$1
FREQUENCY=$2
FILTER=$3

curl -XPOST 'http://127.0.0.1:8181/pcr1000/tune' \
-H 'Content-type: application/json' -d '
{
	"mode": "'${MODE}'",
	"filter": "'${FILTER}'",
	"frequency": '${FREQUENCY}'
}'
