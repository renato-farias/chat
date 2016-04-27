#!/bin/bash
source ~/.profile
exec java -Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=true -jar hello-cometd-1.0.jar --webxml=web.xml --config=chat.properties --log4j=log4j.properties
