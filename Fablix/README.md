## CS 122B Project 5

### Team
Marc Tran 64207734

Ethan Sanchez 70748407

### Parser:
We used a simple Python script to parse the logged data. Input was a log file with rows seperated by newlines and columns seperated by semicolons. Column 1 was the name of the movie, column 2 is the TJ for that search, and column 3 the TS. We simply added the entire column 2 and the entire column 3, and divided by the number of rows to get the average TJ/TS. We then divide by 1e6, to convert the value into milliseconds. The script then prints out the averages.
<br>
You can find the log files we created from this parser in the root of our Github repo under "Fablix Logs"