#!/bin/bash
ip route add "$(getent hosts "$nameWebServer" | awk '{ print $1 }')" via "$(getent hosts "$nextHop" | awk '{ print $1 }')"
while true; do sleep 15 ; done
