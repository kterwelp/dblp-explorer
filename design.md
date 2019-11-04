# dblp-explorer
This is the Homework 5 Assignment.

There were over 600,000 JSON objects created for this assignment.  

The program has to search through each object to find the article
titles that match the keyword input by the user.  

After these articles are found, the program has to search through each object again
to determine which articles cite the articles whose title contains the keyword for Tier 1.

For Tier 2, the program has to find the articles that cite the articles from Tier 1.

This continues for n number of Tiers and requires several searches through the large amount
of objects.  Therefore, my program divides the objects into 10 groups and has 10 different
threads perform these searches on around 60,000 objects. This allows these searches to occur 
simultaneously and results in a much more efficient program that takes around 7 seconds to execute.  

My program prints this execution time at the end of the program.
