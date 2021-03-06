package com.example.trilateration;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Trilateration extends AppCompatActivity {
    private static final String TAG = "Trilateration";
    
    TextView txtWifiName1,txtWifiName2,txtWifiName3;
    EditText txtWifi1X,txtWifi2X,txtWifi3X,txtWifi1Y,txtWifi2Y,txtWifi3Y;
    Button btnHesapla;
    List<String> selectedResults;
    double[][] mPositions;
    double[] mDistances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trilateration);

        txtWifiName1 = findViewById(R.id.txtWifiName1);
        txtWifiName2 = findViewById(R.id.txtWifiName2);
        txtWifiName3 = findViewById(R.id.txtWifiName3);
        btnHesapla =findViewById(R.id.btnHesapla);

        txtWifi1X = findViewById(R.id.txtWifi1X);
        txtWifi2X = findViewById(R.id.txtWifi2X);
        txtWifi3X = findViewById(R.id.txtWifi3X);
        txtWifi1Y = findViewById(R.id.txtWifi1Y);
        txtWifi2Y = findViewById(R.id.txtWifi2Y);
        txtWifi3Y = findViewById(R.id.txtWifi3Y);

        selectedResults =  new ArrayList<>();
        mDistances = new double[3];
        mPositions = new double[3][2];

        Intent intent = getIntent();
        selectedResults = (List<String>) intent.getStringArrayListExtra("wifi");
        //Toast.makeText(this, "" + selectedResults.get(0).SSID, Toast.LENGTH_SHORT).show();
        String wifi1 = selectedResults.get(0);
        String wifi2 = selectedResults.get(1);
        String wifi3 = selectedResults.get(2);
        StringTokenizer tokens1 = new StringTokenizer(wifi1,"|");
        StringTokenizer tokens2 = new StringTokenizer(wifi2,"|");
        StringTokenizer tokens3 = new StringTokenizer(wifi3,"|");

        // Set Wifi Name TextViews
        txtWifiName1.setText(tokens1.nextToken());
        txtWifiName2.setText(tokens2.nextToken());
        txtWifiName3.setText(tokens3.nextToken());

        Log.d(TAG, "onCreate: "+ selectedResults.size());

       for(int i = 0; i< selectedResults.size(); i++){
           if (i == 0){
               double signalLevel = Double.valueOf(tokens1.nextToken());
               double signalFrequency = Double.valueOf(tokens1.nextToken());
               double distance = calculateDistance(signalLevel,signalFrequency);
               mDistances[i] = distance;
           }
           else if (i == 1){
               double signalLevel = Double.valueOf(tokens2.nextToken());
               double signalFrequency = Double.valueOf(tokens2.nextToken());
               double distance = calculateDistance(signalLevel,signalFrequency);
               mDistances[i] = distance;
           }else {
               double signalLevel = Double.valueOf(tokens3.nextToken());
               double signalFrequency = Double.valueOf(tokens3.nextToken());
               double distance = calculateDistance(signalLevel,signalFrequency);
               mDistances[i] = distance;

           }

        }



        //double[][] positions = new double[][] { { 2.0, 1.0 }, { 5.0, 4.0 }, { 8.0, 2.0 }};
        //double[] distances = new double[] { 3.16, 2, 3};

        final double[] distances = mDistances;



        btnHesapla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (    txtWifi1X.getText().toString().trim().isEmpty() ||
                        txtWifi1Y.getText().toString().trim().isEmpty() ||
                        txtWifi2X.getText().toString().trim().isEmpty() ||
                        txtWifi2Y.getText().toString().trim().isEmpty() ||
                        txtWifi3X.getText().toString().trim().isEmpty() ||
                        txtWifi3Y.getText().toString().trim().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Lütfen Tüm Alanları Doğru Şekilde Doldurunuz !", Toast.LENGTH_LONG).show();
                }
                else{

                    mPositions[0][0] = Double.valueOf(txtWifi1X.getText().toString());
                    mPositions[0][1] = Double.valueOf(txtWifi1Y.getText().toString());
                    mPositions[1][0] = Double.valueOf(txtWifi2X.getText().toString());
                    mPositions[1][1] = Double.valueOf(txtWifi2Y.getText().toString());
                    mPositions[2][0] = Double.valueOf(txtWifi3X.getText().toString());
                    mPositions[2][1] = Double.valueOf(txtWifi3Y.getText().toString());

                    double[][] positions = mPositions;

                    NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
                    LeastSquaresOptimizer.Optimum optimum = solver.solve();

                    // the answer
                    double[] centroid = optimum.getPoint().toArray();
                    DecimalFormat df = new DecimalFormat("#.##");


                    new TTFancyGifDialog.Builder(Trilateration.this)
                            .setTitle("Your Location")
                            .setMessage("X: "+ df.format(centroid[0])+"  Y: "+df.format(centroid[1]))
                            .setPositiveBtnText("OK")
                            .setPositiveBtnBackground("#22b573")
                            .setGifResource(R.drawable.location)      //pass your gif, png or jpg
                            .isCancellable(true)
                            .OnPositiveClicked(new TTFancyGifDialogListener() {
                                @Override
                                public void OnClick() {
                                }
                            })
                            .build();





                }
            }
        });

    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }
}
