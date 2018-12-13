#!/bin/bash
JARURI=$1 # 第一个参数为jar包绝对路径
CONFENV=$2 # 第二个参数为配置环境
LOGSDIR=$3 # 第三个参数为日志目录

# 判断文件是否存在
if [ ! -f "$JARURI" ];then
    echo "ERROR:File does not exist;"
    exit 1
fi

# 判断日志目录是否存在
if [ ! -d "$LOGSDIR" ];then
    echo "ERROR:Directory does not exist;"
    exit 1
fi

# 判断配置环境
ENVARR=("dev" "test" "prod")
if echo "${ENVARR[@]}" | grep -w "$CONFENV" &>/dev/null; then
    :
else
    echo "ERROR:Env does not exist;"
    exit 1
fi

# 修改时区
ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 启动
OLD_IFS="$IFS"
IFS="/"
file_array=($JARURI)
IFS="$OLD_IFS"
file_array_len=${#file_array[*]}
file_name_index=`expr $file_array_len - 1`
log_uri=${LOGSDIR%*/}/${file_array[$file_name_index]}_"$CONFENV"_$(date "+%Y_%b_%d_%H_%M_%S_%N").txt
exec java -jar $JARURI > $log_uri --spring.profiles.active=$CONFENV &
