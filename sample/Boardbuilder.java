//*********************************************************************************************************
//Gabriel Urbaitis
// The Boardbuilder Class is responsible for managing the logic of Conway's game of life,
// as well as handling the threads and setting the appropriate patterns onto the board.
//*********************************************************************************************************
package sample;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Boardbuilder
{
    private static boolean DEBUG;
    private final int GRID_LEN;
    private final int THREAD_COUNT;
    private final Display GUI;
    byte[][] read;
    Boolean updateBoard;
    boolean threadsComplete;
    private CyclicBarrier barrier;
    private AnimationTimer TIMER;
    private byte[][] write;
    private byte[][] temp;
    private boolean paused, once;

    Boardbuilder(int size, int threadCount, Display controller)
    {
        GRID_LEN = size;
        THREAD_COUNT = threadCount;
        read = new byte[GRID_LEN + 2][GRID_LEN + 2];
        write = new byte[GRID_LEN + 2][GRID_LEN + 2];
        GUI = controller;
        threadsComplete = false;
        updateBoard = false;
        paused = true;
        once = false;
        startThreads();
    }

    private void startThreads()
    {
        int margin = GRID_LEN / THREAD_COUNT;
        barrier = new CyclicBarrier(this.THREAD_COUNT, () ->
        {
            swapBoards();
            synchronized (updateBoard)
            {
                updateBoard = true;
            }
            if (once)
            {
                once = false;
                paused = true;
            }
        });
        List<GameThread> threads = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++)
        {
            int start = (i * margin) + 1;
            int end = start + margin;
            if (i == THREAD_COUNT - 1 && end != GRID_LEN) end = GRID_LEN;
            System.out.println(start + " to " + end);
            GameThread thread = new GameThread(start, end);
            threads.add(thread);
            thread.start();
        }
    }

    private void swapBoards()
    {
        temp = read;
        read = write;
        write = temp;
    }
    //***********************************
    // @param ints a, b, value"
    // a and b are any valid indices of the
    // board, value is the stored byte from
    // 1-11.
    // Sets the inputted value into the
    // read byte array at the specified
    // indices.
    //***********************************
    void setCell(int a, int b, int value)
    {
        read[a][b] = (byte) value;
    }
    //***********************************
    // @param String presetValue
    // Any of "Random, Almost Full, Glider Gun,
    // Checkerboard, special or Clear
    // Based on the input string, sets a
    // pattern onto the board.
    //***********************************
    boolean setBoard(String presetValue)
    {
        switch (presetValue)
        {
            case "Random":
                newRandomBoard();
                return true;
            case "Almost Full":
                newAlmostFullBoard();
                return true;
            case "Glider Gun":
                newGliderGun();
                return true;
            case "Checkerboard":
                newCheckerboard();
                return true;
            case "Special":
                newSpecial();
                return true;
            case "Clear":
            default:
                newRandomBoard();
                return false;
        }
    }

    private void newSmallExploder()
    {
        // Creates a small explosion pattern which stops changing after 16 steps.
        int center = (int) Math.floor(GRID_LEN / 2);
        read[center][center] = 1;
        read[center + 1][center - 1] = 1;
        read[center + 1][center] = 1;
        read[center + 1][center + 1] = 1;
        read[center + 2][center - 1] = 1;
        read[center + 2][center + 1] = 1;
        read[center + 3][center] = 1;
    }

    private void newRandomBoard()
    {
        Random rand = new Random();
        for (int i = 2; i <= GRID_LEN; i++)
        {
            for (int j = 2; j <= GRID_LEN; j++)
            {
                read[i][j] = (byte) rand.nextInt(2);
            }
        }
    }


    private void newGliderGun()
    {
        read[2][26] = 1;
        read[3][24] = 1;
        read[3][26] = 1;
        read[4][14] = 1;
        read[4][15] = 1;
        read[4][22] = 1;
        read[4][23] = 1;
        read[4][36] = 1;
        read[4][37] = 1;
        read[5][13] = 1;
        read[5][17] = 1;
        read[5][22] = 1;
        read[5][23] = 1;
        read[5][36] = 1;
        read[5][37] = 1;
        read[6][2] = 1;
        read[6][3] = 1;
        read[6][12] = 1;
        read[6][18] = 1;
        read[6][22] = 1;
        read[6][23] = 1;
        read[7][2] = 1;
        read[7][3] = 1;
        read[7][12] = 1;
        read[7][16] = 1;
        read[7][18] = 1;
        read[7][19] = 1;
        read[7][24] = 1;
        read[7][26] = 1;
        read[8][12] = 1;
        read[8][18] = 1;
        read[8][26] = 1;
        read[9][13] = 1;
        read[9][17] = 1;
        read[10][14] = 1;
        read[10][15] = 1;
    }

    private void newCheckerboard()
    {
        int counter = 1;
        int beginScaler = 2;
        for (int i = 2; i < GRID_LEN; i++)
        {
            for (int j = beginScaler; j < GRID_LEN; j+=2)
            {
                if(counter < 8)
                {
                    read[i][j] = 1;
                    counter++;
                }
                else counter = 0;
            }
            counter = (i%9);
            beginScaler++;
        }
    }

    private void newSpecial()
    {
            int offset = GRID_LEN /2;
            read[offset][offset-18] = 1;
            read[offset][offset-17] = 1;
            read[offset][offset-16] = 1;
            read[offset][offset-15] = 1;
            read[offset][offset-14] = 1;
            read[offset][offset-13] = 1;
            read[offset][offset-12] = 1;
            read[offset][offset-11] = 1;
            read[offset][offset-9] = 1;
            read[offset][offset-8] = 1;
            read[offset][offset-7] = 1;
            read[offset][offset-6] = 1;
            read[offset][offset-5] = 1;
            read[offset][offset-1] = 1;
            read[offset][offset] = 1;
            read[offset][offset+1] = 1;
            read[offset][offset+8] = 1;
            read[offset][offset+9] = 1;
            read[offset][offset+10] = 1;
            read[offset][offset+11] = 1;
            read[offset][offset+12] = 1;
            read[offset][offset+13] = 1;
            read[offset][offset+14] = 1;
            read[offset][offset+16] = 1;
            read[offset][offset+17] = 1;
            read[offset][offset+18] = 1;
            read[offset][offset+19] = 1;
            read[offset][offset+20] = 1;

        }

    private void newAlmostFullBoard()
    {
        for (int i = 2; i < GRID_LEN; i++)
        {
            for (int j = 2; j < GRID_LEN; j++)
            {
                read[i][j] = 1;
            }
        }
    }
    //*******************************
    // @param none
    // @return void
    // Unpauses the game
    //*******************************
     void startBoard()
    {
        paused = false;
    }
    //***********************************
    // @param none
    // @return void
    // pauses the game
    //***********************************
    void pauseBoard()
    {
        paused = true;
    }
    //***********************************
    // @param none
    // @return void
    // Moves the pattern ahead one generation
    // and pauses the game
    //***********************************
    void oneStep()
    {
        once = true;
        paused = false;
    }

    private void printBoard(byte[][] board)
    {
        char symbol = '#';
        for (int i = 0; i <= GRID_LEN + 1; i++)
        {
            for (int j = 0; j <= GRID_LEN + 1; j++)
            {
                if (i == 0)
                {
                    System.out.print("_");
                }
                else if (i == GRID_LEN + 1)
                {
                    System.out.print("¯");
                }
                else if (j == 0 || j == GRID_LEN + 1)
                {
                    System.out.print("|");
                }
                else
                {
                    System.out.print(board[i][j] >= 1 ? symbol : ' ');
                }
            }
            System.out.println("");
        }
    }

    private void printBoard()
    {
        printBoard(read);
    }
    //***********************************
      // @param none
      // @return void
      // Replaces all bytes in the read
      // array with 0s.
      //***********************************
    void cleanBoard()
    {
        for (byte[] arr : read)
        {
            Arrays.fill(arr, (byte) 0);
        }
    }
//*********************************************************************************************************
//Gabriel Urbaitis
//
//  The GameThread class sets the Threads on the appropriate rows, updates the
//  cyclic barrier and puts the thread to sleep when the game is paused
//
//*********************************************************************************************************
    class GameThread extends Thread
    {
        int startRow;
        int endRow;

        GameThread(int start, int end)
        {
            startRow = start;
            endRow = end;
        }

        @Override
        public void run()
        {
            while (!threadsComplete)
            {
                if (!paused && !updateBoard)
                {
                    nextStep(startRow, endRow);
                    try
                    {
                        barrier.await();
                    }
                    catch (InterruptedException | BrokenBarrierException ex)
                    {
                        System.exit(51);
                    }
                }
                else
                {
                    try
                    {
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        System.exit(52);
                    }
                }
            }
        }

        private void nextStep(int start, int end)
        {
            byte current;
            int neighbors;
            for (int i = start; i <= end; i++)
            {
                neighbors = checkNeighbors(i, 1);
                for (int j = 1; j <= GRID_LEN; j++)
                {
                    current = read[i][j];
                    if (j > 1) neighbors = updateNeighbors(i, j, neighbors);
                    if (current > 0)
                    {
                        if (neighbors > 3 || neighbors < 2)
                        {
                            write[i][j] = 0;
                        }
                        else
                        {
                            write[i][j] = (byte) (current < 10 ? current + 1 : current);
                        }
                    }
                    else if (neighbors == 3)
                    {
                        write[i][j] = 1;
                    }
                    else
                    {
                        write[i][j] = current;
                    }
                }
            }
        }

        private int updateNeighbors(int x, int y, int oldValue)
        {
            int valueMod = oldValue;
            for (int i = -1; i <= 1; i++)
            {
                if (read[x + i][y - 2] > 0) valueMod--;
                if (read[x + i][y + 1] > 0) valueMod++;
            }
            if (read[x][y - 1] > 0) valueMod++;
            if (read[x][y] > 0) valueMod--;
            return valueMod;
        }

        private int checkNeighbors(int x, int y)
        {
            int neighbors = (read[x][y] > 0 ? -1 : 0);
            for (int i = -1; i <= 1; i++)
            {
                for (int j = -1; j <= 1; j++)
                {
                    if (read[x + i][y + j] > 0) neighbors++;
                }
            }
            return neighbors;
        }
    }
}