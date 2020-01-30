//*********************************************************************************************************
//Gabriel Urbaitis
//
// The Menu Class creates controls for selecting the initial pattern, number of threads and board size.
// It combines the different selections into a neat user interface which communicates the choices out to
// the Display and Boardbuilder.
//*********************************************************************************************************

package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Menu implements Initializable
{

    @FXML
    private Button resetValuesButton;
    @FXML
    private Button beginGameButton;
    @FXML
    private Slider boardSizeSlider;
    @FXML
    private Slider threadCountSlider;
    @FXML
    private ComboBox<String> presetMenu;

    @FXML
    private void switchScene(ActionEvent e) throws IOException
    {
        String presetString = "Clear";
        Stage stage = (Stage) beginGameButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gameGUI.fxml"));
        Parent game = loader.load();
        Display controller = loader.getController();
        if (presetMenu.getValue() != null)
        {
            presetString = presetMenu.getValue();
        }
        else
        {
            controller.setStatusField("Default loaded.");
        }
        controller.setValues(boardSizeSlider.getValue(), threadCountSlider.getValue(), presetString);
        controller.setStage(stage);
        controller.initializeData();
        stage.setTitle("John Conway's Game of Life");
        Scene scene = new Scene(game);
        stage.setScene(scene);
        stage.setMinHeight(642);
        stage.show();
    }

    @FXML
    private void resetValues()
    {
        resetValuesButton.setDisable(true);
        threadCountSlider.setValue(1);
        boardSizeSlider.setValue(500);
    }

    @FXML
    private void cancelApplication()
    {
        System.exit(0);
    }

    @FXML
    private void handleSliderChange()
    {
        resetValuesButton.setDisable(false);
    }

    //***********************************
    // @param none
    // @return void
    // initialize disables the resetValues
    // button.
    //***********************************

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        resetValuesButton.setDisable(true);
    }
}
