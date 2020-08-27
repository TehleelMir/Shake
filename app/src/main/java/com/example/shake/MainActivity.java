package com.example.shake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DateType> {

    private TextView textViewMag, textViewPlace, textViewTime;
    private LoaderManager l;
    private EditText editText;
    private String sharedPrefrenceFileName = "fileForMagValue", magValue = "mag";
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private boolean flagForToast=false;
    private double lastQuake;
    private String lastPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMag = findViewById(R.id.mag_text_view);
        textViewPlace = findViewById(R.id.place_text_view);
        textViewTime = findViewById(R.id.time);
        editText = findViewById(R.id.editText);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTheUiForRefresh();
               // getSupportLoaderManager().initLoader(0, null, MainActivity.this);
               // l.initLoader(0,null,MainActivity.this);
                l.restartLoader(0, null, MainActivity.this);
            }
        };
        sharedPreferences = getSharedPreferences(sharedPrefrenceFileName, MODE_PRIVATE);
        editText.setText(sharedPreferences.getString(magValue, ""));

        Button shake = findViewById(R.id.shake_button);
        shake.setOnClickListener(listener);
        l = getSupportLoaderManager();
        l.initLoader(0, null, this);

       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_adjust_24);// set drawable icon
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        progressBar = findViewById(R.id.progress_circular);

    }

    @NonNull
    @Override
    public Loader<DateType> onCreateLoader(int id, @Nullable Bundle args) {
        double mag = -1.0;
        if (!editText.getText().toString().isEmpty() || !editText.getText().toString().equals("")) {
            try {
                mag = Double.parseDouble(editText.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "Enter valid input", Toast.LENGTH_SHORT).show();
            }
        }
        return new loaderClass(this, mag);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<DateType> loader, DateType data) {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        updateTheUi(data);
        loader.stopLoading();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<DateType> loader) {
    }

    private void updateTheUi(DateType data) {
        progressBar.setVisibility(View.GONE);
        if (data == null) {
            Toast.makeText(this, "No case of " + editText.getText().toString() + " Magnitude", Toast.LENGTH_SHORT).show();
            return;
        }
        String place = data.getPlace();
        if (place.contains("of")) {
            String temp[] = place.split("of");
            place = temp[1];
        } else
            place = data.getPlace();
        String time;
        Date date = new Date(data.getTime());
        SimpleDateFormat format = new SimpleDateFormat("LLL dd, yyyy");
        time = format.format(date);
        format = new SimpleDateFormat("h:mm a");
        time += " " + format.format(date);
        textViewMag.setText(data.getMag() + "");
        textViewTime.setText(time);
        textViewPlace.setText(place);

        GradientDrawable sd=(GradientDrawable) textViewMag.getBackground().mutate();
        sd.setColor(getColorForMag(data.getMag()));
        sd.invalidateSelf();
        if(flagForToast==false){
            flagForToast=true;
            lastQuake=data.getMag();
            lastPlace=data.getPlace();
        }
        else{
            if(lastQuake == data.getMag() && lastPlace.equals(data.getPlace()))
                Toast.makeText(this, "No new case", Toast.LENGTH_SHORT).show();
            else{
                lastPlace=data.getPlace();
                lastQuake=data.getMag();
            }
        }

    }

    public int getColorForMag(double mag){
        int color;
        int mag_color=(int) Math.floor(mag);
        switch (mag_color){
            case 0:
            case 1: color=R.color.magnitude1;
            break;
            case 2:
                color=R.color.magnitude2;
                break;
            case 3:
                color=R.color.magnitude3;
                break;
            case 4:
                color=R.color.magnitude4;
                break;
            case 5:
                color=R.color.magnitude5;
                break;
            case 6:
                color=R.color.magnitude6;
                break;
            case 7:
                color=R.color.magnitude7;
                break;
            case 8:
                color=R.color.magnitude8;
                break;
            case 9:
                color=R.color.magnitude9;
                break;
            case 10:
                color=R.color.magnitude10plus;
                break;
            default:
                color=R.color.magnitude10plus;

        }
        return ContextCompat.getColor(this, color);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(magValue, editText.getText().toString());
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.aboutSection) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateTheUiForRefresh(){
        textViewMag.setText("");
        textViewPlace.setText("");
        textViewTime.setText("");
        progressBar.setVisibility(View.VISIBLE);
    }
}