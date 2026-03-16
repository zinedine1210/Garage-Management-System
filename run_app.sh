#!/bin/bash
java -cp "target/classes:src/main/java:$(find ~/.m2/repository/com/mysql/mysql-connector-j -name '*.jar' | head -n 1)" com.mycompany.garagemanagementsystem.GarageManagementSystem
