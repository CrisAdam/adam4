#!/bin/bash
activeDir=/home/ec2-user/adam4/run
gitDir=/home/ec2-user/adam4
config=`cat /home/ec2-user/config.txt`
srcDir=$gitDir/SFAServerWorkspace/SFAServer/src


cd $gitDir && git reset --hard HEAD
cd $gitDir && git fetch origin
cd $gitDir && git reset --hard origin/master
#remove unrelated projects
rm -r $srcDir/com/adam4/misc
rm -r $srcDir/com/adam4/irc
rm -r $srcDir/com/adam4/spacenet
#remove test files that may not compile
find $srcDir -type f -name '*test*.java' -delete 
find $srcDir -type f -name '*Test*.java' -delete 

/opt/jdk1.8.0_40/bin/javac $(find $srcDir -name *.java)



if [ $? -eq 0 ]
then
	if [ -a /home/ec2-user/run/SFAServer.run ]
	rm /home/ec2-user/run/SFAServer.run
	sleep 60
	pkill -9 java
	fi
nohup /opt/jdk1.8.0_40/bin/java -cp $activeDir/* com.adam4.SFA.SFAServer $config >> /dev/null 2>> /dev/null &
fi


