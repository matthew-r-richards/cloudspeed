#!/usr/bin/env bash

#mvn clean package -T 4C
docker build -t 870594606895.dkr.ecr.eu-west-1.amazonaws.com/phud:0.1 .
docker push 870594606895.dkr.ecr.eu-west-1.amazonaws.com/phud:0.1