#!/bin/sh
#
# Linux startup script for swift
#export JAVA_HOME="/ROOT/server/jdk"
#export JRE_HOME="/ROOT/server/jdk/jre"
export JAVA_OPTS="-server -Xms2048M -Xmx4096M -Xmn1024M -XX:SurvivorRatio=4 -Xss256k -XX:PermSize=128m -XX:MaxPermSize=256m -XX:-DisableExplicitGC -verbose:gc -XX:+UseParNewGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:-CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:ParallelCMSThreads=8 -XX:ParallelGCThreads=8 -XX:MaxTenuringThreshold=5 -XX:-UseAdaptiveSizePolicy -XX:TargetSurvivorRatio=90 -XX:+ScavengeBeforeFullGC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/ROOT/logs/swifts/stdout/oom-error_8089.log -Xloggc:/ROOT/logs/swifts/stdout/swifts_gc_8089.log"

ENCODE="-Dfile.encoding=UTF-8 -Dclient.encoding.override=UTF-8"
VMARGS="$ENCODE"
logfie=`date +%Y%m%d.log`
nohup java -server $VMARGS -Djava.net.preferIPv4Stack=true -Dswifts.home=. -Djava.library.path=. -jar libs/swifts-loader-0.1.0-SNAPSHOT.jar startup > ./logs/$logfie 2>&1 &
exit 0
