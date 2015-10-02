#!/usr/bin/make -f

SOURCE_DIR= src/

all:
	cd $(SOURCE_DIR) && $(MAKE)

.PHONY : clean
clean :
	cd $(SOURCE_DIR) && $(MAKE) clean
