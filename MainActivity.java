package com.simongiesen.makerapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etiennelawlor.discreteslider.library.ui.DiscreteSlider;
import com.etiennelawlor.discreteslider.library.utilities.DisplayUtility;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.util.Locale;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

/**
 * Created by simongiesen on 30.10.16.
 */

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener{
    private PulsatorLayout pulsator;
    private TextView konstantin;
    private FloatingActionButton fab;
    private EditText inputSolution;
    private int zahl;
    private int ergebnis;
    private String ausgabe;
    private double solution;
    private String mathQuestion;
    private DiscreteSlider discreteSlider;
    private RelativeLayout tickMarkLabelsRelativeLayout;
    private TextToSpeech textToSpeech;
    private int grade;
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.GERMAN);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
    @Override
    public void onUtteranceCompleted(String arg0) {
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setSubtitle(getResources().getString(R.string.vendorToolbar));
        textToSpeech = new TextToSpeech(this, this);
        zahl = 8;
        grade = 99;
        ausgabe="";
        fab = (FloatingActionButton) findViewById(R.id.fab);
        konstantin = (TextView) findViewById(R.id.konstantin);
        inputSolution = (EditText) findViewById(R.id.hiddenInput);
        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        discreteSlider = (DiscreteSlider) findViewById(R.id.discreteSlider);
        tickMarkLabelsRelativeLayout = (RelativeLayout) findViewById(R.id.tick_mark_labels_rl);
        fab.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        fab.setImageDrawable((new IconicsDrawable(fab.getContext(), GoogleMaterial.Icon.gmd_question_answer).color(Color.WHITE).sizeDp(26)));
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mathQuestions = "";
                Calculator c = new Calculator();
                String operand[] = "+,-,*,/".split(",");
                boolean isInteger;
                do {
                    do {
                        mathQuestion = c.randInt(0, grade) + " " + operand[c.randInt(0, 3)] + " " + c.randInt(0, grade);
                        solution = c.createMath(mathQuestion);
                        mathQuestion = mathQuestion + " = ";
                        isInteger = c.isInteger(solution);
                    } while (!isInteger);
                } while (solution < 0);
                mathQuestions = mathQuestions + mathQuestion + "\n";
                konstantin.setText(mathQuestions.replace("/", ":").replace("*", "x"));
                pulsator.start();
                inputSolution.setText("");
                inputSolution.requestFocus();
                inputSolution.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                String utteranceMath = mathQuestion;
                utteranceMath = utteranceMath.replace("*", "mal");
                utteranceMath = utteranceMath.replace("/", "geteilt durch");
                utteranceMath = utteranceMath.replace("-", "minus");
                textToSpeech.speak(utteranceMath + "?", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        inputSolution.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputSolution.getWindowToken(), 0);
                    try {
                        int mysolution = Integer.parseInt(inputSolution.getText().toString());
                        if (mysolution == (int) solution) {
                            Context context = getApplicationContext();
                            CharSequence text = "Richtig! " + mathQuestion + " " + (int) solution;
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            textToSpeech.speak("Richtig!", TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            Context context = getApplicationContext();
                            CharSequence text = "Falsch! " + mathQuestion + " " + (int) solution;
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            textToSpeech.speak("Leider falsch!", TextToSpeech.QUEUE_FLUSH, null);
                        }
                        return true;
                    } catch (NumberFormatException ex) {}
                }
                return false;
            }
        });
        inputSolution.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                konstantin.setText(mathQuestion + s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        discreteSlider.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                int childCount = tickMarkLabelsRelativeLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    TextView tv = (TextView) tickMarkLabelsRelativeLayout.getChildAt(i);
                    if (i == position)
                        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    else
                        tv.setTextColor(getResources().getColor(R.color.grey_400));
                }
                switch (position) {
                    case 0:
                        grade = 9;
                        break;
                    case 1:
                        grade = 10;
                        break;
                    case 2:
                        grade = 99;
                        break;
                    case 3:
                        grade = 499;
                        break;
                }
            }
        });
        tickMarkLabelsRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tickMarkLabelsRelativeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                addTickMarkTextLabels();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_info).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_info).color(Color.WHITE).actionBarSize());
        menu.findItem(R.id.action_settings).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_settings).color(Color.WHITE).actionBarSize());
        menu.findItem(R.id.action_share).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_share).color(Color.WHITE).actionBarSize());
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorPrimaryDark)
                        .setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_info).color(Color.WHITE).actionBarSize())
                        .setTitle(R.string.about)
                        .setMessage(getResources().getString(R.string.about_message) + "\n\nVersion " + BuildConfig.VERSION_NAME + ", Build " + BuildConfig.VERSION_CODE + "")
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void addTickMarkTextLabels() {
        int tickMarkCount = discreteSlider.getTickMarkCount();
        float tickMarkRadius = discreteSlider.getTickMarkRadius();
        int width = tickMarkLabelsRelativeLayout.getMeasuredWidth();
        int discreteSliderBackdropLeftMargin = DisplayUtility.dp2px(this, 28);
        int discreteSliderBackdropRightMargin = DisplayUtility.dp2px(this, 28);
        float firstTickMarkRadius = tickMarkRadius;
        float lastTickMarkRadius = tickMarkRadius;
        int interval = (width - (discreteSliderBackdropLeftMargin + discreteSliderBackdropRightMargin) - ((int) (firstTickMarkRadius + lastTickMarkRadius)))
                / (tickMarkCount - 1);
        String[] tickMarkLabels = {"Klasse 1", "2", "3", "4"};
        int tickMarkLabelWidth = DisplayUtility.dp2px(this, 54);
        for (int i = 0; i < tickMarkCount; i++) {
            TextView tv = new TextView(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    tickMarkLabelWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
            tv.setText(tickMarkLabels[i]);
            tv.setTextSize(13.0f);
            tv.setGravity(Gravity.CENTER);
            if (i == discreteSlider.getPosition())
                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            else
                tv.setTextColor(getResources().getColor(R.color.grey_400));
            int left = discreteSliderBackdropLeftMargin + (int) firstTickMarkRadius + (i * interval) - (tickMarkLabelWidth / 2);
            layoutParams.setMargins(left,
                    0,
                    0,
                    0);
            tv.setLayoutParams(layoutParams);
            tickMarkLabelsRelativeLayout.addView(tv);
        }
    }
}