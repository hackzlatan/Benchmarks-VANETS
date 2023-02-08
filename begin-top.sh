echo "scritp: Geting process id of Omnetpp"
pid=$(pidof opp_run)
echo "pid: $pid"

echo "script: Getting Total system Memory (in kbytes)"
#top -d 1 -n 1 | grep Mem | awk '{print $3}'

top -d 1 -n 1 -b | grep Mem | awk '{print $2}' > parameters.out
top -p $pid -d 1 -b | grep opp_run >>  parameters.out &

#top -p $pid -n 10 -d 1  -b | grep opp_run > top-output.txt &
echo "script: Running top command in background" 
