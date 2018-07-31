# cs6250-server

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/78aaf29c243e4b6d8dc3e4eba46ddff7)](https://app.codacy.com/app/farzonl/cs6250-server?utm_source=github.com&utm_medium=referral&utm_content=farzonl/cs6250-server&utm_campaign=badger)

## Required Tools to Build
* JDK8
* Android Studio
  * Android Testing Device
    * Android Emulator or Physical Device
    * Running Android 8.0
    * OpenCV Manager APK installed (See "Android Client Libs" Below)
* Eclipse IDE

## Required Libraries to Build Server
* All `<classpathentry>` entries of `kind="lib"` in [the .classpath file](./.classpath) specify JARs which must be downloaded and placed (Unix: requires building, Windows: prebuilt available) into the relative locations specified in the file (ignore "cs6250/gradle/...") (ensure that OpenCV native libraries are in their location, see the `<attribute>` child tag in the file (DLL, x86, and x64))
  * Download Links
    * [avro-1.8.2](http://www.gtlib.gatech.edu/pub/apache/avro/avro-1.8.2/java/avro-1.8.2.jar)
    * [jackson-mapper-asl-1.9.13](http://central.maven.org/maven2/org/codehaus/jackson/jackson-mapper-asl/1.9.13/jackson-mapper-asl-1.9.13.jar)
    * [jackson-core-asl-1.9.13](http://central.maven.org/maven2/org/codehaus/jackson/jackson-core-asl/1.9.13/jackson-core-asl-1.9.13.jar)
    * [avro-ipc-1.8.2](http://www.gtlib.gatech.edu/pub/apache/avro/avro-1.8.2/java/avro-ipc-1.8.2.jar)
    * [netty-3.10.6](http://central.maven.org/maven2/io/netty/netty/3.10.6.Final/netty-3.10.6.Final.jar)
    * [slf4j-1.7.25](http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar)
    * [commons-compress-1.15](http://central.maven.org/maven2/org/apache/commons/commons-compress/1.15/commons-compress-1.15.jar)
    * OpenCV
      * [Win](https://sourceforge.net/projects/opencvlibrary/files/opencv-win/3.3.0/opencv-3.3.0-vc14.exe/download)
      * [Unix](https://sourceforge.net/projects/opencvlibrary/files/opencv-unix/3.3.0/opencv-3.3.0.zip/download)

## Initialize Submodule
Follow the [Git Book](https://git-scm.com/book/en/v2/Git-Tools-Submodules#_cloning_submodules) by Scott Chacon and Ben Straub, on how to initilize the cs650 submodule:
```sh
cd ./cs6250
git submodule init
git submodule update
```

## Required Libraries to Build Android Client
* [OpenCV-3.3.0 for Android](https://sourceforge.net/projects/opencvlibrary/files/opencv-android/3.3.0/opencv-3.3.0-android-sdk.zip/download)
  *  Copy the contents of `OpenCV-android-sdk/sdk/native/libs/` from the downloaded ZIP...
  * Into `./cs6250/openCVLibrary330/src/main/jniLibs/`
  * Also contains the APK for the emulator, if needed

## Iperf3:
  ------
  * This is the standard tool we use to measure the quality of a network
  *  connection. To compile this go the cs6250 submodule folder. It will be under app/jni.
  *  Copy the tar ball to another directory and decompress it. Then type the following commands:

    * $ ./configure
    * $ make

    * go to the source folder. There should an "iperf3" executable. Run this in a
    * terminal using:

    * $ iperf -s

    * This will start up the server side of the bandwidth measurement tool

