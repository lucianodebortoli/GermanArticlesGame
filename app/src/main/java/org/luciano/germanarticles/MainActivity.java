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
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
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
    private TextView levelTV;
    private TextView correctTV;
    private TextView translationTV;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ProgressBar scoreBar;
    private Button submitButton;
    private Button derButton, dieButton, dasButton;
    private Spinner dropdown;
    private Switch languageSwitch;
    private String dataPath = "data.csv";
    private String LOG_TAG = "article_log";
    private List<Entry> entries= new ArrayList<>();
    private int[] levels;
    private int[] ansPerLevels;
    private int currentLevel;
    private int currentAnsPerLevel;
    private int currentCorrectAnsPerLevel;
    private String currentAns;
    private Entry currentEntry;
    private String articleSelected;
    private int  score = 0;
    private int answers = 0;
    private int correctAnswers = 0;
    private String language = "en";


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
        //initSpinner();
        initSubmitButton();
        initLevels();
        initSwitch();
    }

    private void initLevels() {
        levels = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        ansPerLevels = new int[] {3, 5, 8, 11, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584};
        currentLevel = levels[0];
        currentAnsPerLevel = ansPerLevels[0];
        currentCorrectAnsPerLevel = 0;
    }


    private void initSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonClicked();
            }
        });

        derButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentAns = "Der";
                buttonClicked();
            }
        });

        dasButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentAns = "Das";
                buttonClicked();
            }
        });

        dieButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentAns = "Die";
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
        levelTV = findViewById(R.id.levelTV);
        correctTV = findViewById(R.id.correctTV);
        progressBar = findViewById(R.id.progressBar);
        scoreBar = findViewById(R.id.scoreBar);
        derButton = findViewById(R.id.derButton);
        dasButton = findViewById(R.id.dasButton);
        dieButton = findViewById(R.id.dieButton);
        translationTV = findViewById(R.id.translationTV);
        languageSwitch = findViewById(R.id.languageSwitch);
    }

    private void initSwitch() {
        languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)  {
                if (isChecked)
                    language = "es";
                else
                    language = "en";
                refreshUI();
            }
        });
    }

    private void refreshUI() {
        nounTV.setText(currentEntry.getNoun());
        String word = null;
        if (language.equals("en")) { word = currentEntry.getTranslation(); }
        else if (language.equals("es")) { word = currentEntry.getTraduccion(); }
        translationTV.setText("("+ word +")");
    }

    private void startApp() {
        loadData();
        showNextEntry();
    }

    private void loadData()  {
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
                entry.setID(tokens[Entry.columns.COLUMN_ID.ordinal()]);
                entry.setArticle(tokens[Entry.columns.COLUMN_ARTICLE.ordinal()]);
                entry.setNoun(tokens[Entry.columns.COLUMN_NOUN.ordinal()]);
                entry.setTranslation(tokens[Entry.columns.COLUMN_TRANSLATION.ordinal()]);
                entry.setTraduccion(tokens[Entry.columns.COLUMN_TRADUCCION.ordinal()]);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error reading Line");
        }
    }

    private void showNextEntry() {
        currentEntry = getRandomEntry();
        refreshUI();
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
        //if (articleSelected.equals("Select Article")){userMessageTV.setText("Please select article from list");}
        validateChoice();
        updateScore();
        updateLevel();
        showNextEntry();
    }

    private void initActionButton() {
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Unavailable", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void updateScore() {
        score =  Math.round(100*(float) correctAnswers / answers) ;
        scoreTV.setText("Score "+score+" %");
        scoreBar.setProgress(score);
        //scoreBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorScore),android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void updateLevel() {
        if (currentCorrectAnsPerLevel>=currentAnsPerLevel)
            nextLevel();
        String levelText = "Level "+currentLevel+" ("+currentCorrectAnsPerLevel+"/"+currentAnsPerLevel+")";
        levelTV.setText(levelText);
        float levelProgress = 100*(float)currentCorrectAnsPerLevel/currentAnsPerLevel;
        progressBar.setProgress((int)levelProgress);
    }

    @SuppressLint("ShowToast")
    private void nextLevel() {
        currentLevel += 1;
        currentAnsPerLevel = ansPerLevels[currentLevel-1];
        currentCorrectAnsPerLevel = 0;
        Toast.makeText(getApplicationContext(),"Leveled Up to Lv"+currentLevel, Toast.LENGTH_LONG);
    }

    private Entry getRandomEntry() {
        Random rand = new Random();
        return entries.get(rand.nextInt(entries.size()));
    }

    @SuppressLint("ResourceAsColor")
    private void validateChoice() {
        answers += 1;
        if (currentAns.equals(currentEntry.getArticle())) {
            incrementCounters();
            showCorrectMsg();
        }
        else {
            shotIncorrectMsg();
            resetCounters();
        }
    }

    private void resetCounters() {
        currentCorrectAnsPerLevel = 0;
    }

    private void incrementCounters() {
        correctAnswers +=1;
        currentCorrectAnsPerLevel +=1;
    }

    private void shotIncorrectMsg() {
        correctTV.setText("Incorrect");
        correctTV.setTextColor(getResources().getColor(R.color.colorIncorrect));
        String msg = "Answer was " + currentEntry.getArticle() + " " + currentEntry.getNoun();
        userMessageTV.setText(msg);
    }

    private void showCorrectMsg() {
        correctTV.setText("Correct");
        correctTV.setTextColor(getResources().getColor(R.color.colorCorrect));
        String msg = "Answer was " + currentEntry.getArticle() + " " + currentEntry.getNoun();
        userMessageTV.setText(msg);
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