#!/bin/bash

sudo apt-get update
sudo apt-get install vim python-software-properties git -y


sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java7-installer -y
sudo apt-get install maven redis-server mongodb -y
