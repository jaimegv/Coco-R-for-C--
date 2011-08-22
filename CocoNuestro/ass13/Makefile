CFLAGS = -O2

LNFLAGS = 

OBJS = ass13.o

ass:		$(OBJS)
		gcc $(OBJS) -o ass $(LNFLAGS)
.c.o:
		gcc -c $(CFLAGS) $*.c
clean:
		rm -f $(OBJS)
		rm -f ass
install:
		rm -f *.o
