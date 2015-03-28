#!/bin/bash

gitDir=$HOME/adam4
activeDir=$gitDir/run
config=`cat $HOME/config.txt`
config=`cat $HOME/config.txt | sed "/^-c$/d"`
srcDir=$gitDir/SFAServerWorkspace/SFAServer/src
compileresult=1 

run()
{
cd $HOME && nohup /opt/jdk1.8.0_40/bin/java -cp $HOME/run/:/home/ec2-user/adam4/SFAServerWorkspace/SFAServer/lib/javax.mail.jar:/home/ec2-user/adam4/SFAServerWorkspace/SFAServer/lib/mysql-connector-java-5.1.35-bin.jar com.adam4.SFA.SFAServer $config >> console 2>> errors &
}

compile()
{
	echo "compiling"
	#remove unrelated projects
	rm -r $srcDir/com/adam4/misc
	rm -rf /home/ec2-user/run/com 2> /dev/null 
	#remove test files that may not compile
	find $srcDir -type f -name '*test*.java' -delete 
	find $srcDir -type f -name '*Test*.java' -delete 

	/opt/jdk1.8.0_40/bin/javac -cp .:/home/ec2-user/adam4/SFAServerWorkspace/SFAServer/lib/javax.mail.jar:/home/ec2-user/adam4/SFAServerWorkspace/SFAServer/lib/mysql-connector-java-5.1.35-bin.jar -d $HOME/run/ $(find $srcDir -name *.java)
	
	compileresult=$?
	if [ $compileresult -eq 0 ]
	then
        echo "successful compile"
	fi
}

cd $gitDir && git reset --hard HEAD > gitstate.txt
cd $gitDir && git fetch origin
cd $gitDir && git reset --hard origin/master
cd $gitDir && git pull

pkill -9 java
rm -rf $HOME/logs $HOME/console $HOME/logs $HOME/SFAServer.run $HOME/errors $HOME/run
mkdir $HOME/run
compile
run
echo `pgrep java`





