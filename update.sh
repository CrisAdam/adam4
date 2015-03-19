#!/bin/bash

#test update
activeDir=/home/ec2-user/adam4/run
gitDir=/home/ec2-user/adam4
config=`cat /home/ec2-user/config.txt`
srcDir=$gitDir/SFAServerWorkspace/SFAServer/src

oldstate=`git log -1 | grep "commit"`

cd $gitDir && git reset --hard HEAD > gitstate.txt
cd $gitDir && git fetch origin
cd $gitDir && git reset --hard origin/master
cd $gitDir && git pull

newstate=`git log -1 | grep "commit"`

if [ "$oldstate" != "$newstate" ];
then
	#remove unrelated projects
	rm -r $srcDir/com/adam4/misc
#	rm -r $srcDir/com/adam4/irc
#	rm -r $srcDir/com/adam4/spacenet
	#remove test files that may not compile
	find $srcDir -type f -name '*test*.java' -delete 
	find $srcDir -type f -name '*Test*.java' -delete 

	/opt/jdk1.8.0_40/bin/javac $(find $srcDir -name *.java)

	if [ $? -eq 0 ]
	then
		echo "successful compile"
		if [ -a /home/ec2-user/adam4/SFAServer.run ]
		then
			echo "run file exists, deleting"
			rm /home/ec2-user/adam4/SFAServer.run
			echo "waiting 5 seconds for shutdown"
			sleep 5
			killed=`pgrep java`
			pkill -9 java
		fi
	nohup /opt/jdk1.8.0_40/bin/java -cp $HOME/run/ com.adam4.SFA.SFAServer $config >> /dev/null 2>> /dev/null &
	fi
	echo "updated to $newstate from $oldstate on `date`, had to kill $killed"| mail -s `hostname` cristianradam@gmail.com
fi

#check for successful running instance, else restart
if [ -a /home/ec2-user/adam4/SFAServer.run ]
then
	echo "run file found"
else
	nohup /opt/jdk1.8.0_40/bin/java -cp $HOME/run/ com.adam4.SFA.SFAServer $config >> /dev/null 2>> /dev/null &
	echo "run file missing - restarting `date`" | mail -s `hostname` cristianradam@gmail.com
fi



