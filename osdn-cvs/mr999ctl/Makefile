# Makefile for CV-Capture APPLICATION
PKG_CONFIG_PATH = ${GUMSTIXTOP}/tmp/cross/arm-angstrom-linux-gnueabi/lib/pkgconfig/
CXX = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-g++
CC = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-gcc

HEADERS = mydefine.h myproto.h io_usbio.h

PROGRAM = mr999ctl
CFLAGS =  -Wall -O3 `pkg-config --cflags opencv` `pkg-config --cflags libusb`
LIBS = `pkg-config --libs opencv``pkg-config --libs libusb`

.SUFFIXES: .o .c


SRCS = main.c parseOption.c cameraControl.c background.c prepare.c mainloop.c finish.c detectline.c io_usbio.c ctl_mr999.c

OBJS = $(SRCS:%.c=%.o)

$(PROGRAM) : $(OBJS)
	${CC} -o $(PROGRAM) $^ $(LIBS)

$(OBJS) : $(SRCS) $(HEADERS)
	$(CC) $(CFLAGS) -o $@ -c $(basename $(notdir $@)).c

#.c.o:
#	$(CC) $(CFLAGS) -c $<

clean:
	$(RM) $(PROGRAM) $(OBJS) *~ 
