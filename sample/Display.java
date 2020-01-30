//*********************************************************************************************************
//Gabriel Urbaitis
//
// The Display class is responsible for managing the graphical components of the game
// including interpreting mouse actions, panning, zooming, and single clicks to change
// cells. It also communicates back to the logic the changes reflecting its changing state.
//*********************************************************************************************************

package sample;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;




public class Display implements Initializable
{
  int y;
  private Stage stage;
  @FXML
  private Button startButton;
  @FXML
  private Button pauseButton;
  @FXML
  private Button clearButton;
  @FXML
  private Button randomButton;
  @FXML
  private Button presetButton1;
  @FXML
  private Button presetButton2;
  @FXML
  private Canvas canvas;
  private GraphicsContext gc;
  private double boxSize;
  private Boardbuilder grid;
  private int boardSize;
  private String preset;
  private double zoomScalar = 1;
  private int threads;
  private int yOrigin, xOrigin, xFinal, yFinal, deltaX, deltaY, xStart, yStart;

  private boolean dragged;
  @FXML
  private Label statusField;

  private void startGraphics()
  {
    gc = canvas.getGraphicsContext2D();
    boxSize = (zoomScalar * boardSize) / boardSize;
    pauseButton.setDisable(true);
    yOrigin = 1;
    xOrigin = 1;
    cleanCanvas();
  }

  private void startCanvas()
  {

    canvas.setHeight(500);
    canvas.setWidth(500);
    canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::singleBlock);
    canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::MousePress);
    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragging);
    canvas.addEventHandler(ScrollEvent.SCROLL, this::handleZoomEvent);
  }


  private void MousePress(MouseEvent event)
  {
    xStart = getGridValue(event.getY());
    yStart = getGridValue(event.getX());
  }

  private void singleBlock(MouseEvent event)
  {
    int x, y;
    if (!dragged)
    {
      x = getGridValue(event.getX());
      y = getGridValue(event.getY());
      if ((x > boardSize || x < 0) || (y > boardSize || y < 0)) return;
      toggleCell(x, y);
      String mouseLocation = "(" + x + ", " + y + ")";
      setStatusField("Mouse click at cell: " + mouseLocation);
    } else
    {
      int newY = yOrigin - deltaY;
      int newX = xOrigin - deltaX;
      if (newY > 0 && newY <= boardSize)
      {
        yOrigin = newY;
      }

      if (newX > 0 && newX <= boardSize)
      {
        xOrigin = newX;
      }
      redraw();
    }
    dragged = false;
  }


  @FXML
  private void dragging(MouseEvent event)
  {
    xFinal = getGridValue(event.getY());
    yFinal = getGridValue(event.getX());
    deltaX = xFinal - xStart;
    deltaY = yFinal - yStart;
    System.out.println(deltaX);
    yOrigin = -(deltaY > 0 ? 1 : deltaY == 0 ? 0 : -1);
    xOrigin = -(deltaX > 0 ? 1 : deltaX == 0 ? 0 : -1);
    redraw();
    dragged = true;
  }


  private void handleZoomEvent(ScrollEvent event)
  {
    zoomScalar = event.getDeltaY() > 0 ? 1 : -1;
    resizeBoxes();
    redraw();
  }

  private int getGridValue(double val)
  {
    return (int) (Math.floor(val / boxSize));
  }

  private void resizeBoxes()
  {
    double newSize = zoomScalar + boxSize;
    if (newSize > 50)
    {
      newSize = 50;
    } else if (newSize < 1) newSize = 1;
    boxSize = newSize;
  }

  private void toggleCell(int x, int y)
  {
    int a = y + xOrigin;
    int b = x + yOrigin;
    if (a > boardSize || a < 0 || b > boardSize || b < 0) return;
    if (grid.read[a][b] == 0)
    {
      gc.setFill(Color.LAWNGREEN);
      grid.setCell(a, b, 1);
    } else if (grid.read[a][b] > 0)
    {
      gc.setFill(Color.BLACK);
      grid.setCell(a, b, 0);
    }
    gc.fillRect(x * boxSize, y * boxSize, boxSize, boxSize);
  }

  @FXML
  private void startGame()
  {
    grid.startBoard();
    startButton.setDisable(true);
    pauseButton.setDisable(false);
//    nextButton.setDisable(true);
    clearButton.setDisable(true);
    randomButton.setDisable(true);
    presetButton1.setDisable(true);
    presetButton2.setDisable(true);

  }

  @FXML
  private void pauseGame()
  {
    grid.pauseBoard();
    pauseButton.setDisable(true);
    //nextButton.setDisable(false);
    startButton.setDisable(false);
    clearButton.setDisable(false);
    randomButton.setDisable(false);
    presetButton1.setDisable(false);
    presetButton2.setDisable(false);

  }

  @FXML
  private void nextBoardStep()
  {
    grid.oneStep();
  }

  @FXML
  private void drawBoard()
  {
    byte current;
    int x, y;
    for (int i = 1; i <= canvas.getWidth(); i++)
    {
      for (int j = 1; j <= canvas.getHeight(); j++)
      {
        x = i + xOrigin;
        y = j + yOrigin;
        if ((x < 1 || x > boardSize) || (y < 1 || y > boardSize)) continue;
        current = grid.read[x][y];
        gc.setFill(Color.rgb(200, 255 - (current * 15), (current * 3)));
        if (current > 0)
        {
          gc.fillRect(j * boxSize, i * boxSize,
                  boxSize > 5 ? boxSize - 1 : boxSize, boxSize > 5 ? boxSize - 1 : boxSize);
        }
      }
    }
  }

  @FXML
  private void setupRandomBoard()
  {
    grid.cleanBoard();
    grid.setBoard("Random");
    cleanCanvas();
    drawBoard();
  }

  @FXML
  private void setupGliderGun()
  {
    grid.cleanBoard();
    grid.setBoard("Glider Gun");
    cleanCanvas();
    drawBoard();
  }

  @FXML
  private void setupCheckerboard()
  {
    grid.cleanBoard();
    grid.setBoard("Checkerboard");
    cleanCanvas();
    drawBoard();
  }

  @FXML
  private void setupSpecial()
  {
    grid.cleanBoard();
    grid.setBoard("Special");
    cleanCanvas();
    drawBoard();
  }

  @FXML
  private void setupAlmostFullBoard()
  {
    grid.cleanBoard();
    grid.setBoard("Almost Full");
    cleanCanvas();
    drawBoard();
  }

  //***********************************
  // @param none
  // @return void
  // Clears the canvas, redraws the board
  // and indicates that the board is done
  // updating.
  //***********************************
  void redraw()
  {
    cleanCanvas();
    drawBoard();
    grid.updateBoard = false;
  }

  private void cleanCanvas()
  {
    gc.setFill(Color.BLACK);
    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
  }

  @FXML
  private void clearBoard()
  {
    cleanCanvas();
    grid.cleanBoard();
    setStatusField("Cleared Canvas.");
  }

  //***********************************
  // @param doubles newSize and threadCount
  // String preset. The selections made by the
  // use. Size scales from 250x250 to 10000x10000,
  // Threads from 1-8, and presets are the 5 required.
  // Takes the selections made by the user
  // and sets the board size, threads and pattern
  // accordingly.
  //***********************************
  void setValues(double newSize, double threadCount, String preset)
  {
    threads = (int) threadCount;
    boardSize = (int) newSize;
    this.preset = preset;
  }

  //***********************************
  // @param Stage stage
  // any stage withing the resizing limits
  // Sets the stage for purposes
  // of resizing
  //***********************************
  void setStage(Stage stage)
  {
    this.stage = stage;
  }

  //***********************************
  // @param none
  // @return void
  // Instantiates the major pieces such
  // as the grid, animation timer and canvas.
  //***********************************
  void initializeData()
  {
    grid = new Boardbuilder(boardSize, threads, this);
    startCanvas();
    startGraphics();
    grid.setBoard(preset);
    drawBoard();
    new AnimationTimer()
      {
      @Override
      public void handle(long now)
      {
        if (grid.updateBoard)
        {
          redraw();
        }
      }
    }.start();
    stage.setOnCloseRequest(event ->
    {
      grid.threadsComplete = true;
    });
  }

  //***********************************
  // @param String status
  // A string indicating the status of
  // and event or the canvas.
  // Prints a string for debugging purposes.
  //***********************************
  void setStatusField(String status)
  {
    statusField.setText(status);
    System.out.println(status);
  }

  //***********************************
  // NOT USED
  // Overriden to implement initializable.
  //***********************************
  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
  }

}