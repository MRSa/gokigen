# Makefile
#PKG_CONFIG_PATH = ${GUMSTIXTOP}/tmp/cross/arm-angstrom-linux-gnueabi/lib/pkgconfig/
#CXX = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-g++
#CC = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-gcc


HEADERS = 
#HEADERS = mydefine.h

PROGRAM = gles-test
CFLAGS = -O4 -Wall -march=armv7-a -mtune=cortex-a8 -mfloat-abi=softfp -mfpu=neon -g
LIBS = -lncurses -legl13 -lgles20

.SUFFIXES: .o .c



#SRCS = sample0.c 
SRCS = main.c preparation.c drawer.c

OBJS = $(SRCS:%.c=%.o)

$(PROGRAM) : $(OBJS)
	${CC} -o $(PROGRAM) $^ $(LIBS)

$(OBJS) : $(SRCS) $(HEADERS)
	$(CC) $(CFLAGS) -o $@ -c $(basename $(notdir $@)).c

#.c.o:
#	$(CC) $(CFLAGS) -c $<

clean:
	$(RM) $(PROGRAM) $(OBJS) *~ 
