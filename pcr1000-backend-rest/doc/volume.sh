#!/bin/bash

VOLUME=$1
echo Setting volume to $VOLUME

curl -XPOST 'http://127.0.0.1:8181/pcr1000/volume' \
-H 'Content-type: application/json' -d '
{
	"level": '${VOLUME}'
}'
