package nn;

import java.awt.Graphics2D;

/**
 *
 * @author user
 */
public class Network {

    public Layer[] Layers;
    public float learning_rate = 0.03f;
    public String activation_function;
    public float[] loss_history;
    public String dataset_type;
    public DataSet data;
    public DataPoint current_dta;
    public float min = -6f, max = 6f;
    float total_train_loss = 0;
    float total_test_loss = 0;

    public Network(int num_hidden_layers, String activation_f, String dataset_type) {
        //construct input layer
        activation_function = activation_f;
        this.dataset_type = dataset_type;
        data = new DataSet(dataset_type, 0.5f, 1000, min, max);
        Layer input = build_input_layer();
        Layer output = build_output_layer();
        if (num_hidden_layers == 0) {
            connect(input, output);
            Layers = new Layer[2];
            Layers[0] = input;
            Layers[1] = output;
        } else {
            Layers = new Layer[num_hidden_layers + 2];
            Layers[0] = input;
            Layers[Layers.length - 1] = output;
            Layer[] hidden = build_hidden_layers(num_hidden_layers);
            connect(input, hidden[0]);
            connect(hidden[num_hidden_layers - 1], output);
            for (int i = 0; i < hidden.length - 1; i++) {
                connect(hidden[i], hidden[i + 1]);
            }
            for (int i = 0; i < hidden.length; i++) {
                Layers[i + 1] = hidden[i];
            }

        }
    }

    /**
     * computes the total loss from the network accumulated from all sampled
     * points in the dataset.
     */
    public void compute_loss() {
        //get output layer
        Layer out_l = Layers[Layers.length - 1];
        Node out_n = out_l.neurons[0];

        float predicted_value = (out_n.activation_value);
        //get the actual label of the data_point
        float target_value = current_dta.label;
        //compute the loss
        total_train_loss += 0.5 * (Math.pow(predicted_value - target_value, 2));
        //compute the loss gradient wrt the output node activation value.
        out_n.d_Etotal_d_out = predicted_value - target_value;
    }

    /**
     * the back-propagation pass of the perceptron, updates the weights of the
     * links and the bias value of each node in the network except for the
     * input/feature layer.
     */
    public void back_prop() {
        for (int i = Layers.length - 1; i > 0; i--) {
            Layer l = Layers[i];
            for (Node n : l.neurons) {
                if (n.type != 0) {
                    n.update_derivatives(learning_rate);
                }
            }
        }
    }

    /**
     * the forward pass of the perceptron , where activation value of each node
     * is computed for a specific x1,x2 data point injested through the feature
     * layer. (the feature layer computes the corresponding label values defined
     * by the function of that feature eg. f(x1,x2)=sin(x1)...)
     */
    public void forward_prop() {

        for (Layer l : Layers) {
            for (Node n : l.neurons) {
                //n.update_output();
                if (n.type == 0) {
                    n.update_feature(current_dta.loc);
                } else {
                    n.update_output();
                }
            }
        }
    }

    /**
     * updates the network state
     */
    public void update_network() {
        total_train_loss = 0;
        for (DataPoint train : data.train) {
            current_dta = train;
            forward_prop();
            compute_loss();
            back_prop();
        }
        for (Layer l : Layers) {
            for (Node n : l.neurons) {
                if (n.type == 0) {
                    n.update_feature_map(-6, 6, 40);
                } else {
                    n.update_activation_map(-6, 6, 40);

                }
            }
        }
    }

    public void draw_network(Graphics2D g2d, int x, int y) {

        for (Layer l : Layers) {
            //draw layers in a horizontal array
            l.draw_layer(g2d);
        }
    }

    public Layer build_input_layer() {
        Node n1 = new Node("x1");
        Node n2 = new Node("x2");
        Node n3 = new Node("x1x2");
        Node n4 = new Node("x1sq");
        Node n5 = new Node("x2sq");
        Node n6 = new Node("sinx1");
        Node n7 = new Node("sinx2");
        int layerY = 60;
        int layerX = 100;
        Layer input = new Layer(new Node[]{n1, n2, n3, n4, n5, n6, n7}, layerX, layerY);
        for (int i = 0; i < input.neurons.length; i++) {
            input.neurons[i].loc = new Point(layerX, layerY + i * 65);
        }
        return input;
    }

    public Layer build_output_layer() {
        Node n1 = new Node(activation_function, 2);
        n1.loc = new Point(800, 90);
        n1.data = data;
        Layer output = new Layer(new Node[]{n1}, 800, 90);
        return output;
    }

    public Layer[] build_hidden_layers(int num) {
        Layer[] h_layers = new Layer[num];
        int layer_spacing = 600 / (num + 1);
        for (int i = 0; i < num; i++) {
            int layerY = 120;
            int layerX = 170 + (i + 1) * layer_spacing;
            Layer l = construct_hidden_layer(2, layerX, layerY);
            h_layers[i] = l;
        }
        return h_layers;
    }

    public Layer construct_hidden_layer(int no_neurons, int layerX, int layerY) {
        Node[] n = new Node[no_neurons];
        for (int j = 0; j < no_neurons; j++) {
            n[j] = new Node(activation_function, 1);
            n[j].loc = new Point(layerX, layerY + j * 60);
        }
        Layer l = new Layer(n, layerX, layerY);
        l.no_neurons = no_neurons;
        return l;

    }

    /**
     * creates links between nodes in each layer.
     * @param l1 the source layer.
     * @param l2 the destination layer
     */
    public void connect(Layer l1, Layer l2) {
        //generate the link matrix
        Link[][] l = new Link[l1.neurons.length][l2.neurons.length];
        for (int i = 0; i < l1.neurons.length; i++) {
            for (int j = 0; j < l2.neurons.length; j++) {
                Node src = l1.neurons[i];
                Node dst = l2.neurons[j];
                l[i][j] = new Link(src, dst);
            }
        }
        
        //assign output links to each src layer neuron
        for (int i = 0; i < l1.neurons.length; i++) {
            Node src = l1.neurons[i];
            src.output = l[i];
        }

        //assign input links to each dst layer neuron
        for (int i = 0; i < l2.neurons.length; i++) {
            Node dst = l2.neurons[i];
            Link[] r = new Link[l.length];
            for (int j = 0; j < l.length; j++) {
                r[j] = l[j][i];
            }
            dst.input = r;
        }

    }

    /**
     * reset the topology between two layer. Used when the number of neurons
     * in one layer has been changed.
     * @param i 
     */
    public void reconnect(int i) {
        connect(Layers[i - 1], Layers[i]);
        connect(Layers[i], Layers[i + 1]);
    }

    public void reset() {
        for (int i = 0; i < Layers.length; i++) {
            Layer l = Layers[i];
            for (int j = 0; j < l.neurons.length; j++) {
                Node n = l.neurons[j];
                n.reset();
                if (n.type != 2) {
                    for (int k = 0; k < n.output.length; k++) {
                        Link lnk = n.output[k];
                        lnk.reset();
                    }
                }
            }
        }
    }
    
    public void set_activation_function(String activation_f){
        for (int i = 0; i < Layers.length; i++) {
            Layer l = Layers[i];
            for (int j = 0; j < l.neurons.length; j++) {
                Node n = l.neurons[j];
                n.activation_function=activation_f;
                
            }
        }
    }

}
