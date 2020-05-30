#!/bin/sh

HOST=hi-load-social-network.svn.su

getNumberThreads() {
  case $1 in
    1) return 1 ;;
    10) return 4 ;;
    *) return 8 ;;
  esac
}

for C in 1 10 100 1000
do
  getNumberThreads $C
  T=$?
  echo "T="$T",C="$C
  wrk -t$T -c$C -d3m --timeout 30s --latency -s ./scripts/firstName-surNames-wrk.lua http://$HOST | tee ./reports/wrk-C$C.txt
done
