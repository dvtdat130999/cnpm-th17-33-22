package com.example.pdfscanner.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.pdfscanner.Object.BottomNavigationViewBehavior;
import com.example.pdfscanner.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ScanActivity extends AppCompatActivity {

    String text_filename;
    EditText edtResult;
    ImageView imageView;
    Button btn_convert, btnSaveText, btnLoadText, btnClearText;
    ScrollView scrollView;
    BottomNavigationView bottomNavigationView;
    ShareActionProvider mShareActionProvider;
    TextView shareText, sharePDF, sign_btn;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];
    int prvX, prvY;
    Bitmap bitmapMaster;
    Canvas canvasMaster;
    Paint paintDraw;
    public static String targetPdf = "/sdcard/";


    boolean boolean_save = false;
    Bitmap bitmap;
    public static final int REQUEST_PERMISSIONS = 1;

    private static File file;
    private static Uri _imagefileUri;
    private static final String IMAGE_CAPTURE_FOLDER = "cmscanner";
    String _imageFileName;
    Uri image_uri;
    String filename;
    File filePath;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        edtResult = (EditText) findViewById(R.id.edtResult);

        imageView = (ImageView) findViewById(R.id.imgView);
        btn_convert = (Button) findViewById(R.id.btn_convert);
        btnSaveText = (Button) findViewById(R.id.btnSaveText);
        btnLoadText = (Button) findViewById(R.id.btnLoadText);
        btnClearText = (Button) findViewById(R.id.btnClearText);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        btnClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtResult.setText("");
            }
        });

        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolean_save) {
                    Intent intent1 = new Intent(getApplicationContext(), PDFViewActivity.class);
                    startActivity(intent1);

                } else {
                    displayAlertDialogFileName();
                }

            }
        });
        btnSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlertDialogFileNameSave();
            }
        });
        btnLoadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlertDialogFileNameLoad();
            }
        });
        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        addActionBottomNavigationView();
        addScrollEventForBottomNavigationBar();
        addShareEvent();
        addSign();
    }

    private void addShareEvent() {
        shareText = findViewById(R.id.shareText);
        sharePDF = findViewById(R.id.sharePDF);
        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtResult.getText().toString())) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, edtResult.getText().toString());
                    startActivity(Intent.createChooser(intent, "Share"));
                }
            }
        });
        sharePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() != null) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    long miliSeconds = System.currentTimeMillis();
                    Date date = new Date(miliSeconds);
                    filename = dateFormat.format(date);
                    createPdf();
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("application/pdf");
                    Intent intent1 = intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ScanActivity.this, getApplicationContext().getPackageName() + ".provider", filePath));
                    startActivity(Intent.createChooser(intent, "Share"));
                }
            }
        });
    }
    
    private void addSign() {
        sign_btn = findViewById(R.id.sign_btn);
        // config paint
        paintDraw = new Paint();
        paintDraw.setStyle(Paint.Style.FILL);
        paintDraw.setColor(Color.WHITE);
        paintDraw.setStrokeWidth(10);


        sign_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {

                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                prvX = x;
                                prvY = y;
                                drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                                prvX = x;
                                prvY = y;
                                break;
                            case MotionEvent.ACTION_UP:
                                drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                                break;
                        }
                        return true;
                    }
                });

            }
        });
    }

    private void drawOnProjectedBitMap(ImageView iv, Bitmap bm,
                                       float x0, float y0, float x, float y){
        if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
            //outside ImageView
            return;
        }else{

            float ratioWidth = (float)bm.getWidth()/(float)iv.getWidth();
            float ratioHeight = (float)bm.getHeight()/(float)iv.getHeight();

            canvasMaster.drawLine(
                    x0 * ratioWidth,
                    y0 * ratioHeight,
                    x * ratioWidth,
                    y * ratioHeight,
                    paintDraw);
            imageView.invalidate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addScrollEventForBottomNavigationBar() {
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY > 0 && bottomNavigationView.isShown()) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else if (oldScrollY < 0) {
                    bottomNavigationView.setVisibility(View.VISIBLE);

                }
            }

        });
    }

    private void addActionBottomNavigationView() {
        bottomNavigationView.setSelectedItemId(R.id.scanf);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.user:
                        intent = new Intent(ScanActivity.this, UserActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    //save and load text file
    public void saveTextFile() {
        String text = edtResult.getText().toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(text_filename + ".txt", MODE_PRIVATE);
            fos.write(text.getBytes());

            Toast.makeText(this, "Save to" + getFilesDir() +
                    "/" + text_filename + ".txt", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public void loadTextFile() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(text_filename + ".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");

            }

            edtResult.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //action bar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate menu
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    //handle action bar item

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.addImage) {
            showImageImportDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        //item to display dialog
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //set title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //camera option clicked

                    if (!checkCameraPermission()) {
                        //not allowed
                        requestCameraPermission();
                    } else {
                        //allowed
                        pickCamera();
                    }

                }
                if (i == 1) {
                    //gallery option clicked
                    if (!checkStoragePermission()) {
                        //not allowed
                        requestStoragePermission();

                    } else {
                        //allowed
                        pickGallery();
                    }

                }
            }
        });
        dialog.create().show();


    }

    private void pickGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);

        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);


    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);

    }

    //handle permission result

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }


    //handle image result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //get image from camera

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {


            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)//enable image guideline
                        .start(this);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)//enable image guideline
                        .start(this);

            }
        }
        //get croped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //button check pdf change to convert
                boolean_save = false;
                btn_convert.setText("Convert");
                Uri resultUri = result.getUri();//get image uri

                try {
                    Bitmap tempBitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(resultUri));

                    Bitmap.Config config;
                    if(tempBitmap.getConfig() != null){
                        config = tempBitmap.getConfig();
                    }else{
                        config = Bitmap.Config.ARGB_8888;
                    }
                    //bitmapMaster is Mutable bitmap
                    bitmapMaster = Bitmap.createBitmap(
                            tempBitmap.getWidth(),
                            tempBitmap.getHeight(),
                            config);

                    canvasMaster = new Canvas(bitmapMaster);
                    canvasMaster.drawBitmap(tempBitmap, 0, 0, null);

                    imageView.setImageBitmap(bitmapMaster);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //set image to text
//                imageView.setImageURI(resultUri);
                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    //get text from sb until there is no text
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    //set text to edit text
                    edtResult.setText(sb.toString());
                }


            } else {
                if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    //if there is any error show it
                    Exception error = result.getError();
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void createPdf() {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmapMaster.getWidth(), bitmapMaster.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);


        bitmapMaster = Bitmap.createScaledBitmap(bitmapMaster, bitmapMaster.getWidth(), bitmapMaster.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmapMaster, 0, 0, null);
        document.finishPage(page);


        // write the document content
        filePath = new File(targetPdf + filename + ".pdf");
        try {
            document.writeTo(new FileOutputStream(filePath));
            btn_convert.setText("Check PDF");
            boolean_save = true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }

    private File getFile() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath, IMAGE_CAPTURE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        return new File(file + File.separator + _imageFileName
                + ".jpg");
    }

    public void genRandom() {
        Random r = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        final int N = 10;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        _imageFileName = sb.toString();

    }

    public void displayAlertDialogFileName() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.activity_filename, null);
        final EditText edtFilename = (EditText) alertLayout.findViewById(R.id.edtFilename);


        AlertDialog.Builder alert = new AlertDialog.Builder(ScanActivity.this);
        alert.setTitle("Save PDF");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                filename = edtFilename.getText().toString();
                createPdf();

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void displayAlertDialogFileNameSave() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.activity_filename, null);
        final EditText edtFilename = (EditText) alertLayout.findViewById(R.id.edtFilename);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Save Text");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                text_filename = edtFilename.getText().toString();
                saveTextFile();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void displayAlertDialogFileNameLoad() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.activity_filename, null);
        final EditText edtFilename = (EditText) alertLayout.findViewById(R.id.edtFilename);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Load Text");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Open", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                text_filename = edtFilename.getText().toString();
                loadTextFile();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

}
