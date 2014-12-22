import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Maze extends java.applet.Applet implements Runnable {

	static int[][] maze;
	static int[][] mazeValForDoor;
	String mazeConfFile="mazeConfigurationA.txt";
	String mazeConf;
	final static int backgroundCode = 1;
	final static int wallCode = 1;
	final static int pathCode = 2;		// part of the current path through the maze
	final static int visitedCode = 3;	// has already been explored
	final static int emptyCode = 4;   	// has not been explored
	final static int thiefCode = 5;
	final static int goldCode = 6;
	final static int doorCode = 7;
	final static int securityCode = 8;
	final static int loopCode = 9;
	final static int foundCode = 10;

	// These values are just for default initialization
	// These values will be replaced by parameter passing from mazeapplet.html file
	int rows = 12;
	int columns = 12;
	int ndirection = 4;
	int nvisibility = 3;
	int border = 1;
	int sleepSpeed = 300;
	int nextMaze = 5000;
	int mapType = 1;
	int security = 1;
	Color[] color = new Color[11];

	int thiefPosX = 0;
	int thiefPosY = 0;
	int goldPosX = 0;
	int goldPosY = 0;
	int doorPosX = 0;
	int doorPosY = 0;
	int securityPosX = 0;
	int securityPosY = 0;
	int lastSecurityPosX = 0;
	int lastSecurityPosY = 0;
	
	int south=0;
	int east=0;
	int north=0;
	int west=0;
	
	/*
	int thiefPosX = 2-1;
	int thiefPosY = 2-1;
	int goldPosX = 9-1;
	int goldPosY = 3-1;
	int doorPosX = 2-1;
	int doorPosY = 11-1;
	int securityPosX = 9-1;
	int securityPosY = 11-1;
	*/

	Thread mazeThread;
	int width = -1;
	int height = -1;
	int totalWidth;
	int totalHeight;
	int left;
	int top;

	boolean mazeExists = false;
	int status = 0;
	final static int GO = 0;
	final static int SUSPEND = 1;
	final static int TERMINATE = 2;
	
	int phase = 0;
	final static int STREASDOOR = 0;
	final static int FTREASDOOR = 1;
	final static int STREAFDOOR = 2;
	final static int FTREAFDOOR = 3;
	
	static int goldisFound = 0;
	static int doorisFound = 0;
	static int doorValue = 0;
	
	static int[][] visible;
	static int[][] visibleLoop;
	static int[][] knowledge;
	
	static int[][] thiefPosLog;
	static int thiefPosLogPtr=0;
	static int numberofsteps=0;
	
	final static int SOUTH = 0;
	final static int EAST = 1;
	final static int NORTH = 2;
	final static int WEST = 3;
	
	static int direction = 0;
	static int prevDirection = 0;
	static int additionalMove = 0;
	
	String temp = "";
	static int stepCounter = -1;
	
	//loop avoidance
	static int[][] storedMovePos;
	static int[][] checkPtMovePos;
	static int[] storedMoveDir;
	static int storedMovePtr = 0;
	
	static int times=0;

	Integer getIntParam(String paramName) {
		String param = getParameter(paramName);
		if (param == null)
			return null;
		int i;
		try {
			i = Integer.parseInt(param);
		}
		catch (NumberFormatException e) {
			return null;
		}
		return new Integer(i);
	}

	Color getColorParam(String paramName) {
		String param = getParameter(paramName);
		if (param == null || param.length() == 0)
			return null;
		if (param.equalsIgnoreCase("black"))
			return Color.black;
		if (param.equalsIgnoreCase("white"))
			return Color.white;
		if (param.equalsIgnoreCase("red"))
			return Color.red;
		if (param.equalsIgnoreCase("green"))
			return Color.green;
		if (param.equalsIgnoreCase("blue"))
			return Color.blue;
		if (param.equalsIgnoreCase("yellow"))
			return Color.yellow;
		if (param.equalsIgnoreCase("cyan"))
			return Color.cyan;
		if (param.equalsIgnoreCase("magenta"))
			return Color.magenta;
		if (param.equalsIgnoreCase("pink"))
			return Color.pink;
		if (param.equalsIgnoreCase("orange"))
			return Color.orange;
		if (param.equalsIgnoreCase("gray"))
			return Color.gray;
		if (param.equalsIgnoreCase("darkgray"))
			return Color.darkGray;
		if (param.equalsIgnoreCase("lightgray"))
			return Color.lightGray;
		return null;
	}

	public void init() {
		Integer param;
		param = getIntParam("rows");
		if (param != null && param.intValue() > 4 && param.intValue() <= 100) {
			rows = param.intValue();
		}
		param = getIntParam("columns");
		if (param != null && param.intValue() > 4 && param.intValue() <= 100) {
			columns = param.intValue();
		}
		param = getIntParam("border");
		if (param != null && param.intValue() > 0 && param.intValue() <= 100)
			border = param.intValue();
		param = getIntParam("sleepSpeed");
		if (param != null && param.intValue() > 0)
			sleepSpeed = param.intValue();
		param = getIntParam("nextMaze");
		if (param != null && param.intValue() > 0)
			nextMaze = param.intValue();
		param = getIntParam("mapType");
		if (param != null && param.intValue() > 0)
			mapType = param.intValue();
		param = getIntParam("security");
		if (param != null)
			security = param.intValue();

		color[backgroundCode] = getColorParam("borderColor");
		if (color[backgroundCode] == null)
			color[backgroundCode] = Color.black;
		setBackground(color[backgroundCode]);
		color[wallCode] = getColorParam("wallColor");
		if (color[wallCode] == null) 
			color[wallCode] = Color.gray;
		color[pathCode] = getColorParam("pathColor");
		if (color[pathCode] == null)
			color[pathCode] = Color.blue;
		color[visitedCode] = getColorParam("visitedColor");
		if (color[visitedCode] == null)
			color[visitedCode] = Color.cyan;
		color[emptyCode] = getColorParam("emptyColor");
		if (color[emptyCode] == null)
			color[emptyCode] = Color.white;
			
		color[thiefCode] = getColorParam("thiefColor");
		if (color[thiefCode] == null) 
			color[thiefCode] = Color.blue;
		color[goldCode] = getColorParam("goldColor");
		if (color[goldCode] == null) 
			color[goldCode] = Color.yellow;
		color[doorCode] = getColorParam("doorColor");
		if (color[doorCode] == null) 
			color[doorCode] = Color.orange;
		color[securityCode] = getColorParam("securityColor");
		if (color[securityCode] == null) 
			color[securityCode] = Color.magenta;
		if (color[loopCode] == null) 
			color[loopCode] = Color.red;
		if (color[foundCode] == null) 
			color[foundCode] = Color.green;
	}

	void checkSize() { // check the applet size
		if (getWidth() != width || getHeight() != height) {
			width = getWidth();
			height = getHeight();
			int w = (width - 2*border) / columns;	// for width=482, border=1, and columns=12, w=40
			int h = (height - 2*border) / rows;		// for height=482, border=1, and rows=12, h=40
			left = (width - w*columns) / 2;			// then left=1
			top = (height - h*rows) / 2;			// then top=1
			totalWidth = w*columns;					// totalWidth = 480
			totalHeight = h*rows;					// totalHeight = 480
		}
	}

	synchronized public void start() {	// start the thread
		status = GO;
		if (mazeThread == null || ! mazeThread.isAlive()) {
			mazeThread = new Thread(this);
			mazeThread.start();						// Causes this thread to begin execution; the Java Virtual Machine calls the run method of this thread.
		}
		else
			notify();
	}

	synchronized public void stop() {		// Forces the thread to stop executing. 
		if (mazeThread != null) {
			status = SUSPEND;
			notify();
		}
	}

	synchronized public void destroy() {	// destroy the thread without any clean-up.
		if (mazeThread != null) {
			status = TERMINATE;
			notify();
		}
	}
	
	synchronized int checkStatus() {
		while (status == SUSPEND) {
			try { wait(); }
			catch (InterruptedException e) { }
		}
		return status;
	}

	public boolean isTerminate() {
		if (checkStatus() == TERMINATE) {
			return true;
		} else
			return false;
	}

	public void update(Graphics g) {
		checkSize();
		redrawMaze(g);
	}

	synchronized void redrawMaze(Graphics g) {	// redraw the entire maze
		g.setColor(color[backgroundCode]);
		g.fillRect(0, 0, width, height);
		
		if (mazeExists) {
			int w = totalWidth / columns;	// cell width
			int h = totalHeight / rows;		// cell height
			for (int i=0; i<columns; i++) {
				for (int j=0; j<rows; j++) {
					g.setColor(color[maze[i][j]]);
					g.fillRect( (j * w) + left, (i * h) + top, w, h );
				}
			}
		}
	}

	synchronized void updateCell(int row, int col, int colorNum, int printStep, int actor) {
		checkSize();
		int w = totalWidth / columns;	// cell width
		int h = totalHeight / rows;		// cell height
		Graphics gr = getGraphics();
		gr.setColor(color[colorNum]);
		gr.fillRect( (col * w) + left, (row * h) + top, w, h );
		
		if( printStep == 1 ) {
			gr.setColor(color[5]);
			
			if( actor == 1 ) { // thief
				if( storedMovePtr == 0 ) {
					//gr.drawString(temp+" "+stepCounter+" "+storedMovePtr+" "+storedMovePos[storedMovePtr][0]+" "+storedMovePos[storedMovePtr][1], (col * w) + (w/2) - 25, (row * h) + (h/2));
					gr.drawString(temp+stepCounter, (col * w) + (w/2) - 15, (row * h) + (h/2));
					gr.drawString(temp+storedMovePos[storedMovePtr][0]+" "+storedMovePos[storedMovePtr][1], (col * w) + (w/2) - 15, (row * h) + (h) - 5);
				}
				else {
					//gr.drawString(temp+storedMovePtr, (col * w) + (w/2) - 15, (row * h) + (h/2));
					gr.drawString(temp+stepCounter, (col * w) + (w/2) - 15, (row * h) + (h/2));
					gr.drawString(temp+storedMovePos[storedMovePtr-1][0]+" "+storedMovePos[storedMovePtr-1][1], (col * w) + (w/2) - 15, (row * h) + (h) - 5);
				}
			}
			//gr.drawString(temp+stepCounter, (col * w) + (w/2) - 5, (row * h) + (h/2));
			else if(actor == 2){
			
			}
		}
		gr.dispose(); // disposes of graphics context and releases any system resources that it is using.
		
	}

	public void run() {

		try { Thread.sleep(2000); }
		catch (InterruptedException e) { }

		//if( phase == STREASDOOR || phase == STREAFDOOR ) {
			while (true) {
				if (isTerminate()) break;
				//mazeConf = readMazeConf(mazeConfFile);
				//createMaze(mazeConf);
				//createMaze1();
				
				initVariables();
				createMaze1();
				//createEmptyMaze();

				if (isTerminate()) break;

				if( goldisFound == 0 ) {
					stepCounter=-1;
					//findGoldA(thiefPosX, thiefPosY);
					//findthief(securityPosX, securityPosY);
					
					//findGold01(thiefPosY, thiefPosY);
					
					//thiefPosX = goldPosX;
					//thiefPosY = goldPosY;
					//findDoor01(goldPosX, goldPosY);
					
					//main
					findGoldB(thiefPosX, thiefPosY);
				}
				
				try { Thread.sleep(1000); }
				catch (InterruptedException e) { }
				
				if( goldisFound == 1 ) {
					stepCounter=-1;
					// if door pos is known
					// updateMazeforDoor(doorPosX, doorPosY);
					//createMaze1();	
					//findDoorA(goldPosX, goldPosY);
					
					//findDoor01(goldPosX, goldPosY);
					
					// main
					findDoorB(goldPosX, goldPosY);
				}
				
				try { Thread.sleep(1000); }
				catch (InterruptedException e) { }
				
				if (isTerminate()) break;

				synchronized(this) {
					try { wait(nextMaze); }
					catch (InterruptedException e) { }
				}
				if (isTerminate()) break;

				mazeExists = false;
				checkSize();
				Graphics gr = getGraphics();
				redrawMaze(gr);
				gr.dispose();		// disposes of graphics context and releases any system resources that it is using.
			}
	}
	
	void initVariables() {
	
		//phase = 0;	// reset phase
		//boolean temp = false;
		
		goldisFound = 0;
		doorisFound = 0;
		doorValue = 35;
		
		storedMovePtr = 0;
		
		int i=0,j=0;
		if (visible == null) {
			visible = new int[ndirection][nvisibility];
		}
		
		for (i=0; i<ndirection; i++) {
			for (j=0; j < nvisibility; j++) {
				visible[i][j] = 0;
			}
		}
		
		if (visibleLoop == null) {
			visibleLoop = new int[2*ndirection][nvisibility];
		}
		
		for (i=0; i<ndirection; i++) {
			for (j=0; j < nvisibility; j++) {
				visibleLoop[i][j] = 0;
			}
		}
		
		if (knowledge == null) {
			knowledge = new int[rows][columns];
		}
		
		for (i=0; i<rows; i++) {
			for (j=0; j < columns; j++) {
				knowledge[i][j] = 0;
			}
		}
		
		if (thiefPosLog == null) {
			thiefPosLog = new int[2000][2];
		}
		
		for (i=0; i<2000; i++) {
			for (j=0; j < 2; j++) {
				thiefPosLog[i][j] = -1;
			}
		}
		
		if (storedMovePos == null) {
			storedMovePos = new int[120][2];
		}
		
		for (i=0; i<120; i++) {
			for (j=0; j < 2; j++) {
				storedMovePos[i][j] = 0;
			}
		}
		
		if (checkPtMovePos == null) {
			checkPtMovePos = new int[120][2];
		}
		
		for (i=0; i<120; i++) {
			for (j=0; j < 2; j++) {
				checkPtMovePos[i][j] = 0;
			}
		}
		
		if (storedMoveDir == null) {
			storedMoveDir = new int[120];
		}
		
		for (i=0; i<120; i++) {
			storedMoveDir[i] = 9;
		}
		
	}

	String readMazeConf(String filename) {
		String result="";

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			mazeConf = sb.toString();
			br.close();
		}
		catch (FileNotFoundException e) {
		//File is not found
		}
		catch (IOException e) {
		//Exception! IOException error occurred
		}

		return result;
	}

	//void createMaze(String mazeInput) {
	void createMaze1() {

		if (maze == null) {
			maze = new int[rows][columns];
		}
		
		int i=0, j=0, m=0, l=0;
		//int mapType = 9;
	
		String mazeInput = "";
	
		if(false) {
		mazeInput=
		//"OOOOOOOOOOOOOT  XX    DOO X   XXX  OO    X  X XOOXXX  X X  OO          OO XXXX XX  OO    X X X OO  X X XSGXOO XX     XXOO     XX   OOOOOOOOOOOOO";
		//"OOOOOOOOOOOOOT  XX    DOO X   XXX  OO    X  X XOOXXX  X X  OO          OO XXXX XX  OO    X X X OO  X X XSGXOO XX     XXOO     X    OOOOOOOOOOOOO";
		// maze A
		
		//String mazeInput=
		//"OOOOOOOOOOOOOTXXX     XOO X   XXX XOO X X    GXOO   XXXXX XOOXX        OOXX X XX X OOXX X X  X OOXX X X XX OOXX     XX OOXXXDXX    OOOOOOOOOOOOO";
		//maze B
		//"OOOOOOOOOOOOOTXXX     XOO X   XXX XOO X X   SGXOO   XXXXX XOOXX        OOXX X XX X OOXX X X  X OOXX X X XX OOXX     XX OOXXXDXX    OOOOOOOOOOOOO";
		// maze B with security
		
		//String mazeInput=
		//"OOOOOOOOOOOOOT    XX  DOO X   X X  OO    X    XOO         XOOX X      XOOX  X  X X OO   X      OO     XGX  OO        X OO X        OOOOOOOOOOOOO";
		"OOOOOOOOOOOOOT    XX  DOO X   X X  OO    X    XOO         XOOX X      XOOX  X  X X OO   X      OO     XGXS OO        X OO X        OOOOOOOOOOOOO";
		// maze C with security
		}
		
		//String mazeInput=
		//"OOOOOOOOOOOOOT         OO          OO          OO          OO          OO          OO          OO          OO          OOD        SOOOOOOOOOOOOO";
		// empty maze
		
		//"OOOOOOOOOOOOOT XXX    DOO X   XXX  OO    X  X XOOXXX  X X  OO          OO XXXX XX  OO    X X X OO  X X X GSOO XX     XXOO     XX   OOOOOOOOOOOOO";		
		//"OOOOOOOOOOOOOT  XX    DOO X   XXX  OO    X  X XOOXXX  X X  OO          OO XXXX XX  OO    X X X OO GX X X  SOO XX     XXOO     XX   OOOOOOOOOOOOO";
		//"OOOOOOOOOOOOOT  XXG   DOO X   XXX  OO    X  X XOOXXX  X X  OO          OO XXXX XX  OO    X X X OO  X X X  SOO XX     XXOO     XX   OOOOOOOOOOOOO";
		
		// Original maze A
		if( mapType==21 ) {
		mazeInput="OOOOOOOOOOOOOT XXX    DOO XX  XXX  OO    X  X XOOXXX  X X  OO          OO XXXX XX  OO    X X X OO  X X X GXOO XX     XXOO     XX   OOOOOOOOOOOOO";
		} else
		
		// Original maze B
		if( mapType==22 ) {
		mazeInput="OOOOOOOOOOOOOTXXX     XOO X   XXX XOO X X    GXOO   XXXXX XOOXX        OOXX X XX X OOXX X X  X OOXX X X XX OOXX     XX OOXXXDXX    OOOOOOOOOOOOO";
		} else
		
		// Original maze C
		if( mapType==23 ) {
		mazeInput="OOOOOOOOOOOOOT    XX  DOO X   X X  OO    X    XOO         XOOX X      XOOX  X  X X OO   X      OO     XGX  OO        X OO X        OOOOOOOOOOOOO";
		} else
		
		// Original Empty
		if( mapType==24 ) {
		mazeInput="OOOOOOOOOOOOOT         OO          OO          OO          OO          OO          OO          OO          OO          OOD         OOOOOOOOOOOOO";
		} else
		
		
		// maze A
		if( mapType==1 ) {
		mazeInput="OOOOOOOOOOOOOT  XX    DOO X   XXX  OO    X  X XOOXXX  X X  OO          OO XXXX XX  OO    X X X OO  X X X GXOO XX     XXOO     X    OOOOOOOOOOOOO";
		} else
		
		// maze A with security
		if( mapType==2 ) {
		mazeInput="OOOOOOOOOOOOOT  XX    GOO X   XXX  OO    X  X XOOXXX  X X  OO          OOXXX X XX  OO    X X X OO  X X X DXOO XXS    XXOO     X    OOOOOOOOOOOOO";
		} else
		
		//maze B
		if( mapType==3 ) {
		mazeInput="OOOOOOOOOOOOOTXXX     XOO X   XXX XOO X X    DXOO   XXXXX XOOXX        OOXX X XX X OOXX X X  X OOXX X X XX OOXX     XX OOXXXGXX    OOOOOOOOOOOOO";
		} else
		
		//maze B with security
		if( mapType==4 ) {
		mazeInput="OOOOOOOOOOOOOTXXX    SXOO X   XXX XOO X X    GXOO   XXXXX XOOXX        OOXX X XX X OOXX X X  X OOXX X X XX OOXX     XX OOXXXDXX    OOOOOOOOOOOOO";
		} else
		
		//maze B with security
		if( mapType==99 ) {
		mazeInput="OOOOOOOOOOOOOTXXX     XOO X   XXX XOO X X    GXOO   XXXXX XOOXX        OOXX X XX X OOXX X X  X OOXX X X XX OOXX     XX OOXXXDXX    OOOOOOOOOOOOO";
		} else
		
		//maze B
		if( mapType==9 ) {
		mazeInput="OOOOOOOOOOOOOTXXX     XOO X   XXX XOO X X    DXOO   XXXXX XOOXX        OOXX X XX X OOXX X X  XSOOXX X X XX OOXX     XX OOXXXGXX    OOOOOOOOOOOOO";
		} else
		
		// maze C
		if( mapType==5 ) {
		mazeInput="OOOOOOOOOOOOOT    XX  DOO X   X X  OO    X    XOO         XOOX X      XOOX  X  X X OO   X      OO     XGX  OO        X OO X        OOOOOOOOOOOOO";
		} else
		
		// maze C with security
		if( mapType==6 ) {
		mazeInput="OOOOOOOOOOOOOT    XX  DOO X   X X  OO    X    XOO         XOOX X      XOOX  X  X X OO   X      OO     XGXS OO        X OO X        OOOOOOOOOOOOO";
		}
		
		// empty
		if( mapType==7 ) {
		mazeInput="OOOOOOOOOOOOOT         OO          OO          OO          OO         DOO          OO         GOO          OO          OO         SOOOOOOOOOOOOO";
		}

		//l=mazeInput.length();
		m=0;
		for (i=0; i<rows; i++) {
			for (j=0; j < columns; j++) {
				switch(mazeInput.charAt(m)){
					case 'O':	maze[i][j] = wallCode;
								break;
					case 'X':	maze[i][j] = wallCode;
								break;
					case ' ':	maze[i][j] = emptyCode;
								break;
					case 'T':	maze[i][j] = thiefCode;
								thiefPosX = i; 
								thiefPosY = j;
								break;
					case 'G':	maze[i][j] = goldCode;
								goldPosX = i; 
								goldPosY = j;
								break;
					case 'D':	maze[i][j] = doorCode;
								doorPosX = i; 
								doorPosY = j;
								break;
					case 'S':	maze[i][j] = securityCode;
								securityPosX = i; 
								securityPosY = j;
								if( security==0 ) {
								maze[i][j] = emptyCode;
								}
								break;
				}
				m++;
			}
		}

		mazeExists = true;
		checkSize();
		if (isTerminate()) return;
			
		Graphics gr = getGraphics();
		redrawMaze(gr);
		gr.dispose();					// disposes of graphics context and releases any system resources that it is using.
	}
	
	/*
	void createEmptyMaze() {
		if (maze == null)
			maze = new int[rows][columns];
		int i=0,j=0;
		
		for (i=0; i<rows; i++) {
			for (j=0; j < columns; j++) {
				maze[i][j] = wallCode;
			}
		}
			
		for (i=1; i<rows-1; i++) {
			for (j=1; j<columns-1; j++) {
				maze[i][j] = emptyCode;
			}
		}
		
		thiefPosX = 2-1;	// 1, 1
		thiefPosY = 2-1;
		goldPosX = 9-1;		// 8, 8
		goldPosY = 9-1;
		doorPosX = 2-1;		// 1, 10
		doorPosY = 11-1;
		securityPosX = 9-1;	// 8, 10
		securityPosY = 11-1;
		
		maze[thiefPosX][thiefPosY] = thiefCode;
		maze[goldPosX][goldPosY] = goldCode;
		maze[doorPosX][doorPosY] = doorCode;
		maze[securityPosX][securityPosY] = securityCode;
		
		mazeExists = true;
		checkSize();
		if (isTerminate()) return;
			
		Graphics gr = getGraphics();
		redrawMaze(gr);
		gr.dispose();
	}
	*/
	
	void updateMazeforDoor(int doorPosX, int doorPosY) {
		
		// update only for path, visited, empty, found, loop
		//final static int pathCode = 2;
		//final static int visitedCode = 3;	// has already been explored
		//final static int emptyCode = 4;   	// has not been explored
		//final static int loopCode = 9;
		//final static int foundCode = 10;
		
		//	case 2 : value= 5; break;			// path
		//	case 3 : value= 5; break;			// visited
		//	case 4 : value= 35; break;			// empty
		//	case 9 : value= 1; break;			// loop
		//	case 10 : value= 5; break;			// found
		
		if (mazeValForDoor == null) {
			mazeValForDoor = new int[rows][columns];
		}
			
		int i=0,j=0;//, m=0, l=0;
		int rowIndex=0;
		int weight = 500;
		
		for (i=0; i < rows; j++) {
			for (j=0; j < columns; j++) {
				maze[i][j] = 0;
			}
		}
		
		// doorPosX and doorPosY based on maze size and configuration are between 1 to 10
		switch(doorPosX) {
			case 1:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight;		maze[6][j] = weight-250;
							maze[2][j] = weight-50;		maze[7][j] = weight-300;
							maze[3][j] = weight-100;	maze[8][j] = weight-350;
							maze[4][j] = weight-150;	maze[9][j] = weight-400;
							maze[5][j] = weight-200;	maze[10][j] = weight-450;
						}
					
					break;
			case 2:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-50;		maze[6][j] = weight-200;
							maze[2][j] = weight;		maze[7][j] = weight-250;
							maze[3][j] = weight-50;		maze[8][j] = weight-300;
							maze[4][j] = weight-100;	maze[9][j] = weight-350;
							maze[5][j] = weight-150;	maze[10][j] = weight-400;
						}
					
					break;
			case 3:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-100;	maze[6][j] = weight-150;
							maze[2][j] = weight-50;		maze[7][j] = weight-200;
							maze[3][j] = weight;		maze[8][j] = weight-250;
							maze[4][j] = weight-50;		maze[9][j] = weight-300;
							maze[5][j] = weight-100;	maze[10][j] = weight-350;
						}
					
					break;
			case 4:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-150;	maze[6][j] = weight-100;
							maze[2][j] = weight-100;	maze[7][j] = weight-150;
							maze[3][j] = weight-50;		maze[8][j] = weight-200;
							maze[4][j] = weight;		maze[9][j] = weight-250;
							maze[5][j] = weight-50;		maze[10][j] = weight-300;
						}
					
					break;
			case 5:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-200;	maze[6][j] = weight-50;
							maze[2][j] = weight-150;	maze[7][j] = weight-100;
							maze[3][j] = weight-100;	maze[8][j] = weight-150;
							maze[4][j] = weight-50;		maze[9][j] = weight-200;
							maze[5][j] = weight;		maze[10][j] = weight-250;
						}
					
					break;
			case 6:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-250;	maze[6][j] = weight;
							maze[2][j] = weight-200;	maze[7][j] = weight-50;
							maze[3][j] = weight-150;	maze[8][j] = weight-100;
							maze[4][j] = weight-100;	maze[9][j] = weight-150;
							maze[5][j] = weight-50;		maze[10][j] = weight-200;
						}
					
					break;
			case 7:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-300;	maze[6][j] = weight-50;
							maze[2][j] = weight-250;	maze[7][j] = weight;
							maze[3][j] = weight-200;	maze[8][j] = weight-50;
							maze[4][j] = weight-150;	maze[9][j] = weight-100;
							maze[5][j] = weight-100;	maze[10][j] = weight-150;
						}
					
					break;
			case 8:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-350;	maze[6][j] = weight-100;
							maze[2][j] = weight-300;	maze[7][j] = weight-50;
							maze[3][j] = weight-250;	maze[8][j] = weight;
							maze[4][j] = weight-200;	maze[9][j] = weight-50;
							maze[5][j] = weight-150;	maze[10][j] = weight-100;
						}
					
					break;
			case 9:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-400;	maze[6][j] = weight-150;
							maze[2][j] = weight-350;	maze[7][j] = weight-100;
							maze[3][j] = weight-300;	maze[8][j] = weight-50;
							maze[4][j] = weight-250;	maze[9][j] = weight;
							maze[5][j] = weight-200;	maze[10][j] = weight-50;
						}
					
					break;
			case 10:	
						for (j=1; j < columns-1; j++) {
							maze[1][j] = weight-450;	maze[6][j] = weight-200;
							maze[2][j] = weight-400;	maze[7][j] = weight-150;
							maze[3][j] = weight-350;	maze[8][j] = weight-100;
							maze[4][j] = weight-300;	maze[9][j] = weight-50;
							maze[5][j] = weight-250;	maze[10][j] = weight;
						}
					
					break;
		}
		
	switch(doorPosY) {
			case 1:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight;		maze[j][6] = weight-250;
							maze[j][2] = weight-50;	maze[j][7] = weight-300;
							maze[j][3] = weight-100;	maze[j][8] = weight-350;
							maze[j][4] = weight-150;	maze[j][9] = weight-400;
							maze[j][5] = weight-200;	maze[j][10] = weight-450;
						}
					
					break;
			case 2:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-50;		maze[j][6] = weight-200;
							maze[j][2] = weight;		maze[j][7] = weight-250;
							maze[j][3] = weight-50;		maze[j][8] = weight-300;
							maze[j][4] = weight-100;	maze[j][9] = weight-350;
							maze[j][5] = weight-150;	maze[j][10] = weight-400;
						}
					
					break;
			case 3:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-100;	maze[j][6] = weight-150;
							maze[j][2] = weight-50;		maze[j][7] = weight-200;
							maze[j][3] = weight;		maze[j][8] = weight-250;
							maze[j][4] = weight-50;		maze[j][9] = weight-300;
							maze[j][5] = weight-100;	maze[j][10] = weight-350;
						}
					
					break;
			case 4:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-150;	maze[j][6] = weight-100;
							maze[j][2] = weight-100;	maze[j][7] = weight-150;
							maze[j][3] = weight-50;		maze[j][8] = weight-200;
							maze[j][4] = weight;		maze[j][9] = weight-250;
							maze[j][5] = weight-50;		maze[j][10] = weight-300;
						}
					
					break;
			case 5:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-200;	maze[j][6] = weight-50;
							maze[j][2] = weight-150;	maze[j][7] = weight-100;
							maze[j][3] = weight-100;	maze[j][8] = weight-150;
							maze[j][4] = weight-50;		maze[j][9] = weight-200;
							maze[j][5] = weight;		maze[j][10] = weight-250;
						}
					
					break;
			case 6:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-250;	maze[j][6] = weight;
							maze[j][2] = weight-200;	maze[j][7] = weight-50;
							maze[j][3] = weight-150;	maze[j][8] = weight-100;
							maze[j][4] = weight-100;	maze[j][9] = weight-150;
							maze[j][5] = weight-50;		maze[j][10] = weight-200;
						}
					
					break;
			case 7:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-300;	maze[j][6] = weight-50;
							maze[j][2] = weight-250;	maze[j][7] = weight;
							maze[j][3] = weight-200;	maze[j][8] = weight-50;
							maze[j][4] = weight-150;	maze[j][9] = weight-100;
							maze[j][5] = weight-100;	maze[j][10] = weight-150;
						}
					
					break;
			case 8:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-350;	maze[j][6] = weight-100;
							maze[j][2] = weight-300;	maze[j][7] = weight-50;
							maze[j][3] = weight-250;	maze[j][8] = weight;
							maze[j][4] = weight-200;	maze[j][9] = weight-50;
							maze[j][5] = weight-150;	maze[j][10] = weight-100;
						}
					
					break;
			case 9:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-400;	maze[j][6] = weight-150;
							maze[j][2] = weight-350;	maze[j][7] = weight-100;
							maze[j][3] = weight-300;	maze[j][8] = weight-50;
							maze[j][4] = weight-250;	maze[j][9] = weight;
							maze[j][5] = weight-200;	maze[j][10] = weight-50;
						}
					
					break;
			case 10:	
						for (j=1; j < rows-1; j++) {
							maze[j][1] = weight-450;	maze[j][6] = weight-200;
							maze[j][2] = weight-400;	maze[j][7] = weight-150;
							maze[j][3] = weight-350;	maze[j][8] = weight-100;
							maze[j][4] = weight-300;	maze[j][9] = weight-50;
							maze[j][5] = weight-250;	maze[j][10] = weight;
						}
					
					break;
		}
		/*
		switch(doorPosX) {
			case 0:	
					rowIndex=0;
					for (j=0; j < columns; j++) {
						colIndex = j;
						if (maze[rowIndex][colIndex] == emptyCode || maze[rowIndex][colIndex] == pathCode || maze[rowIndex][colIndex] == visitedCode
							|| maze[rowIndex][colIndex] == loopCode || maze[rowIndex][colIndex] == foundCode ) {
							maze[rowIndex][colIndex] = OneCode;
						}
					}
					rowIndex=0;
					for (j=0; j < columns; j++) {
						colIndex = j;
						if (maze[rowIndex][colIndex] == emptyCode || maze[rowIndex][colIndex] == pathCode || maze[rowIndex][colIndex] == visitedCode
							|| maze[rowIndex][colIndex] == loopCode || maze[rowIndex][colIndex] == foundCode ) {
							maze[rowIndex][colIndex] = OneCode;
						}
					}
					rowIndex=0;
					for (j=0; j < columns; j++) {
						colIndex = j;
						if (maze[rowIndex][colIndex] == emptyCode || maze[rowIndex][colIndex] == pathCode || maze[rowIndex][colIndex] == visitedCode
							|| maze[rowIndex][colIndex] == loopCode || maze[rowIndex][colIndex] == foundCode ) {
							maze[rowIndex][colIndex] = OneCode;
						}
					}
						
		}
		*/
		
		/*
		l=mazeInput.length();
		m=0;
		for (i=0; i<rows; i++) {
			for (j=0; j < columns; j++) {
				switch(mazeInput.charAt(m)){
					case 'O':	maze[i][j] = wallCode;
								break;
					case 'X':	maze[i][j] = wallCode;
								break;
					case ' ':	maze[i][j] = emptyCode;
								break;
					case 'T':	maze[i][j] = thiefCode;
								thiefPosX = i; 
								thiefPosY = j;
								break;
					case 'G':	maze[i][j] = goldCode;
								goldPosX = i; 
								goldPosY = j;
								break;
					case 'D':	maze[i][j] = doorCode;
								doorPosX = i; 
								doorPosY = j;
								break;
					case 'S':	maze[i][j] = securityCode;
								securityPosX = i; 
								securityPosY = j;
								break;
				}
				m++;
			}
		}
		*/
	}
	
	void updateKnowledge(int trow, int tcol) {
		int i=0,j=0;
		int pitrow=0, pitcol=0, pdtrow=0, pdtcol=0;
	
		pitrow=trow;
		pitcol=tcol;
		pdtrow=trow;
		pdtcol=tcol;
		
		// for unseen
		for (i=0; i<ndirection; i++) {
			for (j=0; j < nvisibility; j++) {
				visible[i][j] = 0;
			}
		}
		
		for (j=0; j<3; j++) {
			pitrow++;
				visible[0][j] = maze[pitrow][tcol];
				knowledge[pitrow][tcol] = maze[pitrow][tcol];
				if( visible[0][j] == 1 ) break;			// 1 as wallcode
		}
		
		for (j=0; j<3; j++) {
			pitcol++;
				visible[1][j] = maze[trow][pitcol];
				knowledge[trow][pitcol] = maze[trow][pitcol];
				if( visible[1][j] == 1 ) break;
		}
		
		for (j=0; j<3; j++) {
			pdtrow--;
				visible[2][j] = maze[pdtrow][tcol];
				knowledge[pdtrow][tcol] = maze[pdtrow][tcol];
				if( visible[2][j] == 1 ) break;
		}
		
		for (j=0; j<3; j++) {
			pdtcol--;
				visible[3][j] = maze[trow][pdtcol];
				knowledge[trow][pdtcol] = maze[trow][pdtcol];
				if( visible[3][j] == 1 ) break;
		}
		
		/*
		for (i=0; i<; i++) {
			for (j=0; j<; j++) {
				visible[0][0] = maze[ptrow+1][ptcol];
				visible[0][1] = maze[ptrow+2][ptcol];
				visible[0][2] = maze[ptrow+3][ptcol];
				
				visible[1][0] = maze[ptrow][ptcol+1];
				visible[1][1] = maze[ptrow][ptcol+2];
				visible[1][2] = maze[ptrow][ptcol+3];
				
				visible[2][0] = maze[ptrow-1][ptcol];
				visible[2][1] = maze[ptrow-2][ptcol];
				visible[2][2] = maze[ptrow-3][ptcol];
				
				visible[3][0] = maze[ptrow][ptcol-1];
				visible[3][1] = maze[ptrow][ptcol-2];
				visible[3][2] = maze[ptrow][ptcol-3];
			}
		}
		*/
		
	}
	
	/*
	void updateKnowledgeLoop(int trow1, int tcol1, int trow2, int tcol2) {
		int i=0,j=0;
		int pitrow1=0, pitcol1=0, pdtrow1=0, pdtcol1=0;
		int pitrow2=0, pitcol2=0, pdtrow2=0, pdtcol2=0;
	
		pitrow1=trow1;
		pitcol1=tcol1;
		pdtrow1=trow1;
		pdtcol1=tcol1;
		pitrow2=trow2;
		pitcol2=tcol2;
		pdtrow2=trow2;
		pdtcol2=tcol2;
		
		// for unseen
		for (i=0; i<2*ndirection; i++) {
			for (j=0; j < nvisibility; j++) {
				visibleLoop[i][j] = 0;
			}
		}
		
		for (j=0; j<3; j++) {
			pitrow1++;
				visibleLoop[0][j] = maze[pitrow1][tcol1];
				//knowledge[pitrow1][tcol1] = maze[pitrow1][tcol1];
				if( visibleLoop[0][j] == 1 ) break;
		}
		
		for (j=0; j<3; j++) {
			pitcol1++;
				visibleLoop[1][j] = maze[trow1][pitcol1];
				//knowledge[trow1][pitcol1] = maze[trow1][pitcol1];
				if( visibleLoop[1][j] == 1 ) break;
		}
		
		for (j=0; j<3; j++) {
			pdtrow1--;
				visibleLoop[2][j] = maze[pdtrow1][tcol1];
				//knowledge[pdtrow1][tcol1] = maze[pdtrow1][tcol1];
				if( visibleLoop[2][j] == 1 ) break;
		}
		
		for (j=0; j<3; j++) {
			pdtcol1--;
				visibleLoop[3][j] = maze[trow1][pdtcol1];
				//knowledge[trow1][pdtcol1] = maze[trow1][pdtcol1];
				if( visibleLoop[3][j] == 1 ) break;
		}
		

		
		for (j=0; j<3; j++) {
			pitrow2++;
				visibleLoop[4][j] = maze[pitrow2][tcol2];
				//knowledge[pitrow2][tcol2] = maze[pitrow2][tcol2];
				if( visibleLoop[4][j] == 1 ) break;
		}
		
		for (j=0; j<3; j++) {
			pitcol2++;
				visibleLoop[5][j] = maze[trow2][pitcol2];
				//knowledge[trow2][pitcol2] = maze[trow2][pitcol2];
				if( visibleLoop[5][j] == 1 ) break;
		}
		
		for (j=0; j<3; j++) {
			pdtrow2--;
				visibleLoop[6][j] = maze[pdtrow2][tcol2];
				//knowledge[pdtrow2][tcol2] = maze[pdtrow2][tcol2];
				if( visibleLoop[6][j] == 1 ) break;
		}
		
		for (j=0; j<3; j++) {
			pdtcol2--;
				visibleLoop[7][j] = maze[trow2][pdtcol2];
				//knowledge[trow2][pdtcol2] = maze[trow2][pdtcol2];
				if( visibleLoop[7][j] == 1 ) break;
		}
		
	}
	*/
	
	int codetovalue(int code) {
		int value=0;
	
		/*
		final static int backgroundCode = 0;
		final static int wallCode = 1;
		final static int pathCode = 2;		// part of the current path through the maze
		final static int visitedCode = 3;	// has already been explored
		final static int emptyCode = 4;   	// has not been explored
		final static int thiefCode = 5;
		final static int goldCode = 6;
		final static int doorCode = 7;
		final static int securityCode = 8;
		final static int loopCode = 9;
		final static int foundCode = 10;
		*/
	
		switch(code) {
			case 0 : value= 0; break;
			case 1 : value= -15; break;			// wall
			case 2 : value= 5; break;			// path
			case 3 : value= 5; break;			// visited
			case 4 : value= 35; break;			// empty
			case 5 : value= 0; break;			// thief
			case 6 : value= 125; break;			// gold
			case 7 : value= doorValue; break;	// door
			case 8 : value= -175; break;		// security
			case 9 : value= 1; break;			// loop
			case 10 : value= 5; break;			// found
		}
	
		return value;
	}
	
	int updateDirection() {
		int i=0,j=0;
		int wSouth = 0, wEast = 0, wNorth = 0, wWest = 0;
		int mostWeight=0;
		int updatedDirection=0;
		
		for (j=0; j < 3; j++) {
			wSouth += codetovalue( visible[0][j] );
			wEast += codetovalue( visible[1][j] );
			wNorth += codetovalue( visible[2][j] );
			wWest += codetovalue( visible[3][j] );
		}
		
		mostWeight = wSouth; updatedDirection=0;
		if( wEast > mostWeight ) { mostWeight = wEast; updatedDirection=1; }
		if( wNorth > mostWeight ) { mostWeight = wNorth; updatedDirection=2; }
		if( wWest > mostWeight ) { mostWeight = wWest; updatedDirection=3; }
		
		return updatedDirection;
	}
	
	void execStoreMovement(int direction, int trow, int tcol) {
		checkPtMovePos[storedMovePtr][0] = trow;
		checkPtMovePos[storedMovePtr][1] = tcol;
				
		switch(direction) {
			case 0: // South
				storedMoveDir[storedMovePtr] = 0;
				storedMovePos[storedMovePtr][0] = trow+1;
				storedMovePos[storedMovePtr][1] = tcol;
				storedMovePtr++;
				break;
			case 1: // East
				storedMoveDir[storedMovePtr] = 1;
				storedMovePos[storedMovePtr][0] = trow;
				storedMovePos[storedMovePtr][1] = tcol+1;
				storedMovePtr++;
				break;
			case 2: // North
				storedMoveDir[storedMovePtr] = 2;
				storedMovePos[storedMovePtr][0] = trow-1;
				storedMovePos[storedMovePtr][1] = tcol;
				storedMovePtr++;
				break;
			case 3: // West
				storedMoveDir[storedMovePtr] = 3;
				storedMovePos[storedMovePtr][0] = trow;
				storedMovePos[storedMovePtr][1] = tcol-1;
				storedMovePtr++;
				break;
		}

	}
	
	void storeMovement(int prevDir, int trow, int tcol, int updatedDirection, int wS, int wE, int wN, int wW) {
		int cSouth = 0, cEast = 0, cNorth = 0, cWest = 0;
		int wSouth = 0, wEast = 0, wNorth = 0, wWest = 0;
		int mostWeight = 0;
		
		cSouth = visible[0][0];
		cEast = visible[1][0];
		cNorth = visible[2][0];
		cWest = visible[3][0];
		
		wSouth = wS;
		wEast = wE;
		wNorth = wN;
		wWest = wW;
		
		//South	0
		//East	1
		//North	2
		//West	3
		
		switch(prevDir) {
			case 0: // South
				if( updatedDirection==0 ) { // To be stored: West or East
					if( cWest == emptyCode && cEast == emptyCode ) {
						if( wWest >= wEast ) {	
							execStoreMovement( 3, trow, tcol );
							execStoreMovement( 1, trow, tcol );
						} else { // wWest >= wEast
							execStoreMovement( 1, trow, tcol );
							execStoreMovement( 3, trow, tcol );
						}
					} else {
						if( cWest == emptyCode ) {
							execStoreMovement( 3, trow, tcol );
						}
						if( cEast == emptyCode ) {
							execStoreMovement( 1, trow, tcol );
						}
					}
				} else if( updatedDirection==1 ) { // To be stored: South or West
					if( cSouth == emptyCode && cWest == emptyCode ) {
						if( wSouth >= wWest ) {	
							execStoreMovement( 0, trow, tcol );
							execStoreMovement( 3, trow, tcol );
						} else { // 
							execStoreMovement( 3, trow, tcol );
							execStoreMovement( 0, trow, tcol );
						}
					} else {
						if( cSouth == emptyCode ) {
							execStoreMovement( 0, trow, tcol );
						}
						if( cWest == emptyCode ) {
							execStoreMovement( 3, trow, tcol );
						}
					}
				} else if( updatedDirection==2 ) { // To be stored: nothing, means backtrack?

				} else if( updatedDirection==3 ) { // To be stored: South or East
					if( cSouth == emptyCode && cEast == emptyCode ) {
						if( wSouth >= wEast ) {	
							execStoreMovement( 0, trow, tcol );
							execStoreMovement( 1, trow, tcol );
						} else { // 
							execStoreMovement( 1, trow, tcol );
							execStoreMovement( 0, trow, tcol );
						}
					} else {
						if( cSouth == emptyCode ) {
							execStoreMovement( 0, trow, tcol );
						}
						if( cEast == emptyCode ) {
							execStoreMovement( 1, trow, tcol );
						}
					}
				}
				break;
			case 1: // East
				if( updatedDirection==0 ) { // To be stored: East or North
					if( cEast == emptyCode && cNorth == emptyCode ) {
						if( wEast >= wNorth ) {	
							execStoreMovement( 1, trow, tcol );
							execStoreMovement( 2, trow, tcol );
						} else { // wWest >= wEast
							execStoreMovement( 2, trow, tcol );
							execStoreMovement( 1, trow, tcol );
						}
					} else {
						if( cEast == emptyCode ) {
							execStoreMovement( 1, trow, tcol );
						}
						if( cNorth == emptyCode ) {
							execStoreMovement( 2, trow, tcol );
						}
					}
				} else if( updatedDirection==1 ) { // To be stored: South or North
					if( cSouth == emptyCode && cNorth == emptyCode ) {
						if( wSouth >= wNorth ) {	
							execStoreMovement( 0, trow, tcol );
							execStoreMovement( 2, trow, tcol );
						} else { // 
							execStoreMovement( 2, trow, tcol );
							execStoreMovement( 0, trow, tcol );
						}
					} else {
						if( cSouth == emptyCode ) {
							execStoreMovement( 0, trow, tcol );
						}
						if( cNorth == emptyCode ) {
							execStoreMovement( 2, trow, tcol );
						}
					}
				} else if( updatedDirection==2 ) { // To be stored: East or South
					if( cEast == emptyCode && cSouth == emptyCode ) {
						if( wEast >= wSouth ) {	
							execStoreMovement( 1, trow, tcol );
							execStoreMovement( 0, trow, tcol );
						} else { // 
							execStoreMovement( 0, trow, tcol );
							execStoreMovement( 1, trow, tcol );
						}
					} else {
						if( cEast == emptyCode ) {
							execStoreMovement( 1, trow, tcol );
						}
						if( cSouth == emptyCode ) {
							execStoreMovement( 0, trow, tcol );
						}
					}
				} else if( updatedDirection==3 ) { // To be stored: nothing because of backtrack?
				
				}
				break;
			case 2: // North
				if( updatedDirection==0 ) { // To be stored: nothing because of backtrack?

				} else if( updatedDirection==1 ) { // To be stored: North or West
					if( cNorth == emptyCode && cWest == emptyCode ) {
						if( wNorth >= wWest ) {	
							execStoreMovement( 2, trow, tcol );
							execStoreMovement( 3, trow, tcol );
						} else { // 
							execStoreMovement( 3, trow, tcol );
							execStoreMovement( 2, trow, tcol );
						}
					} else {
						if( cNorth == emptyCode ) {
							execStoreMovement( 2, trow, tcol );
						}
						if( cWest == emptyCode ) {
							execStoreMovement( 3, trow, tcol );
						}
					}
				} else if( updatedDirection==2 ) { // To be stored: East or West
					if( cEast == emptyCode && cWest == emptyCode ) {
						if( wEast >= wWest ) {	
							execStoreMovement( 1, trow, tcol );
							execStoreMovement( 3, trow, tcol );
						} else { // 
							execStoreMovement( 3, trow, tcol );
							execStoreMovement( 1, trow, tcol );
						}
					} else {
						if( cEast == emptyCode ) {
							execStoreMovement( 1, trow, tcol );
						}
						if( cWest == emptyCode ) {
							execStoreMovement( 3, trow, tcol );
						}
					}
				} else if( updatedDirection==3 ) { // To be stored: North or East
					if( cNorth == emptyCode && cEast == emptyCode ) {
						if( wNorth > wEast ) {	
							execStoreMovement( 2, trow, tcol );
							execStoreMovement( 1, trow, tcol );
						} else { // wWest >= wEast
							execStoreMovement( 1, trow, tcol );
							execStoreMovement( 2, trow, tcol );
						}
					} else {
						if( cNorth == emptyCode ) {
							execStoreMovement( 2, trow, tcol );
						}
						if( cEast == emptyCode ) {
							execStoreMovement( 1, trow, tcol );
						}
					}
				}
				break;
			case 3: // West
				if( updatedDirection==0 ) { // To be stored: West or North
					if( cWest == emptyCode && cNorth == emptyCode ) {
						if( wWest > wNorth ) {	
							execStoreMovement( 3, trow, tcol );
							execStoreMovement( 2, trow, tcol );
						} else { // wWest >= wEast
							execStoreMovement( 2, trow, tcol );
							execStoreMovement( 3, trow, tcol );
						}
					} else {
						if( cWest == emptyCode ) {
							execStoreMovement( 3, trow, tcol );
						}
						if( cNorth == emptyCode ) {
							execStoreMovement( 2, trow, tcol );
						}
					}
				} else if( updatedDirection==1 ) { // To be stored: nothing because of backtrack?

				} else if( updatedDirection==2 ) { // To be stored: West or South
					if( cWest == emptyCode && cSouth == emptyCode ) {
						if( wWest >= wSouth ) {	
							execStoreMovement( 3, trow, tcol );
							execStoreMovement( 0, trow, tcol );
						} else { // 
							execStoreMovement( 0, trow, tcol );
							execStoreMovement( 3, trow, tcol );
						}
					} else {
						if( cWest == emptyCode ) {
							execStoreMovement( 3, trow, tcol );
						}
						if( cSouth == emptyCode ) {
							execStoreMovement( 0, trow, tcol );
						}
					}
				} else if( updatedDirection==3 ) { // To be stored: North or South
					if( cNorth == emptyCode && cSouth == emptyCode ) {
						if( wNorth >= wSouth ) {	
							execStoreMovement( 2, trow, tcol );
							execStoreMovement( 0, trow, tcol );
						} else { // 
							execStoreMovement( 0, trow, tcol );
							execStoreMovement( 2, trow, tcol );
						}
					} else {
						if( cNorth == emptyCode ) {
							execStoreMovement( 2, trow, tcol );
						}
						if( cSouth == emptyCode ) {
							execStoreMovement( 0, trow, tcol );
						}
					}
				}
				break;
		}
	}
	
	/*
	int storedMovement() {
		
		return 1;
	}
	*/
	
	int updateDirectionB(int prevDir, int trow, int tcol) {	// forward algorithm (backward direction assigned as the least preference)
		int i=0,j=0;
		int wSouth = 0, wEast = 0, wNorth = 0, wWest = 0;
		int mostWeight=0;
		int updatedDirection=0;
		
		for (j=0; j < 3; j++) {
			wSouth += codetovalue( visible[0][j] );
			wEast += codetovalue( visible[1][j] );
			wNorth += codetovalue( visible[2][j] );
			wWest += codetovalue( visible[3][j] );
		}
		
		switch(prevDir) {
			case 0: // South
				mostWeight = wSouth; updatedDirection=0;
				if( wEast > mostWeight ) { mostWeight = wEast; updatedDirection=1; }
				if( wWest >= mostWeight ) { mostWeight = wWest; updatedDirection=3; }
				if( wSouth >= mostWeight ) { mostWeight = wSouth; updatedDirection=0; }
				if( wSouth == codetovalue(wallCode) && wWest == codetovalue(wallCode) && wEast == codetovalue(wallCode) ) {
					//mostWeight = wNorth;
					updatedDirection=2; }
				break;
			case 1: // East
				mostWeight = wEast; updatedDirection=1;
				if( wNorth > mostWeight ) { mostWeight = wNorth; updatedDirection=2; }
				if( wSouth >= mostWeight ) { mostWeight = wSouth; updatedDirection=0; }
				if( wEast >= mostWeight ) { mostWeight = wEast; updatedDirection=1; }
				if( wEast == codetovalue(wallCode) && wSouth == codetovalue(wallCode) && wNorth == codetovalue(wallCode) ) {
					//mostWeight = wWest;
					updatedDirection=3; }
				break;
			case 2: // North
				mostWeight = wNorth; updatedDirection=2;
				if( wWest > mostWeight ) { mostWeight = wWest; updatedDirection=3; }
				if( wEast >= mostWeight ) { mostWeight = wEast; updatedDirection=1; }
				if( wNorth >= mostWeight ) { mostWeight = wNorth; updatedDirection=2; }
				if( wNorth == codetovalue(wallCode) && wEast == codetovalue(wallCode) && wWest == codetovalue(wallCode) ) {
					//mostWeight = wSouth;
					updatedDirection=0; }
				break;
			case 3: // West
				mostWeight = wWest; updatedDirection=3;
				if( wSouth > mostWeight ) { mostWeight = wSouth; updatedDirection=0; }
				if( wNorth >= mostWeight ) { mostWeight = wNorth; updatedDirection=2; }
				if( wWest >= mostWeight ) { mostWeight = wWest; updatedDirection=3; }
				if( wWest == codetovalue(wallCode) && wNorth == codetovalue(wallCode) && wSouth == codetovalue(wallCode) ) {
					//mostWeight = wEast;
					updatedDirection=1; }
				break;
		}
		
		/*
		mostWeight = wSouth; updatedDirection=0;
		if( wEast > mostWeight ) { mostWeight = wEast; updatedDirection=1; }
		if( wNorth > mostWeight ) { mostWeight = wNorth; updatedDirection=2; }
		if( wWest > mostWeight ) { mostWeight = wWest; updatedDirection=3; }
		*/
		
		storeMovement(prevDir, trow, tcol, updatedDirection, wSouth, wEast, wNorth, wWest);
		
		return updatedDirection;
	}
	
	/*
	int updateDirectionLoop(int prevDir) {
		int i=0,j=0;
		int wSouth = 0, wEast = 0, wNorth = 0, wWest = 0;
		//int mostWeight=0;
		int updatedDirection=0;
		
		switch( prevDir ) {
			case 0: 
				wSouth += codetovalue( visibleLoop[0][0] );
				wSouth += codetovalue( visibleLoop[0][1] );
				wSouth += codetovalue( visibleLoop[0][2] );
				//wSouth += codetovalue( visibleLoop[4][0] );
				//wSouth += codetovalue( visibleLoop[4][1] );
				//wSouth += codetovalue( visibleLoop[4][2] );
				
				//wNorth += codetovalue( visibleLoop[2][0] );
				//wNorth += codetovalue( visibleLoop[2][1] );
				//wNorth += codetovalue( visibleLoop[2][2] );
				wNorth += codetovalue( visibleLoop[6][0] );
				wNorth += codetovalue( visibleLoop[6][1] );
				wNorth += codetovalue( visibleLoop[6][2] );
				
				if( wSouth >= wNorth ) {
					updatedDirection = 0;
					additionalMove = 0;
					//updateCell(trow1, tcol1, foundCode);
				}
				else {
					updatedDirection = 2;
					additionalMove = 1;
					//updateCell(trow2, tcol2, foundCode);
				}
				break;
				
			case 1:
				wEast += codetovalue( visibleLoop[1][0] );
				wEast += codetovalue( visibleLoop[1][1] );
				wEast += codetovalue( visibleLoop[1][2] );
				//wEast += codetovalue( visibleLoop[5][0] );
				//wEast += codetovalue( visibleLoop[5][1] );
				//wEast += codetovalue( visibleLoop[5][2] );
				
				//wWest += codetovalue( visibleLoop[3][0] );
				//wWest += codetovalue( visibleLoop[3][1] );
				//wWest += codetovalue( visibleLoop[3][2] );
				wWest += codetovalue( visibleLoop[7][0] );
				wWest += codetovalue( visibleLoop[7][1] );
				wWest += codetovalue( visibleLoop[7][2] );
				
				if( wEast >= wWest ) {
					updatedDirection = 1;
					additionalMove = 0;
					//updateCell(trow1, tcol1, foundCode);
				}
				else {
					updatedDirection = 3;
					additionalMove = 1;
					//updateCell(trow2, tcol2, foundCode);
				}
				break;
				
			case 2: 
				//wSouth += codetovalue( visibleLoop[0][0] );
				//wSouth += codetovalue( visibleLoop[0][1] );
				//wSouth += codetovalue( visibleLoop[0][2] );
				wSouth += codetovalue( visibleLoop[4][0] );
				wSouth += codetovalue( visibleLoop[4][1] );
				wSouth += codetovalue( visibleLoop[4][2] );
				
				wNorth += codetovalue( visibleLoop[2][0] );
				wNorth += codetovalue( visibleLoop[2][1] );
				wNorth += codetovalue( visibleLoop[2][2] );
				//wNorth += codetovalue( visibleLoop[6][0] );
				//wNorth += codetovalue( visibleLoop[6][1] );
				//wNorth += codetovalue( visibleLoop[6][2] );
				
				if( wNorth >= wSouth ) {
					updatedDirection = 2;
					additionalMove = 0;
					//updateCell(trow1, tcol1, foundCode);
				}
				else {
					updatedDirection = 0;
					additionalMove = 1;
					//updateCell(trow2, tcol2, foundCode);
				}
				break;

			case 3:
				//wEast += codetovalue( visibleLoop[1][0] );
				//wEast += codetovalue( visibleLoop[1][1] );
				//wEast += codetovalue( visibleLoop[1][2] );
				wEast += codetovalue( visibleLoop[5][0] );
				wEast += codetovalue( visibleLoop[5][1] );
				wEast += codetovalue( visibleLoop[5][2] );
				
				wWest += codetovalue( visibleLoop[3][0] );
				wWest += codetovalue( visibleLoop[3][1] );
				wWest += codetovalue( visibleLoop[3][2] );
				//wWest += codetovalue( visibleLoop[7][0] );
				//wWest += codetovalue( visibleLoop[7][1] );
				//wWest += codetovalue( visibleLoop[7][2] );
				
				if( wWest >= wEast ) {
					updatedDirection = 3;
					additionalMove = 0;
					//updateCell(trow1, tcol1, foundCode);
				}
				else {
					updatedDirection = 1;
					additionalMove = 1;
					//updateCell(trow2, tcol2, foundCode);
				}
				break;
		}
		
		return updatedDirection;
	}
	*/
	
	/*
	boolean isLoopMovement(int pointer) {
		boolean consecutivePos = false;
		
		if( thiefPosLog[pointer][0] == thiefPosLog[pointer-2][0] &&  thiefPosLog[pointer][1] == thiefPosLog[pointer-2][1] 
		&& thiefPosLog[pointer-1][0] == thiefPosLog[pointer-3][0] &&  thiefPosLog[pointer-1][1] == thiefPosLog[pointer-3][1] ) {
			consecutivePos = true;
		}
		
		//if( thiefPosLog[pointer][0] == thiefPosLog[pointer-2][0] &&  thiefPosLog[pointer][1] == thiefPosLog[pointer-2][1] 
		//&& thiefPosLog[pointer][0] == thiefPosLog[pointer-4][0] &&  thiefPosLog[pointer][1] == thiefPosLog[pointer-4][1] 
		//&& thiefPosLog[pointer][0] == thiefPosLog[pointer-6][0] &&  thiefPosLog[pointer][1] == thiefPosLog[pointer-6][1] 
		//&& thiefPosLog[pointer][0] == thiefPosLog[pointer-8][0] &&  thiefPosLog[pointer][1] == thiefPosLog[pointer-8][1]
		//&& thiefPosLog[pointer-1][0] == thiefPosLog[pointer-3][0] &&  thiefPosLog[pointer-1][1] == thiefPosLog[pointer-3][1] 
		//&& thiefPosLog[pointer-1][0] == thiefPosLog[pointer-5][0] &&  thiefPosLog[pointer-1][1] == thiefPosLog[pointer-5][1] 
		//&& thiefPosLog[pointer-1][0] == thiefPosLog[pointer-7][0] &&  thiefPosLog[pointer-1][1] == thiefPosLog[pointer-7][1] 
		//&& thiefPosLog[pointer-1][0] == thiefPosLog[pointer-9][0] &&  thiefPosLog[pointer-1][1] == thiefPosLog[pointer-9][1] ) {
		//	consecutivePos = true;
		//}
		
		return consecutivePos;
	}
	*/
	
	boolean findGoldB(int trow, int tcol) {
		int moveToEmpty = 1;
	
		if (maze[trow][tcol] == emptyCode || maze[trow][tcol] == thiefCode || maze[trow][tcol] == goldCode || maze[trow][tcol] == doorCode
		|| maze[trow][tcol] == securityCode || maze[trow][tcol] == pathCode || maze[trow][tcol] == visitedCode || maze[trow][tcol] == loopCode
		|| maze[trow][tcol] == foundCode ) {
			
			if (checkStatus() == TERMINATE) return false;

			//maze[trow][tcol] = pathCode;
			//updateCell(trow, tcol, pathCode);
			
			stepCounter++;
			updateCell(trow, tcol, thiefCode, 1, 1);
			if (trow == securityPosX && tcol == securityPosY) {
					try { Thread.sleep(sleepSpeed); }
				catch (InterruptedException e) { }
				
				updateCell(trow, tcol, loopCode, 1, 1);
					try { Thread.sleep(9000); }
				catch (InterruptedException e) { }
			}
			
			if (trow == goldPosX && tcol == goldPosY) {
				goldisFound = 1;
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
				
				doorValue = 125;
				maze[doorPosX][doorPosY] = doorCode;		// restore door position in case it was updated as visited cell
				updateCell(doorPosX, doorPosY, doorCode, 2, 1);
				return true;
			}
				
			try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
			maze[trow][tcol] = visitedCode;
			updateCell(trow, tcol, visitedCode, 1, 1);
			
			if (trow == doorPosX && tcol == doorPosY) {
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
				// call function store door pos
			}
			
			updateKnowledge(trow, tcol);
			prevDirection = direction;
			direction = updateDirectionB(prevDirection, trow, tcol);
			
			thiefPosLog[thiefPosLogPtr][0] = trow;
			thiefPosLog[thiefPosLogPtr][1] = tcol;
			thiefPosLogPtr++;
			
			thiefPosX = trow;
			thiefPosY = tcol;
		
			//findthief(securityPosX, securityPosY);
			
			if(security==1){
			
				if( times == 1 ) {
					findthief(securityPosX, securityPosY);
					times=0;
				} else {
				times++;
				}
				
				//findthief(securityPosX, securityPosY);
			}
			
			/*
			numberofsteps++;
			thiefPosLog[thiefPosLogPtr][0] = trow;
			thiefPosLog[thiefPosLogPtr][1] = tcol;
			
			if( numberofsteps>4 ) {
				if( isLoopMovement(thiefPosLogPtr) ) {
					maze[ thiefPosLog[thiefPosLogPtr][0] ][ thiefPosLog[thiefPosLogPtr][1] ] = loopCode;
					maze[ thiefPosLog[thiefPosLogPtr-1][0] ][ thiefPosLog[thiefPosLogPtr-1][1] ] = loopCode;
					updateCell(thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1], loopCode);
					updateCell(thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1], loopCode);
					
					synchronized(this) {
						try { wait(sleepSpeed); }
						catch (InterruptedException e) { }
					}
					
					updateKnowledgeLoop( thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1],
					thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1] );
					direction = updateDirectionLoop(prevDirection);
					
					//direction = updateDirectionLoop(prevDirection,
					//thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1],
					//thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1]
					//);
					
					thiefPosLogPtr++;
					//direction = 2;
					//additionalMove = 0;
					
					if( additionalMove == 1 ) {
						switch(direction) {
							case 0: return findGoldA(trow+2, tcol);
							case 1: return findGoldA(trow, tcol+2);
							case 2: return findGoldA(trow-2, tcol);
							case 3: return findGoldA(trow, tcol-2);
						}
					} else {
						switch(direction) {
							case 0: return findGoldA(trow+1, tcol);
							case 1: return findGoldA(trow, tcol+1);
							case 2: return findGoldA(trow-1, tcol);
							case 3: return findGoldA(trow, tcol-1);
						}
					}
				}
			}
			thiefPosLogPtr++;
			*/
			
			// 
			moveToEmpty = 1;
			switch(direction) {
				case 0: if( maze[trow+1][tcol] == visitedCode || maze[trow+1][tcol] == pathCode )
						moveToEmpty = 0;
						break;
				case 1: if( maze[trow][tcol+1] == visitedCode || maze[trow][tcol+1] == pathCode )
						moveToEmpty = 0;
						break;
				case 2: if( maze[trow-1][tcol] == visitedCode || maze[trow-1][tcol] == pathCode )
						moveToEmpty = 0;
						break;
				case 3: if( maze[trow][tcol-1] == visitedCode || maze[trow][tcol-1] == pathCode )
						moveToEmpty = 0;
						break;
			}
			
			if( moveToEmpty==1 && checkStatus() != TERMINATE ) {
				if ( direction==SOUTH ) return findGoldB(trow+1, tcol);
				else if( direction==EAST ) return findGoldB(trow, tcol+1);
				else if ( direction==NORTH ) return findGoldB(trow-1, tcol);
				else if ( direction==WEST ) return findGoldB(trow, tcol-1);
			} else if( moveToEmpty==0 ) {
				// perform backtrack to top stored movement
				//storedMovePtr--;
				//trow = storedMovePos[storedMovePtr][0];
				//tcol = storedMovePos[storedMovePtr][1];
				//prevDirection = storedMoveDir[storedMovePtr];
				//prevDirection = 1;
				
				storedMovePtr--;
				
				prevDirection = storedMoveDir[storedMovePtr];
				
				direction = backtrackfindGoldB(trow, tcol, checkPtMovePos[storedMovePtr][0], checkPtMovePos[storedMovePtr][1]);	// backtrack to checkPt
				
				//try { Thread.sleep(sleepSpeed); }
				//catch (InterruptedException e) { }
				//maze[trow][tcol] = visitedCode;
				//updateCell(trow, tcol, visitedCode, 1);
				
				if ( direction==SOUTH ) return findGoldB(checkPtMovePos[storedMovePtr][0]+1, checkPtMovePos[storedMovePtr][1]);
				else if( direction==EAST ) return findGoldB(checkPtMovePos[storedMovePtr][0], checkPtMovePos[storedMovePtr][1]+1);
				else if ( direction==NORTH ) return findGoldB(checkPtMovePos[storedMovePtr][0]-1, checkPtMovePos[storedMovePtr][1]);
				else if ( direction==WEST ) return findGoldB(checkPtMovePos[storedMovePtr][0], checkPtMovePos[storedMovePtr][1]-1);
			}
			
			if (checkStatus() == TERMINATE) return false;
		}
		return false;
	}
	
	int backtrackfindGoldB(int trow, int tcol, int targetRow, int targetCol) {
		boolean stopbacktrack = false;
		int curRow, curCol, nextRow, nextCol;
		
		stepCounter--;
		while( stopbacktrack == false ) {
			stopbacktrack = execBacktrack(trow, tcol, targetRow, targetCol);
		}
		
		curRow = targetRow;
		curCol = targetCol;
		nextRow = storedMovePos[storedMovePtr][0];
		nextCol = storedMovePos[storedMovePtr][1];
		
		if ( nextRow==curRow+1 && nextCol==curCol ) return 0;	// south
		else if( nextRow==curRow && nextCol==curCol+1 ) return 1;	// east
		else if ( nextRow==curRow-1 && nextCol==curCol ) return 2;	// north
		else if ( nextRow==curRow && nextCol==curCol-1 ) return 3;	// west
		
		return 9;
	}
	
	boolean execBacktrack(int trow, int tcol, int targetRow, int targetCol) {
	
		int curRow, curCol, nextRow, nextCol;
	
		if (maze[trow][tcol] == emptyCode || maze[trow][tcol] == thiefCode || maze[trow][tcol] == goldCode || maze[trow][tcol] == doorCode
		|| maze[trow][tcol] == securityCode || maze[trow][tcol] == pathCode || maze[trow][tcol] == visitedCode || maze[trow][tcol] == loopCode
		|| maze[trow][tcol] == foundCode ) {
			
			if (checkStatus() == TERMINATE) return false;

			//maze[trow][tcol] = pathCode;
			//updateCell(trow, tcol, pathCode);
			
			stepCounter++;
			updateCell(trow, tcol, thiefCode, 1, 1);

			if (trow == securityPosX && tcol == securityPosY) {
					try { Thread.sleep(sleepSpeed); }
				catch (InterruptedException e) { }
				
				updateCell(securityPosX, securityPosY, loopCode, 1, 1);
				
				updateCell(lastSecurityPosX, lastSecurityPosY, emptyCode, 1, 1);
					try { Thread.sleep(9000); }
				catch (InterruptedException e) { }
			}
			
			if (trow == targetRow && tcol == targetCol) {
				
				try { Thread.sleep(sleepSpeed); }
				catch (InterruptedException e) { }
				
				maze[trow][tcol] = visitedCode;
				updateCell(trow, tcol, visitedCode, 1, 1);
				
				return true;	// stop backtrack
			}
				
			try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
			maze[trow][tcol] = visitedCode;
			updateCell(trow, tcol, visitedCode, 1, 1);
			
			if (trow == doorPosX && tcol == doorPosY) {
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
				// call function store door pos
			}
			
			updateKnowledge(trow, tcol);
			//prevDirection = direction;
			//direction = updateDirectionB(prevDirection, trow, tcol);
			
			thiefPosLogPtr--;
			curRow = thiefPosLog[thiefPosLogPtr][0];
			curCol = thiefPosLog[thiefPosLogPtr][1];
			nextRow = thiefPosLog[thiefPosLogPtr-1][0];	// assuming no backtrack should happen after only thief moved for the first time
			nextCol = thiefPosLog[thiefPosLogPtr-1][1];
			
			if( checkStatus() != TERMINATE ) {
				if ( nextRow==curRow+1 && nextCol==curCol ) return execBacktrack(trow+1, tcol, targetRow, targetCol);	// south
				else if( nextRow==curRow && nextCol==curCol+1 ) return execBacktrack(trow, tcol+1, targetRow, targetCol);	// east
				else if ( nextRow==curRow-1 && nextCol==curCol ) return execBacktrack(trow-1, tcol, targetRow, targetCol);	// north
				else if ( nextRow==curRow && nextCol==curCol-1 ) return execBacktrack(trow, tcol-1, targetRow, targetCol);	// west
			}
			
			if (checkStatus() == TERMINATE) return false;
		}
		return false;
	}
	
	boolean findDoorB(int trow, int tcol) {
		if (maze[trow][tcol] == emptyCode || maze[trow][tcol] == thiefCode || maze[trow][tcol] == goldCode || maze[trow][tcol] == doorCode
		|| maze[trow][tcol] == securityCode || maze[trow][tcol] == pathCode || maze[trow][tcol] == visitedCode || maze[trow][tcol] == loopCode
		|| maze[trow][tcol] == foundCode ) {
			
			if (checkStatus() == TERMINATE) return false;
			
			//maze[trow][tcol] = pathCode;
			//updateCell(trow, tcol, pathCode);
			
			stepCounter++;
			updateCell(trow, tcol, thiefCode, 1, 1);
			if (trow == doorPosX && tcol == doorPosY) {
				doorisFound = 1;
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
				doorValue = 35;
				//goldisFound = 0;
				return true;
			}
				
			try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
			maze[trow][tcol] = visitedCode;
			updateCell(trow, tcol, visitedCode, 1, 1);
			
			if (trow == goldPosX && tcol == goldPosY) {
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
			}
			
			updateKnowledge(trow, tcol);
			prevDirection = direction;
			direction = updateDirectionB(prevDirection, trow, tcol);
			
			if(security==1){
				if( times == 1 ) {
					findthief(securityPosX, securityPosY);
					times=0;
				} else {
				times++;
				}
			}
			
			/*
			numberofsteps++;
			thiefPosLog[thiefPosLogPtr][0] = trow;
			thiefPosLog[thiefPosLogPtr][1] = tcol;
			
			if( numberofsteps>4 ) {
				if( isLoopMovement(thiefPosLogPtr) ) {
					maze[ thiefPosLog[thiefPosLogPtr][0] ][ thiefPosLog[thiefPosLogPtr][1] ] = loopCode;
					maze[ thiefPosLog[thiefPosLogPtr-1][0] ][ thiefPosLog[thiefPosLogPtr-1][1] ] = loopCode;
					updateCell(thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1], loopCode);
					updateCell(thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1], loopCode);
					
					synchronized(this) {
						try { wait(sleepSpeed); }
						catch (InterruptedException e) { }
					}
					
					updateKnowledgeLoop( thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1],
					thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1] );
					direction = updateDirectionLoop(prevDirection);
					
					//direction = updateDirectionLoop(prevDirection,
					//thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1],
					//thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1]
					//);
					
					thiefPosLogPtr++;
					//direction = 2;
					//additionalMove = 0;
					
					if( additionalMove == 1 ) {
						switch(direction) {
							case 0: return findGoldA(trow+2, tcol);
							case 1: return findGoldA(trow, tcol+2);
							case 2: return findGoldA(trow-2, tcol);
							case 3: return findGoldA(trow, tcol-2);
						}
					} else {
						switch(direction) {
							case 0: return findGoldA(trow+1, tcol);
							case 1: return findGoldA(trow, tcol+1);
							case 2: return findGoldA(trow-1, tcol);
							case 3: return findGoldA(trow, tcol-1);
						}
					}
				}
			}
			thiefPosLogPtr++;
			*/
			
			if( checkStatus() != TERMINATE ) {
				if ( direction==SOUTH ) return findDoorB(trow+1, tcol);
				else if( direction==EAST ) return findDoorB(trow, tcol+1);
				else if ( direction==NORTH ) return findDoorB(trow-1, tcol);
				else if ( direction==WEST ) return findDoorB(trow, tcol-1);
			}
			if (checkStatus() == TERMINATE) return false;
		}
		return false;
	}
	
	/*
	boolean findGoldA(int trow, int tcol) {
		if (maze[trow][tcol] == emptyCode || maze[trow][tcol] == thiefCode || maze[trow][tcol] == goldCode || maze[trow][tcol] == doorCode
		|| maze[trow][tcol] == securityCode || maze[trow][tcol] == pathCode || maze[trow][tcol] == visitedCode || maze[trow][tcol] == loopCode
		|| maze[trow][tcol] == foundCode ) {
			
			if (checkStatus() == TERMINATE) return false;
			
			//maze[trow][tcol] = pathCode;
			//updateCell(trow, tcol, pathCode);
			
			updateCell(trow, tcol, thiefCode, 1);
			if (trow == goldPosX && tcol == goldPosY) {
				goldisFound = 1;
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1);
				
				doorValue = 125;
				maze[doorPosX][doorPosY] = doorCode;		// restore door position in case it was updated as visited cell
				updateCell(doorPosX, doorPosY, doorCode, 2);
				return true;
			}
				
			try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
			maze[trow][tcol] = visitedCode;
			updateCell(trow, tcol, visitedCode, 1);
			
			if (trow == doorPosX && tcol == doorPosY) {
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1);
				
				// call function store door pos
			}
			
			updateKnowledge(trow, tcol);
			prevDirection = direction;
			direction = updateDirection();
			
			numberofsteps++;
			thiefPosLog[thiefPosLogPtr][0] = trow;
			thiefPosLog[thiefPosLogPtr][1] = tcol;
			
			if( numberofsteps>4 ) {
				if( isLoopMovement(thiefPosLogPtr) ) {
					maze[ thiefPosLog[thiefPosLogPtr][0] ][ thiefPosLog[thiefPosLogPtr][1] ] = loopCode;
					maze[ thiefPosLog[thiefPosLogPtr-1][0] ][ thiefPosLog[thiefPosLogPtr-1][1] ] = loopCode;
					updateCell(thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1], loopCode, 1);
					updateCell(thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1], loopCode, 1);
					
					synchronized(this) {
						try { wait(sleepSpeed); }
						catch (InterruptedException e) { }
					}
					
					updateKnowledgeLoop( thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1],
					thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1] );
					direction = updateDirectionLoop(prevDirection);
					
					//direction = updateDirectionLoop(prevDirection,
					//thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1],
					//thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1]
					//);
					
					thiefPosLogPtr++;
					//direction = 2;
					//additionalMove = 0;
					
					if( additionalMove == 1 ) {
						switch(direction) {
							case 0: return findGoldA(trow+2, tcol);
							case 1: return findGoldA(trow, tcol+2);
							case 2: return findGoldA(trow-2, tcol);
							case 3: return findGoldA(trow, tcol-2);
						}
					} else {
						switch(direction) {
							case 0: return findGoldA(trow+1, tcol);
							case 1: return findGoldA(trow, tcol+1);
							case 2: return findGoldA(trow-1, tcol);
							case 3: return findGoldA(trow, tcol-1);
						}
					}
				}
			}
			thiefPosLogPtr++;
			
			if( checkStatus() != TERMINATE ) {
				if ( direction==SOUTH ) return findGoldA(trow+1, tcol);
				else if( direction==EAST ) return findGoldA(trow, tcol+1);
				else if ( direction==NORTH ) return findGoldA(trow-1, tcol);
				else if ( direction==WEST ) return findGoldA(trow, tcol-1);
			}
			if (checkStatus() == TERMINATE) return false;
		}
		return false;
	}
	*/
	
	/*
	boolean findDoorA(int trow, int tcol) {
		if (maze[trow][tcol] == emptyCode || maze[trow][tcol] == thiefCode || maze[trow][tcol] == goldCode || maze[trow][tcol] == doorCode
		|| maze[trow][tcol] == securityCode || maze[trow][tcol] == pathCode || maze[trow][tcol] == visitedCode || maze[trow][tcol] == loopCode
		|| maze[trow][tcol] == foundCode ) {
			
			if (checkStatus() == TERMINATE) return false;
			
			//maze[trow][tcol] = pathCode;
			//updateCell(trow, tcol, pathCode);
			
			updateCell(trow, tcol, thiefCode, 1);
			if (trow == doorPosX && tcol == doorPosY) {
				doorisFound = 1;
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1);
				doorValue = 35;
				//goldisFound = 0;
				return true;
			}
				
			try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
			maze[trow][tcol] = visitedCode;
			updateCell(trow, tcol, visitedCode, 1);
			
			if (trow == goldPosX && tcol == goldPosY) {
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1);
			}
			
			updateKnowledge(trow, tcol);
			prevDirection = direction;
			direction = updateDirection();
			
			numberofsteps++;
			thiefPosLog[thiefPosLogPtr][0] = trow;
			thiefPosLog[thiefPosLogPtr][1] = tcol;
			
			if( numberofsteps>4 ) {
				if( isLoopMovement(thiefPosLogPtr) ) {
					maze[ thiefPosLog[thiefPosLogPtr][0] ][ thiefPosLog[thiefPosLogPtr][1] ] = loopCode;
					maze[ thiefPosLog[thiefPosLogPtr-1][0] ][ thiefPosLog[thiefPosLogPtr-1][1] ] = loopCode;
					updateCell(thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1], loopCode, 1);
					updateCell(thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1], loopCode, 1);
					
					synchronized(this) {
						try { wait(sleepSpeed); }
						catch (InterruptedException e) { }
					}
					
					updateKnowledgeLoop( thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1],
					thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1] );
					direction = updateDirectionLoop(prevDirection);
					
					//direction = updateDirectionLoop(prevDirection,
					//thiefPosLog[thiefPosLogPtr][0], thiefPosLog[thiefPosLogPtr][1],
					//thiefPosLog[thiefPosLogPtr-1][0], thiefPosLog[thiefPosLogPtr-1][1]
					//);
					
					thiefPosLogPtr++;
					//direction = 2;
					//additionalMove = 0;
					
					if( additionalMove == 1 ) {
						switch(direction) {
							case 0: return findDoorA(trow+2, tcol);
							case 1: return findDoorA(trow, tcol+2);
							case 2: return findDoorA(trow-2, tcol);
							case 3: return findDoorA(trow, tcol-2);
						}
					} else {
						switch(direction) {
							case 0: return findDoorA(trow+1, tcol);
							case 1: return findDoorA(trow, tcol+1);
							case 2: return findDoorA(trow-1, tcol);
							case 3: return findDoorA(trow, tcol-1);
						}
					}
				}
			}
			thiefPosLogPtr++;
			
			if( checkStatus() != TERMINATE ) {
				if ( direction==SOUTH ) return findDoorA(trow+1, tcol);
				else if( direction==EAST ) return findDoorA(trow, tcol+1);
				else if ( direction==NORTH ) return findDoorA(trow-1, tcol);
				else if ( direction==WEST ) return findDoorA(trow, tcol-1);
			}
			if (checkStatus() == TERMINATE) return false;
		}
		return false;
	}
	*/
	
	///*
	boolean findGold01(int trow, int tcol) {
		if (maze[trow][tcol] == emptyCode || maze[trow][tcol] == thiefCode || maze[trow][tcol] == goldCode || maze[trow][tcol] == doorCode || maze[trow][tcol] == securityCode || maze[trow][tcol] == loopCode
		|| maze[trow][tcol] == foundCode) {
			maze[trow][tcol] = pathCode;
			if (checkStatus() == TERMINATE) return false;
			
			stepCounter++;
			updateCell(trow, tcol, pathCode, 1, 1);
			if (trow == goldPosX && tcol == goldPosY) {
				goldisFound = 1;
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
				
				doorValue = 125;
				maze[doorPosX][doorPosY] = doorCode;		// restore door position in case it was updated as visited cell
				updateCell(doorPosX, doorPosY, doorCode, 2, 1);
				return true;
			}
				
			try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
			if (trow == doorPosX && tcol == doorPosY) {
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
				// call function store door pos
			}
			
			if ( (findGold01(trow+1, tcol) && checkStatus() != TERMINATE)  || 
			(findGold01(trow, tcol+1) && checkStatus() != TERMINATE)  ||
			(findGold01(trow-1, tcol) && checkStatus() != TERMINATE)  ||
			findGold01(trow, tcol-1) )
				return true;
			if (checkStatus() == TERMINATE) return false;
			
			stepCounter++;
			maze[trow][tcol] = visitedCode;		// gold is not found, backtrack
			updateCell(trow, tcol, visitedCode, 1, 1);
			synchronized(this) {
				try { wait(sleepSpeed); }
				catch (InterruptedException e) { }
			}
			
			if (checkStatus() == TERMINATE) return false;
		}
		return false;
	}
	//*/
	
	boolean findDoor01(int trow, int tcol) {
		if (maze[trow][tcol] == emptyCode || maze[trow][tcol] == thiefCode || maze[trow][tcol] == goldCode || maze[trow][tcol] == doorCode || maze[trow][tcol] == securityCode || maze[trow][tcol] == loopCode || maze[trow][tcol] == foundCode) {
			maze[trow][tcol] = pathCode;
			if (checkStatus() == TERMINATE) return false;
			
			stepCounter++;
			updateCell(trow, tcol, pathCode, 1, 1);
			if (trow == doorPosX && tcol == doorPosY) {
				doorisFound = 1;
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
				
				doorValue = 35;
				//maze[doorPosX][doorPosY] = doorCode;		// restore door position in case it was updated as visited cell
				//updateCell(doorPosX, doorPosY, doorCode, 2);
				return true;
			}
				
			try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
			if (trow == goldPosX && tcol == goldPosY) {
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1, 1);
				// call function store door pos
			}
			
			if ( (findDoor01(trow+1, tcol) && checkStatus() != TERMINATE)  || 
			(findDoor01(trow, tcol+1) && checkStatus() != TERMINATE)  ||
			(findDoor01(trow-1, tcol) && checkStatus() != TERMINATE)  ||
			findDoor01(trow, tcol-1) )
				return true;
			if (checkStatus() == TERMINATE) return false;
			
			stepCounter++;
			maze[trow][tcol] = visitedCode;		// gold is not found, backtrack
			updateCell(trow, tcol, visitedCode, 1, 1);
			synchronized(this) {
				try { wait(sleepSpeed); }
				catch (InterruptedException e) { }
			}
			
			if (checkStatus() == TERMINATE) return false;
		}
		return false;
	}
	
	/*
	boolean findDoor01(int trow, int tcol) {
		if (maze[trow][tcol] == emptyCode || maze[trow][tcol] == thiefCode || maze[trow][tcol] == goldCode || maze[trow][tcol] == doorCode || maze[trow][tcol] == securityCode  || maze[trow][tcol] == loopCode
		|| maze[trow][tcol] == foundCode) {
			maze[trow][tcol] = pathCode;
			if (checkStatus() == TERMINATE) return false;
			
			stepCounter++;
			updateCell(trow, tcol, pathCode, 1);
			if (trow == doorPosX && tcol == doorPosY) {
				doorisFound = 1;
				maze[trow][tcol] = foundCode;
				updateCell(trow, tcol, foundCode, 1);
				//doorValue = 35;
				//goldisFound = 0;
				return true;
			}
				
			try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
			if ( (findDoor01(trow+1, tcol) && checkStatus() != TERMINATE)  || 
			(findDoor01(trow, tcol+1) && checkStatus() != TERMINATE)  ||
			(findDoor01(trow-1, tcol) && checkStatus() != TERMINATE)  ||
			findDoor01(trow, tcol-1) )
				return true;
			if (checkStatus() == TERMINATE) return false;
			
			stepCounter++;
			maze[trow][tcol] = visitedCode;		// door is not found, backtrack
			updateCell(trow, tcol, visitedCode, 1);
			synchronized(this) {
				try { wait(sleepSpeed); }
				catch (InterruptedException e) { }
			}
			
			if (checkStatus() == TERMINATE) return false;
		}
		return false;
	}
	*/
	
	void findthiefOld(int securityPosX, int securityPosY) {
		int x1 = securityPosX;       // current position of security
		int y1 = securityPosY;
		// Get current position of thief
		int x2 = thiefPosX;          // current position of thief
		int y2 = thiefPosY;

		if( lastSecurityPosX==0 && lastSecurityPosY==0 ) {
		
		}
		else {
			if (lastSecurityPosX == goldPosX && lastSecurityPosY == goldPosY) {
			
			}
			else {
				updateCell(lastSecurityPosX, lastSecurityPosY, emptyCode, 1, 2);
			}
		}
		
		lastSecurityPosX = securityPosX;
		lastSecurityPosY = securityPosY;
		
		updateCell(securityPosX, securityPosY, securityCode, 1, 2);
		
		try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
			
		if (securityPosX == goldPosX && securityPosY == goldPosY) {
			//maze[x1][x2] = goldCode
			updateCell(securityPosX, securityPosY, goldCode, 1, 2);
		}
		
		//maze[securityPosX][securityPosY] = visitedCode;
		//updateCell(securityPosX, securityPosY, emptyCode, 1);
		
		if (x2>x1) { 
			if(y2<y1) {               // thief is sw to the security
				sw();
			}
			else if(y2==y1) {         // thief is s to the security
				s();
			}
			else {                    // thief is se to the security
				se();
			}
		}
		else if(x2<x1) {
			if(y2<y1) {               // thief is nw to the security
				nw();
			}
			else if(y2==y1) {         // thief is n to the security
				n();
			}
			else {
				ne();                 // thief is ne to the security
			}
		}
		else {
			if(y2<y1) {
				w();                  // thief is to the w of the security
			}
			else {
				e();                  // thief is to the e of the security
			}
		}

		if( x1==x2 && y1==y2) {
			updateCell(x1, y1, loopCode, 1, 2);
			try { Thread.sleep(9000); }
				catch (InterruptedException e) { }
		}
	}
	
	void findthief(int securityPosX, int securityPosY) {
	
	
	
		int x1 = securityPosX;       // current position of security
		int y1 = securityPosY;
		// Get current position of thief
		int x2 = thiefPosX;          // current position of thief
		int y2 = thiefPosY;
	
		if( lastSecurityPosX==0 && lastSecurityPosY==0 ) {
		
		}
		else {
			if (lastSecurityPosX == goldPosX && lastSecurityPosY == goldPosY) {
			
			} else if (lastSecurityPosX == doorPosX && lastSecurityPosY == doorPosY) {
			
			}
			else {
				updateCell(lastSecurityPosX, lastSecurityPosY, emptyCode, 1, 2);
			}
		}
		
		lastSecurityPosX = securityPosX;
		lastSecurityPosY = securityPosY;

		updateCell(securityPosX, securityPosY, securityCode, 1, 2);
		
		try { Thread.sleep(sleepSpeed); }
			catch (InterruptedException e) { }
		
		if (securityPosX == goldPosX && securityPosY == goldPosY) {
			//maze[x1][x2] = goldCode
			updateCell(securityPosX, securityPosY, goldCode, 1, 2);
		}
		
		if (securityPosX == doorPosX && securityPosY == doorPosY) {
			//maze[x1][x2] = goldCode
			updateCell(securityPosX, securityPosY, doorCode, 1, 2);
		}
		
		//maze[securityPosX][securityPosY] = visitedCode;
		//updateCell(securityPosX, securityPosY, emptyCode, 1);
		
		if (x2>x1) 
		{ 
			if(y2<y1)               // thief is sw to the security
			{
				if(east!=0)
				   {
				     e();
					 east--;
				   }
				else if(south!=0)
				   {
				     s();
					 south--;
				   }
                else if(north!=0)
                   {
                     s();
                     north--;
                   }
                else if(west!=0)
				   {
				     w();
					 west--;
				   }
				else
                   {			
                  	 sw();
				   }
			}
			else if(y2==y1)     			// thief is s to the security
			{
			    if(north!=0)
				   {
				     n();
					 north--;
				   }
				else if(east!=0)
				   {
				     e();
					 east--;
				   }
                else if(west!=0)
                   {
                     w();
                     west--;
                   }
				else 
                   {				
				     s();
				   }	  
			}
			else  			// thief is se to the security
			{
			    if(west!=0)
				   {
				     w();
					 west--;
				   }
				else if(south!=0)
				   {
				     s();
					 south--;
				   }
                else if(north!=0)
                   {
                     s();
                     north--;
                   }
                else if(east!=0)
				   {
				     e();
					 east--;
				   }
				else
                   {			
                  	 se();
				   }
			}
		}
		else if(x2<x1)
		{
			if(y2<y1)                // thief is nw to the security
			{
			    if(east!=0)
				   {
				     e();
					 east--;
				   }
				else if(north!=0)
				   {
				     n();
					 north--;
				   }
                else if(south!=0)
                   {
                     s();
                     south--;
                   }
                else if(west!=0)
				   {
				     w();
					 west--;
				   }
				else
                   {			
                  	 nw();
				   }
			}
			else if(y2==y1)        // thief is n to the security
			{
				if(south!=0)
				   {
				     s();
					 south--;
				   }
				else if(east!=0)
				   {
				     e();
					 east--;
				   }
                else if(west!=0)
                   {
                     w();
                     west--;
                   }
				else 
                   {				
				     n();
				   }	  
			}
			else               // thief is ne to the security
			{
			    if(west!=0)
				   {
				     w();
					 west--;
				   }
				else if(north!=0)
				   {
				     n();
					 north--;
				   }
                else if(south!=0)
                   {
                     s();
                     south--;
                   }
                else if(east!=0)
				   {
				     e();
					 east--;
				   }
				else
                   {			
                  	 ne();
				   }
			}
		}
		else 
		{
			if(y2<y1)   			// thief is to the w of the security
			{
			    if(east!=0)
				   {
				     e();
					 east--;
				   }
				else if(north!=0)
				   {
				     n();
					 north--;
				   }
                else if(south!=0)
                   {
                     s();
                     south--;
                   }
				else 
                   {				
				     w();
				   }	  
			}
			else           // thief is to the e of the security   
            {			
			    if(west!=0)
				   {
				     w();
					 west--;
				   }
				else if(north!=0)
				   {
				     n();
					 north--;
				   }
                else if(south!=0)
                   {
                     s();
                     south--;
                   }
				else 
                   {				
				     e();
				   }	  
			} 
			 
		}
		
		if( x1==x2 && y1==y2) {
			updateCell(x1, y1, loopCode, 1, 2);
			try { Thread.sleep(9000); }
				catch (InterruptedException e) { }
		}

	}

	void sw() {                        // preference SWEN
        if(maze[securityPosX+1][securityPosY]==wallCode && maze[securityPosX][securityPosY-1]==wallCode)
        {
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX-m][securityPosY-1]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       north++;
					    }
			         west = 1;
			       }
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
        }		   
		
		if(maze[securityPosX+1][securityPosY]==wallCode && maze[securityPosX][securityPosY-1]==wallCode && maze[securityPosX-1][securityPosY]==wallCode)
		{
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX+1][securityPosY+m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       east++;
					    }
			         south = 1;
			       }
			   else if(maze[securityPosX-1][securityPosY+m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       east++;
					    }
			         north = 1;
			       }	    
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
		}		
	
		if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}    
	}        

	void s() {                         //preference SEWN
	    if(maze[securityPosX+1][securityPosY]==wallCode && maze[securityPosX][securityPosY-1]==wallCode && maze[securityPosX][securityPosY+1]==wallCode)
		{
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX-m][securityPosY+1]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       north++;
					    }
			         east = 1;
			       }
			   else if(maze[securityPosX-m][securityPosY+1]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       north++;
					    }
			         west = 1;
			       }	    
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
		}	
	   
		if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}    
	}

	void se() {                           //preference SEWN
	    if(maze[securityPosX+1][securityPosY]==wallCode && maze[securityPosX][securityPosY+1]==wallCode)
        {
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX-m][securityPosY-1]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       north++;
					    }
			         east = 1;
			       }
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
        }		   
		
		if(maze[securityPosX+1][securityPosY]==wallCode && maze[securityPosX][securityPosY-1]==wallCode && maze[securityPosX-1][securityPosY]==wallCode)
		{
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX+1][securityPosY-m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       west++;
					    }
			         south = 1;
			       }
			   else if(maze[securityPosX-1][securityPosY-m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       west++;
					    }
			         north = 1;
			       }	    
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
		}		
	
		if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}
	} 

	void nw() {                         //preference NWES
	
	
	    if(maze[securityPosX-1][securityPosY]==wallCode && maze[securityPosX][securityPosY-1]==wallCode)
        {
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX+m][securityPosY-1]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       south++;
					    }
			         west = 1;
			       }
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
        }		   
		
		if(maze[securityPosX-1][securityPosY]==wallCode && maze[securityPosX][securityPosY-1]==wallCode && maze[securityPosX+1][securityPosY]==wallCode)
		{
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX-1][securityPosY+m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       east++;
					    }
			         north = 1;
			       }
			   else if(maze[securityPosX+1][securityPosY+m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       east++;
					    }
			         south = 1;
			       }	    
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
		}	
		    
              					 
								 
		if(maze[securityPosX-1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}  
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
	}       

	void n() {    	//preference NEWS
	    if(maze[securityPosX-1][securityPosY]==wallCode && maze[securityPosX][securityPosY-1]==wallCode && maze[securityPosX][securityPosY+1]==wallCode)
		{
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX+m][securityPosY+1]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       south++;
					    }
			         east = 1;
			       }
			   else if(maze[securityPosX+m][securityPosY-1]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       south++;
					    }
			         west = 1;
			       }	    
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
		}	
	   
		if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
	}

	void ne() {                      //preference NEWS
        if(maze[securityPosX-1][securityPosY]==wallCode && maze[securityPosX][securityPosY+1]==wallCode)
        {
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX+m][securityPosY+1]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       south++;
					    }
			         east = 1;
			       }
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
        }		   
		
		if(maze[securityPosX-1][securityPosY]==wallCode && maze[securityPosX][securityPosY+1]==wallCode && maze[securityPosX+1][securityPosY]==wallCode)
		{
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX-1][securityPosY-m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       west++;
					    }
			         north = 1;
			       }
			   else if(maze[securityPosX+1][securityPosY-m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       west++;
					    }
			         south = 1;
			       }	    
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
		}	
		    	
	
		if(maze[securityPosX-1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}  
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
	}        

	void w() {      	//preference WSNE
	    if(maze[securityPosX-1][securityPosY]==wallCode && maze[securityPosX][securityPosY-1]==wallCode && maze[securityPosX+1][securityPosY]==wallCode)
		{
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX-1][securityPosY+m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       east++;
					    }
			         north = 1;
			       }
			   else if(maze[securityPosX+1][securityPosY+m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       east++;
					    }
			         south = 1;
			       }	    
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
		}	
	
		if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX-1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}    
		else {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
	}

	void e() {                            // preference ESNW
	    if(maze[securityPosX-1][securityPosY]==wallCode && maze[securityPosX][securityPosY+1]==wallCode && maze[securityPosX+1][securityPosY]==wallCode)
		{
		    int m = 1;
			int n;
			int flag = 0;
			while(m<10)
			{
			   if(maze[securityPosX-1][securityPosY-m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       west++;
					    }
			         north = 1;
			       }
			   else if(maze[securityPosX+1][securityPosY-m]!=wallCode)
			       {
				     flag = 1;
				     for(n=1;n<=m;n++)
				        {
					       west++;
					    }
			         south = 1;
			       }	    
			   if(flag==1)
                   {
                     break;
                   }
               m++;
            }
			findthief(securityPosX,securityPosY);
		}	
	
		if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX-1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}    
		else {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
	}

	/*
	void sw() {                        // preference SWEN
		if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}    
	}        

	void s() {                         //preference SEWN
		if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}    
	}

	void se() {                           //preference SEWN
		if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}
	} 

	void nw() {                         //preference NWES
	
		if(maze[securityPosX-1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}  
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
	}       

	void n() {                          //preference NEWS
		if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
	}

	void ne() {                      //preference NEWS
		if(maze[securityPosX-1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}  
		else if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
	}        

	void w() {                            //preference WSNE
		if(maze[securityPosX][securityPosY-1]!=wallCode) {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX-1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}    
		else {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
	}

	void e() {                            // preference ESNW
		if(maze[securityPosX][securityPosY+1]!=wallCode) {
			securityPosY=securityPosY+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX+1][securityPosY]!=wallCode) {
			securityPosX=securityPosX+1;
			//findthief(securityPosX,securityPosY);
		}
		else if(maze[securityPosX-1][securityPosY]!=wallCode) {
			securityPosX=securityPosX-1;
			//findthief(securityPosX,securityPosY);
		}    
		else {
			securityPosY=securityPosY-1;
			//findthief(securityPosX,securityPosY);
		}
	}
	*/
	
}
