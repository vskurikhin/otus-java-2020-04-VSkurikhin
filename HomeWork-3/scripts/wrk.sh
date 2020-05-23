#!/bin/sh

for C in 1 10 100 1000
do
  X=$(echo 'l('$C')/l(2)+1' | bc -l | sed -e 's/^\(.*\)\..*$/\1/')
  T=$(echo 'x=l('$X'/2)/l(2)+1; scale=0; 2^((x+0.5)/1)' | bc -l)
  echo "T="$T",C="$C
  wrk -t$T -c$C -d5m --timeout 30s --latency -s ./scripts/firstName-surNames-wrk.lua http://localhost:5000 | tee ./reports/wrk-C$C.txt
done
