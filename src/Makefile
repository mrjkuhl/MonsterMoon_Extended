#!/usr/bin/make -f

JC= javac
JCFLAGS= -cp
ARC= jar
ARCFLAGS= cvf

MISC_FILES= ../config.yml ../COPYING ../plugin.yml ../README

PACKAGE_DIR= fi/nimbus/bukkit/plugin/monstermoon
PROPERTIES_DIR= /properties
EXECUTABLE= ../MonsterMoon_Extended.jar
CLASS_PATHS= ../buildtools/Spigot/Spigot-API/target/spigot-api-1.8.8-R0.1-SNAPSHOT.jar:../MonsterMoon.jar:.

SOURCES= $(PACKAGE_DIR)/Calendar.java $(PACKAGE_DIR)/Command.java \
$(PACKAGE_DIR)$(PROPERTIES_DIR)/DayBegins.java $(PACKAGE_DIR)/Day.java \
$(PACKAGE_DIR)$(PROPERTIES_DIR)/DayOrder.java \
$(PACKAGE_DIR)$(PROPERTIES_DIR)/Difficulty.java \
$(PACKAGE_DIR)$(PROPERTIES_DIR)/SpawnAnimals.java \
$(PACKAGE_DIR)/Properties.java $(PACKAGE_DIR)$(PROPERTIES_DIR)/Property.java \
$(PACKAGE_DIR)$(PROPERTIES_DIR)/PVP.java $(PACKAGE_DIR)/MonsterMoon.java \
$(PACKAGE_DIR)$(PROPERTIES_DIR)/SpawnMonsters.java $(PACKAGE_DIR)/World.java
OBJECTS= $(SOURCES:.java=.class)

all:
	$(JC) $(JCFLAGS) $(CLASS_PATHS) $(SOURCES)
	$(ARC) $(ARCFLAGS) $(EXECUTABLE) $(OBJECTS) $(MISC_FILES)

.PHONY: clean
clean:

	-rm $(OBJECTS) $(EXECUTABLE)
