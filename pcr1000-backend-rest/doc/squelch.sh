#!/bin/bash

SQUELCH=$1
echo Setting squelch to $SQUELCH

curl -XPOST 'http://127.0.0.1:8181/pcr1000/squelch' \
-H 'Content-type: application/json' -d '
{
	"level": '${SQUELCH}'
}'
