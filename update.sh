#!/bin/bash

#test update5

gitDir=$HOME/adam4
activeDir=$gitDir/run
config=`cat $HOME/config.txt`
srcDir=$gitDir/SFAServerWorkspace/SFAServer/src

compile()
{
	#remove unrelated projects
	rm -r $srcDir/com/adam4/misc
	rm -r /home/ec2-user/run/com
	#remove test files that may not compile
	find $srcDir -type f -name '*test*.java' -delete 
	find $srcDir -type f -name '*Test*.java' -delete 

	/opt/jdk1.8.0_40/bin/javac -cp .:/home/ec2-user/adam4/SFAServerWorkspace/SFAServer/src/javax.mail.jar:/home/ec2-user/adam4/SFAServerWorkspace/SFAServer/src/mysql-connector-java-5.1.31-bin.jar -d $HOME/run/ $(find $srcDir -name *.java)
}

waitForShutdown()
{
	if [ -a $HOME/SFAServer.run ]
	then
		echo "run file exists, deleting"
		rm $HOME/SFAServer.run
		sleepTimer=0
		while [  $sleepTimer -lt 30 ]; do
			echo "waiting up to $sleepTimer /30 seconds for shutdown"
			sleep 1
			check=`pgrep java | wc -w`
			if [ $check -eq 0 ]
			then
				let sleepTimer=sleepTimer+60
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
}

run()
{
cd $HOME && nohup /opt/jdk1.8.0_40/bin/java -cp $HOME/run/:/home/ec2-user/adam4/SFAServerWorkspace/SFAServer/src/javax.mail.jar:/home/ec2-user/adam4/SFAServerWorkspace/SFAServer/src/mysql-connector-java-5.1.31-bin.jar com.adam4.SFA.SFAServer $config >> /dev/null 2>> /dev/null &
	startTimer=0
		while [  $startTimer -lt 10 ]; do
			echo "waiting up to $startTimer /10 seconds for startup"
			sleep 1
			if [ -a $HOME/SFAServer.run ]
			then
				startTimer=40
			fi
			let startTimer=startTimer+1
				
		done
		if [ $startTimer -eq 10 ]
		then
			echo "run file missing, unable to restart `date`" | mail -s `hostname` cristianradam@gmail.com
		fi
}

oldstate=`git log -1 | grep "commit"`

cd $gitDir && git reset --hard HEAD > gitstate.txt
cd $gitDir && git fetch origin
cd $gitDir && git reset --hard origin/master
cd $gitDir && git pull

newstate=`git log -1 | grep "commit"`

echo "old: $oldstate new: $newstate"

if [ "$oldstate" != "$newstate" ]
then
	compile
	if [ $? -eq 0 ]
	then
		echo "successful compile"
		waitForShutdown
		run
	else
	mail=`echo -e "updated to \n"`
	gitstate=`cat gitstate.txt`
	mail=`echo -e "$mail $gitstate \n"`
	mail=`echo -e "$mail  $oldstate to \n"`
	mail=`echo -e "$mail  $newstate \n"`
	mail=$mail`date` $killed
	echo $mail | mail -s `hostname` cristianradam@gmail.com
	fi
fi

if [ ! -z "$1" ] 
then
	compile
	waitForShutdown
	run
fi

