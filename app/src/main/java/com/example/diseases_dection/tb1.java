package com.example.diseases_dection;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.ByteOrder;

import org.tensorflow.lite.Interpreter;

public class tb1 extends Activity {
    private static final String TAG = "tb";
    private static final int GALLERY_REQUEST_CODE = 123;
    private static final String MODEL_PATH = "model.tflite";
    private static final int NUM_CLASSES = 4;
    private static final int NUMBER_OF_CLASSES = 4;
    private Interpreter tflite;
    private ImageView imageView;
    private TextView resultTextView;
    private Button predictButton;
    private Button selectImageButton;
    private Bitmap selectedImage;
    private Bitmap bitmap;
    private Interpreter tfliteInterpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tb);
        imageView = findViewById(R.id.inputImage);

        resultTextView = findViewById(R.id.tb_test_result);
        predictButton = findViewById(R.id.tb_analyse);
        selectImageButton = findViewById(R.id.tb_select_img);
        // Initialize the TensorFlow Lite interpreter
        try {
            Interpreter.Options options = new Interpreter.Options();
            tfliteInterpreter = new Interpreter(loadModelFile(getAssets(), "model.tflite"), options);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    float[] predictedDisease = predictWithTFLite(selectedImage);
                    resultTextView.setText("Predicted Disease: " + predictedDisease);
                    gf(predictedDisease);
                } else {
                    resultTextView.setText("Please select an image first.");
                }
            }
        });

    }

//    private void gf(float[] predictedDisease)
//    {
//        if (predictedDisease != null) {
//            // Convert the float array to a human-readable string
//            StringBuilder resultBuilder = new StringBuilder("Predicted Values: ");
//            float[] predictedValues = predictedDisease;
//            for (float value : predictedValues) {
//                // Round the value to 2 decimal places
//                resultBuilder.append(predictedValues).append(", ");
//            }
//
//            // Remove the trailing comma and space
//            resultBuilder.setLength(resultBuilder.length() - 2);
//
//            // Set the result in the predictionTextView
//            resultTextView.setText(resultBuilder.toString());
//            resultTextView.append(" "+(predictedValues));
//        } else {
//            resultTextView.setText("Prediction failed. Please check the model and inputs.");
//        }
//    }
    private void gf(float[] predictedDisease) {
        if (predictedDisease != null) {
            // Convert the float array to a human-readable string
            float[] predictedValues = predictedDisease;
            StringBuilder resultBuilder = new StringBuilder("Predicted Values: ");
            for (float value : predictedValues) {
                // Round the value to 2 decimal places and append to the resultBuilder
                resultBuilder.append(predictedValues).append(", ");
            }

            // Remove the trailing comma and space
            resultBuilder.setLength(resultBuilder.length() - 2);

            // Set the result in the predictionTextView
            resultTextView.setText(resultBuilder.toString());
        } else {
            resultTextView.setText("Prediction failed. Please check the model and inputs.");
        }
    }

    private float[] predictWithTFLite(Bitmap bitmap) {
        // Load the model
        Interpreter tfliteInterpreter = null;
        try {
            Interpreter.Options options = new Interpreter.Options();
            tfliteInterpreter = new Interpreter(loadModelFile(getAssets(), "model.tflite"), options);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Preprocess the input image
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        ByteBuffer inputBuffer = preprocessInput(bitmap);

        // Allocate the output buffer to store the prediction results
        int outputSize = NUMBER_OF_CLASSES; // Replace NUMBER_OF_CLASSES with the actual number of output classes in your model
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(outputSize * 4); // 4 bytes for each float value

        // Run inference
        tfliteInterpreter.run(inputBuffer, outputBuffer);

        // Postprocess the output buffer to get the predicted values
        float[] predictions = postprocessOutput(outputBuffer);

        // Release model resources
        tfliteInterpreter.close();

        return predictions;
    }

    private MappedByteBuffer loadModelFile(AssetManager assets, String s) throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private ByteBuffer preprocessInput(Bitmap bitmap) {
        int BATCH_SIZE = 1;
        int PIXEL_SIZE = 3; // 3 channels (RGB)
        int INPUT_SIZE = 224; // Model input size (assuming square input)

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(BATCH_SIZE * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        // Convert the Bitmap to ByteBuffer in the appropriate format for the model
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];
                inputBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f); // Red channel
                inputBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);  // Green channel
                inputBuffer.putFloat((val & 0xFF) / 255.0f);         // Blue channel
            }
        }

        return inputBuffer;
    }

    private float[] postprocessOutput(ByteBuffer outputBuffer) {
        // Assuming the output buffer contains a sequence of floating-point values representing the predictions
        // Adjust the postprocessing logic based on the format of your model's output.

        int outputSize = outputBuffer.remaining() / 4; // 4 bytes for each float value
        float[] predictions = new float[outputSize];
        outputBuffer.asFloatBuffer().get(predictions);
        return predictions;
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
                resultTextView.setText("image Selected.");
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        if (tfliteInterpreter != null) {
            tfliteInterpreter.close();
        }
        super.onDestroy();
    }

}