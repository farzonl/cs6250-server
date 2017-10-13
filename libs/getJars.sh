if [ ! -f avro-1.8.2.jar ]; then
	wget http://www.gtlib.gatech.edu/pub/apache/avro/avro-1.8.2/java/avro-1.8.2.jar
fi

if [ ! -f jackson-mapper-asl-1.9.13.jar ]; then
	wget http://central.maven.org/maven2/org/codehaus/jackson/jackson-mapper-asl/1.9.13/jackson-mapper-asl-1.9.13.jar
fi

if [ ! -f jackson-core-asl-1.9.13.jar ]; then
	wget http://central.maven.org/maven2/org/codehaus/jackson/jackson-core-asl/1.9.13/jackson-core-asl-1.9.13.jar
fi

if [ ! -f avro-ipc-1.8.2.jar ]; then
	wget http://www.gtlib.gatech.edu/pub/apache/avro/avro-1.8.2/java/avro-ipc-1.8.2.jar
fi

if [ ! -d opencv-3.3.0 ]; then
	if [ ! -f opencv-3.3.0.zip]; then
		wget https://github.com/opencv/opencv/archive/3.3.0.zip
	fi
	unzip 3.3.0.zip
fi

if [ ! -f opencv-3.3.0.jar ]; then
	echo "go to http://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html to build opencv jar"
fi

if [ ! -d netty-3.10.6.Final ]; then
	if [ ! -f netty-3.10.6.Final-dist.tar.bz2 ]; then
		wget http://dl.bintray.com/netty/downloads/netty-3.10.6.Final-dist.tar.bz2 
	fi
	tar -xvf netty-3.10.6.Final-dist.tar.bz2
fi

if [ ! -d slf4j-1.7.25 ]; then
	if [ ! -f slf4j-1.7.25.zip ]; then
		wget https://www.slf4j.org/dist/slf4j-1.7.25.zip
	fi
	unzip slf4j-1.7.25.zip
fi
