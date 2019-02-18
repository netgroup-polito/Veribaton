#!/bin/bash

echo $deny
IFS=';' read -ra ADDR <<< "$deny"
for i in "${ADDR[@]}"; do
    # $i has rule in form src,dest
    IFS=',' read -ra SRCDST <<< "$i"
    #####IFS=':' read -ra SRCPORT <<< ${SRCDST[0]}
    #####IFS=':' read -ra DSTPORT <<< ${SRCDST[1]}
    echo "iptables -A FORWARD -s ${SRCDST[0]} -d ${SRCDST[1]} -j REJECT"
    iptables -A FORWARD -s "${SRCDST[0]}" -d "${SRCDST[1]}" -j REJECT
done

echo $allow
IFS=';' read -ra ADDR <<< "$allow"
for i in "${ADDR[@]}"; do
    # $i has rule in form src,dest
    IFS=',' read -ra SRCDST <<< "$i"
    #####IFS=':' read -ra SRCPORT <<< ${SRCDST[0]}
    #####IFS=':' read -ra DSTPORT <<< ${SRCDST[1]}
    echo "iptables -A FORWARD -s ${SRCDST[0]} -d ${SRCDST[1]} -j ACCEPT"
    iptables -A FORWARD -s "${SRCDST[0]}" -d "${SRCDST[1]}" -j ACCEPT
done

if [[ $defaultAction = [Aa][Ll][Ll][Oo][Ww] ]]; then
    echo "default policy accept"
    iptables -t filter -A FORWARD -j ACCEPT
elif [[ $defaultAction = [Dd][Ee][Nn][Yy] ]]; then
    echo "default policy reject"
    iptables -t filter -A FORWARD -j REJECT
fi

while true; do sleep 15 ; done

