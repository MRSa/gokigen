# Makefile for CV-Capture APPLICATION
PKG_CONFIG_PATH = ${GUMSTIXTOP}/tmp/cross/arm-angstrom-linux-gnueabi/lib/pkgconfig/
CXX = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-g++
CC = ${GUMSTIXTOP}/tmp/cross/bin/arm-linux-gcc


INCPATH = \
         -I ${GUMSTIXTOP}/tmp/staging/arm-angstrom-linux-gnueabi/include/gtk-2.0   \
         -I ${GUMSTIXTOP}/tmp/staging/arm-angstrom-linux-gnueabi/include/cairo     \
         -I ${GUMSTIXTOP}/tmp/staging/arm-angstrom-linux-gnueabi/include/glib-2.0  \
         -I ${GUMSTIXTOP}/tmp/staging/arm-angstrom-linux-gnueabi/include/pango-1.0 \


HEADERS = mydefine.h myproto.h

PROGRAM = mp3ctl
CFLAGS = `pkg-config --cflags opencv` $(INCPATH) -Wall -O3
LIBS = `pkg-config --libs opencv`

.SUFFIXES: .o .c


SRCS = main.c parseOption.c cameraControl.c background.c prepare.c  finish.c detectline.c mainloop.c launch_mpg123.c

OBJS = $(SRCS:%.c=%.o)

$(PROGRAM) : $(OBJS)
	${CC} -o $(PROGRAM) $^ $(LIBS)

$(OBJS) : $(SRCS) $(HEADERS)
	$(CC) $(CFLAGS) -o $@ -c $(basename $(notdir $@)).c

#.c.o:
#	$(CC) $(CFLAGS) -c $<

clean:
	$(RM) $(PROGRAM) $(OBJS) *~ 
