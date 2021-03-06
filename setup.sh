sudo yum install curl-devel expat-devel gettext-devel openssl-devel zlib-devel # install git
sudo yum install wget # to download java
cd /opt/
sudo wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u40-b25/jdk-8u40-linux-x64.tar.gz"
sudo tar xzf jdk-8u40-linux-x64.tar.gz
cd /opt/jdk1.8.0_40/
sudo alternatives --install /usr/bin/java java /opt/jdk1.8.0_40/bin/java 2
sudo alternatives --config java  # optional to configure which java to use if other versions already exist
export JAVA_HOME=/opt/jdk1.8.0_40 #set java variables
export JRE_HOME=/opt/jdk1.8.0_40/jre
export PATH=$PATH:/opt/jdk1.8.0_40/bin:/opt/jdk1.8.0_40/jre/bin # add java to path
sudo yum install glibc.i686 # to allow x32 java to function
sudo yum upgrade # update system