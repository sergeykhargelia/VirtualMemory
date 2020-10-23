#!/bin/bash
test_count=10
error_count=0
for ((current_test=1; current_test <= test_count; current_test++))
do
  java -jar build/libs/VirtualMemory.jar "data/input$current_test.txt" check.txt
  if [[ $? != 0 ]]
  then
    echo "Test $current_test: Failed"
    error_count=$($error_count + 1)
    continue
  fi
  if (diff "data/output$current_test.txt" check.txt)
  then
    echo "Test $current_test: Passed"
  else
    error_count=$($error_count + 1)
    echo "Test $current_test: Failed"
  fi
done
exit $error_count
