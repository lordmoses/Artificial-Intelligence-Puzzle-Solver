
import java.util.ArrayList;

public class BoardState2 implements Comparable<BoardState2>{

	ArrayList<int[][]>childrenBoard = new ArrayList<int[][]>();  //the potential board of the children
	ArrayList<BoardState2> childrenState = new ArrayList<BoardState2>(); //the state i.e the structure that holds the Board parameters
	ArrayList<Integer> childrenHeuristic = new ArrayList<Integer>();
	BoardState2 bestChild;
	int heuristic;
	static boolean IamRoot = false;
	static int[][] initialBoard = new int[3][3];
	int[][] myBoard = new int[3][3];
	BoardState2 parent;
	String birthMove;
	static BinaryHeap<BoardState2> heap = new BinaryHeap<BoardState2>(); 
	ArrayList<String> movesStack;
	ArrayList<int[][]> boardStack;
	//int numberOfMoves;
	public int numberOfMoves;
    static int nodesGenerated = 0;
	


	public BoardState2(int[][] currentBoard, int heuristic, Object parent, String move){
		/* for all boards */
		nodesGenerated++;
		this.parent = (BoardState2) parent;
		this.birthMove = move;
		movesStack = new ArrayList<String>();
		boardStack = new ArrayList<int[][]>();
		
		/* create your board */
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				myBoard[i][j] = currentBoard[i][j];
			}
		}
		
		/* For initial root board */
		if(!IamRoot){  //if nobody is root yet
			IamRoot = true;
			for(int i=0; i<3; i++){
				for(int j=0; j<3; j++){
					initialBoard[i][j] = currentBoard[i][j];
				}
			}
			
			numberOfMoves = 0;
			movesStack.add(birthMove);
			boardStack.add(myBoard);	
		}else{
			/* for descendants board, not root */
			numberOfMoves = this.parent.numberOfMoves + 1;
			/* copy your parents move sequence so far to your movesStack array. add your birth move */
			/* copy your parents move sequence so far to your movesStack array. add your birth move */

			for(int i=0; i< this.parent.movesStack.size(); i++){
				movesStack.add(this.parent.movesStack.get(i));
			}
			movesStack.add(birthMove);
			
			for(int i=0; i< this.parent.boardStack.size(); i++){
				boardStack.add(this.parent.boardStack.get(i));
			}
			boardStack.add(myBoard);	
		}
		this.heuristic = heuristic + numberOfMoves;
		heap.insert(this);

	}

	public void insert(BoardState2 childState){  
		childrenState.add(childState);
		childrenBoard.add(childState.myBoard);
		childrenHeuristic.add(childState.heuristic);
	}

	public void sortChildren(){
		/* Insertion Sort  */
		for(int i=1; i<childrenHeuristic.size(); i++){
			int temp = childrenHeuristic.get(i); 
			int[][] tempBoard = childrenBoard.get(i);
			BoardState2 tempState = childrenState.get(i);
		
			for(int j=i; j>0 && (compare(temp,childrenHeuristic.get(j-1)) <0); j--){
				childrenHeuristic.set(j,childrenHeuristic.get(j-1));
				childrenBoard.set(j,childrenBoard.get(j-1));
				childrenState.set(j,childrenState.get(j-1));

				childrenHeuristic.set(j-1, temp);
				childrenBoard.set(j-1, tempBoard);
				childrenState.set(j-1, tempState);
			}
		}
	}

	/* to be used by the insertion sort */
	public int compare(int temp, int o) {
		return temp < o ? -1 : temp == o ? 0 : 1;
	}
	
	/* to be used by the heap comparator BinaryHeap data structure */
	public int compareTo(BoardState2 p) {
		return this.heuristic < p.heuristic ? -1 : this.heuristic == p.heuristic ? 0 : 1;

	}

	public static void log(String string){
		System.out.print(string + "  ");
	}
	public static void log(int string){
		System.out.print(string + "  ");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

