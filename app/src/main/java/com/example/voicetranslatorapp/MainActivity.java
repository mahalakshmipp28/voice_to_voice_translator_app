package com.example.voicetranslatorapp;

import static com.example.voicetranslatorapp.R.*;
import java.util.*;
import java.util.Set;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.speech.RecognizerIntent;
//import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.speech.tts.TextToSpeech;
import androidx.annotation.Nullable;
//import android.widget.TextView;
//import android.widget.Toast;

import com.google.android.datatransport.runtime.dagger.multibindings.ElementsIntoSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.FirebaseApp;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
/*import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions.Builder;*/
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;
import kotlin.collections.ArrayDeque;

//import javax.xml.namespace.QName;

public class MainActivity extends AppCompatActivity {

    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView micIV;
    private MaterialButton translateBtn;
    private TextView translatedTV;



    String[] fromLanguages = {"From", "Afrikaans", "Albanian", "Belarusian", "Bengali", "Bulgarian", "Catalan", "Chinese", "Creole", "Croatian", "Czech", "Dutch", "English", "Esperanto", "Estonian", "French", "Galician", "Georgian", "German", "Greek", "Gujarati", "Haitian", "Hindi", "Hungarian", "Icelandic", "Indonesian", "Italian", "Japanese", "Kannada", "Korean", "Latvian", "Lithuanian", "Macedonian", "Malay", "Maltese", "Marathi", "Norwegian", "Polish", "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", "Spanish", "Swedish", "Tagalog", "Tamil", "Telugu", "Thai", "Turkish", "Ukrainian", "Vietnamese"};
    String[] ToLanguages = {"To", "Afrikaans", "Albanian", "Belarusian", "Bengali", "Bulgarian", "Catalan", "Chinese", "Creole", "Croatian", "Czech", "Dutch", "English", "Esperanto", "Estonian", "French", "Galician", "Georgian", "German", "Greek", "Gujarati", "Haitian", "Hindi", "Hungarian", "Icelandic", "Indonesian", "Italian", "Japanese", "Kannada", "Korean", "Latvian", "Lithuanian", "Macedonian", "Malay", "Maltese", "Marathi", "Norwegian", "Polish", "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", "Spanish", "Swedish", "Tagalog", "Tamil", "Telugu", "Thai", "Turkish", "Ukrainian", "Vietnamese"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    String languageCode = "";
    String fromLanguageCode = "";
    String TolanguageCode = "";
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        //FirebaseApp.initializeApp(this);
        t1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR)
                    t1.setLanguage(Locale.ENGLISH);
            }
        });
        fromSpinner = findViewById(id.idFromSpinner);
        toSpinner = findViewById(id.idToSpinner);
        sourceEdt = findViewById(id.idEdtSource);
        micIV = findViewById(id.idIVMic);
        translateBtn = findViewById(id.idBtnTranslate);
        translatedTV = findViewById(id.idTVTranslatedTV);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguages[i]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter fromAdaptor = new ArrayAdapter(this, layout.spinner_item, fromLanguages);
        fromAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdaptor);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TolanguageCode = getLanguageCode(ToLanguages[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this, layout.spinner_item, ToLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translatedTV.setText("");

                //translatorLatest("","",sourceEdt.getText().toString());

                if (sourceEdt.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode.equals("")) {
                    Toast.makeText(MainActivity.this, "Please select source language", Toast.LENGTH_SHORT).show();
                } else if (TolanguageCode.equals("")){
                    Toast.makeText(MainActivity.this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
                } else {
                    translatorLatest(fromLanguageCode, TolanguageCode, sourceEdt.getText().toString());
                }

            }
        });

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into text");
                try{
                    startActivityForResult(i, REQUEST_PERMISSION_CODE);
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));
            }
        }
    }
    /*
        private void translateText(int fromLanguageCode, int tolanguageCode, String source){
            translatedTV.setText("Downloading Model..");
            FirebaseTranslatorOptions options = new Builder().setSourceLanguage(fromLanguageCode).setTargetLanguage(tolanguageCode).build();
            FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
            translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    translatedTV.setText("Translating..");
                    translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            translatedTV.setText(s);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Fail to translate: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Fail to download language model: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        */
    public String getLanguageCode(String language){
        //int languageCode = 0;
        switch (language){
            case "Afrikaans":
                languageCode = TranslateLanguage.AFRIKAANS;
                break;
            case "Albanian":
                languageCode = TranslateLanguage.ALBANIAN;
                break;
            case "Belarusian":
                languageCode = TranslateLanguage.BELARUSIAN;
                break;
            case "Bengali":
                languageCode = TranslateLanguage.BENGALI;
                break;
            case "Bulgarian":
                languageCode = TranslateLanguage.BULGARIAN;
                break;
            case "Catalan":
                languageCode = TranslateLanguage.CATALAN;
                break;
            case "Chinese":
                languageCode = TranslateLanguage.CHINESE;
                break;
            case "Creole":
                languageCode = TranslateLanguage.CROATIAN;
                break;
            case "Czech":
                languageCode = TranslateLanguage.CZECH;
                break;
            case "Dutch":
                languageCode = TranslateLanguage.DUTCH;
                break;
            case "English":
                languageCode = TranslateLanguage.ENGLISH;
                break;
            case "Esperanto":
                languageCode = TranslateLanguage.ESPERANTO;
                break;
            case "Estonian":
                languageCode = TranslateLanguage.ESTONIAN;
                break;
            case "French":
                languageCode = TranslateLanguage.FRENCH;
                break;
            case "Galician":
                languageCode = TranslateLanguage.GALICIAN;
                break;
            case "Georgian":
                languageCode = TranslateLanguage.GEORGIAN;
                break;
            case "German":
                languageCode = TranslateLanguage.GERMAN;
                break;
            case "Greek":
                languageCode = TranslateLanguage.GREEK;
                break;
            case "Gujarati":
                languageCode = TranslateLanguage.GUJARATI;
                break;
            case "Haitian":
                languageCode = TranslateLanguage.HAITIAN_CREOLE;
                break;
            case "Hindi":
                languageCode = TranslateLanguage.HINDI;
                break;
            case "Hungarian":
                languageCode = TranslateLanguage.HUNGARIAN;
                break;
            case "Icelandic":
                languageCode = TranslateLanguage.ICELANDIC;
                break;
            case "Indonesian":
                languageCode = TranslateLanguage.INDONESIAN;
                break;
            case "Italian":
                languageCode = TranslateLanguage.ITALIAN;
                break;
            case "Japanese":
                languageCode = TranslateLanguage.JAPANESE;
                break;
            case "Kannada":
                languageCode = TranslateLanguage.KANNADA;
                break;
            case "Korean":
                languageCode = TranslateLanguage.KOREAN;
                break;
            case "Latvian":
                languageCode = TranslateLanguage.LATVIAN;
                break;
            case "Lithuanian":
                languageCode = TranslateLanguage.LITHUANIAN;
                break;
            case "Macedonic":
                languageCode = TranslateLanguage.MACEDONIAN;
                break;
            case "Malay":
                languageCode = TranslateLanguage.MALAY;
                break;
            case "Maltese":
                languageCode = TranslateLanguage.MALTESE;
                break;
            case "Marathi":
                languageCode = TranslateLanguage.MARATHI;
                break;
            case "Norwegian":
                languageCode = TranslateLanguage.NORWEGIAN;
                break;
            case "Polish":
                languageCode = TranslateLanguage.POLISH;
                break;
            case "Portuguese":
                languageCode = TranslateLanguage.PORTUGUESE;
                break;
            case "Romanian":
                languageCode = TranslateLanguage.ROMANIAN;
                break;
            case "Russian":
                languageCode = TranslateLanguage.RUSSIAN;
                break;
            case "Slovak":
                languageCode = TranslateLanguage.SLOVAK;
                break;
            case "Slovenian":
                languageCode = TranslateLanguage.SLOVENIAN;
                break;
            case "Spanish":
                languageCode = TranslateLanguage.SPANISH;
                break;
            case "Swedish":
                languageCode = TranslateLanguage.SWEDISH;
                break;
            case "Tagalog":
                languageCode = TranslateLanguage.TAGALOG;
                break;
            case "Tamil":
                languageCode = TranslateLanguage.TAMIL;
                break;
            case "Telugu":
                languageCode = TranslateLanguage.TELUGU;
                break;
            case "Thai":
                languageCode = TranslateLanguage.THAI;
                break;
            case "Turkish":
                languageCode = TranslateLanguage.TURKISH;
                break;
            case "Ukrainian":
                languageCode = TranslateLanguage.UKRAINIAN;
                break;
            case "Vietnamese":
                languageCode = TranslateLanguage.VIETNAMESE;
                break;
            /*case "Slovenian":
                languageCode = TranslateLanguage.SLOVENIAN;
                break;*/
            default:
                languageCode = "";

        }
        return languageCode;
    }


    public void translatorLatest(String fromLanguageCode, String tolanguageCode, String source) {
        translatedTV.setText("Downloading Model..");

        //if(fromLanguageCode.equals("ENGLISH")&&tolanguageCode.equals("HINDI"))
        TranslatorOptions options =
                new TranslatorOptions.Builder()

                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(tolanguageCode)
                        .build();
        final Translator englishGermanTranslator =
                Translation.getClient(options);

        englishGermanTranslator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translatedTV.setText("Translating..");
                englishGermanTranslator.translate(source)
                        .addOnSuccessListener(
                                new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(@NonNull String translatedText) {
                                        translatedTV.setText(translatedText);
                                        String text= translatedTV.getText().toString();
                                        t1.speak(text, TextToSpeech.QUEUE_FLUSH,null);
                                        //Toast.makeText(MainActivity.this, fromLanguageCode+" & "+ tolanguageCode, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Fail to translate: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to download language model: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}