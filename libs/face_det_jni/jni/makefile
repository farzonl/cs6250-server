 #_____________G++ STUFF__________________________________________
UNAME := $(shell uname)

ifeq ($(UNAME), Darwin)
LOWER_VAR = `echo $(UNAME) | tr A-Z a-z`
CC = clang++ -stdlib=libc++
INCPATH = -I /opt/local/include/ -I /usr/local/include/ -I /usr/local/Cellar/dlib/19.7/include -I /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/include -I /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/include/$(LOWER_VAR) -I . -I /usr/local/Cellar/glog/0.3.5_1/include
LIBPATH = -L/Users/thang911/Desktop/cs6250-server/libs/opencv-3.3.0/release/lib -L/usr/local/Cellar/dlib/19.7/lib  -L /opt/local/lib/ -L /usr/local/lib/ -L /usr/lib
CCFLAGS = -dynamiclib -std=c++11
endif

OPTIONS = -lopencv_core -lopencv_imgproc -lopencv_video -lopencv_highgui -lopencv_imgcodecs -lpthread -ldlib -lblas -llapack

CFLAGS = $(INCPATH) $(LIBPATH) $(OPTIONS) $(CCFLAGS) -Wall
UNAME := $(shell uname)
EXE     := libface_det.dylib
#_____________G++________________________________________________
 
build-release : CFLAGS += -O3

build-release : jni_detections.o jni_common.o
	$(CC) $(CFLAGS) *.o -o $(EXE)

build-debug : CFLAGS += -g -DDEBUG
build-debug : jni_detections.o jni_common.o
	$(CC) $(CFLAGS) *.o -o $(EXE)

jni_detections.o : jni_detections/jni_face_det.cpp jni_detections/detector.h
	$(CC) $(CFLAGS) -c jni_detections/jni_face_det.cpp jni_detections/detector.h 

jni_common.o : jni_common/jni_fileutils.cpp  jni_common/jni_fileutils.h jni_common/jni_primitives.h jni_common/jni_utils.cpp jni_common/jni_utils.h jni_common/types.h
	$(CC) $(CFLAGS) -c jni_common/jni_fileutils.cpp  jni_common/jni_fileutils.h jni_common/jni_primitives.h jni_common/jni_utils.cpp jni_common/jni_utils.h jni_common/types.h

clean :
	rm *.o* $(EXE)*


