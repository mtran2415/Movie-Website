import sys

if __name__ == "__main__":
    avg_TJ = 0
    avg_TS = 0
    numRows = 0
    with open(sys.argv[1]) as log:
        for line in log:
            split_line = line.split(";")
            avg_TJ = avg_TJ + float(split_line[1])
            avg_TS = avg_TS + float(split_line[2])
            numRows = numRows + 1
    
    print(numRows)
    print("Average TJ: " + str(avg_TJ/numRows/1000000))
    print("Average TS: " + str(avg_TS/numRows/1000000))

