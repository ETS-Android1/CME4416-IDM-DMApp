package com.example.enes_.dataminingapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.functions.supportVector.NormalizedPolyKernel;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.PrecomputedKernelMatrixKernel;
import weka.classifiers.functions.supportVector.Puk;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.StringKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.ChebyshevDistance;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.MinkowskiDistance;
import weka.core.NormalizableDistance;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.LinearNNSearch;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.supervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ChangeDateFormat;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.RemoveType;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.Randomize;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // ----- EditTexts --------------------------------------------------------
    EditText nFoldCrossValidation,percentageSplit;
    EditText confidence_text,dt_seed_text,learning_rate_text, momentum_text, nn_seed_text;
    EditText knn,max_depth_text, num_trees_text,c_text,epsilon_text;
    // ----- TextViews --------------------------------------------------------
    TextView knnParametersTV,svmParametersTV,randomForestParametersTV,neuralNetworkParametersTV,decisionTreeParametersTV;
    TextView tvB2,tvB4,tvB6,tvB8,tvB10,tvB12,tvB14,tvB16,tvB18,tvB20;
    TextView textView;// dosya adını gösteren
    // ----- Buttons -----------------------------------------------------------
    Button buttonShowResults;
    Button select;
    // ----- RadioGroup --------------------------------------------------------
    RadioGroup rg;
    RadioButton rb;
    // ----- Spinners ----------------------------------------------------------
    Spinner spinner,spinnerknn,spinnersvm;
    // ----- Variables ---------------------------------------------------------
    boolean r1 = false, r2 = false;
    int n,percentage_split,default_n = 10,default_percentage_split = 66;
    int k,dt_seed,nn_seed,max_depth, num_trees;
    double c, epsilon,learning_rate,momentum;
    float confidence;
    String algorithm,path,matrix,summary,distanceMethod,kernel;
    private static final int REQUEST_CODE = 43;
    public static Instances data = null;
    public static Evaluation eval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tvB2 = findViewById(R.id.tvB2);
        tvB4 = findViewById(R.id.tvB4);
        tvB6 = findViewById(R.id.tvB6);
        tvB8 = findViewById(R.id.tvB8);
        tvB10 = findViewById(R.id.tvB10);
        tvB12 = findViewById(R.id.tvB12);
        tvB14 = findViewById(R.id.tvB14);
        tvB16 = findViewById(R.id.tvB16);
        tvB18 = findViewById(R.id.tvB18);
        tvB20 = findViewById(R.id.tvB20);

        select = (Button) findViewById(R.id.button2);
        buttonShowResults = findViewById(R.id.buttonShowResults);
        buttonShowResults.setEnabled(false);

        spinner = findViewById(R.id.spinner); // select algorithm id
        spinnerknn = findViewById(R.id.spinner2); // knn spinner id
        //spinnerknn.setVisibility(View.INVISIBLE);
        //spinner.setVisibility(View.INVISIBLE);
        spinner.setEnabled(false);
        spinnersvm = findViewById(R.id.spinner3); // select kernel id
        //spinnersvm.setVisibility(View.INVISIBLE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.SelectAlgorithm,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.SelectDistanceMeasure,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerknn.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.SelectKernel,android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersvm.setAdapter(adapter3);

        rg = (RadioGroup) findViewById(R.id.rgroup);
        //rg.setVisibility(View.INVISIBLE);



        knnParametersTV = findViewById(R.id.knnParametersTV);
        svmParametersTV = findViewById(R.id.svmParametersTV);
        randomForestParametersTV = findViewById(R.id.randomForestParametersTV);
        neuralNetworkParametersTV = findViewById(R.id.neuralNetworkParametersTV);
        decisionTreeParametersTV = findViewById(R.id.decisionTreeParametersTV);

        textView = (TextView)findViewById(R.id.textView);
        nFoldCrossValidation = (EditText) findViewById(R.id.editText);
        nFoldCrossValidation.setVisibility(View.INVISIBLE);
        percentageSplit = (EditText) findViewById(R.id.editText2);
        percentageSplit.setVisibility(View.INVISIBLE);
        nFoldCrossValidation.setEnabled(false);
        percentageSplit.setEnabled(false);
        knn = (EditText) findViewById(R.id.editText4);
        //knn.setVisibility(View.INVISIBLE);
        c_text = (EditText) findViewById(R.id.editText3);
        //c_text.setVisibility(View.INVISIBLE);
        epsilon_text = (EditText)findViewById(R.id.editText5);
        //epsilon_text.setVisibility(View.INVISIBLE);
        confidence_text = (EditText)findViewById(R.id.editText7);
        //confidence_text.setVisibility(View.INVISIBLE);
        dt_seed_text = (EditText)findViewById(R.id.editText6);
        //dt_seed_text.setVisibility(View.INVISIBLE);
        nn_seed_text = (EditText)findViewById(R.id.editText10);
        //nn_seed_text.setVisibility(View.INVISIBLE);
        momentum_text = (EditText)findViewById(R.id.editText9);
        //momentum_text.setVisibility(View.INVISIBLE);
        learning_rate_text = (EditText)findViewById(R.id.editText8);
        //learning_rate_text.setVisibility(View.INVISIBLE);
        max_depth_text = (EditText)findViewById(R.id.editText11);
        //max_depth_text.setVisibility(View.INVISIBLE);
        num_trees_text = (EditText)findViewById(R.id.editText12);
        //num_trees_text.setVisibility(View.INVISIBLE);

        nFoldCrossValidation.setText(String.valueOf(default_n));
        percentageSplit.setText(String.valueOf(default_percentage_split));

        findViewById(R.id.button).setEnabled(false); // run button

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(algorithm.equals("Naive Bayes")) {
                    if(r1 && !r2){
                        n = Integer.parseInt(nFoldCrossValidation.getEditableText().toString());
                        NaiveBayes_with_nFold(n);
                    }
                    else if (!r1 && r2){
                        percentage_split = Integer.parseInt(percentageSplit.getEditableText().toString());
                        NaiveBayes_with_percentageSplit(percentage_split);
                    }
                }
                if(algorithm.equals("Decision Trees")) {
                    n = Integer.parseInt(nFoldCrossValidation.getEditableText().toString());
                    percentage_split = Integer.parseInt(percentageSplit.getEditableText().toString());
                    dt_seed = Integer.parseInt(dt_seed_text.getEditableText().toString());
                    confidence = Integer.parseInt(num_trees_text.getEditableText().toString());
                    if(r1 && !r2){
                        DecisionTree_with_nFold(n,confidence,dt_seed);
                    }
                    else if (!r1 && r2){
                        DecisionTree_with_percentageSplit(percentage_split,confidence,dt_seed);
                    }
                }
                if(algorithm.equals("Support Vector Machine")) {
                    n = Integer.parseInt(nFoldCrossValidation.getEditableText().toString());
                    percentage_split = Integer.parseInt(percentageSplit.getEditableText().toString());
                    kernel = spinnersvm.getSelectedItem().toString();
                    c = Double.valueOf(c_text.getEditableText().toString());
                    if(epsilon_text.getEditableText().toString().isEmpty())
                    {
                        epsilon = 1.0E-12;
                    }
                    else
                    {
                        epsilon = Double.valueOf(epsilon_text.getEditableText().toString());
                    }
                    if(r1 && !r2){
                        SupportVectorMachine_with_nFold(n, kernel,c,epsilon);
                    }
                    else if (!r1 && r2){
                        SupportVectorMachine_with_percentageSplit(percentage_split,kernel,c,epsilon);
                    }
                }
                if(algorithm.equals("k-Nearest Neighbor")) {
                    n = Integer.parseInt(nFoldCrossValidation.getEditableText().toString());
                    percentage_split = Integer.parseInt(percentageSplit.getEditableText().toString());
                    k = Integer.parseInt(knn.getEditableText().toString());
                    distanceMethod =  spinnerknn.getSelectedItem().toString();
                    if(k % 2 != 1) {
                        knn.setError("k cannot be even");
                        //Toast.makeText(getApplicationContext(), "K cannot be even", Toast.LENGTH_LONG).show();
                    }
                    else{
                        if(r1 && !r2){
                            kNearestNeighbor_with_nFold(n,k,distanceMethod);
                        }
                        else if (!r1 && r2){
                            kNearestNeighbor_with_percentageSplit(percentage_split, k, distanceMethod);
                        }
                    }
                }
                if(algorithm.equals("Random Forest")) {
                    n = Integer.parseInt(nFoldCrossValidation.getEditableText().toString());
                    percentage_split = Integer.parseInt(percentageSplit.getEditableText().toString());
                    max_depth = Integer.parseInt(max_depth_text.getEditableText().toString());
                    num_trees = Integer.parseInt(num_trees_text.getEditableText().toString());
                    if(r1 && !r2){
                        randomForest_with_nFold(n,max_depth,num_trees);
                    }
                    else if (!r1 && r2){
                        randomForest_with_percentageSplit(percentage_split,max_depth,num_trees);
                    }
                }
                if(algorithm.equals("Neural Network")) {
                    n = Integer.parseInt(nFoldCrossValidation.getEditableText().toString());
                    percentage_split = Integer.parseInt(percentageSplit.getEditableText().toString());
                    learning_rate = Double.valueOf(learning_rate_text.getEditableText().toString());
                    momentum = Double.valueOf(learning_rate_text.getEditableText().toString());
                    nn_seed = Integer.valueOf(learning_rate_text.getEditableText().toString());
                    if(r1 && !r2){
                        neuralNetwork_with_nFold(n,learning_rate,momentum,nn_seed);
                    }
                    else if (!r1 && r2){
                        neuralNetwork_with_percentageSplit(percentage_split,learning_rate,momentum,nn_seed);
                    }
                }
            }
        });
        // select file
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
        // display confusion matrix
        buttonShowResults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openActivity2();
                }
        });


    }
    public void rbclick(View v){
        int radiobuttonid = rg.getCheckedRadioButtonId();
        rb = (RadioButton)findViewById(radiobuttonid);

        if(rb == (RadioButton)findViewById(R.id.radioButton8)){
            nFoldCrossValidation.setEnabled(true);
            percentageSplit.setEnabled(false);
            r1 = true;
            r2 = false;
        }
        else if(rb == (RadioButton)findViewById(R.id.radioButton9)){
            percentageSplit.setEnabled(true);
            nFoldCrossValidation.setEnabled(false);
            r1 = false;
            r2 = true;
        }
    }
    public void openActivity2(){
        Intent i = new Intent(getBaseContext(), Main2Activity.class);
        i.putExtra("value1",matrix);
        i.putExtra("value2",summary);
        startActivity(i);
    }
    private void startSearch(){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
        startActivityForResult(intent,REQUEST_CODE);

    }
    protected void onActivityResult(int requestCode,int resultCode,Intent file_data){
        super.onActivityResult(requestCode,resultCode,file_data);

        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){

            if(file_data != null){
                Uri uri = file_data.getData();
                Instances newdata = null;
                try {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
                    ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(inputStream);
                    newdata = dataSource.getDataSet();
                    data = new Instances(newdata);
                    data.setClassIndex(data.numAttributes()-1);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),"File Not Found!!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"File Not Opened!!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                String[] parts = uri.getPath().split("/");
                path = parts[parts.length-1];
            }
        }
        textView.setText(path);
        //spinner.setVisibility(View.VISIBLE);
        spinner.setEnabled(true);
        findViewById(R.id.button).setEnabled(true);

        //Read_File(path);
    }
    void Read_File(String path){
        // bu uygulama içindeki assest dosyasından buluyordu orda yoksa okumuyordu
        try {
            InputStream is = getAssets().open(path);
            Reader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            data = new Instances(br);
            data.setClassIndex(data.numAttributes()-1);

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String text = parent.getItemAtPosition(position).toString();
            algorithm = text;

            parametersEnable();

            if(algorithm.equals("Naive Bayes")){
                paramVisible();
                parametersEnable();
                parametersResetColor();
            }
            if(algorithm.equals("Decision Trees")){
                paramVisible();
                parametersEnable();
                confidence_text.setEnabled(true);
                dt_seed_text.setEnabled(true);
                parametersResetColor();
                decisionTreeParametersTV.setTextColor(getResources().getColor(R.color.colorParameter));
            }
            if(algorithm.equals("Neural Network")){
                paramVisible();
                parametersEnable();
                learning_rate_text.setEnabled(true);
                momentum_text.setEnabled(true);
                nn_seed_text.setEnabled(true);
                parametersResetColor();
                neuralNetworkParametersTV.setTextColor(getResources().getColor(R.color.colorParameter));
            }
            if(algorithm.equals("k-Nearest Neighbor")){
                paramVisible();
                parametersEnable();
                knn.setEnabled(true);
                spinnerknn.setEnabled(true);
                parametersResetColor();
                knnParametersTV.setTextColor(getResources().getColor(R.color.colorParameter));
            }
            if(algorithm.equals("Random Forest")){
                paramVisible();
                parametersEnable();
                max_depth_text.setEnabled(true);
                num_trees_text.setEnabled(true);
                parametersResetColor();
                randomForestParametersTV.setTextColor(getResources().getColor(R.color.colorParameter));
            }
            if(algorithm.equals("Support Vector Machine")){
                paramVisible();
                parametersEnable();
                c_text.setEnabled(true);
                epsilon_text.setEnabled(true);
                spinnersvm.setEnabled(true);
                parametersResetColor();
                svmParametersTV.setTextColor(getResources().getColor(R.color.colorParameter));
            }
    }
    void NaiveBayes_with_nFold(int n){

        try {

            Classifier nb = new NaiveBayes();

            DataPreparation();

            nb.buildClassifier(data);
            eval = new Evaluation(data);

            eval.crossValidateModel(nb,data,n, new Random(1));
            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void NaiveBayes_with_percentageSplit(int percentage){

        try {
            Classifier nb = new NaiveBayes();

            DataPreparation();

            int trainSetSize = data.numInstances()*percentage/100;
            int testSetSize = data.numInstances() - trainSetSize;

            data.randomize(new java.util.Random(1));
            Instances train = new Instances(data,0,trainSetSize);
            Instances test = new Instances(data,trainSetSize,testSetSize);

            nb.buildClassifier(train);

            for(int i = 0; i<data.numInstances(); i++){
                eval = new Evaluation(train);
                eval.evaluateModel(nb,test);
            }

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void DecisionTree_with_nFold(int n, float confidence, int seed){
        try {

            Classifier dt = new J48();

            DataPreparation();

            dt.buildClassifier(data);
            ((J48) dt).setConfidenceFactor(confidence);
            ((J48) dt).setSeed(seed);

            eval = new Evaluation(data);
            eval.crossValidateModel(dt,data,n, new Random(1));
            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void DecisionTree_with_percentageSplit(int percentage,float confidence, int seed){
        try {

            Classifier dt = new J48();

            DataPreparation();

            int trainSetSize = data.numInstances()*percentage/100;
            int testSetSize = data.numInstances() - trainSetSize;

            data.randomize(new java.util.Random(1));
            Instances train = new Instances(data,0,trainSetSize);
            Instances test = new Instances(data,trainSetSize,testSetSize);

            dt.buildClassifier(train);
            ((J48) dt).setConfidenceFactor(confidence);
            ((J48) dt).setSeed(seed);
            for(int i = 0; i<data.numInstances(); i++){
                eval = new Evaluation(train);
                eval.evaluateModel(dt,test);

            }

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void SupportVectorMachine_with_nFold(int n, String kernel, double c, double epsilon){

        Kernel kernelType = new PolyKernel();
        if(kernel.equals("Normalized Poly Kernel")){
            kernelType = new NormalizedPolyKernel();
        }
        else if(kernel.equals("Precomputed Kernel Matrix Kernel")){
            kernelType = new PrecomputedKernelMatrixKernel();
        }
        else if(kernel.equals("Puk")){
            kernelType = new Puk();
        }
        else if(kernel.equals("RBF Kernel")){
                kernelType = new RBFKernel();
        }
        else if(kernel.equals("String Kernel")){
                kernelType = new StringKernel();
        }
        try {

            Classifier svm = new SMO();

            DataPreparation2();

            svm.buildClassifier(data);

            ((SMO) svm).setKernel(kernelType);
            ((SMO) svm).setC(c);
            ((SMO) svm).setEpsilon(epsilon);
            eval = new Evaluation(data);

            eval.crossValidateModel(svm,data,n, new Random(1));

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void SupportVectorMachine_with_percentageSplit(int percentage, String kernel, double c, double epsilon){

        Kernel kernelType = new PolyKernel();
        if(kernel.equals("Normalized Poly Kernel")){
            kernelType = new NormalizedPolyKernel();
        }
        else if(kernel.equals("Precomputed Kernel Matrix Kernel")){
            kernelType = new PrecomputedKernelMatrixKernel();
        }
        else if(kernel.equals("Puk")){
            kernelType = new Puk();
        }
        else if(kernel.equals("RBF Kernel")){
            kernelType = new RBFKernel();
        }
        else if(kernel.equals("String Kernel")){
            kernelType = new StringKernel();
        }
        try {


            Classifier svm = new SMO();

            DataPreparation2();

            int trainSetSize = data.numInstances()*percentage/100;
            int testSetSize = data.numInstances() - trainSetSize;

            data.randomize(new java.util.Random(1));
            Instances train = new Instances(data,0,trainSetSize);
            Instances test = new Instances(data,trainSetSize,testSetSize);

            svm.buildClassifier(train);
            ((SMO) svm).setKernel(kernelType);
            ((SMO) svm).setC(c);
            ((SMO) svm).setEpsilon(epsilon);
            for(int i = 0; i<data.numInstances(); i++){
                eval = new Evaluation(train);
                eval.evaluateModel(svm,test);

            }

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void kNearestNeighbor_with_nFold(int n,int k, String method){

        DistanceFunction distanceMeasure = new EuclideanDistance();
        if(method.equals("Chebyshev Distance")){
            distanceMeasure = new ChebyshevDistance();
        }
        else if(method.equals("Manhattan Distance")){
            distanceMeasure = new ManhattanDistance();
        }
        else if(method.equals("Minkowski Distance")){
            distanceMeasure = new MinkowskiDistance();
        }


        try {

            Classifier knn = new IBk();

            DataPreparation2();

            knn.buildClassifier(data);
            ((IBk) knn).setKNN(k);
            ((IBk) knn).getNearestNeighbourSearchAlgorithm().setDistanceFunction(distanceMeasure);
            eval = new Evaluation(data);

            eval.crossValidateModel(knn,data,n, new Random(1));

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void kNearestNeighbor_with_percentageSplit(int percentage,int k, String method){
        DistanceFunction distanceMeasure = new EuclideanDistance();
        if(method.equals("Chebyshev Distance")){
            distanceMeasure = new ChebyshevDistance();
        }
        else if(method.equals("Manhattan Distance")){
            distanceMeasure = new ManhattanDistance();
        }
        else if(method.equals("Minkowski Distance")){
            distanceMeasure = new MinkowskiDistance();
        }

        try {


            Classifier knn = new IBk();

            DataPreparation2();

            int trainSetSize = data.numInstances()*percentage/100;
            int testSetSize = data.numInstances() - trainSetSize;

            data.randomize(new java.util.Random(1));
            Instances train = new Instances(data,0,trainSetSize);
            Instances test = new Instances(data,trainSetSize,testSetSize);

            knn.buildClassifier(train);
            ((IBk) knn).setKNN(k);
            ((IBk) knn).getNearestNeighbourSearchAlgorithm().setDistanceFunction(distanceMeasure);
            for(int i = 0; i<data.numInstances(); i++){
                eval = new Evaluation(train);
                eval.evaluateModel(knn,test);

            }

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void randomForest_with_nFold(int n,int max_depth, int num_trees){
        try {



            Classifier rf = new RandomForest();

            DataPreparation();

            rf.buildClassifier(data);
            ((RandomForest) rf).setMaxDepth(max_depth);
            ((RandomForest) rf).setNumTrees(num_trees);

            eval = new Evaluation(data);

            eval.crossValidateModel(rf,data,n, new Random(1));

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void randomForest_with_percentageSplit(int percentage,int max_depth, int num_trees){
        try {


            Classifier rf = new RandomForest();


            DataPreparation();

            int trainSetSize = data.numInstances()*percentage/100;
            int testSetSize = data.numInstances() - trainSetSize;

            data.randomize(new java.util.Random(1));
            Instances train = new Instances(data,0,trainSetSize);
            Instances test = new Instances(data,trainSetSize,testSetSize);

            rf.buildClassifier(train);
            ((RandomForest) rf).setMaxDepth(max_depth);
            ((RandomForest) rf).setNumTrees(num_trees);
            for(int i = 0; i<data.numInstances(); i++){
                eval = new Evaluation(train);
                eval.evaluateModel(rf,test);

            }

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void neuralNetwork_with_nFold(int n,double learning_rate, double momentum, int seed){


        try {



            Classifier nn = new MultilayerPerceptron();

            DataPreparation3();

            nn.buildClassifier(data);

            ((MultilayerPerceptron) nn).setSeed(seed);
            ((MultilayerPerceptron) nn).setMomentum(momentum);
            ((MultilayerPerceptron) nn).setLearningRate(learning_rate);
            eval = new Evaluation(data);

            eval.crossValidateModel(nn,data,n, new Random(1));

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();

            detailsPrint(eval);


            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    void neuralNetwork_with_percentageSplit(int percentage,double learning_rate, double momentum, int seed){
        try {


            Classifier nn = new MultilayerPerceptron();

            DataPreparation3();

            int trainSetSize = data.numInstances()*percentage/100;
            int testSetSize = data.numInstances() - trainSetSize;

            data.randomize(new java.util.Random(1));
            Instances train = new Instances(data,0,trainSetSize);
            Instances test = new Instances(data,trainSetSize,testSetSize);

            nn.buildClassifier(train);

            ((MultilayerPerceptron) nn).setSeed(seed);
            ((MultilayerPerceptron) nn).setMomentum(momentum);
            ((MultilayerPerceptron) nn).setLearningRate(learning_rate);
            for(int i = 0; i<data.numInstances(); i++){
                eval = new Evaluation(train);
                eval.evaluateModel(nn,test);

            }

            matrix = eval.toMatrixString();
            summary = eval.toClassDetailsString();


            detailsPrint(eval);

            Toast.makeText(getApplicationContext(),"Completed successfuly",Toast.LENGTH_SHORT).show();
            buttonShowResults.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Process can not be completed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("DataMiningApp");
                builder1.setMessage("DataMiningApp is a weka-based dataset analyze application for android mobile devices."+"\r\n\n"+
                        "This application was developed for project of Introduction to Data Mining lecture on Dokuz Eylül University Computer Engineering Department by Enes Bildiren, Ali Gür and Burak Erdal in 2018.");
                AlertDialog alert1 = builder1.create();
                alert1.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                alert1.show();
                break;
            case R.id.tutorial:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("How to Use?");
                builder2.setMessage("1.) Select .arff file."+"\r\n"+
                                    "2.) Select algorithm."+"\r\n"+
                                    "3.) Pick nFold or percentage split and you can change parameters about that algorithm if you want."+"\r\n"+
                                    "4.) Run and show details and confusion matrix in new page.");
                AlertDialog alert2 = builder2.create();
                alert2.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                alert2.show();
                break;
            default:
                break;
        }

        return true;
    }
    void detailsPrint(Evaluation eval){

        String correct = String.valueOf(eval.correct());
        String incorrect = String.valueOf(eval.incorrect());
        String kappa = String.format("%.3f", eval.kappa());
        String meanAbsoluteError = String.format("%.3f", eval.meanAbsoluteError());
        String rootMeanSquaredError = String.format("%.3f", eval.rootMeanSquaredError());
        String relativeAbsoluteError = null;
        try {
            relativeAbsoluteError = String.format("%.3f", eval.relativeAbsoluteError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String rootRelativeSquaredError = String.format("%.3f", eval.rootRelativeSquaredError());
        String coverageOfTestCasesByPredictedRegions = String.format("%.3f", eval.coverageOfTestCasesByPredictedRegions());
        String sizeOfPredictedRegions = String.format("%.3f", eval.sizeOfPredictedRegions());
        String numInstances = String.valueOf(eval.numInstances());

        double correctPercentage = eval.correct()/eval.numInstances();
        String cP = String.format("%.2f", correctPercentage);
        double incorrectPercentage = eval.incorrect()/eval.numInstances();
        String icP = String.format("%.2f", incorrectPercentage);

        tvB2.setText(correct+" ("+cP.substring(2)+"%)");
        tvB4.setText(incorrect+" ("+icP.substring(2)+"%)");
        tvB6.setText(kappa);
        tvB8.setText(meanAbsoluteError);
        tvB10.setText(rootMeanSquaredError);
        tvB12.setText(relativeAbsoluteError+" %");
        tvB14.setText(rootRelativeSquaredError+" %");
        tvB16.setText(coverageOfTestCasesByPredictedRegions+" %");
        tvB18.setText(sizeOfPredictedRegions+" %");
        tvB20.setText(numInstances);

    }
    void DataPreparation() throws Exception {
        Filter useless = new RemoveUseless();
        useless.setInputFormat(data);                        // gereksizleri drop et
        data = weka.filters.Filter.useFilter(data, useless);

        Filter rmvothers = new RemoveType();
        rmvothers.setInputFormat(data);                        // numeric nominal dışındakileri drop et
        data = weka.filters.Filter.useFilter(data, rmvothers);

        Filter rplmissing = new ReplaceMissingValues();
        rplmissing.setInputFormat(data);
        data = weka.filters.Filter.useFilter(data, rplmissing);

        Filter myDiscretize = new NumericToNominal();
        myDiscretize.setInputFormat(data);					// numeric degerler için Discretize islemi
        data = weka.filters.Filter.useFilter(data, myDiscretize);

    }
    void DataPreparation2() throws Exception {
        Filter useless = new RemoveUseless();
        useless.setInputFormat(data);                        // gereksizleri drop et
        data = weka.filters.Filter.useFilter(data, useless);

        Filter rmvothers = new RemoveType();
        rmvothers.setInputFormat(data);                        // numeric nominal dışındakileri drop et
        data = weka.filters.Filter.useFilter(data, rmvothers);

        Filter rplmissing = new ReplaceMissingValues();
        rplmissing.setInputFormat(data);
        data = weka.filters.Filter.useFilter(data, rplmissing);

        Filter nominalToNumeric = new NominalToBinary();
        nominalToNumeric.setInputFormat(data);
        data = weka.filters.Filter.useFilter(data, nominalToNumeric);

    }
    void DataPreparation3() throws Exception {
        Filter useless = new RemoveUseless();
        useless.setInputFormat(data);                        // gereksizleri drop et
        data = weka.filters.Filter.useFilter(data, useless);

        Filter rmvothers = new RemoveType();
        rmvothers.setInputFormat(data);                        // numeric nominal dışındakileri drop et
        data = weka.filters.Filter.useFilter(data, rmvothers);

        Filter rplmissing = new ReplaceMissingValues();
        rplmissing.setInputFormat(data);
        data = weka.filters.Filter.useFilter(data, rplmissing);

        Filter nominalToNumeric = new NominalToBinary();
        nominalToNumeric.setInputFormat(data);
        data = weka.filters.Filter.useFilter(data, nominalToNumeric);

        Filter Normalize = new Normalize();
        Normalize.setInputFormat(data);
        data = Filter.useFilter(data, Normalize);


    }
    void parametersResetColor(){
        knnParametersTV.setTextColor(getResources().getColor(R.color.background));
        svmParametersTV.setTextColor(getResources().getColor(R.color.background));
        randomForestParametersTV.setTextColor(getResources().getColor(R.color.background));
        neuralNetworkParametersTV.setTextColor(getResources().getColor(R.color.background));
        decisionTreeParametersTV.setTextColor(getResources().getColor(R.color.background));
    }
    void parametersEnable(){
        knn.setEnabled(false);
        spinnerknn.setEnabled(false);
        c_text.setEnabled(false);
        epsilon_text.setEnabled(false);
        spinnersvm.setEnabled(false);
        confidence_text.setEnabled(false);
        dt_seed_text.setEnabled(false);
        learning_rate_text.setEnabled(false);
        momentum_text.setEnabled(false);
        nn_seed_text.setEnabled(false);
        max_depth_text.setEnabled(false);
        num_trees_text.setEnabled(false);
    }
    void paramVisible(){
        nFoldCrossValidation.setVisibility(View.VISIBLE);
        percentageSplit.setVisibility(View.VISIBLE);
        rg.setVisibility(View.VISIBLE);
    }
}