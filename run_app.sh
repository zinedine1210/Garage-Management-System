#!/bin/bash
java -cp "src/main/java:$(find ~/.m2/repository/com/mysql/mysql-connector-j -name '*.jar' | head -n 1):$(find ~/.m2/repository/org/jfree/jfreechart -name '*.jar' | head -n 1)" com.mycompany.garagemanagementsystem.GarageManagementSystem
