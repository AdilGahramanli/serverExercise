#!/bin/bash

for i in {1..10}
do

   #curl -X POST -H "Content-Type: application/json" -d "5" http://localhost:8080/counters/66d18233d47f877b4225825c

#   seq 1 10 | xargs -P 10 -I {} curl -X POST -H "Content-Type: application/json" -d "5" http://localhost:8080/counters/66d18233d47f877b4225825c

# Function to log output with timestamp and process ID
log_with_timestamp() {
    echo "$(date +'%Y-%m-%d %H:%M:%S') [$$] $1"
}

# Export the function to be used in xargs
export -f log_with_timestamp

# Run the curl commands in parallel and log output
seq 1 10 | xargs -n1 -P10 -I {} bash -c 'log_with_timestamp "Request #{}: $(curl -s -o /dev/null -w "%{http_code}" -X POST -H "Content-Type: application/json" -d "5" http://localhost:8080/counters/66d18233d47f877b4225825c)"'

done

wait
