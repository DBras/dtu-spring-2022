# Introduction to Cyber Technology Spring 2022

Contains the source code for the projects gone through in the 2022 spring course.

## src/guitest

This folder contains the code for the first two tutorials.

### Converter.java

Contains the source code for the converter GUI. It can convert between decimal, binary and hexadecimal numbers.

### Tictactoegui.java

Program for creating a GUI for the tic-tac-toe game. Shows a 3x3 grid with buttons for each field of the game board. 
It establishes a connection to the server that runs the game and renders the game board every time something changes. 
If the user wishes to re-try the game, a new connection is established.

## src/asyncgui

Contains the source code for the third tutorial, which creates a window that (asynchronously) writes prime numbers and draws random rectangles on the screen. Also contains a "quit" button.

### Main.java

The main program to be run. Contains the static main()-method which initialises the GUI and starts the threads.

### PrimeRunnable.java

Implements the _Runnable_-interface so that it can run as a separate thread by the main program. 
This program is given the main GUI as an argument and then writes the prime numbers on the field dedicated to those. The program is also given an amount of time in which to sleep in between writing the prime numbers.

### RectanglePanel.java

Extends JPanel. When constructed, it initialises a JPanel on which to draw (with a BufferedImage). It starts by drawing a white background which fills the entire panel. 
This class also contains a method `drawRandomRectangle()` which draws a rectangle on the panel within the limits of the panel size. The rectangle is given a random color.

### RectangleRunnable.java

Implements the _Runnable_-interface. This class is run as a separate thread. It calls the `drawRandomRectangle()`-method from `RectanglePanel.java` and then sleeps for the specified amount of time given in the construction of the object. 
