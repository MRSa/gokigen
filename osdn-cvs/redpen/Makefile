# Makefile for CV-Capture APPLICATION
PKG_CONFIG_PATH = ${GUMSTIXTOP}/tmp/cross/arm-angstrom-linux-gnueabi/lib/pkgconfig/
CXX = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-g++
CC = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-gcc

HEADERS = mydefine.h myproto.h

PROGRAM = redpen
CFLAGS = `pkg-config --cflags opencv` -Wall -O3
LIBS = `pkg-config --libs opencv`

.SUFFIXES: .o .c


SRCS = main.c parseOption.c cameraControl.c background.c prepare.c mainloop.c finish.c detectline.c

OBJS = $(SRCS:%.c=%.o)

$(PROGRAM) : $(OBJS)
	${CC} -o $(PROGRAM) $^ $(LIBS)

$(OBJS) : $(SRCS) $(HEADERS)
	$(CC) $(CFLAGS) -o $@ -c $(basename $(notdir $@)).c

#.c.o:
#	$(CC) $(CFLAGS) -c $<

clean:
	$(RM) $(PROGRAM) $(OBJS) *~ 
