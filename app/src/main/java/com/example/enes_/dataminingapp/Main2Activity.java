package com.example.enes_.dataminingapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.enes_.dataminingapp.MainActivity.data;
import  static com.example.enes_.dataminingapp.MainActivity.eval;

import org.w3c.dom.Text;

import weka.classifiers.Evaluation;

public class Main2Activity extends AppCompatActivity {


    TextView textView6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // summary yani === Detailed Accuracy By Class === düzgün gösterilecek kaymalar giderilecek

        // matrix === Confisuon Matrix === deki kaymalar giderilecek

        String matrix = getIntent().getStringExtra("value1");
        String summary = getIntent().getStringExtra("value2");



        int numClasses = data.numClasses();

        String deneme ="=== Detailed Accuracy By Class ===\r\n\n";
        deneme += "                           TPRate FPRate Precision Recall F-Measure ROCArea ";
        deneme += "\r\n\n                             ";
        double total_tp = 0;
        double total_fp = 0;
        double total_pre = 0;
        double total_rec = 0;
        double total_fM = 0;
        double total_ROC = 0;

        for(int i=0;i<numClasses;i++){
            String tp = String.format("%.3f", eval.truePositiveRate(i));
            String fp = String.format("%.3f", eval.falsePositiveRate(i));
            String pre = String.format("%.3f", eval.precision(i));
            String rec = String.format("%.3f", eval.recall(i));
            String fM = String.format("%.3f", eval.fMeasure(i));
            String ROC = String.format("%.3f", eval.areaUnderROC(i));
            total_tp += eval.truePositiveRate(i);
            total_fp += eval.falsePositiveRate(i);
            total_pre += eval.precision(i);
            total_rec += eval.recall(i);
            total_fM += eval.fMeasure(i);
            total_ROC += eval.areaUnderROC(i);
            deneme += tp+"    "+fp+"      "+pre+"      "+rec+"     "+fM+"       "+ROC+"   ";
            deneme +="\r\n                             ";
        }
        deneme +="\r\n";
        String avg_tp = String.format("%.3f", total_tp/numClasses);
        String avg_fp = String.format("%.3f", total_fp/numClasses);
        String avg_pre = String.format("%.3f", total_pre/numClasses);
        String avg_rec = String.format("%.3f", total_rec/numClasses);
        String avg_fM = String.format("%.3f", total_fM/numClasses);
        String avg_ROC = String.format("%.3f", total_ROC/numClasses);
        deneme += "Weighted Avg. ";
        deneme += " "+avg_tp+"    "+avg_fp+"      "+avg_pre+"      "+avg_rec+"     "+avg_fM+"       "+avg_ROC+"   ";

        /*
        String a = "";
        String b = "";
        String[] words = summary.split("\\s");
        for (int i=0; i<words.length;i++){
            if(!words[i].equalsIgnoreCase("")){
                a+=words[i]+" "+"\r\n";
            }

        }
        int line = 1;
        int last_line = 0;
        int count = 0;
        String[] words2 = a.split("\r\n");
        for (int i=0; i<words2.length;i++){
            if(words2[i].equalsIgnoreCase("Weighted ")){
                last_line++;
                count=0;
            }
            if(count<6 && line==1 && last_line==0){
                b += words2[i];
            }
            if(count==6 && line==1 && last_line==0){
                b += "\r\n\n                           ";
                line++;
                count=0;
            }
            if(count<10 && line==2 && last_line==0){
                b += words2[i];
            }
            if(count==10 && line==2 && last_line==0){
                b += "\r\n                             ";
                line++;
                count=0;
            }
            if(count<7 && line>2 && last_line==0){

                Double num = 0.0;
                boolean numeric = true;

                try {
                    num = Double.parseDouble(words2[i]);
                } catch (NumberFormatException e) {
                    numeric = false;
                }

                if(numeric){
                    b += String.format("%.3f", num);
                    b += "      ";
                }
                else{
                    b += words2[i];
                }

            }
            if(count==7 && line>2 && last_line==0){
                b += "\r\n                             ";
                Double num = 0.0;
                boolean numeric = true;

                try {
                    num = Double.parseDouble(words2[i]);
                } catch (NumberFormatException e) {
                    numeric = false;
                }

                if(numeric){
                    b += String.format("%.3f", num);
                    b += "      ";
                }
                else{
                    b += words2[i];
                }
                line++;
                count=0;
            }
            if(last_line>0 && count==0){
                b+="\r\n";
                count++;
            }
            if(last_line>0 && count<9){
                Double num = 0.0;
                boolean numeric = true;

                try {
                    num = Double.parseDouble(words2[i]);
                } catch (NumberFormatException e) {
                    numeric = false;
                }

                if(numeric){
                    b += String.format("%.3f", num);
                    b += "      ";
                }
                else{
                    b += words2[i];
                }
            }
            count++;

        }

         */


        textView6 = (TextView) findViewById(R.id.textView6);
        textView6.setMovementMethod(new ScrollingMovementMethod());

        textView6.setText(deneme+"\r\n\n"+matrix);



    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
