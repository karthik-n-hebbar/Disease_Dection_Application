package com.example.diseases_dection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diseases_dection.ml.BrainTumor;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class tb extends Activity {

    private static final int GALLERY_REQUEST_CODE = 123;
    private ImageView imageView;
    private Button selectImageButton;
    private Button predictButton1;
    private TextView predictionTextView;
    private Bitmap selectedImage;
    private Interpreter tfliteInterpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tb);

        imageView = findViewById(R.id.inputImage);
        selectImageButton = findViewById(R.id.tb_select_img);
        predictButton1 = findViewById(R.id.tb_analyse);
        predictionTextView = findViewById(R.id.tb_test_result);

//        selectImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//            }
//        });
//
//        predictButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (selectedImage != null) {
//                // Perform TensorFlow model inference here
//                // Call a method to predict the disease based on the selectedImage bitmap
////                    String predictedDisease = performInference(selectedImage);
////                    predictionTextView.setText("Predicted Disease: " + predictedDisease);
//
//                selectedImage = Bitmap.createScaledBitmap(selectedImage, 224, 224, true);
//                String[] labels1 = new String[5];
//                int cnt = 0;
//                try {
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels1.txt")));
//                    String line = bufferedReader.readLine();
//                    while (line != null) {
//                        labels1[cnt] = line;
//                        cnt++;
//                        line = bufferedReader.readLine();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                TensorBuffer outputFeature0=null;
////                try {
//////                    BrainTumor model = BrainTumor.newInstance(brain_tumor.this);
////
////                    // Creates inputs for reference.
////                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
////                    TensorImage ti = new TensorImage(DataType.FLOAT32);
////                    ti.load(selectedImage);
////                    ByteBuffer byteBuffer = ti.getBuffer();
////                    inputFeature0.loadBuffer(byteBuffer);
////
////                    // Runs model inference and gets result.
//////                    BrainTumor.Outputs outputs = model.process(inputFeature0);
////
//////                    outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
////                    //predictionTextView.setText(outputFeature0.getFloatArray().toString());
////                    // Releases model resources if no longer used.
//////                    model.close();
//////                } catch (IOException e) {
//////                    // TODO Handle the exception
//////                    e.printStackTrace();
////                }
//
////                if (outputFeature0 != null) {
////                    // Convert the float array to a human-readable string
////                    StringBuilder resultBuilder = new StringBuilder("Predicted Values: ");
////                    float[] predictedValues = outputFeature0.getFloatArray();
////                    for (float value : predictedValues) {
////                        // Round the value to 2 decimal places
////                        String formattedValue = String.format("%.2f", value);
////                        resultBuilder.append(formattedValue).append(", ");
////                    }
////
////                    // Remove the trailing comma and space
////                    resultBuilder.setLength(resultBuilder.length() - 2);
////
////                    // Set the result in the predictionTextView
////                    predictionTextView.setText(resultBuilder.toString());
////                    predictionTextView.append(labels1[getMax(predictedValues)]);
////                } else {
////                    predictionTextView.setText("Prediction failed. Please check the model and inputs.");
////                }
////          }
////        });
////
////    }
////
////    private int getMax(float []arr){
////        int max = 0;
////        for (int i = 0; i < arr.length; i++) {
////            if (arr[i] > arr[max])
////                max = i;
////        }
////        return max;
////    }
//                private void openGallery () {
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setType("image/*");
//                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
//                }
//
//                @Override
//                protected void onActivityResult ( int requestCode, int resultCode, Intent data){
//                    super.onActivityResult(requestCode, resultCode, data);
//                    if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//                        // Get the selected image from the gallery and display it in the ImageView
//                        try {
//                            predictionTextView.setText("image Selected.");
//                            final Uri imageUri = data.getData();
//                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                            selectedImage = BitmapFactory.decodeStream(imageStream);
//                            imageView.setImageBitmap(selectedImage);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//
//        }
    }
}