import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class MazeSolver extends JFrame implements ActionListener {
	static final long serialVersionUID = 1;
	JButton setSizeButton = new JButton("Set Size");
	JButton resetButton = new JButton("Reset");
	JButton solveButton = new JButton("Solve");
	JTextField widthTF = new JTextField(5);
	JTextField heightTF = new JTextField(5);
	JLabel widthLabel = new JLabel("Width:");
	JLabel heightLabel = new JLabel("Height:");
	JScrollPane scrollPane = new JScrollPane();
	int mazeWidth = 15;
	int mazeHeight = 10;
	Maze maze;
	 

	public static void main(String args[]) {
		JFrame f = new MazeSolver();
		f.setVisible(true);
	}
	
	public MazeSolver() {
//set up the JFrame
		setLocation(5, 40);
		setTitle("Maze");
		setLayout(null);
		setSize(494, 409);
		setResizable(false);
//create an initial maze
		maze = new Maze(mazeWidth, mazeHeight);
		maze.setLocation(3, 3);
//set up a ScrollPane to contain the maze
		scrollPane.setLocation(20, 10);
		scrollPane.setSize(452, 330);
		scrollPane.add(maze);
		add(scrollPane);
//set up a Label for the width JTextField
		widthLabel.setLocation(20,351);
		widthLabel.setSize(43,16);
		add(widthLabel);
//set up the width JTextField
		widthTF.setLocation(65,348);
		widthTF.setSize(30,20);
		widthTF.setText(mazeWidth+"");
		add(widthTF);
//set up a Label for the height JTextField
		heightLabel.setLocation(110,351);
		heightLabel.setSize(50,16);
		add(heightLabel);
//set up the height JTextField
		heightTF.setLocation(157,348);
		heightTF.setSize(30,20);
		heightTF.setText(mazeHeight+"");
		add(heightTF);
//set up the setSize Button
		setSizeButton.setLocation(205,348);
		setSizeButton.setSize(85,20);
		add(setSizeButton);
		setSizeButton.addActionListener(this);
//set up the reset Button
		resetButton.setLocation(310,348);
		resetButton.setSize(70,20);
		add(resetButton);
		resetButton.addActionListener(this);
//set up the solve Button
		solveButton.setLocation(400,348);
		solveButton.setSize(70,20);
		add(solveButton);
		solveButton.addActionListener(this);
//catch the windowClosing event to quit the application
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == setSizeButton) {      //get width & height and create new maze
			try {
				int w = Integer.parseInt(widthTF.getText().trim());
				if (w > 16 || w < 1) {	//illegal width is reset to initial value of 15
					w = 15;
					widthTF.setText(w+"");
				}
				int h = Integer.parseInt(heightTF.getText().trim());
				if (h > 11 || h < 1) {	//illegal height is reset to initial value of 10
					h = 10;
					heightTF.setText(h+"");
				}
				mazeWidth = w;
				mazeHeight = h;
				scrollPane.remove(maze);
				maze = new Maze(mazeWidth,mazeHeight);
				scrollPane.add(maze);
			} catch (NumberFormatException nfe) {   //restore values if bad input
				widthTF.setText(mazeWidth+"");
				heightTF.setText(mazeHeight+"");
			}
		} else if (e.getSource() == resetButton) {  //clear doors and path
			maze.reset();
		} else if (e.getSource() == solveButton) {  //solve the current maze
			solveIt(maze,mazeWidth,mazeHeight);
		}
	}

//****************************************************************************************	
void solveIt(Maze maze, int width, int height) {
	// creation of point array
	ArrayList<Point> path = new ArrayList<Point>();

	//  creation of possible door points 
	Boolean[][] hasVisited = new Boolean[height][width];

	// looping through all possible points adding them to the 2d array  
	for (int i = 0; i<height; i++){
		for (int j =0; j<width; j++){
			//  set to false since none of the points have been visited yet
			hasVisited[i][j] = false;
	}}
	for (int i =0; i<hasVisited.length; i++){
		for (int j =0; j<hasVisited[i].length; j++){
			//System.out.println(hasVisited[i][j]);
		}
		
	}



	//  recursive function to add points to the path 
	ArrayList<Integer> start = maze.checkStartingPoint(width, height);
	int i = start.get(0);
	int j = start.get(1);
	int prevDoor = start.get(2);
	maze.recursiveSolve(width, height, i, j, path, hasVisited, prevDoor);
	if (!path.isEmpty()){
		maze.setPath(path);
	}
	

	//System.out.println(path);
	
	//  setting the path using the array of points
	return;
}

//****************************************************************************************	
}


class Maze extends Canvas {
	static final long serialVersionUID = 1;
	private static final int WALL_SIZE = 28;
	private int maze[][];
	private ArrayList<Point> path = null;
	
	public Maze(int width, int height) {
	//create an array of the specified size
		maze = new int[height][width];
		reset();
	//set the size of the canvas
		setSize(width*WALL_SIZE+2, height*WALL_SIZE+2);
	//listen for clicks in the canvas
	//change a door into a wall or a wall into a door
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int x = Math.max(0,Math.min(e.getX(),maze[0].length*WALL_SIZE-1));
				int y = Math.max(0,Math.min(e.getY(),maze.length*WALL_SIZE-1));
				int j = x / (WALL_SIZE);
				int i = y / (WALL_SIZE);
				if (x % (WALL_SIZE) <= 2) {            //wall on left of this room
					maze[i][j]   = maze[i][j]   ^ 8;     //toggle left door
					if (j > 0)
						maze[i][j-1] = maze[i][j-1] ^ 2; //toggle right door in adjacent room
				} else if (x % (WALL_SIZE) >= WALL_SIZE - 2) {  //wall on right of this room
					maze[i][j]   = maze[i][j]   ^ 2;     //toggle right door
					if (j < maze[0].length-1)
						maze[i][j+1] = maze[i][j+1] ^ 8;      //toggle left door in adjacent room
				} else if (y % (WALL_SIZE) <= 2) {     //wall above this room
					maze[i][j]   = maze[i][j]   ^ 1;     //toggle upper door of this room
					if (i > 0)
						maze[i-1][j] = maze[i-1][j] ^ 4; //toggle lower door in adjacent room
				} else if (y % (WALL_SIZE) >= WALL_SIZE - 2) {  //wall below this room
					maze[i][j]   = maze[i][j]   ^ 4;     //toggle lower door of this room
					if (i < maze.length-1)
						maze[i+1][j] = maze[i+1][j] ^ 1; //toggle upper door in adjacent room
				}
				repaint();
			}
		});
	}
	
	public void reset() {
	//clear all doors and the path
		for (int i=0; i<maze.length; i++)
			for (int j=0; j<maze[0].length; j++)
				maze[i][j] = 0;
		path = null;
		repaint();
	}
	
	public void paint(Graphics g) {
	//draw the doors and the path if there is one
		drawDoors(g);
		if (path != null)
			drawPath(g);
	}
	
	private void drawDoors(Graphics g) {
		int i,j;
		//for each room, draw door/wall above and to the left
		for (i=0; i<maze.length; i++)
			for (j=0; j<maze[0].length; j++) {
				if ((maze[i][j] & 1) != 0) {   //door above this room
					g.drawLine(j*WALL_SIZE,                i*WALL_SIZE,      j*WALL_SIZE + WALL_SIZE/4,i*WALL_SIZE);
					g.drawLine(j*WALL_SIZE + 3*WALL_SIZE/4,i*WALL_SIZE,  (j+1)*WALL_SIZE,              i*WALL_SIZE);
				} else {
					g.drawLine(j*WALL_SIZE,i*WALL_SIZE,  (j+1)*WALL_SIZE,i*WALL_SIZE);
				}
				if ((maze[i][j] & 8) != 0) {   //door to the left in this room
					g.drawLine(j*WALL_SIZE,i*WALL_SIZE,                j*WALL_SIZE,    i*WALL_SIZE + WALL_SIZE/4);
					g.drawLine(j*WALL_SIZE,i*WALL_SIZE + 3*WALL_SIZE/4,j*WALL_SIZE,(i+1)*WALL_SIZE);
				} else {
					g.drawLine(j*WALL_SIZE,i*WALL_SIZE,j*WALL_SIZE,(i+1)*WALL_SIZE);
				}
		}
		//for bottom row, draw door/wall below
		i = maze.length;
		for (j=0; j<maze[0].length; j++)
			if ((maze[i-1][j] & 4) != 0) {   //door below this room
				g.drawLine(j*WALL_SIZE,                i*WALL_SIZE,      j*WALL_SIZE + WALL_SIZE/4,i*WALL_SIZE);
				g.drawLine(j*WALL_SIZE + 3*WALL_SIZE/4,i*WALL_SIZE,  (j+1)*WALL_SIZE,              i*WALL_SIZE);
			} else {
				g.drawLine(j*WALL_SIZE,i*WALL_SIZE,  (j+1)*WALL_SIZE,i*WALL_SIZE);
			}
		//for right column, draw door/wall to the right
		j = maze[0].length;
		for (i=0; i<maze.length; i++)
			if ((maze[i][j-1] & 2) != 0) {   //door to the right in this room
				g.drawLine(j*WALL_SIZE,i*WALL_SIZE,                j*WALL_SIZE,    i*WALL_SIZE + WALL_SIZE/4);
				g.drawLine(j*WALL_SIZE,i*WALL_SIZE + 3*WALL_SIZE/4,j*WALL_SIZE,(i+1)*WALL_SIZE);
			} else {
				g.drawLine(j*WALL_SIZE,i*WALL_SIZE,j*WALL_SIZE,(i+1)*WALL_SIZE);
			}
	}
	
	private void drawPath(Graphics g) {
	//for each entry in the ArrayList<Point>, draw a line from the 
	//middle of that room to the middle of the next room
		for (int n=0; n<(path.size()-1); n++) {
			Point here = (Point)path.get(n);
			Point next = (Point)path.get(n+1);
			g.setColor(Color.red);
			g.drawLine(here.x*WALL_SIZE+WALL_SIZE/2, here.y*WALL_SIZE+WALL_SIZE/2,
			           next.x*WALL_SIZE+WALL_SIZE/2, next.y*WALL_SIZE+WALL_SIZE/2);
			g.setColor(Color.black);
		}
	}
	
	public void setPath(ArrayList<Point> path) {
	//put the path in our instance variable and redraw the maze
		this.path = path;
		repaint();
	}
	
	public boolean doorUp(int i, int j) {
	//true if there is a door going up from this room
		return (maze[i][j] & 1) != 0;
	}
	
	public boolean doorRight(int i, int j) {
	//true if there is a door going to the right from this room
		return (maze[i][j] & 2) != 0;
	}
	
	public boolean doorDown(int i, int j) {
	//true if there is a door going down from this room
		return (maze[i][j] & 4) != 0;
	}
	
	public boolean doorLeft(int i, int j) {
	//true if there is a door going to the left from this room
		return (maze[i][j] & 8) != 0;
	}


//****************************************************************************************	


	//  checking for the starting point in the maze 
	//  returns the starting point
	public ArrayList<Integer> checkStartingPoint(int width, int height){
		ArrayList<Integer> ans = new ArrayList<>();

    //  looping through the top row
    for (int col = 0; col < width; col++) {
        if (doorUp(0, col)) {
			ans.add(0);
			ans.add(col);
			ans.add(1);
            return ans;
        }
    }

    // looping through the rightmost column 
    for (int row = 0; row < height; row++) {
        if (doorRight(row, width - 1)) {
			ans.add(row);
			ans.add(width-1);
			ans.add(2);
            return ans;
        }
    }
    
    // looping through the bottom
    for (int col = 0; col < width; col++) {
        if (doorDown(height - 1, col)) {
			ans.add(height-1);
			ans.add(col);
			ans.add(4);
            return ans;
        }
    }

    // looping through the leftmost column
    for (int row = 0; row < height; row++) {
        if (doorLeft(row, 0)) {
			ans.add(row);
			ans.add(0);
			ans.add(8);
            return ans;
        }
    }
    return null;
}

	public void recursiveSolve(int numCols, int numRows, int i, int j, ArrayList<Point> path, Boolean[][] hasVisited, int prevDoor ){
		//  note that the y refers to i = row 
		//  note that the x refers to j = col 
		//  for calling in a 2d array syntax is [i/y/row][j/x/col]

		//  setting true  since this point is now visisted in the array
		hasVisited[i][j] = true;
		//System.out.println(i);
		//System.out.println(j);
		//System.out.println(prevDoor);

		
		//  checking if there is a doorup from current position
	//  end of maze found exit door / Base case
		if (doorUp(i, j) && i == 0 && prevDoor != 1){

			//  add new point 
			path.add(new Point(j,i));}
		else{

			//  checks if there is a door above, if there is and it hasnt been visited call the function again with that starting point
			if (doorUp(i,j) && i > 0 && !hasVisited[i-1][j]){

				//  inductive step
				recursiveSolve(numCols, numRows, i-1, j, path, hasVisited, -1);  //  -1 since the prev door doeesn't matter since we wont be worried about a loop

				//  once all recursion is done if there are points in path then success path has been found 
				if(path.size() > 0){
					path.add(new Point(j,i));
					return;
				}
			}
		}

		//  checking if there is a doorRight from current position

		//  if there is a door on the right, but current position is in the right most column / base case
		if (doorRight(i,j) && j == numCols-1 && prevDoor !=2){
			path.add(new Point(j,i));
		}
		else{

			//  if there is a door on the right but it is not an exit door and has not yet been visited, call function again but with new point
			if(doorRight(i, j) && j < numCols-1 && !hasVisited[i][j+1]){

				//  inductive step 
				recursiveSolve(numCols, numRows, i, j+1, path, hasVisited, -1);
				if(path.size() > 0){
					path.add(new Point(j,i));
					return;
				}
			}
		}

		//  checking if there is a doorBottom from current position 

		//  if there is a door below, but current positon is at the bottom of the maze / base case
		if (doorDown(i, j) && i == numRows-1 && prevDoor != 4){
			path.add(new Point(j,i));
		}
		else{

			//  if there is a door below but it is not at the bottom of the maze 
			if (doorDown(i, j) && i < numRows-1 && !hasVisited[i+1][j]){

				//  inductive step 
				recursiveSolve(numCols, numRows, i+1, j, path, hasVisited, -1);
				if(path.size() > 0){
					path.add(new Point(j,i));
					return;
				}
			}
		}

		//  checking if there id a doorLeft from current position

		//  if there is a door on the left and current position is in the left most column / base case
		if (doorLeft(i, j) && j == 0 && prevDoor != 8){
			path.add(new Point(j,i));
		}
		else{

			//  if there is a door on the left but j is more than 0 meaning it is not in the left most column
			if (doorLeft(i, j) && j > 0 && !hasVisited[i][j-1]){

				//  inductive step 
				recursiveSolve(numCols, numRows, i, j-1, path, hasVisited, -1);
				if(path.size() > 0){
					path.add(new Point(j,i));
					return;
				}
			}
		}

		
		return;
	}

	//  to sovle this need to just act like it is a new starting point each time

//****************************************************************************************	

	

}