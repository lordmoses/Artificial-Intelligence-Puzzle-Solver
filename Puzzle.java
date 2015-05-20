
/* Author Moses Ike
/*  Artificial Intelligence Program to solve the 3 X 3 8 puzzle */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Puzzle {

	static String goalCoordinates[] = {"00","01","02","10","11","12","20","21","22"};
	static String possibleMoves[] = {"rd","lrd","ld","rud","lrud","lud","ru","lru","lu"};
	/* the moves above are relative to the '0' movement. so the '0' moving left --> piece moving right*/
	static char previousMove ='s';

	static boolean goalState = false;

	static int cycle = 0;

	public static void main(String[] args) {
		if (args.length != 1){
			System.out.println("Usage: java Puzzle8 input.txt");
			System.exit(1);
		}
        
        long startTime = System.nanoTime();
		int[][] currentBoard = new int[3][3];
		/* reading and parsing the input game text file and storing in 2 X 2 array */
		String filename = args[0]; //put directly inside the project folder
		try{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			int row = 0;
			while ((line = reader.readLine()) != null){
				int column = 0;
				for(int i=0; i<5; i++){
					if(line.charAt(i) != '\t'){
						currentBoard[row][column] = Integer.valueOf(line.substring(i,i+1));
						column++;
					}
				}
				row++;
			}
			log("\n");
			reader.close();
		}catch (Exception e){
			System.err.format("Exception occurred trying to read '%s'.", filename);
			e.printStackTrace();
		}

		int heuristic = calculateHeuristic(currentBoard);
		/* create the initial State object */
		BoardState2 initialState = new BoardState2(currentBoard, heuristic, null, "start");
		BoardState2 goal = initialState;

		if(heuristic != 0){
			goalState = false;
			//numberOfMoves++;
			cycle++;

			while(!goalState){
				goal = optimizeState(initialState.heap.findMin().myBoard,initialState.heap.findMin());
			}
		}
        
        long estimatedTime = System.nanoTime() - startTime;
		log("\nPuzzle Solved !! by Moses AI\n\n");
		/* printing out the returned goal state board */
		for(int i =0; i<3; i++){
			for(int a=0; a<3; a++){
				log(goal.myBoard[i][a] + "   " );
			}
			log("\n");
		}
		log("\n" + goal.numberOfMoves + " move(s)\n");

		/* printing out moves */
		int rows=0;
		int size = goal.movesStack.size();
		for(int a=0; a<size; a++){
			if(rows%5 == 0){
				log("\n");
			}
			log(goal.movesStack.get(a) +"      " );
			rows++;
		}
		log("\n\n");

		/* printing the board transitions */
		size = goal.boardStack.size();
		for(int a=0; a<size; a++){
			for(int i =0; i<3; i++){
				for(int j=0;j<3; j++){
					log(goal.boardStack.get(a)[i][j] + "   " );
				}
				log("\n");
			}
			log("\n");
		}
        log("Nodes Generated: " + goal.nodesGenerated + "\n");
		log("Time Elapsed: " + (double)estimatedTime/1000000000 + " seconds");
	}

	public static int calculateHeuristic(int[][] currentBoard){
		int heuristic = 0;
		for(int x1=0; x1<3; x1++){

			for(int y1=0; y1< 3; y1++){
				int value = currentBoard[x1][y1];
				if(value == 0){
					/* do nothing, 0 means its blank */
				}else{
					String x2y2 = goalCoordinates[value];
					int x2 = Integer.valueOf(x2y2.substring(0,1));
					int y2 = Integer.valueOf(x2y2.substring(1));
					heuristic += Math.abs(x1 - x2) + Math.abs(y1 - y2);
				}
			}
		}
		return heuristic;
	}

	public static BoardState2 optimizeState(int[][] currentBoard, BoardState2 boardState2){
		/* get where zero is on the board */
		boolean found = false;
		int x1 =0, y1 = 0;
		String possibleMove = "";
		for( x1=0; x1<3; x1++){
			for(y1=0; y1< 3; y1++){
				if(currentBoard[x1][y1] == 0){
					possibleMove = possibleMoves[3*x1+y1];
					found = true;
					break;
				}
			}
			if(found){
				break;
			}
		}

		/* Proceed to delete yourself from the binary heap if you dont have a goal */
		boardState2.heap.deleteMin();

		/* analysis each possible move, perform the move with min heuristic that leads closer to the goal state */
		char minHeuristicMove = 'a'; int currentMinHeuristic = 999999999; //Maximun int value
		if(cycle > 1 ){
			previousMove = boardState2.birthMove.charAt(0);
		}

		//log("previous move: " + previousMove + "\n");
		int index = 0;
		for(int i=0; i<possibleMove.length(); i++){
			if(possibleMove.charAt(i) != reverse(previousMove)){
				int tempHeuristic = analyzeMove(possibleMove.charAt(i), x1, y1, currentBoard,boardState2);

				if(tempHeuristic == 0){
					goalState = true;

					return boardState2.childrenState.get(index);
				}

				if(tempHeuristic < currentMinHeuristic){ 
					currentMinHeuristic = tempHeuristic;
					minHeuristicMove = possibleMove.charAt(i);
				}	
			}else{
				index--;
			}
			index++;
		}

		boardState2.sortChildren();  /* insertion sort of children based on their heuristic */
		/*
		log(" <sorted>possible moves \n");
		for(int a=0; a<boardState2.childrenBoard.size(); a++){
			for(int i =0; i<3; i++){
				for(int j=0;j<3; j++){
					log(boardState2.childrenBoard.get(a)[i][j] + "   " );
				}
				if(i == 2){
					log("cost(" + boardState2.childrenState.get(a).birthMove + ") = "
							+ boardState2.childrenHeuristic.get(a));
				}
				log("\n");
			}
			log("\n");
		}
		cycle++;
		/*
	if(cycle > 3){
		log("cycle exceeded \n");
		System.exit(0);
	}
		 */

		/* RECURSION BLOCK */
		/* if my first child is still the best among the frontier */
		BoardState2 goal;
		if(boardState2.childrenHeuristic.get(0)  < boardState2.heap.findMin().heuristic){
			//log("expanding first child "  + " move_count: " + (boardState2.numberOfMoves + 1) + "\n");
			previousMove = minHeuristicMove;  //here we confirm that the move will be made
			boardState2.heap.deleteMin();
			int [][] workingBoard = boardState2.childrenBoard.get(0);
			goal = optimizeState(workingBoard, boardState2.childrenState.get(0));

		}{
			//log("returning to base initial\n");
			goal = boardState2;
		}

		return goal;

	}

	public static int analyzeMove(char move, int x, int y, int[][] currentBoard, BoardState2 boardState2){
		/* copy currentBoard into a temporarily board */
		int analysisBoard[][] = {{0,0,0},{0,0,0},{0,0,0}};
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				analysisBoard[i][j] = currentBoard[i][j];
			}
		}
		int heuristic;

		if(move == 'l'){ //moves zero to the left --> moves the number N right
			/* basically swapping positions */
			int temp = analysisBoard[x][ y-1];
			analysisBoard[x][y-1] = 0;
			analysisBoard[x][y] = temp;

			heuristic = calculateHeuristic(analysisBoard);
			boardState2.insert(new BoardState2(analysisBoard, heuristic, boardState2, "left"));

		}else if(move == 'r'){ //moves zero to the right
			int temp = analysisBoard[x][ y+1];
			analysisBoard[x][y+1] = 0;
			analysisBoard[x][y] = temp;

			heuristic = calculateHeuristic(analysisBoard);
			boardState2.insert(new BoardState2(analysisBoard, heuristic, boardState2, "right"));

		}else if(move == 'u'){ //moves zero up
			int temp = analysisBoard[x-1][ y];
			analysisBoard[x-1][y] = 0;
			analysisBoard[x][y] = temp;

			heuristic = calculateHeuristic(analysisBoard);
			boardState2.insert(new BoardState2(analysisBoard, heuristic, boardState2, "up"));

		}else if(move == 'd'){ //moves zero down
			int temp = analysisBoard[x+1][ y];
			analysisBoard[x+1][y] = 0;
			analysisBoard[x][y] = temp;

			heuristic = calculateHeuristic(analysisBoard);
			boardState2.insert(new BoardState2(analysisBoard, heuristic, boardState2, "down"));

		}else{
			log("ERROR ERROR !! invalid move type");
			heuristic = calculateHeuristic(analysisBoard);
			boardState2.insert(new BoardState2(analysisBoard, heuristic, boardState2, "p"));
			System.exit(0);
		}

		return heuristic;
	}

	public static char reverse(char previousMove){
		if(previousMove == 'l'){
			return 'r';
		}else if(previousMove == 'r'){
			return 'l';
		}else if(previousMove == 'u'){
			return 'd';
		}else if(previousMove == 'd'){
			return 'u';
		}else if(previousMove == 's'){ //0 is the starting previous node initialization
			return previousMove;
		}else{
			log("ERROR ! ERROR Invalid  reverse of previous move\n");
			System.exit(0);
			return previousMove;
		}
	}

	public static void log(String string){
		System.out.print(string);
	}
	public static void log(int string){
		System.out.print(string);
	}
}
