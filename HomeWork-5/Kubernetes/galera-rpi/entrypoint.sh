#!/bin/bash

mkdir -p /run/mysqld
if ! chown mysql:mysql /run/mysqld ; then
  echo "Operation not permitted"
fi

ARGS="--pid-file=/run/mysqld/mysqld.pid --wsrep-cluster-name=$POD_SERVICE_NAME --wsrep-node-address=$POD_IP"
IP_LIST=$(dig +short +search $POD_SERVICE_NAME | grep -v $POD_IP | paste -s -d ',' -)

/docker-entrypoint.sh mysqld $ARGS --wsrep-cluster-address=gcomm://$IP_LIST
