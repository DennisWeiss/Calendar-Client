import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    static Map<Integer, String> dayMap = new HashMap<>();
    static Map<Integer, String> timeMap = new HashMap<>();

    AppointmentCalendar agent1 = new AppointmentCalendar();
    AppointmentCalendar agent2 = new AppointmentCalendar();

    int availableAppointments = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox();
        GridPane gridPane = new GridPane();
        VBox vBox = new VBox();
        for (int i = 0; i < dayMap.size(); i++) {
            gridPane.add(new Text(dayMap.get(i)), i+1, 0);
        }
        for (int i = 0; i < timeMap.size()-1; i++) {
            gridPane.add(new Text(timeMap.get(i)), 0, i+1);
        }
        Button[][] buttons = new Button[dayMap.size()][timeMap.size()];
        VBox.setVgrow(vBox, Priority.ALWAYS);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        for (int i = 0; i < dayMap.size(); i++) {
            for (int j = 0; j < timeMap.size()-1; j++) {
                buttons[i][j] = new Button();
                VBox.setVgrow(buttons[i][j], Priority.ALWAYS);
                HBox.setHgrow(buttons[i][j], Priority.ALWAYS);
                gridPane.setVgrow(buttons[i][j], Priority.ALWAYS);
                gridPane.setHgrow(buttons[i][j], Priority.ALWAYS);
                buttons[i][j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                final int x = i;
                final int y = j;
                if (agent1.free[x][y]) {
                    buttons[x][y].setStyle("-fx-base: #737373");
                } else {
                    buttons[x][y].setStyle("-fx-base: #111111");
                }
                buttons[i][j].setOnAction(event -> {
                    agent1.free[x][y] = !agent1.free[x][y];
                    if (agent1.free[x][y]) {
                        buttons[x][y].setStyle("-fx-base: #666666");
                    } else {
                        buttons[x][y].setStyle("-fx-base: #111111");
                    }
                });
                gridPane.add(buttons[i][j], i+1, j+1);
            }
        }
        gridPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        gridPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        GridPane gridPane2 = new GridPane();
        for (int i = 0; i < dayMap.size(); i++) {
            gridPane2.add(new Text(dayMap.get(i)), i+1, 0);
        }
        for (int i = 0; i < timeMap.size()-1; i++) {
            gridPane2.add(new Text(timeMap.get(i)), 0, i+1);
        }
        Button[][] buttons2 = new Button[dayMap.size()][timeMap.size()];
        VBox.setVgrow(gridPane2, Priority.ALWAYS);
        HBox.setHgrow(gridPane2, Priority.ALWAYS);
        for (int i = 0; i < dayMap.size(); i++) {
            for (int j = 0; j < timeMap.size()-1; j++) {
                buttons2[i][j] = new Button();
                VBox.setVgrow(buttons2[i][j], Priority.ALWAYS);
                HBox.setHgrow(buttons2[i][j], Priority.ALWAYS);
                gridPane2.setVgrow(buttons2[i][j], Priority.ALWAYS);
                gridPane2.setHgrow(buttons2[i][j], Priority.ALWAYS);
                buttons2[i][j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                final int x = i;
                final int y = j;
                if (agent2.free[x][y]) {
                    buttons2[x][y].setStyle("-fx-base: #737373");
                } else {
                    buttons2[x][y].setStyle("-fx-base: #111111");
                }
                buttons2[i][j].setOnAction(event -> {
                    agent2.free[x][y] = !agent2.free[x][y];
                    if (agent2.free[x][y]) {
                        buttons2[x][y].setStyle("-fx-base: #666666");
                    } else {
                        buttons2[x][y].setStyle("-fx-base: #111111");
                    }
                });
                gridPane2.add(buttons2[i][j], i+1, j+1);
            }
        }
        gridPane2.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        gridPane2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);


        TextField hours = new TextField();
        hours.setPromptText("Termindauer");
        Button go = new Button("Go");

        go.setOnAction(event -> {
            try {
                int number = Integer.parseInt(hours.getText());
                ArrayList<String> availableHours = getAvailableHours(number);
                String result = "";
                for (String str : availableHours) {
                    result += str + "\n";
                }
                try {
                    if (!availableHours.get(0).equals("noConnection")) {
                        resultSet(new Stage(), result);
                    }
                } catch (IndexOutOfBoundsException e) {
                    resultSet(new Stage(), "Keine freien Termine verfügbar!");
                }
            } catch (NumberFormatException e) {
                noValidInput(new Stage());
            }

        });

        HBox hBox2 = new HBox();
        HBox hBox3 = new HBox();
        gridPane.setPadding(new Insets(5));
        gridPane2.setPadding(new Insets(5));
        HBox.setHgrow(hBox3, Priority.ALWAYS);
        VBox.setVgrow(hBox3, Priority.ALWAYS);
        hBox3.getChildren().addAll(gridPane, gridPane2);
        vBox.getChildren().addAll(hBox3, hBox2);
        hBox2.getChildren().addAll(hours, go);
        hBox.getChildren().addAll(vBox);
        Scene scene = new Scene(hBox, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void noValidInput(Stage stage) {
        VBox vBox = new VBox();
        Text text = new Text("Keine gültige Eingabe!\n(ganze Zahlen 1-10 in Stunden)");
        text.setTranslateX(5);
        text.setTranslateY(5);
        Button ok = new Button("OK");
        ok.setTranslateX(75);
        ok.setTranslateY(20);
        ok.setOnAction(event -> stage.close());
        vBox.getChildren().addAll(text, ok);
        Scene scene = new Scene(vBox, 180, 100);
        stage.setScene(scene);
        stage.show();
    }

    private static void defineMap() {
        dayMap.put(0, "Montag");
        dayMap.put(1, "Dienstag");
        dayMap.put(2, "Mittwoch");
        dayMap.put(3, "Donnerstag");
        dayMap.put(4, "Freitag");

        timeMap.put(0, "8 Uhr");
        timeMap.put(1, "9 Uhr");
        timeMap.put(2, "10 Uhr");
        timeMap.put(3, "11 Uhr");
        timeMap.put(4, "12 Uhr");
        timeMap.put(5, "13 Uhr");
        timeMap.put(6, "14 Uhr");
        timeMap.put(7, "15 Uhr");
        timeMap.put(8, "16 Uhr");
        timeMap.put(9, "17 Uhr");
        timeMap.put(10, "18 Uhr");
    }

    public static void main(String[] args) {
        defineMap();
        launch(args);
    }


    private ArrayList<String> getAvailableHours(int time) {
        ArrayList<String> availableHours = new ArrayList<>();
        try {
            Socket socket = new Socket("127.0.0.1", 1337);
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(agent1);
            outputStream.writeObject(agent2);
            printStream.println(time);
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<int[]> available = (ArrayList) inputStream.readObject();
            availableAppointments = available.size();
            for (int[] arr : available) {
                availableHours.add(dayMap.get(arr[0]) + ": " + timeMap.get(arr[1]) + " - " + timeMap.get(arr[1]+time));
            }
        } catch (IOException e) {
            e.printStackTrace();
            availableHours.add("noConnection");
            noConnectionToServer(new Stage());
            return availableHours;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return availableHours;
    }

    private void resultSet(Stage stage, String result) {
        VBox vBox = new VBox();
        Text text = new Text("Verfügbare Termine:\n\n" + result);
        text.setTranslateX(10);
        text.setTranslateY(10);
        Button ok = new Button("OK");
        ok.setTranslateX(70);
        ok.setTranslateY(20);
        ok.setOnAction(event -> stage.close());
        vBox.getChildren().addAll(text, ok);
        Scene scene = new Scene(vBox, 180, 120 + 16 * availableAppointments);
        stage.setScene(scene);
        stage.show();
    }

    private void noConnectionToServer(Stage stage) {
        VBox vBox = new VBox();
        Text message = new Text("Verbindung zum Server konnte\nnicht hergestellt werden.\n\nPrüfe, ob der Server gestartet wurde!");
        Button ok = new Button("OK");
        message.setTranslateX(5);
        message.setTranslateY(5);
        ok.setTranslateX(80);
        ok.setTranslateY(20);
        ok.setOnAction(event -> stage.close());
        vBox.getChildren().addAll(message, ok);
        Scene scene = new Scene(vBox, 210, 140);
        stage.setScene(scene);
        stage.show();
    }
}