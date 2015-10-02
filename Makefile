#!/usr/bin/make -f

all:
	cd src/ && $(MAKE)

.PHONY : clean
clean :
	cd src/ && $(MAKE) clean
