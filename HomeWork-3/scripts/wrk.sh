#!/bin/sh

T=1
for C in 1 10 100 1000
do
  echo "T="$T",C="$C
  wrk -t$T -c$C -d5m --timeout 30s --latency -s ./scripts/firstName-surNames-wrk.lua http://localhost:5000 | tee ./reports/wrk-C$C.txt
  T=$(($T*2))
done
