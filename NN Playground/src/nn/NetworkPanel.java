package nn;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.rgb;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author user
 */
public class NetworkPanel extends JPanel implements MouseListener {

    //color interpolator
    public static NetworkPanel dPanel;
    public static float[][] val_matrix;
    public static float min;
    public static float max;
    public static String activation_function = "Tanh";
    public static int num_of_hidden_layer = 3;
    public static String dataset = "XOR";
    public static boolean PAUSED = true;
    static Label EpochNumber;
    MouseEvent m;
    static Network n;
    static LossChart l_c;
    static int epoch = 0;
    static Util p = new Util();

    public NetworkPanel() {
        addMouseListener(this);
        l_c = new LossChart();
        n = new Network(num_of_hidden_layer, activation_function, dataset);
        n.update_network();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, getWidth(), getHeight());
        step(g2d);
    }

    public void step(Graphics2D g2d) {
        if (PAUSED) {
            n.draw_network(g2d, 50, 50);
            l_c.draw_loss_history(g2d, 900, 40);
            g2d.setColor(Color.black);
            g2d.drawString("Loss (Tr.):" + String.format("%.3f", n.total_train_loss / n.data.train.length), 800, 60);
            g2d.drawString("Epoch :" + epoch, 800, 80);

        } else {
            n.total_train_loss = 0;
            n.update_network();
            try {
                Thread.sleep(30);
            } catch (Exception e) {

            }

            epoch++;
            n.draw_network(g2d, 50, 50);
            l_c.update_loss_data(n.total_train_loss / n.data.train.length);
            l_c.draw_loss_history(g2d, 900, 40);
            g2d.setColor(Color.black);
            g2d.drawString("Loss (Tr.):" + String.format("%.3f", n.total_train_loss / n.data.train.length), 800, 60);
            g2d.drawString("Epoch :" + epoch, 800, 80);
            repaint(50);
        }
    }

    private static Scene createScene() {
        Group root = new Group();
        Scene scene = new Scene(root);
        Label EpochLabel = new Label(" Epoch");
        EpochNumber = new Label("0");
        EpochLabel.setFont(javafx.scene.text.Font.font("Aliquam", 16));
        EpochLabel.setTextFill(rgb(1, 161, 133));
        EpochNumber.setFont(javafx.scene.text.Font.font("Aliquam", 36));
        EpochNumber.setTextFill(rgb(1, 161, 133));

        VBox EpochPane = new VBox();
        EpochPane.setSpacing(5.0f);
        EpochPane.getChildren().addAll(new HBox(), EpochLabel, EpochNumber);
        EpochPane.setMinWidth(150);
        EpochPane.setPadding(new Insets(0, 0, 00, 00));
        StackPane content = new StackPane();

        JFXDrawer settingsDrawer = new JFXDrawer();
        HBox settingsDrawerPane = new HBox();
        settingsDrawerPane.setSpacing(40.0);
        settingsDrawerPane.setPadding(new Insets(10, 00, 00, 40));

        Label LearnLabel = new Label("Learning Rate");
        JFXComboBox Learning_Rate = new JFXComboBox();
        Learning_Rate.setMaxWidth(200);
        // Learning_Rate.setPromptText();
        Learning_Rate.setStyle("-fx-text-fill: BLACK;-fx-font-size: 13px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        VBox LearnPane = new VBox();
        LearnPane.setSpacing(5.0f);
        LearnPane.getChildren().addAll(new HBox(), LearnLabel, Learning_Rate);
        LearnPane.setMinWidth(200);
        ObservableList<String> learning_rates = FXCollections.observableArrayList("0.03", "0.05", "0.07", "0.09", "0.1");
        Learning_Rate.setItems(learning_rates);
        Learning_Rate.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    PAUSED = true;
                    epoch = 0;
                    n.learning_rate = Float.parseFloat(newValue.toString());
                    n.reset();
                    n.update_network();
                    l_c = new LossChart();
                    dPanel.repaint();
                }
        );
        Learning_Rate.setValue(learning_rates.get(0));

        JFXComboBox Activation = new JFXComboBox();
        Activation.setMaxWidth(200);
        Label ActivLabel = new Label("Activation");
        Activation.setStyle("-fx-text-fill: BLACK;-fx-font-size: 13px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        VBox ActivationPane = new VBox();
        ActivationPane.setSpacing(5.0f);
        ActivationPane.getChildren().addAll(new HBox(), ActivLabel, Activation);
        ActivationPane.setMinWidth(200);
        ObservableList<String> activation_functions = FXCollections.observableArrayList("Tanh", "Sigmoid", "Linear", "ReLU");
        Activation.setItems(activation_functions);
        Activation.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    PAUSED = true;
                    epoch = 0;
                    activation_function = newValue.toString();
                    n.set_activation_function(activation_function);
                    n.reset();
                    //n = new Network(num_of_hidden_layer, activation_function, dataset);
                    n.update_network();
                    l_c = new LossChart();
                    dPanel.repaint();
                }
        );
        Activation.setValue(activation_functions.get(0));

        JFXComboBox DataSets = new JFXComboBox();
        DataSets.setMaxWidth(250);
        Label RegLabel = new Label("Data Sets");
        DataSets.setStyle("-fx-text-fill: BLACK;-fx-font-size: 13px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        VBox DataSetPane = new VBox();
        DataSetPane.setSpacing(5.0f);
        DataSetPane.getChildren().addAll(new HBox(), RegLabel, DataSets);
        DataSetPane.setMinWidth(200);
        ObservableList<String> data_sets = FXCollections.observableArrayList("Gaussian", "XOR", "Spiral", "Circle", "H.Wave", "V.Wave", "I.Wave");
        DataSets.setItems(data_sets);
        DataSets.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    PAUSED = true;
                    epoch = 0;
                    dataset = newValue.toString();
                    n.dataset_type = dataset;
                    n.data = new DataSet(n.dataset_type, 0.5f, 1000, n.min, n.max);
                    n.Layers[n.Layers.length-1].neurons[0].data=n.data;
                    n.reset();
                    n.update_network();
                    l_c = new LossChart();
                    dPanel.repaint();
                }
        );
        DataSets.setValue(data_sets.get(1));

        FontAwesomeIconView run_icon = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
        run_icon.setStyle("-fx-font-size: 18px; -fx-fill: WHITE;");

        FontAwesomeIconView pause_icon = new FontAwesomeIconView(FontAwesomeIcon.PAUSE);
        pause_icon.setStyle("-fx-font-size: 18px; -fx-fill: WHITE;");

        FontAwesomeIconView reset_icon = new FontAwesomeIconView(FontAwesomeIcon.UNDO);
        reset_icon.setStyle("-fx-font-size: 18px; -fx-fill: BLACK;");

        FontAwesomeIconView step_icon = new FontAwesomeIconView(FontAwesomeIcon.STEP_FORWARD);
        step_icon.setStyle("-fx-font-size: 18px; -fx-fill: BLACK;");

        JFXButton run = new JFXButton();
        run.setButtonType(JFXButton.ButtonType.FLAT);
        run.setRipplerFill(rgb(238, 190, 182));
        run.setStyle("-fx-background-color:rgb(20,20,20);-fx-font-size: 24px;-fx-pref-width: 50px;-fx-pref-height: 50px;-fx-text-fill: WHITE; -fx-border-radius: 50px;-fx-background-radius: 50px;");
        run.setGraphic(run_icon);
        //     run.setFont(f);

        JFXButton reset = new JFXButton();
        reset.setButtonType(JFXButton.ButtonType.FLAT);
        reset.setRipplerFill(rgb(220, 220, 220));
        reset.setStyle("-fx-background-color:rgb(250,250,250);-fx-font-size: 12px;-fx-pref-width: 25px;-fx-pref-height: 25px;-fx-text-fill: WHITE; -fx-border-radius: 25px;-fx-background-radius: 25px;");
        reset.setGraphic(reset_icon);

        JFXButton step = new JFXButton();
        step.setButtonType(JFXButton.ButtonType.FLAT);
        step.setRipplerFill(rgb(220, 220, 220));
        step.setStyle("-fx-background-color:rgb(250,250,250);-fx-font-size: 12px;-fx-pref-width: 25px;-fx-pref-height: 25px;-fx-text-fill: WHITE; -fx-border-radius: 25px;-fx-background-radius: 25px;");
        step.setGraphic(step_icon);

        VBox buttonPane2 = new VBox();
        HBox buttonPane = new HBox();

        buttonPane.setSpacing(15.0);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.getChildren().addAll(reset, run, step);
        buttonPane.setPrefWidth(200);
        buttonPane.setPadding(new Insets(10, 00, 00, 00));
        buttonPane2.getChildren().addAll(buttonPane, new HBox(new Label("     ")));
        buttonPane2.setSpacing(20.0f);

        VBox vb = new VBox();
        vb.setSpacing(8);
        vb.setPadding(new Insets(10, 10, 0, 0));
        Label num_layers_label = new Label("Hidden Layers");
        JFXSlider num_layers = new JFXSlider();
        num_layers.setMinWidth(200);
        num_layers.setMax(6);
        num_layers.setMin(0);
        num_layers.setValue(3);
        num_layers.setBlockIncrement(1);
        num_layers.setSnapToTicks(true);
        num_layers.setMajorTickUnit(1);
        num_layers.setMinorTickCount(0);
        num_layers.setShowTickMarks(true);
        num_layers.setShowTickLabels(true);
        num_layers.setIndicatorPosition(JFXSlider.IndicatorPosition.LEFT);
        num_layers.addEventHandler(javafx.scene.input.MouseEvent.DRAG_DETECTED, (e) -> {
            PAUSED = true;
            epoch = 0;
            if (num_of_hidden_layer != (int) num_layers.getValue()) {
                num_of_hidden_layer = (int) num_layers.getValue();
                n = new Network(num_of_hidden_layer, activation_function, dataset);
                n.update_network();
                l_c = new LossChart();
                dPanel.repaint();
            }
        });
        num_layers.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            PAUSED = true;
            epoch = 0;
            if (num_of_hidden_layer != (int) num_layers.getValue()) {
                num_of_hidden_layer = (int) num_layers.getValue();
                n = new Network(num_of_hidden_layer, activation_function, dataset);
                n.update_network();
                l_c = new LossChart();
                dPanel.repaint();
            }
        });
        num_layers.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, (e) -> {
            PAUSED = true;
            epoch = 0;
            if (num_of_hidden_layer != (int) num_layers.getValue()) {
                num_of_hidden_layer = (int) num_layers.getValue();
                n = new Network(num_of_hidden_layer, activation_function, dataset);
                n.update_network();
                l_c = new LossChart();
                dPanel.repaint();
            }
        });
        num_layers.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            PAUSED = true;
            epoch = 0;
            if (num_of_hidden_layer != (int) num_layers.getValue()) {
                num_of_hidden_layer = (int) num_layers.getValue();
                n = new Network(num_of_hidden_layer, activation_function, dataset);
                n.update_network();
                l_c = new LossChart();
                dPanel.repaint();
            }
        });

        vb.getChildren().addAll(num_layers_label, num_layers);

        settingsDrawerPane.getChildren().addAll(buttonPane2, LearnPane, ActivationPane, DataSetPane, vb, new HBox());
        settingsDrawerPane.setAlignment(Pos.CENTER);
        settingsDrawerPane.setStyle("-fx-background-color:rgb(255,255,255)");
        settingsDrawer.setDefaultDrawerSize(150);
        settingsDrawer.setPrefWidth(1000);
        settingsDrawer.setDirection(JFXDrawer.DrawerDirection.TOP);
        settingsDrawer.setSidePane(settingsDrawerPane);
        settingsDrawer.setOverLayVisible(true);
        settingsDrawer.setResizableOnDrag(false);

        JFXDrawersStack drawersStack = new JFXDrawersStack();
        drawersStack.setContent(content);
        drawersStack.toggle(settingsDrawer, true);

        run.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            run.setGraphic(run_icon);
            if (PAUSED) {
                run.setGraphic(pause_icon);
                PAUSED = !PAUSED;
                dPanel.repaint();
            } else {
                run.setGraphic(run_icon);
                PAUSED = !PAUSED;
            }
        });

        reset.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            PAUSED = true;
            epoch = 0;
            n.reset();
            n.update_network();
            l_c = new LossChart();
            dPanel.repaint();
        });

        step.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            PAUSED = true;
            n.update_network();
            l_c.update_loss_data(n.total_train_loss / n.data.train.length);
            dPanel.repaint();
        });

        root.getChildren().add(drawersStack);
        return (scene);
    }

    private static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);

    }

    private static void initAndShowGUI() {
        JFrame dFrame = new JFrame("Neural Network Playground");
        dFrame.setLayout(null);
        dPanel = new NetworkPanel();
        dPanel.setSize(1300, 600);
        dPanel.setLocation(0, 100);
        dPanel.setDoubleBuffered(true);

        final JFXPanel fxPanel = new JFXPanel();
        fxPanel.setLocation(0, 0);
        fxPanel.setSize(1300, 100);

        dFrame.setLocation(50, 90);
        dFrame.setSize(1300, 650);
        dFrame.add(dPanel);
        dFrame.add(fxPanel);

        dFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dFrame.setVisible(true);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    public static void main(String[] args) {
        initAndShowGUI();
    }

    /**
     * handles mouse events related to the number of neurons per each layer.
     * (the "plus" and "minus" buttons)
     *
     * @param mx mouse coordinate X
     * @param my mouse coordinate Y
     * @param n the network
     */
    public void check_layer_util(int mx, int my, Network n) {
        for (int i = 1; i < n.Layers.length - 1; i++) {
            Layer l = n.Layers[i];
            if (l.pos_btn_clicked(mx, my)) {
                if (l.no_neurons <= 5) {
                    l.no_neurons++;
                    n.Layers[i] = n.construct_hidden_layer(l.no_neurons, l.layerX, l.layerY);
                    n.reconnect(i);
                    n.update_network();
                    dPanel.repaint();
                }
            }
            if (l.neg_btn_clicked(mx, my)) {
                if (l.no_neurons >= 2) {
                    l.no_neurons--;
                    n.Layers[i] = n.construct_hidden_layer(l.no_neurons, l.layerX, l.layerY);
                    n.reconnect(i);
                    n.update_network();
                    dPanel.repaint();
                }
            }

        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int mx = me.getX();
        int my = me.getY();
        check_layer_util(mx, my, n);
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

}
