//*********************************************************************************************************
//Gabriel Urbaitis
//
// The Main class is simply responsible for getting the program to run by
// loading the FXML and displaying the Menu
//*********************************************************************************************************
package sample;

        import javafx.application.Application;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.canvas.Canvas;
        import javafx.stage.Stage;

public class Main extends Application
{
    Canvas gameCanvas;
  //***********************************
  // @param String[] "args"
  // Any number of command line arguments
  // NOT USED.
  // Launches the Arguments for JAVAFX.
  //***********************************
    public static void main(String[] args)
    {
        launch(args);
    }
  //***********************************
  // @param Stage stage
  // The Menu Scene
  // Runs the FXML for the Menu.
  //***********************************
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent startup = FXMLLoader.load(getClass().getResource("menuGUI.fxml"));
        Scene startScene = new Scene(startup);
        stage.setTitle("John Conway's Game of Life");
        stage.setScene(startScene);
        stage.show();
    }
}
