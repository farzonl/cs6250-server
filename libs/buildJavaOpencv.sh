if [ -d opencv-3.3.0 ]; then
	cd opencv-3.3.0/

	if [ ! -d release ]; then
		echo "release dir created"
		mkdir release
	fi

	if [ ! -f release/bin/opencv-330.jar ]; then
		echo "building opencv-330.jar"
		cd release/
		cmake -D CMAKE_BUILD_TYPE=RELEASE -D BUILD_FAT_JAVA_LIB=ON ..
		make

		if [ -f lib/ibopencv_java330.dylib ]; then
			cd lib
			ln -s libopencv_java330.dylib libopencv_java330.so
		fi
	fi
fi
