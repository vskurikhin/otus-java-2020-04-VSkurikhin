#!/bin/sh

for R in 1 10 100 1000
do
  X=$(echo 'l('$R')/l(2)+1' | bc -l | sed -e 's/^\(.*\)\..*$/\1/')
  T=$(echo 'x=l('$X'/2)/l(2)+1; scale=0; 2^((x+0.5)/1)' | bc -l)
  C=$(echo 'scale=2;(((10^2)*'$R'/10)+0.5)/(10^2)*5' | bc | awk '{printf("%d\n",$1 + 0.5)}')
  echo "T="$T",C="$C",R="$R
  wrk2 -t$T -c$C -R$R -d5m --timeout 30s --u_latency -s ./scripts/firstName-surNames-wrk2.lua http://localhost:5000 | tee ./reports/wrk2-R$R.txt
done
