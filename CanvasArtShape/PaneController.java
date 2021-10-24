package CanvasArtShape;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.Stack;

public class PaneController {

    public Button undoButton;
    @FXML
    private Slider redSlider;
    @FXML
    private Slider greenSlider;
    @FXML
    private Slider blueSlider;
    @FXML
    private Slider thicknessSlider;
    @FXML
    private Rectangle colorRectangle;
    @FXML
    private RadioButton lineRadioButton;
    @FXML
    private ToggleGroup shapeToggleGroup;
    @FXML
    private RadioButton rectangleRadioButton;
    @FXML
    private RadioButton ovalRadioButton;
    @FXML
    private RadioButton eraserRadioButton;
    @FXML
    private Button clearButton;
    @FXML
    private Canvas drawingAreaCanvas;
    @FXML
    private RadioButton moveRadioButton;
    @FXML
    private RadioButton deleteRadioButton;

//    @FXML private Pane drawingAreaPane;


    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private double alpha = 1.0;

    private GraphicsContext gc;

    Line line = new Line();
    Rectangle rectangle = new Rectangle();
    Ellipse ellipse = new Ellipse();

    Stack<Shape> history = new Stack();

    private int selected;
    private double selectedX;
    private double selectedY;

    public void initialize() {

        gc = drawingAreaCanvas.getGraphicsContext2D();

        redSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                        red = newValue.intValue();
                        colorRectangle.setFill(Color.rgb(red, green, blue, alpha));

                    }
                }
        );
        greenSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                        green = newValue.intValue();
                        colorRectangle.setFill(Color.rgb(red, green, blue, alpha));

                    }
                }
        );
        blueSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                        blue = newValue.intValue();
                        colorRectangle.setFill(Color.rgb(red, green, blue, alpha));

                    }
                }
        );

        drawingAreaCanvas.setOnMousePressed(e -> {
            if (lineRadioButton.isSelected()) {
                line.setStartX(e.getX());
                line.setEndY(e.getY());
                line.setEndX(e.getX());
                line.setEndY(e.getY());

            } else if (rectangleRadioButton.isSelected()) {
                rectangle.setX(e.getX());
                rectangle.setY(e.getY());

            } else if (ovalRadioButton.isSelected()) {
                ellipse.setCenterX(e.getX());
                ellipse.setCenterY(e.getY());
            } else if (eraserRadioButton.isSelected()) {
                erase(e.getX(), e.getY());
            } else if (deleteRadioButton.isSelected()) {
                for (int i = history.size() - 1; i > -1; i--) {
                    if (isInShapeArea(history.get(i), e.getX(), e.getY())) {
                        history.remove(i);
                        gc.clearRect(0, 0, drawingAreaCanvas.getWidth(), deleteRadioButton.getHeight());
                        drawShapes(history);
                        break;
                    }
                }


            } else if (moveRadioButton.isSelected()) {
                for (int i = history.size() - 1; i > -1; i--) {
                    if (isInShapeArea(history.get(i), e.getX(), e.getY())) {
                        selected = i;
                        selectedX = e.getX();
                        selectedY = e.getY();
                        break;

                    }

                }

            }
        });


        drawingAreaCanvas.setOnMouseDragged(e -> {
            if (eraserRadioButton.isSelected()) {
                erase(e.getX(), e.getY());
            }
        });

        drawingAreaCanvas.setOnMouseReleased(e -> {
            gc.setStroke(Color.rgb(red, green, blue));

            if (lineRadioButton.isSelected()) {
                line.setStrokeWidth(thicknessSlider.getValue());
                line.setFill(Color.rgb(red, green, blue));
                line.setEndX(e.getX());
                line.setEndY(e.getY());

                gc.setLineWidth(thicknessSlider.getValue());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                Line templine = new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                templine.setStroke(Color.rgb(red, green, blue));
                templine.setStrokeWidth(line.getStrokeWidth());
                history.push(templine);

            } else if (rectangleRadioButton.isSelected()) {

                rectangle.setWidth(Math.abs(e.getX() - rectangle.getX()));
                rectangle.setHeight(Math.abs(e.getY() - rectangle.getY()));
                rectangle.setX(Math.min(rectangle.getX(), rectangle.getY()));
                rectangle.setY(Math.min(rectangle.getX(), rectangle.getY()));

                rectangle.setFill(Color.rgb(red, green, blue));
                gc.setFill(Color.rgb(red, green, blue));
                gc.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                gc.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());

                Rectangle tempRect = new Rectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                history.push(tempRect);


            } else if (ovalRadioButton.isSelected()) {
                ellipse.setRadiusX((Math.abs(e.getX() - ellipse.getCenterX())));
                ellipse.setRadiusY(Math.abs(e.getY() - ellipse.getCenterY()));

                ellipse.setCenterX(Math.min(ellipse.getCenterX(), e.getX()));
                ellipse.setCenterY(Math.min(ellipse.getCenterY(), e.getY()));

                ellipse.setFill(Color.rgb(red, green, blue));
                gc.setFill(Color.rgb(red, green, blue));

                gc.fillOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
                gc.strokeOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
                Ellipse tempOval = new Ellipse(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
                tempOval.setFill(Color.rgb(red, green, blue));
                history.push(tempOval);

            } else if (eraserRadioButton.isSelected()) {
                erase(e.getX(), e.getY());
            } else if (moveRadioButton.isSelected() && selected != -1) {
                Shape s = history.get(selected);
                if (s.getClass() == Line.class) {
                    line = (Line) s;
                    line.setStartX(line.getStartX() + e.getX() - selectedX);
                    line.setStartY(line.getStartY() + e.getY() - selectedY);
                    line.setEndX(line.getEndX() + e.getX() - selectedX);
                    line.setEndY(line.getEndY() + e.getY() - selectedY);

                } else if (s.getClass() == Rectangle.class) {
                    rectangle = (Rectangle) history.get(selected);
                    rectangle.setX(rectangle.getX() + e.getX() - selectedX);
                    rectangle.setY(rectangle.getY() + e.getY() - selectedY);

                } else if (s.getClass() == Ellipse.class) {
                    ellipse = (Ellipse) history.get(selected);
                    ellipse.setCenterX(ellipse.getCenterX() + e.getX() - selectedX);
                    ellipse.setCenterY(ellipse.getCenterY() + e.getY() - selectedY);

                }
                selected = -1;
                gc.clearRect(0, 0, drawingAreaCanvas.getWidth(), drawingAreaCanvas.getHeight());
                drawShapes(history);

            }
        });


    }

    private void erase(double x, double y) {
        double size = thicknessSlider.getValue();
        gc.clearRect(x - size / 2, y - size / 2, size, size);
    }

    public void undoButtonPressed() {
        if (!history.isEmpty()) {
            history.pop();
            gc.clearRect(0, 0, drawingAreaCanvas.getWidth(), drawingAreaCanvas.getHeight());
            drawShapes(history);
        }
    }

    private void drawShapes(Stack<Shape> Shapes) {
        for (Shape s : Shapes) {
            if (s.getClass() == Line.class) {
                Line templine = (Line) s;
                gc.setLineWidth(templine.getStrokeWidth());
                gc.setStroke(templine.getStroke());
                gc.strokeLine(templine.getStartX(), templine.getStartY(), templine.getEndX(), templine.getEndY());
            } else if (s.getClass() == Ellipse.class) {
                Ellipse tempEllipse = (Ellipse) s;
                gc.setStroke(tempEllipse.getStroke());
                gc.setLineWidth(tempEllipse.getStrokeWidth());
                gc.setFill(tempEllipse.getFill());
                gc.fillOval(tempEllipse.getCenterX(), tempEllipse.getCenterY(), tempEllipse.getRadiusX(), tempEllipse.getRadiusY());
            } else if (s.getClass() == Rectangle.class) {
                Rectangle tempRectangle = (Rectangle) s;
                gc.setLineWidth(tempRectangle.getStrokeWidth());
                gc.setFill(tempRectangle.getFill());
                gc.setStroke(tempRectangle.getStroke());
                gc.fillRect(tempRectangle.getX(), tempRectangle.getY(), tempRectangle.getWidth(), tempRectangle.getHeight());
            }
        }
    }

    private boolean isInShapeArea(Shape s, double x, double y) {
        if (s.getClass() == Line.class) {
            line = (Line) s;
            if (Math.abs(Math.pow(Math.pow(line.getStartX() - x, 2) + Math.pow(line.getStartY() - y, 2), .5) +
                    Math.pow(Math.pow(line.getEndX() - x, 2) + Math.pow(line.getEndY() - y, 2), .5) -
                    Math.pow(Math.pow(line.getEndX() - line.getStartX(), 2) + Math.pow(line.getEndY() - line.getStartY(), 2), .5)) < 5)
                return true;
        } else if (s.getClass() == Rectangle.class) {
            rectangle = (Rectangle) s;
            if ((x >= rectangle.getX()) && (x <= (rectangle.getX() + rectangle.getWidth()))
                    && (y >= rectangle.getY()) && (y <= (rectangle.getY() + rectangle.getHeight())))
                return true;
        } else if (s.getClass() == Ellipse.class) {
            ellipse = (Ellipse) s;
            if (((Math.pow((x - ellipse.getCenterX() - ellipse.getRadiusX() / 2), 2) / Math.pow(ellipse.getRadiusX() / 2, 2)) +
                    (Math.pow((y - ellipse.getCenterY() - ellipse.getRadiusY() / 2), 2) / Math.pow(ellipse.getRadiusY() / 2, 2))) <= 1)
                return true;
        }
        return false;
    }

    @FXML
    void clearButtonPressed() {
        gc.clearRect(0, 0, drawingAreaCanvas.getWidth(), drawingAreaCanvas.getHeight());
        history.clear();

    }
}