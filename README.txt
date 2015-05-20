
Author - Moses Ike    http://www.mosesike.org      http://utdallas.edu/~mji120030

Artificial Intelligence Slide Puzzle Solver

This is a program that solves any 3X3 Slide Puzzle.
Given any solvable configuration of the 3X3 Slide Puzzle, 
this program is able to solve it  at the most EFFICIENT time and memory space.

I implemented the AI engine using a Recursive Best First Search Algorithm, 
using a modified manhattan distance strategy as a heuristic.


Also included are sample input files (specifying puzzle initial configuration), 
which you could use to run or demo or enjoy the program



Compilation.        (NOTE Please ignore the warning message for -xlint)
------------

javac Puzzle.java  BoardState2.java BinaryHeap.java
or
javac  *.java


Execution
----------
java Puzzle input.txt

where input.txt is the name of an input file. I provided several input files for your execution pleasure. e.g input36.txt



Thanks
Moses Ike