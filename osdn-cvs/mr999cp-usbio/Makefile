############################################################
#   Makefile for MR-999CP CONTROL APPLICATION
#
############################################################

## PLEASE UNCOMMENT BELOW THREE LINES IF YOU USE FOR bc-9.
PKG_CONFIG_PATH = ${GUMSTIXTOP}/tmp/cross/arm-angstrom-linux-gnueabi/lib/pkgconfig/
CXX = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-g++
CC = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-gcc

HEADERS = io_usbio.h

PROGRAM = mr999cp-usbio
CFLAGS = `pkg-config --cflags libusb` -Wall -O3
LIBS = `pkg-config --libs libusb` -lncurses

.SUFFIXES: .o .c


SRCS = io_usbio.c mainloop.c main.c


OBJS = $(SRCS:%.c=%.o)

$(PROGRAM) : $(OBJS)
	${CC} -o $(PROGRAM) $^ $(LIBS)

$(OBJS) : $(SRCS) $(HEADERS)
	$(CC) $(CFLAGS) -o $@ -c $(basename $(notdir $@)).c

#.c.o:
#	$(CC) $(CFLAGS) -c $<

clean:
	$(RM) $(PROGRAM) $(OBJS) *~
