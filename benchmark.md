# dblp-explorer
This is the Homework 5 Assignment.

Unfortunately, I learned after I completed the program that creating JSON objects in memory is not the best solution for this assignment.
My program parses the large text file with over 600,000 indices and converts them into over 600,000 JSON objects that are stored in 
memory.  If I had time to change the program, I would parse the data in the text file and create a JSON file with all of the information.
This would require less memory consumption and no active memory consumption within the program.

Due to the large amount of JSON objects created in memory during program execution, my current program uses approximately 984,169,368 bytes
of storage.  This memory consumption is printed out at the end of the program.

On the other hand, my program is more efficient with execution time.  I was able to implement concurrent tasks in my program by using
threads.  Every time a task involves searching through the 600,000+ JSON objects, my program creates 10 threads and divides the 600,000
JSON objects among each thread resulting in around 62,981 objects for each thread.  The computer systems with multiple cores can run a
thread on each core and complete these tasks faster.  This results in a program execution time around 7 seconds (on my computer).  
This execution time is printed at the end of my program.
