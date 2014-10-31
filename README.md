VI-and-Path-Planning
====================

Modified Value Iteration Algorithm and Path Planning  for Solving Sequential Decision Problem in a Grid   World Simulation Game


// Ganesh Rakate, Faris Al Afif
// Email: rakat001@umn.edu, afifx001@umn.edu


The program is java applet based program that needs to be launched using the browser.

1. Open the mazeapplet.html file using the browser and confirm the java dialog box/pop up.
   The mazeapplet.html file will load and run the java compiled program Maze.class.

2. There are several available parameters to be set to modify how the program will run.
   Modify the paramaters and refresh/restart the browser to restart the program.
   This parameter passing allow the user to run the program differently without re-compiling it first.


Information for the parameters are as follow.
<param name="mazeType" value="1">
<param name="nIteration" value="20">
<param name="iterSpeed" value="200">
<param name="sleepSpeed" value="300">
<param name="nextMaze" value="1000">

<param name="borderColor" value="gray">
<param name="wallColor" value="gray">
<param name="agentColor" value="blue">
<param name="defaultColor" value="cyan">
<param name="targetColor" value="yellow">
<param name="dodgeColor" value="magenta">
<param name="foundColor" value="green">


- mazeType
description:
four provided maze configuration (variation in position of agent, wall, positive, and negative state)

value setting:
1, 2, 3, or 4


- nIteration
description:
number of Iteration for the value iteration process

value setting:
Example of number of iteration: 20, 40, 60, or 80


- iterSpeed
description:
adjust visualization speed of the value iteration process

value setting:
in milliseconds, 100 for fast speed, 200 for medium speed


- sleepSpeed
description:
adjust visualization speed of the agent movement process

value setting:
in milliseconds, 100 for fast speed, 200 for medium speed


- nextMaze
description:
adjust the delay speed of showing the new maze.
the program is refreshed automatically after it completed its full run

value setting:
in milliseconds, 1000 for medium refreshing speed


- various Color parameter:
description:
color parameter to assign/modify the color for different kind of cell/object

value setting:
green, red, yellow, blue, cyan, magenta, orange, pink, gray, darkgray, lightgray, black, white.
