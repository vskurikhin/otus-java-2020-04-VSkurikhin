#!/bin/sh

HOST=localhost

getNumberThreads() {
  case $1 in
    1) return 1 ;;
    2) return 2 ;;
    8) return 4 ;;
    10) return 4 ;;
    *) return 8 ;;
  esac
}

for C in 2
do
  getNumberThreads $C
  T=$?
  echo "T="$T",C="$C
  wrk -t$T -c$C -d1m --timeout 30s --latency -s ./scripts/transaction.localhost.lua http://$HOST:5000 | tee ./reports/wrk-C$C.txt
done
