package com.example.trilateration;

import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.List;

public class TestTrilateration extends AppCompatActivity {

    TextView txtWifiName1,txtWifiName2,txtWifiName3;
    EditText txtWifi1X,txtWifi2X,txtWifi3X,txtWifi1Y,txtWifi2Y,txtWifi3Y,txtWifi1R,txtWifi2R,txtWifi3R;
    Button btnHesapla;
    double[][] mPositions;
    double[] mDistances;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_trilateration);

        btnHesapla =findViewById(R.id.btnHesapla);

        txtWifi1X = findViewById(R.id.txtWifi1X);
        txtWifi2X = findViewById(R.id.txtWifi2X);
        txtWifi3X = findViewById(R.id.txtWifi3X);

        txtWifi1Y = findViewById(R.id.txtWifi1Y);
        txtWifi2Y = findViewById(R.id.txtWifi2Y);
        txtWifi3Y = findViewById(R.id.txtWifi3Y);

        txtWifi1R = findViewById(R.id.txtWifi1R);
        txtWifi2R = findViewById(R.id.txtWifi2R);
        txtWifi3R = findViewById(R.id.txtWifi3R);

        mDistances = new double[3];
        mPositions = new double[3][2];

        btnHesapla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (    txtWifi1X.getText().toString().trim().isEmpty() ||
                        txtWifi1Y.getText().toString().trim().isEmpty() ||
                        txtWifi2X.getText().toString().trim().isEmpty() ||
                        txtWifi2Y.getText().toString().trim().isEmpty() ||
                        txtWifi3X.getText().toString().trim().isEmpty() ||
                        txtWifi3Y.getText().toString().trim().isEmpty() ||
                        txtWifi3R.getText().toString().trim().isEmpty() ||
                        txtWifi3R.getText().toString().trim().isEmpty() ||
                        txtWifi3R.getText().toString().trim().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Lütfen Tüm Alanları Doğru Şekilde Doldurunuz !", Toast.LENGTH_LONG).show();
                }
                else{

                    mPositions[0][0] = Double.valueOf(txtWifi1X.getText().toString());
                    mPositions[0][1] = Double.valueOf(txtWifi1Y.getText().toString());
                    mPositions[1][0] = Double.valueOf(txtWifi2X.getText().toString());

                    mPositions[1][1] = Double.valueOf(txtWifi2Y.getText().toString());
                    mPositions[2][0] = Double.valueOf(txtWifi3X.getText().toString());
                    mPositions[2][1] = Double.valueOf(txtWifi3Y.getText().toString());

                    mDistances[0] = Double.valueOf(txtWifi1R.getText().toString());
                    mDistances[1] = Double.valueOf(txtWifi2R.getText().toString());
                    mDistances[2] = Double.valueOf(txtWifi3R.getText().toString());


                    double[][] positions = mPositions;
                    double[] distances = mDistances;

                    NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
                    LeastSquaresOptimizer.Optimum optimum = solver.solve();

                    // the answer
                    double[] centroid = optimum.getPoint().toArray();
                    DecimalFormat df = new DecimalFormat("#.##");


                    new TTFancyGifDialog.Builder(TestTrilateration.this)
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
}
