package org.luciano.germanarticles;
import android.annotation.SuppressLint;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView nounTV;
    private TextView scoreTV;
    private TextView userMessageTV;
    private Toolbar toolbar;
    private Button submitButton;
    private Spinner dropdown;
    private String dataPath = "data.csv";
    private String LOG_TAG = "article_log";
    private List<Entry> entries= new ArrayList<>();
    private Entry currentEntry;
    private String articleSelected;
    private Float score = 1.0f;
    private int answers = 0;
    private int correctAnswers = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startUI();
        startApp();
    }


    private void startUI() {
        initActionButton();
        findGlobalViews();
        initSpinner();
        initSubmitButton();
    }


    private void initSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonClicked();
            }
        });
    }


    private void findGlobalViews() {
        nounTV = findViewById(R.id.nounTV);
        toolbar = findViewById(R.id.toolbar);
        scoreTV = findViewById(R.id.scoreTV);
        submitButton = findViewById(R.id.submit_button);
        userMessageTV = findViewById(R.id.user_message);
    }


    private void startApp() {
        loadData(dataPath);
        initQuery();
    }

    private void initQuery() {
        currentEntry = getRandomEntry();
        nounTV.setText(currentEntry.getNoun());
    }


    private void loadData(String dataPath)  {

        InputStream inputStream = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String line = "";
        try {
            String[] headers = reader.readLine().split(",");
            while ((line = reader.readLine()) != null )
            {
                String[] tokens = line.split(",");
                if (tokens.length != headers.length)
                    continue;
                Entry entry = new Entry();
                entry.setID(tokens[0]);
                entry.setArticle(tokens[1]);
                entry.setNoun(tokens[2]);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error reading Line");
        }
    }

    private void initSpinner() {
        // Spinner:
        dropdown = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,R.array.articles,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
    }

    @SuppressLint("ShowToast")
    private void buttonClicked() {
        if (articleSelected.equals("Select Article")){
            userMessageTV.setText("Please select article from list");
        }
        else {
            validateChoice();
            updateScore();
            showNext();
        }
    }


    private void initActionButton() {
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private void updateScore() {
        score =  (float) Math.round(correctAnswers / (float) answers * 100) / 100;
        scoreTV.setText((Math.round(100*score))+" %");
    }


    private void showNext() {
        // update noun
        currentEntry = getRandomEntry();
        nounTV.setText(currentEntry.getNoun());
    }

    private Entry getRandomEntry() {
        Random rand = new Random();
        return entries.get(rand.nextInt(entries.size()));
    }

    @SuppressLint("ResourceAsColor")
    private void validateChoice() {
        answers += 1;
        if (articleSelected.equals(currentEntry.getArticle())) {
            userMessageTV.setText("Correct");
            userMessageTV.setTextColor(getResources().getColor(R.color.colorCorrect));
            correctAnswers +=1;
        }
        else {
            String msg = "Incorrect. Answer was " + currentEntry.getArticle() + " " + currentEntry.getNoun();
            userMessageTV.setText(msg);
            userMessageTV.setTextColor(getResources().getColor(R.color.colorIncorrect));
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        articleSelected = adapterView.getItemAtPosition(i).toString();
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(LOG_TAG, "Nothing Selected");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}