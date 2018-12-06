#!/usr/bin/env bash

aws cloudformation create-stack --stack-name phud --template-body file://./cloud-formation.yml