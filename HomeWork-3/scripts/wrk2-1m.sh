#!/bin/sh

wrk2 -t8 -c50 -R200 -d1m --timeout 60s --u_latency \
  -s ./scripts/firstName-surNames-wrk2-1m.lua http://localhost:5000 |
 tee ./reports/wrk2-R200.txt
