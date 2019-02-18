#!/bin/bash
echo $DROP
IFS=';' read -ra ADDR <<< "$DROP"
for i in "${ADDR[@]}"; do
    # $i has rule in form src,dest
    IFS=', ' read -ra SRCDST <<< "$i"
    iptables -A FORWARD -s "${SRCDST[0]}" -d "${SRCDST[1]}" -j REJECT
done
while true; do sleep 15 ; done