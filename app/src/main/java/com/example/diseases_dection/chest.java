package com.example.diseases_dection;

import android.annotation.SuppressLint;
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

import com.example.diseases_dection.ml.ChestXray;

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

public class chest extends Activity {

    private static final int GALLERY_REQUEST_CODE = 123;
    private ImageView imageView;
    private Button selectImageButton;
    private Button predictButton;
    private TextView predictionTextView;
    private Bitmap selectedImage;
    private Interpreter tfliteInterpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chest);


        imageView = findViewById(R.id.inputImage);
        selectImageButton = findViewById(R.id.chest_select_img);
        predictButton = findViewById(R.id.chest_analyse);
        predictionTextView = findViewById(R.id.chest_test_result);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage != null) {
                    // Perform TensorFlow model inference here
                    // Call a method to predict the disease based on the selectedImage bitmap
                    String predictedDisease = performInference(selectedImage);
                    //predictionTextView.setText("Predicted Disease: " + predictedDisease);
                } else {
                    predictionTextView.setText("Please select an image first.");
                }
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected image from the gallery and display it in the ImageView
            try {
                predictionTextView.setText("image Selected.");
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private String performInference(Bitmap bitmap) {

        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        String[] labels = new String[3];
        int cnt = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line = bufferedReader.readLine();
            while (line != null) {
                labels[cnt] = line;
                cnt++;
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        TensorBuffer outputFeature0 = null;
        try {
            ChestXray model = ChestXray.newInstance(chest.this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            TensorImage ti = new TensorImage(DataType.FLOAT32);
            ti.load(bitmap);
            ByteBuffer byteBuffer = ti.getBuffer();
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ChestXray.Outputs outputs = model.process(inputFeature0);
            outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
        if (outputFeature0 != null) {
            // Convert the float array to a human-readable string
            StringBuilder resultBuilder = new StringBuilder("Predicted Values: ");
            float[] predictedValues = outputFeature0.getFloatArray();
            for (float value : predictedValues) {
                // Round the value to 2 decimal places
                String formattedValue = String.format("%.2f", value);
                resultBuilder.append(formattedValue).append(", ");
            }

            // Remove the trailing comma and space
            resultBuilder.setLength(resultBuilder.length() - 2);

            // Set the result in the predictionTextView
            predictionTextView.setText(resultBuilder.toString());
            predictionTextView.append(" "+labels[getMax(predictedValues)]);
        } else {
            predictionTextView.setText("Prediction failed. Please check the model and inputs.");
        }
        return labels[getMax(outputFeature0.getFloatArray())] + " ";
    }

    private int getMax(float []arr){
            int max = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] > arr[max]) max = i;
            }
            return max;
        }
    }

