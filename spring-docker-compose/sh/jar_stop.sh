#!/bin/bash
echo "The jar package that is running:"
echo "--------------------------------------------------"
ps ax | grep "java -jar .*jar"
echo "--------------------------------------------------"
if read -t 30 -p "Input PID of the process: " PID
then

    if [ -n "$(echo $PID| sed -n "/^[0-9]\+$/p")" ]
    then
        kill -9 $PID
        echo "End process successful."
    fi

else
    echo
    echo "Error: Sorry, too slow"
    exit 1
fi