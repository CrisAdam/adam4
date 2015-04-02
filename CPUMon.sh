#!/bin/bash

hproc=`top -b -n 1 | head -n 8  | tail -n 1 | tr -s " " | cut -d' ' -f11`
test=`echo " $hproc < 5.0 " | bc -l`

if [ "$test" = "0" ]
then
top -b -n 1 | head -n 12  | tail -n 6 | mail -s "cpu" cristianradam@gmail.com
else
echo "false"
fi