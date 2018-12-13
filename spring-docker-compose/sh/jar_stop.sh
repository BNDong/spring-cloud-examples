#!/bin/bash
JARURI=$1 # 第一个参数为jar包名称
ps ax | grep "java -jar .*$JARURI" | grep -v grep | awk '{print $1}' | xargs kill -SIGTERM