#!/bin/bash
echo "The container currently running:"
echo "--------------------------------------------------"

DOCKER_LIST=$(docker ps --format "{{.Names}}")
if [ "$DOCKER_LIST" = "" ]
then
    echo "Error: No containers are running"
    exit 1
else
    DCSTR=$(echo "$DOCKER_LIST" | awk '{print $0 "|||"}')
    OLD_IFS="$IFS"
    IFS="|||"
    DOCKER_TEM=($DCSTR)
    IFS="$OLD_IFS"
    DOCKER_ARRAY=()
    DOCKER_ARRAY_i=1
    for var in ${DOCKER_TEM[@]}
    do
        if [ "$var" != "" ]
        then
            DOCKER_ARRAY[$DOCKER_ARRAY_i]=$var
            DOCKER_ARRAY_i=$(($DOCKER_ARRAY_i+1))
        fi
    done
    echo "$DOCKER_LIST" | awk '{print NR,$0}'
fi

echo "--------------------------------------------------"

if read -t 30 -p "Please enter the container line number: " NUM
then

    if [ "$NUM" = "" ] || [ ! -n "$(echo $NUM| sed -n "/^[0-9]\+$/p")" ] || [ $NUM -lt 1 ]
    then
        echo "Error: Illegal container line number"
        exit 1
    else
        CPID=$(docker inspect --format "{{.State.Pid}}" ${DOCKER_ARRAY[$NUM]})
        nsenter --target "$CPID" --mount --uts --ipc --net --pid
    fi

else
    echo
    echo "Error: Sorry, too slow"
    exit 1
fi