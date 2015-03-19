#!/bin/bash

#test update5

gitDir=$HOME/adam4
activeDir=$gitDir/run
config=`cat $HOME/config.txt`
srcDir=$gitDir/SFAServerWorkspace/SFAServer/src

oldstate=`git log -1 | grep "commit"`

cd $gitDir && git reset --hard HEAD > gitstate.txt
cd $gitDir && git fetch origin
cd $gitDir && git reset --hard origin/master
cd $gitDir && git pull

newstate=`git log -1 | grep "commit"`

if [ "$oldstate" != "$newstate" ]
then
	#remove unrelated projects
	rm -r $srcDir/com/adam4/misc

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
			sleepTimer=0
			while [  $sleepTimer -lt 30 ]; do
				sleep 1
				check=`pgrep java | wc -w`
				if [ $check -eq 0 ]
				then
					let sleepTimer=sleepTimer+60
				else
					echo "waiting up to $sleepTimer /30 seconds for shutdown"
				fi
				let sleepTimer=sleepTimer+1
				
			done
			let sleepTimer=sleepTimer-60
			if [ $sleepTimer -eq 30 ]
			then
			killed=`pgrep java | tr '\n' ' '`
			killed=`echo -n "had to force kill $killed"`
			pkill -9 java
			fi
		fi
	nohup /opt/jdk1.8.0_40/bin/java -cp $HOME/run/ com.adam4.SFA.SFAServer $config >> /dev/null 2>> /dev/null &
	mail=`echo -e "updated to \n"`
	gitstate=`cat gitstate.txt`
	mail=`echo -e "$mail $gitstate \n"`
	mail=`echo -e "$mail  $oldstate to \n"`
	mail=`echo -e "$mail  $newstate \n"`
	mail=$mail`date` $killed
	echo $mail | mail -s `hostname` cristianradam@gmail.com
	else
		echo -e "failed compile to `cat gitstate.txt`
	   on `date`  )" | mail -s `hostname` cristianradam@gmail.com
	fi
	
	
else
	
fi

sleep 5

#check for successful running instance, else restart
if [ -a /home/ec2-user/adam4/SFAServer.run ]
then
	echo "run file found"
else
	startTimer=0
			while [  $startTimer -lt 10 ]; do
				sleep 1
				if [ -a /home/ec2-user/adam4/SFAServer.run ]
				then
					startTimer=40
				fi
				let startTimer=startTimer+1
				echo "waiting up to $startTimer /10 seconds for startup"
			done
			if [ $startTimer -eq 10 ]
			then
				nohup /opt/jdk1.8.0_40/bin/java -cp $HOME/run/ com.adam4.SFA.SFAServer $config >> /dev/null 2>> /dev/null &
				echo "run file missing - restarting `date`" | mail -s `hostname` cristianradam@gmail.com
			fi
	
fi



