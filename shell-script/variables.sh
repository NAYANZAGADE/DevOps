#!/bin/sh
<< comment 
anything here 
will not 
execute
comment

#variables
name="nayan"
echo "name is $name, and date is $(date)"

#user input
echo "enter your name:"
read username #user input value will be stored here
echo "your entered name is $username"