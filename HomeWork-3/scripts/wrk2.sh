#!/bin/sh
for R in 1 10
do
  wrk2 -t2 -c4 -d5m -R$R --timeout 30s --u_latency -s ./scripts/firstName-surNames.lua http://localhost:5000 | tee wrk2-R$R.txt
done
for R in 100 1000
do
  wrk2 -t2 -c50 -d5m -R$R --timeout 30s --u_latency -s ./scripts/firstName-surNames.lua http://localhost:5000 | tee wrk2-R$R.txt
done
