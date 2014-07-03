#!/bin/bash

 


backupDir=/SFA/backups
activeDir=/SFA/StarFleetAssaultServer/Server
gitDir=/staging/SFA/StarFleetAssaultServer
package=com/adam4/SFA
url=ec2-54-187-17-28.us-west-2.compute.amazonaws.com
config=`cat /SFA/config/ServerConfig.txt`



if [ $2 ]
then
graceTime=`echo $2"s"`
else
graceTime=5s
fi


status=`mktemp`

archive=`date +%Y%m%d`
archiveDir=`echo $backupDir/$archive`

mkdir $archiveDir 2> /dev/null # backup may already have been run this date
if [ $? -eq 0 ]
then

#cp logs and errors to backup directory?


mkdir $archiveDir/Profiles/
cp -r $activeDir/Profiles/ $archiveDir/

fi


cd $gitDir && git reset --hard HEAD >> $status
cd $gitDir && git pull >> $status


if [ "$status" != "Already up-to-date." ];
then
cd $gitDir && git fetch origin >> $status
cd $gitDir && git reset --hard origin/master >> $status

# kill active 
rm $activeDir/run 2> /dev/null

cd $gitDir && find -name '*Test.java' -type f -delete
javac -cp $gitDir/Server/src/$package/*.java >> $status

mv $gitDir/Server/src/$package/*.class $activeDir/bin/$package/

#allow chance for it to gracefully stop
sleep $graceTime
#force stop if it fails to stop gracefully
pkill -9 java
#restart
nohup java -cp $activeDir/lib/*:$activeDir/bin/ $package.Server $config >> /dev/null 2>> /dev/null &

#give time for it to start-up
sleep $graceTime

fi


#run test client
nohup java -cp $activeDir/lib/*:$activeDir/bin/ $package.TestClient $url >> $status 2>>  $status &
#give test client time to test
sleep $graceTime

echo `date` " $1 $2 " >> $status

if [ $1 ]
then
cat $status | tr -cd '\11\12\15\40-\176' | mail -s "SFA code update status" $1
else
cat $status
fi

rm $status






