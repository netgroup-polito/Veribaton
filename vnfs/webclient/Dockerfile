FROM node:latest

RUN apt-get update && apt-get install -y \
	bridge-utils \
	net-tools \
    inetutils-traceroute
ADD run.sh /tmp/run.sh
RUN chmod +x /tmp/run.sh
ENTRYPOINT ["/tmp/run.sh"]
