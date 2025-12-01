#!/bin/bash
read -p "Enter the number: " num

if (( $num % 2 == 0 )); 
then
    echo "Number is even"
else
    echo "Number is odd"
fi

# #!/bin/bash

# read -p "Enter a number: " num

# if (( num > 0 )); 
# then
#     echo "The number is positive."
# elif (( num < 0 )); 
# then
#     echo "The number is negative."
# else
#     echo "The number is zero."
# fi

