package sample;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
/*
    CS 2450 Homework 4
    Group members: Alan Huang, Francisco Ceja
 */

public class Main extends Application {

    private RadioButton box, sphere, cylinder;
    private TextField inputX, inputY, inputWidth, inputHeight, inputRadius, inputLength;
    private BorderPane myMainPane;
    private Group shapesGroup;
    private SubScene shapesSub;
    private MenuBar menuBar;
    private Button AddShapeSubmit;
    private Button submit;
    private PerspectiveCamera pCamera;
    private VBox rightVBox, buttonBox;
    private createObjects currentSelected = null;
    private double xPoint, yPoint, inputW, inputH, inputL, inputR;
    private String backColor, objectColor;
    private Slider sliderX, sliderY, sliderZ;
    private Button increaseScale, decreaseScale;
    private Button increaseX, decreaseX, increaseY, decreaseY, increaseZ, decreaseZ;
    private ListView<String> backgroundColor;
    private ListView<String> shapeColor;
    private File curFilePath;

    private ArrayList<createObjects> shapeList;

    @Override
    public void start(Stage primaryStage) {

        curFilePath = new File("");
        shapeList = new ArrayList<>();
        backColor = "White";

        //Setting up all menus and borderPane items
        createMenus(primaryStage);
        createRighMenu();
        createSubScene();
        createListener();

        //Setting up BorderPane
        myMainPane = new BorderPane();
        myMainPane.setTop(menuBar);
        myMainPane.setCenter(shapesSub);
        myMainPane.setRight(rightVBox);
        myMainPane.setBottom(buttonBox);

        //Adding style sheets
        myMainPane.getStylesheets().add("myStyles.css");

        //My main scene
        Scene myMainScene = new Scene(myMainPane, 1200, 700);


        primaryStage.setScene(myMainScene);
        primaryStage.show();

        AddShapeSubmit.setOnAction(event -> addObjectsMenu());

    }

    private void createMenus(Stage primaryStage) {

        //Create menus
        menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(openItem, saveItem, new SeparatorMenuItem(), exitItem);
        menuBar.getMenus().add(fileMenu);

        menuBar.setId("menubar"); //setCSS id
        fileMenu.setId("menu1");
        openItem.setId("menu1");
        saveItem.setId("menu1");
        exitItem.setId("menu1");

        exitItem.setOnAction(event -> primaryStage.close());

        saveItem.setOnAction(event -> saveFile(primaryStage));

        openItem.setOnAction(event -> openFile(primaryStage));
    }

    private void saveFile(Stage primaryStage) {
        try {
            FileChooser fc = new FileChooser();
            // only allow the user to create a .txt file.
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            curFilePath = fc.showSaveDialog(primaryStage);

            FileWriter fileWriter = new FileWriter(curFilePath.getPath(), true);
            PrintWriter outputFile = new PrintWriter(fileWriter);

            //Use my ArrayList size to save all shapes
            for (int i = 0; i < shapeList.size(); i++) {

                //For box
                if (shapeList.get(i).getShapeType() == 1) {
                    outputFile.println(shapeList.get(i).getShapeType() + " " + shapeList.get(i).getXPoint() + " " + shapeList.get(i).getYPoint()
                            + " " + shapeList.get(i).getZPoint() + " " + shapeList.get(i).getHeight() + " " + shapeList.get(i).getWidth()
                            + " " + shapeList.get(i).getDepth() + " " + shapeList.get(i).getRadius() + " " + shapeList.get(i).getScaleFactor()
                            + " " + shapeList.get(i).getRotateAngleX() + " " + shapeList.get(i).getRotateAngleY() + " " + shapeList.get(i).getRotateAngleZ()
                            + " " + shapeList.get(i).getColor() + " " + backColor); //13
                }

                //For sphere
                if (shapeList.get(i).getShapeType() == 2) {
                    outputFile.println(shapeList.get(i).getShapeType() + " " + shapeList.get(i).getXPoint() + " " + shapeList.get(i).getYPoint()
                            + " " + shapeList.get(i).getZPoint() + " " + shapeList.get(i).getRadius() + " " + shapeList.get(i).getScaleFactor()
                            + " " + shapeList.get(i).getRotateAngleX() + " " + shapeList.get(i).getRotateAngleY() + " " + shapeList.get(i).getRotateAngleZ()
                            + " " + shapeList.get(i).getColor() + " " + backColor); //10
                }


                //For cylinder
                if (shapeList.get(i).getShapeType() == 3) {
                    outputFile.println(shapeList.get(i).getShapeType() + " " + shapeList.get(i).getXPoint() + " " + shapeList.get(i).getYPoint()
                            + " " + shapeList.get(i).getZPoint() + " " + shapeList.get(i).getHeight() + " " + shapeList.get(i).getRadius() + " "
                            + shapeList.get(i).getScaleFactor() + " " + shapeList.get(i).getRotateAngleX() + " " + shapeList.get(i).getRotateAngleY()
                            + " " + shapeList.get(i).getRotateAngleZ() + " " + shapeList.get(i).getColor() + " " + backColor); //11
                }
            }

            outputFile.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error Saving File!");
            alert.show();
        }
    }

    private void openFile(Stage primaryStage) {

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        curFilePath = fc.showOpenDialog(primaryStage);

        try {
            Scanner inputFile = new Scanner(curFilePath);
            shapesGroup.getChildren().clear();
            shapeList.clear();
            String fileinput;
            String[] id;

            while (inputFile.hasNext()) {
                fileinput = inputFile.nextLine();
                id = fileinput.split(" ");

                //Box
                if (Double.parseDouble(id[0]) == 1) {
                    createObjects newObject = new createObjects(Double.parseDouble(id[1]), Double.parseDouble(id[2]),
                            Double.parseDouble(id[3]), Double.parseDouble(id[5]), Double.parseDouble(id[4]),
                            Double.parseDouble(id[6]), Double.parseDouble(id[7]), Double.parseDouble(id[8]), 1);

                    newObject.setRotateX(Double.parseDouble(id[9]));
                    newObject.setRotateY(Double.parseDouble(id[10]));
                    newObject.setRotateZ(Double.parseDouble(id[11]));
                    newObject.setColor(id[12]);

                    switch (id[12]) {
                        case "White":
                            newObject.myBox().setMaterial(new PhongMaterial(Color.WHITE));
                            break;

                        case "Black":
                            newObject.myBox().setMaterial(new PhongMaterial(Color.BLACK));
                            break;

                        case "Red":
                            newObject.myBox().setMaterial(new PhongMaterial(Color.RED));
                            break;

                        case "Blue":
                            newObject.myBox().setMaterial(new PhongMaterial(Color.BLUE));
                            break;

                        case "Green":
                            newObject.myBox().setMaterial(new PhongMaterial(Color.GREEN));
                            break;

                        case "Pink":
                            newObject.myBox().setMaterial(new PhongMaterial(Color.PINK));
                            break;
                    }

                    backColor = id[13];
                    setBackgroundColor();
                    shapeList.add(newObject);
                    shapesGroup.getChildren().add(newObject.myBox());

                    newObject.myBox().setOnMouseClicked(ActionEvent -> {
                        currentSelected = newObject;//Set newObject to current selected
                        sliderListener();
                        createListener();
                    });
                } else if (Double.parseDouble(id[0]) == 2) {
                    createObjects newObject = new createObjects(Double.parseDouble(id[1]), Double.parseDouble(id[2]),
                            Double.parseDouble(id[3]), 0, 0,
                            0, Double.parseDouble(id[4]), Double.parseDouble(id[5]), 2);

                    newObject.setRotateX(Double.parseDouble(id[6]));
                    newObject.setRotateY(Double.parseDouble(id[7]));
                    newObject.setRotateZ(Double.parseDouble(id[8]));
                    newObject.setColor(id[9]);

                    switch (id[9]) {
                        case "White":
                            newObject.mySphere().setMaterial(new PhongMaterial(Color.WHITE));
                            break;

                        case "Black":
                            newObject.mySphere().setMaterial(new PhongMaterial(Color.BLACK));
                            break;

                        case "Red":
                            newObject.mySphere().setMaterial(new PhongMaterial(Color.RED));
                            break;

                        case "Blue":
                            newObject.mySphere().setMaterial(new PhongMaterial(Color.BLUE));
                            break;

                        case "Green":
                            newObject.mySphere().setMaterial(new PhongMaterial(Color.GREEN));
                            break;

                        case "Pink":
                            newObject.mySphere().setMaterial(new PhongMaterial(Color.PINK));
                            break;
                    }

                    shapeList.add(newObject);
                    shapesGroup.getChildren().add(newObject.mySphere());

                    backColor = id[10];
                    setBackgroundColor();
                    newObject.mySphere().setOnMouseClicked(ActionEvent -> {
                        currentSelected = newObject;//Set newObject to current selected
                        sliderListener();
                        createListener();
                    });
                } else if (Double.parseDouble(id[0]) == 3) {
                    createObjects newObject = new createObjects(Double.parseDouble(id[1]), Double.parseDouble(id[2]),
                            Double.parseDouble(id[3]), 0, Double.parseDouble(id[4]),
                            0, Double.parseDouble(id[5]), Double.parseDouble(id[6]), 3);

                    newObject.setRotateX(Double.parseDouble(id[7]));
                    newObject.setRotateY(Double.parseDouble(id[8]));
                    newObject.setRotateZ(Double.parseDouble(id[9]));
                    newObject.setColor(id[10]);

                    switch (id[10]) {
                        case "White":
                            newObject.myCylinder().setMaterial(new PhongMaterial(Color.WHITE));
                            break;

                        case "Black":
                            newObject.myCylinder().setMaterial(new PhongMaterial(Color.BLACK));
                            break;

                        case "Red":
                            newObject.myCylinder().setMaterial(new PhongMaterial(Color.RED));
                            break;

                        case "Blue":
                            newObject.myCylinder().setMaterial(new PhongMaterial(Color.BLUE));
                            break;

                        case "Green":
                            newObject.myCylinder().setMaterial(new PhongMaterial(Color.GREEN));
                            break;

                        case "Pink":
                            newObject.myCylinder().setMaterial(new PhongMaterial(Color.PINK));
                            break;
                    }

                    shapeList.add(newObject);
                    shapesGroup.getChildren().add(newObject.myCylinder());

                    backColor = id[11];
                    setBackgroundColor();
                    newObject.myCylinder().setOnMouseClicked(ActionEvent -> {
                        currentSelected = newObject;//Set newObject to current selected
                        sliderListener();
                        createListener();
                    });
                }

            }

        } catch (Exception e) { // Error Handling
        }

    }

    private void createRighMenu() {

        //Create ListView
        backgroundColor = new ListView<>();
        backgroundColor.setId("listView");
        shapeColor = new ListView<>();
        shapeColor.setId("listView");

        backgroundColor.getItems().addAll("White", "Black", "Red", "Blue", "Green", "Pink");
        shapeColor.getItems().addAll("White", "Black", "Red", "Blue", "Green", "Pink");

        backgroundColor.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        shapeColor.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Label sliderLabel = new Label("X rotate");
        Label sliderLabel2 = new Label("Y rotate");
        Label sliderLabel3 = new Label("Z rotate");
        Label backColor = new Label("Background Color ");
        Label objectColor = new Label("3D objects color");
        Label scaleChange = new Label("Scale change");
        Label translateObjects = new Label("Translate location");

        //Create all button
        increaseScale = new Button("+ Scale");
        decreaseScale = new Button("- Scale");

        HBox scaleBox = new HBox(10, increaseScale, decreaseScale);
        scaleBox.setAlignment(Pos.CENTER);
        scaleBox.setPadding(new Insets(10));

        increaseX = new Button("+ X");
        decreaseX = new Button("- X");
        increaseY = new Button("+ Y");
        decreaseY = new Button("- Y");
        increaseZ = new Button("+ Z");
        decreaseZ = new Button("- Z");

        HBox increaseBox = new HBox(5, increaseX, decreaseX,
                increaseY, decreaseY, increaseZ, decreaseZ);
        increaseBox.setPadding(new Insets(5));
        increaseBox.setAlignment(Pos.CENTER);

        //Create all slider
        sliderX = new Slider(0.0, 100, 0.0);
        sliderX.setShowTickMarks(true);
        sliderX.setShowTickLabels(true);
        sliderX.setPrefWidth(200);

        sliderY = new Slider(0.0, 100, 0.0);
        sliderY.setShowTickMarks(true);
        sliderY.setShowTickLabels(true);
        sliderY.setPrefWidth(200);

        sliderZ = new Slider(0.0, 100, 0.0);
        sliderZ.setShowTickMarks(true);
        sliderZ.setShowTickLabels(true);
        sliderZ.setPrefWidth(200);

        sliderX.setId("color-slider");
        sliderY.setId("color-slider");
        sliderZ.setId("color-slider");

        rightVBox = new VBox(10, backColor, backgroundColor, objectColor, shapeColor,
                scaleChange, scaleBox, translateObjects, increaseBox,
                sliderLabel, sliderX, sliderLabel2, sliderY, sliderLabel3, sliderZ);
        rightVBox.setAlignment(Pos.CENTER);
        rightVBox.setPadding(new Insets(10));

    }

    private void createListener() {
        //backgroundColor listener
        backgroundColor.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            backColor = backgroundColor.getSelectionModel().getSelectedItem();

            setBackgroundColor();
        });

        //Shapes color listener
        shapeColor.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            objectColor = shapeColor.getSelectionModel().getSelectedItem();
            if (objectColor.equals("White")) {
                int type = currentSelected.getShapeType();
                if (type == 1) {
                    currentSelected.myBox().setMaterial(new PhongMaterial(Color.WHITE));
                    currentSelected.setColor(objectColor);
                }
                if (type == 2) {
                    currentSelected.mySphere().setMaterial(new PhongMaterial(Color.WHITE));
                    currentSelected.setColor(objectColor);
                }
                if (type == 3) {
                    currentSelected.myCylinder().setMaterial(new PhongMaterial(Color.WHITE));
                    currentSelected.setColor(objectColor);
                }
            }
            if (objectColor.equals("Black")) {
                int type = currentSelected.getShapeType();
                if (type == 1) {
                    currentSelected.myBox().setMaterial(new PhongMaterial(Color.BLACK));
                    currentSelected.setColor(objectColor);
                }
                if (type == 2) {
                    currentSelected.mySphere().setMaterial(new PhongMaterial(Color.BLACK));
                    currentSelected.setColor(objectColor);
                }
                if (type == 3) {
                    currentSelected.myCylinder().setMaterial(new PhongMaterial(Color.BLACK));
                    currentSelected.setColor(objectColor);
                }
            }
            if (objectColor.equals("Red")) {
                int type = currentSelected.getShapeType();
                if (type == 1) {
                    currentSelected.myBox().setMaterial(new PhongMaterial(Color.RED));
                    currentSelected.setColor(objectColor);
                }
                if (type == 2) {
                    currentSelected.mySphere().setMaterial(new PhongMaterial(Color.RED));
                    currentSelected.setColor(objectColor);
                }
                if (type == 3) {
                    currentSelected.myCylinder().setMaterial(new PhongMaterial(Color.RED));
                    currentSelected.setColor(objectColor);
                }
            }
            if (objectColor.equals("Blue")) {
                int type = currentSelected.getShapeType();
                if (type == 1) {
                    currentSelected.myBox().setMaterial(new PhongMaterial(Color.BLUE));
                    currentSelected.setColor(objectColor);
                }
                if (type == 2) {
                    currentSelected.mySphere().setMaterial(new PhongMaterial(Color.BLUE));
                    currentSelected.setColor(objectColor);
                }
                if (type == 3) {
                    currentSelected.myCylinder().setMaterial(new PhongMaterial(Color.BLUE));
                    currentSelected.setColor(objectColor);
                }
            }
            if (objectColor.equals("Green")) {
                int type = currentSelected.getShapeType();
                if (type == 1) {
                    currentSelected.myBox().setMaterial(new PhongMaterial(Color.GREEN));
                    currentSelected.setColor(objectColor);
                }
                if (type == 2) {
                    currentSelected.mySphere().setMaterial(new PhongMaterial(Color.GREEN));
                    currentSelected.setColor(objectColor);
                }
                if (type == 3) {
                    currentSelected.myCylinder().setMaterial(new PhongMaterial(Color.GREEN));
                    currentSelected.setColor(objectColor);
                }
            }
            if (objectColor.equals("Pink")) {
                int type = currentSelected.getShapeType();
                if (type == 1) {
                    currentSelected.myBox().setMaterial(new PhongMaterial(Color.PINK));
                    currentSelected.setColor(objectColor);
                }
                if (type == 2) {
                    currentSelected.mySphere().setMaterial(new PhongMaterial(Color.PINK));
                    currentSelected.setColor(objectColor);
                }
                if (type == 3) {
                    currentSelected.myCylinder().setMaterial(new PhongMaterial(Color.PINK));
                    currentSelected.setColor(objectColor);
                }
            }
        });
    }

    private void setBackgroundColor() {
        switch (backColor) {
            case "White":
                shapesSub.setFill(Color.WHITE);
                break;

            case "Black":
                shapesSub.setFill(Color.BLACK);
                break;

            case "Red":
                shapesSub.setFill(Color.RED);
                break;

            case "Blue":
                shapesSub.setFill(Color.BLUE);
                break;

            case "Green":
                shapesSub.setFill(Color.GREEN);
                break;

            case "Pink":
                shapesSub.setFill(Color.PINK);
                break;
        }
    }

    private void createSubScene() {
        //We will have more control for right scene later
        Button xButton = new Button("X Value");
        HBox rightSelection = new HBox(10, xButton);
        rightSelection.setAlignment(Pos.CENTER);
        rightSelection.setPadding(new Insets(10));

        //This is the add shape button
        AddShapeSubmit = new Button("Add Shape");
        buttonBox = new VBox(10, AddShapeSubmit);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        //Creating out Group to add 3D object later
        shapesGroup = new Group();
        //SubScene
        shapesSub = new SubScene(shapesGroup, 840, 560,
                true, SceneAntialiasing.DISABLED);
        shapesSub.setCamera(pCamera);
        //Setup SbScene background color
        shapesSub.setFill(Color.WHITE);
        //Create camera
        pCamera = new PerspectiveCamera(true);
        //Setup camera angel
        Translate camAngel = new Translate(0, 0, -60);
        //Add camAngel to Camera
        pCamera.getTransforms().addAll(camAngel);
        shapesSub.setCamera(pCamera);
    }

    private void addObjectsMenu() {
        //Create RadioButton for user to choose
        box = new RadioButton("Box");
        sphere = new RadioButton("Sphere");
        cylinder = new RadioButton("Cylinder");
        box.setId("radioButton");
        sphere.setId("radioButton");
        cylinder.setId("radioButton");


        //Setup ToggleGroup
        ToggleGroup myToggle = new ToggleGroup();
        box.setToggleGroup(myToggle);
        sphere.setToggleGroup(myToggle);
        cylinder.setToggleGroup(myToggle);

        //Create all Label and TextField
        Label header = new Label("Please select the shape you want ");
        Label x = new Label("X location");
        Label y = new Label("Y Location");
        Label width = new Label("Width: ");
        Label height = new Label("Height: ");
        Label radius = new Label("Radius: ");
        Label length = new Label("Length: ");

        //Create all textField
        inputX = new TextField();
        inputY = new TextField();
        inputWidth = new TextField();
        inputHeight = new TextField();
        inputRadius = new TextField();
        inputLength = new TextField();

        //Create some tooltip
        inputX.setTooltip( new Tooltip("Please enter + - number"));
        inputY.setTooltip( new Tooltip("Please enter + - number"));
        inputWidth.setTooltip( new Tooltip("Please enter + number"));
        inputHeight.setTooltip( new Tooltip("Please enter + number"));
        inputRadius.setTooltip( new Tooltip("Please enter + number"));
        inputLength.setTooltip( new Tooltip("Please enter + number"));


        //Create a submit button
        submit = new Button("Submit");

        //Create GridPane to store all Label and TestField
        GridPane myPane = new GridPane();
        myPane.add(x, 0, 0);
        myPane.add(y, 0, 1);
        myPane.add(width, 0, 2);
        myPane.add(height, 0, 3);
        myPane.add(radius, 0, 4);
        myPane.add(length, 0, 5);

        myPane.add(inputX, 1, 0);
        myPane.add(inputY, 1, 1);
        myPane.add(inputWidth, 1, 2);
        myPane.add(inputHeight, 1, 3);
        myPane.add(inputRadius, 1, 4);
        myPane.add(inputLength, 1, 5);
        myPane.setVgap(20);
        myPane.setHgap(20);

        HBox myHbox = new HBox(10, box, sphere, cylinder);
        myHbox.setAlignment(Pos.CENTER);
        myHbox.setPadding(new Insets(10));

        HBox myHbox2 = new HBox(10, myPane);
        myHbox2.setAlignment(Pos.CENTER);
        myHbox2.setPadding(new Insets(10));


        VBox myVBox = new VBox(10, header, myHbox, myHbox2, submit);
        myVBox.setAlignment(Pos.CENTER);
        myVBox.setPadding(new Insets(20));
        myMainPane.setCenter(myVBox);

        inputWidth.setDisable(true);
        inputHeight.setDisable(true);
        inputRadius.setDisable(true);
        inputLength.setDisable(true);
        submit.setDisable(true);

        myToggle.selectedToggleProperty().addListener((ob, o, n) -> {
            RadioButton userInput = (RadioButton) myToggle.getSelectedToggle();

            switch (userInput.getText()) {
                case "Box":
                    inputWidth.setDisable(false);
                    inputHeight.setDisable(false);
                    inputRadius.setDisable(true);
                    inputLength.setDisable(false);
                    inputRadius.setText("");
                    break;
                case "Sphere":
                    inputWidth.setDisable(true);
                    inputHeight.setDisable(true);
                    inputRadius.setDisable(false);
                    inputLength.setDisable(true);
                    inputWidth.setText("");
                    inputHeight.setText("");
                    inputLength.setText("");
                    break;
                case "Cylinder":
                    inputWidth.setDisable(true);
                    inputHeight.setDisable(false);
                    inputRadius.setDisable(false);
                    inputLength.setDisable(true);
                    inputWidth.setText("");
                    inputLength.setText("");
                    break;
            }
        });

        CreateListener();

        //All Button action
        submit.setOnAction(event -> {

            int inputType = 0;

            if (box.isSelected()) {
                inputType = 1;
                xPoint = Double.parseDouble(inputX.getText());
                yPoint = Double.parseDouble(inputY.getText());
                inputW = Double.parseDouble(inputWidth.getText());
                inputH = Double.parseDouble(inputHeight.getText());
                inputL = Double.parseDouble(inputLength.getText());

            }

            if (sphere.isSelected()) {
                inputType = 2;
                xPoint = Double.parseDouble(inputX.getText());
                yPoint = Double.parseDouble(inputY.getText());
                inputR = Double.parseDouble(inputRadius.getText());
            }

            if (cylinder.isSelected()) {
                inputType = 3;
                xPoint = Double.parseDouble(inputX.getText());
                yPoint = Double.parseDouble(inputY.getText());
                inputH = Double.parseDouble(inputHeight.getText());
                inputR = Double.parseDouble(inputRadius.getText());
            }

            createObjects newObject = new createObjects(xPoint, yPoint, 0, inputW, inputH,
                    inputL, inputR, 1, inputType);

            shapeList.add(newObject); // Save each objects to ArrayList

            if (inputType == 1) {
                newObject.myBox().setMaterial(new PhongMaterial(Color.BLUE));
                shapesGroup.getChildren().add(newObject.myBox());

                newObject.myBox().setOnMouseClicked(ActionEvent -> {

                    currentSelected = newObject;//Set newObject to current selected

                    sliderListener();

                });
            }
            if (inputType == 2) {
                newObject.mySphere().setMaterial(new PhongMaterial(Color.BLUE));
                shapesGroup.getChildren().add(newObject.mySphere());

                newObject.mySphere().setOnMouseClicked(ActionEvent -> {

                    currentSelected = newObject;//Set newObject to current selected
                    sliderListener();

                });
            }
            if (inputType == 3) {

                newObject.myCylinder().setMaterial(new PhongMaterial(Color.BLUE));
                shapesGroup.getChildren().add(newObject.myCylinder());

                newObject.myCylinder().setOnMouseClicked(ActionEvent -> {

                    currentSelected = newObject;
                    sliderListener();

                });
            }
            myMainPane.setCenter(shapesSub);
        });


    }

    private void sliderListener() {

        //Slider listener
        sliderX.valueProperty().addListener((observable, oldValue, newValue) -> {

            double newXX = (sliderX.getValue() * 3.6);
            currentSelected.setRotateX(newXX);
        });

        sliderY.valueProperty().addListener((observable, oldValue, newValue) -> {

            double newYY = (sliderY.getValue() * 3.6);
            currentSelected.setRotateY(newYY);
        });

        sliderZ.valueProperty().addListener((observable, oldValue, newValue) -> {

            double newZZ = (sliderZ.getValue() * 3.6);
            currentSelected.setRotateZ(newZZ);
        });

        increaseX.setOnAction(event -> currentSelected.increaseX());

        decreaseX.setOnAction(event -> currentSelected.decreaseX());

        increaseY.setOnAction(event -> currentSelected.increaseY());

        decreaseY.setOnAction(event -> currentSelected.decreaseY());

        increaseZ.setOnAction(event -> currentSelected.increaseZ());

        decreaseZ.setOnAction(event -> currentSelected.decreaseZ());

        increaseScale.setOnAction(event -> currentSelected.increaseScale());

        decreaseScale.setOnAction(event -> currentSelected.decreaseScale());
    }

    //Listener for addObjectMenu
    private void CreateListener() {
        inputX.textProperty().addListener((source, oldString, newString) -> {
            if (!newString.matches("([\\-]\\d{0,2})?")) {
                inputX.setText(newString.replaceAll("([\\-]\\d{0,2})", ""));
            }
            listenerFunction();
        });
        inputY.textProperty().addListener((source, oldString, newString) -> {
            if (!newString.matches("([\\-]\\d{0,2})?")) {
                inputY.setText(newString.replaceAll("([\\-]\\d{0,2})", ""));
            }
            listenerFunction();
        });
        inputWidth.textProperty().addListener((source, oldString, newString) -> {
            if (!newString.matches("\\d*")) {
                inputWidth.setText(newString.replaceAll("[^\\d]", ""));
            }
            listenerFunction();
        });
        inputHeight.textProperty().addListener((source, oldString, newString) -> {
            if (!newString.matches("\\d*")) {
                inputHeight.setText(newString.replaceAll("[^\\d]", ""));
            }
            listenerFunction();
        });
        inputRadius.textProperty().addListener((source, oldString, newString) -> {
            if (!newString.matches("\\d*")) {
                inputRadius.setText(newString.replaceAll("[^\\d]", ""));
            }
            listenerFunction();
        });
        inputLength.textProperty().addListener((source, oldString, newString) -> {
            if (!newString.matches("\\d*")) {
                inputLength.setText(newString.replaceAll("[^\\d]", ""));
            }
            listenerFunction();
        });
    }

    //Listener for addObjectMenu
    private void listenerFunction() {
        if (box.isSelected()) {
            submit.setDisable(inputX.getText().trim().equals("")
                    || inputY.getText().trim().equals("")
                    || inputWidth.getText().trim().equals("")
                    || inputHeight.getText().trim().equals("")
                    || inputLength.getText().trim().equals(""));
            inputRadius.setText("");
        }

        if (sphere.isSelected()) {
            submit.setDisable(inputX.getText().trim().equals("")
                    || inputY.getText().trim().equals("")
                    || inputRadius.getText().trim().equals(""));
            inputWidth.setText("");
            inputHeight.setText("");
            inputLength.setText("");
        }
        if (cylinder.isSelected()) {
            submit.setDisable(inputX.getText().trim().equals("")
                    || inputY.getText().trim().equals("")
                    || inputHeight.getText().trim().equals("")
                    || inputRadius.getText().trim().equals(""));
            inputWidth.setText("");
            inputLength.setText("");
        }

    }

    private static class createObjects {
        double width, height, depth, radius;
        double xPoint, yPoint, zPoint;
        double scaleFactor, rotateAngleX = 0, rotateAngleY = 0, rotateAngleZ = 0;
        int shapeType; // 1 is box 2 is sphere 3 is cylinder
        String color = "Blue";

        Box newBox;
        Sphere newSphere;
        Cylinder newCylinder;

        Translate xyTranslate;
        Scale scale;
        Rotate myObjectRotateX = new Rotate(rotateAngleX, Rotate.X_AXIS);
        Rotate myObjectRotateY = new Rotate(rotateAngleY, Rotate.Y_AXIS);
        Rotate myObjectRotateZ = new Rotate(rotateAngleZ, Rotate.Z_AXIS);

        createObjects(double xPoint, double yPoint, double zPoint, double inputW,
                      double inputH, double inputL, double inputR, double scaleFactor,
                      int shapeType)
        {
            this.xPoint = xPoint;
            this.yPoint = yPoint;
            this.zPoint = zPoint;
            this.width = inputW;
            this.height = inputH;
            this.depth = inputL;
            this.radius = inputR;
            this.scaleFactor = scaleFactor;
            this.shapeType = shapeType; // This will tell what kind of 3D object this object is

            if (shapeType == 1) {
                newBox = new Box(inputW, inputH, inputL);
                xyTranslate = new Translate(xPoint, yPoint, zPoint);
                scale = new Scale(scaleFactor, scaleFactor, scaleFactor);
                newBox.getTransforms().addAll(xyTranslate, myObjectRotateX, myObjectRotateY, myObjectRotateZ, scale);
            }

            if (shapeType == 2) {
                newSphere = new Sphere(inputR);
                xyTranslate = new Translate(xPoint, yPoint, zPoint);
                scale = new Scale(scaleFactor, scaleFactor, scaleFactor);
                newSphere.getTransforms().addAll(xyTranslate, myObjectRotateX, myObjectRotateY, myObjectRotateZ, scale);
            }

            if (shapeType == 3) {
                newCylinder = new Cylinder(inputR, inputH);
                xyTranslate = new Translate(xPoint, yPoint, 0);
                scale = new Scale(scaleFactor, scaleFactor, scaleFactor);
                newCylinder.getTransforms().addAll(xyTranslate, myObjectRotateX, myObjectRotateY, myObjectRotateZ, scale);
            }
        }

        void setRotateX(double x) {
            rotateAngleX = x;
            myObjectRotateX.setAngle(x);
        }

        void setRotateY(double y) {
            rotateAngleY = y;
            myObjectRotateY.setAngle(y);
        }

        void setRotateZ(double z) {
            rotateAngleZ = z;
            myObjectRotateZ.setAngle(z);
        }

        void increaseX() {
            xPoint += 1;
            xyTranslate.setX(xPoint);
        }

        void decreaseX() {
            xPoint -= 1;
            xyTranslate.setX(xPoint);
        }

        void increaseY() {
            yPoint += 1;
            xyTranslate.setY(yPoint);
        }

        void decreaseY() {
            yPoint -= 1;
            xyTranslate.setY(yPoint);
        }

        void increaseZ() {
            zPoint += 1;
            xyTranslate.setZ(zPoint);
        }

        void decreaseZ() {
            zPoint -= 1;
            xyTranslate.setZ(zPoint);
        }

        void increaseScale() {
            scaleFactor *= 1.1;
            scale.setX(scaleFactor);
            scale.setY(scaleFactor);
            scale.setZ(scaleFactor);
        }

        void decreaseScale() {
            scaleFactor *= 0.9;
            scale.setX(scaleFactor);
            scale.setY(scaleFactor);
            scale.setZ(scaleFactor);
        }

        Box myBox() {
            return newBox;
        }

        Sphere mySphere() {
            return newSphere;
        }

        Cylinder myCylinder() {
            return newCylinder;
        }

        void setColor(String color) {
            this.color = color;
        }

        String getColor() {
            return color;
        }

        int getShapeType() {
            return shapeType;
        } // This will be use to save the object

        public void setScaleFactor(double scale) {
            scaleFactor = scale;
        }

        double getScaleFactor() {
            return scaleFactor;
        }

        double getXPoint() {
            return xPoint;
        }

        double getYPoint() {
            return yPoint;
        }

        double getZPoint() {
            return zPoint;
        }

        double getWidth() {
            return width;
        }

        double getHeight() {
            return height;
        }

        double getDepth() {
            return depth;
        }

        double getRadius() {
            return radius;
        }

        double getRotateAngleX() {
            return rotateAngleX;
        }

        double getRotateAngleY() {
            return rotateAngleY;
        }

        double getRotateAngleZ() {
            return rotateAngleZ;
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
