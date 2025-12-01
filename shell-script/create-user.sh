#!/bin/bash 
read -p "Enter username:" username # -p stands for prompt
echo "you entered $username"

sudo useradd -m $username
echo "user $username created"

<< comment
to check user created or not 
go to /etc/passwd
comment
