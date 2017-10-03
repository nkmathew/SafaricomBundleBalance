package net.nkmathew.safaricombundlebalance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        startService(new Intent(this, ClipboardWatcherService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void deleteAllClips(View view) {
        Toast.makeText(this, "All stored clips have been deleted", Toast.LENGTH_LONG).show();
    }

}