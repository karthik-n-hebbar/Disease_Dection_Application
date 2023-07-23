package com.example.diseases_dection;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diseases_dection.ml.BrainTumor;
import com.example.diseases_dection.ml.Model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class tb extends Activity {
    private static final String TAG = "tb";
    private static final int GALLERY_REQUEST_CODE = 123;
    private static final String MODEL_PATH = "model.tflite";
    private static final int NUM_CLASSES = 4;
    private Interpreter tflite;
    private ImageView imageView;
    private TextView resultTextView;
    private Button predictButton;
    private Button selectImageButton;
    private Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tb);
        imageView = findViewById(R.id.inputImage);

        resultTextView = findViewById(R.id.tb_test_result);
        predictButton = findViewById(R.id.tb_analyse);
        selectImageButton = findViewById(R.id.tb_select_img);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            Log.e(TAG, "Error loading the model.", e);
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
                // Run inference on the input data and get the predicted class index
                float[] input = new float[0]; // Replace with your input data
                try {
                    input = getInputData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                float[][] output = new float[1][NUM_CLASSES];
                tflite.run(input, output);

                int predictedClassIndex = argmax(output[0]);
                String predictedClassName = getClassName(predictedClassIndex);

                resultTextView.setText("Predicted class: " + predictedClassName);
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private int argmax(float[] array) {
        int best = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[best]) {
                best = i;
            }
        }
        return best;
    }

    private String getClassName(int classIndex) {
        // Implement a mapping between class indices and class names
        // For example, if your classes are ["ClassA", "ClassB", "ClassC", "ClassD"]
        // then you can return the corresponding class name based on the index.
        String[] classNames = {"ClassA", "ClassB", "ClassC", "ClassD"};
        return classNames[classIndex];
    }

    // Replace this with your method to get the input data for inference
    private float[] getInputData() throws IOException {
        // Your logic to get input data as a float array
        // For example, if your model requires a 128x128 image, you'll need to convert your image to a float array
        selectedImage = Bitmap.createScaledBitmap(selectedImage, 224, 224, true);

            Model model = Model.newInstance(tb.this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            TensorImage ti = new TensorImage(DataType.FLOAT32);
            ti.load(selectedImage);
            ByteBuffer byteBuffer = ti.getBuffer();
            inputFeature0.loadBuffer(byteBuffer);


            // Releases model resources if no longer used.
            model.close();

        return new float[0];
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

}