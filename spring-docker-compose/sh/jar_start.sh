#!/bin/bash
JAR_DIR='/data'
JAR_FILE=''
LOG_DIR='/data/logs'
CONF_ENV='dev'
JAR_NUM=1

if read -t 30 -p "Enter the jar storage directory(default: $JAR_DIR): " I_JAR_DIR
then

    if [ "$I_JAR_DIR" != "" ]
    then
        if [ ! -d "$I_JAR_DIR" ];then
            echo "Error: Directory does not exist;"
            exit 1
        fi
        JAR_DIR=$I_JAR_DIR
    fi

else
    echo
    echo "Error: Sorry, too slow"
    exit 1
fi

JAR_FILE_LIST=$(ls -1 $JAR_DIR | awk '/.*\.jar$/')
if [ "$JAR_FILE_LIST" = "" ]
then
    echo "Error: There is no runnable jar package"
    exit 1
else
    echo "--------------------------------------------------"
    echo "$JAR_FILE_LIST" | awk '/.*\.jar$/{print NR,$0}'
    echo "--------------------------------------------------"

    if read -t 30 -p "Enter the line number to run the jar package(default: $JAR_NUM): " I_JAR_NUM
    then
        if [ "$I_JAR_NUM" = "" ];then
            I_JAR_NUM=$JAR_NUM
        fi
        if [ ! -n "$(echo $I_JAR_NUM| sed -n "/^[0-9]\+$/p")" ] || [ $I_JAR_NUM -lt 1 ]
        then
            echo "Error: Illegal jar package line number"
            exit 1
        else
            JARSTR=$(echo "$JAR_FILE_LIST" | awk '{print $0 "|||"}')
            OLD_IFS="$IFS"
            IFS="|||"
            JAR_TEM=($JARSTR)
            IFS="$OLD_IFS"
            JAR_ARRAY=()
            JAR_ARRAY_i=1
            for var in ${JAR_TEM[@]}
            do
                if [ "$var" != "" ]
                then
                    JAR_ARRAY[$JAR_ARRAY_i]=$var
                    JAR_ARRAY_i=$(($JAR_ARRAY_i+1))
                fi
            done
            JAR_NUM=$I_JAR_NUM
        fi

    else
        echo
        echo "Error: Sorry, too slow"
        exit 1
    fi
fi

if read -t 30 -p "Enter the log storage directory(default: $LOG_DIR): " I_LOG_DIR
then

    if [ "$I_LOG_DIR" != "" ]
    then
        if [ ! -d "$I_LOG_DIR" ];then
            echo "Error: Directory does not exist;"
            exit 1
        fi
        LOG_DIR=$I_LOG_DIR
    fi

else
    echo
    echo "Error: Sorry, too slow"
    exit 1
fi

if read -t 30 -p "Input configuration environment(default: $CONF_ENV): " I_CONF_ENV
then

    if [ "$I_CONF_ENV" != "" ]
    then
        CONF_ENV=$I_CONF_ENV
    fi

else
    echo
    echo "Error: Sorry, too slow"
    exit 1
fi

ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
LOG_URI=${LOG_DIR%*/}/${JAR_ARRAY[$JAR_NUM]}_"$CONF_ENV"_$(date "+%Y_%b_%d_%H_%M_%S_%N").txt
exec java -jar ${JAR_DIR%*/}/${JAR_ARRAY[$JAR_NUM]} > $LOG_URI --spring.profiles.active=$CONF_ENV &
echo "The successful running."