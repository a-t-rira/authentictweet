package com.example.authentictweet;


import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TweetActivity extends FragmentActivity
implements View.OnClickListener {

    private EditText mInputText;
    private Twitter mTwitter;
    private final static int WC=LinearLayout.LayoutParams.WRAP_CONTENT;
    private final static int MP=LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int REQUEST_CODE=0;
    private EditText editText;
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        mTwitter = TwitterUtils.getTwitterInstance(this);

        mInputText = (EditText) findViewById(R.id.input_text);
        


        findViewById(R.id.action_tweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });
        
        //レイアウトの作成
        LinearLayout layout=new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);  
        
        //エディットテキストの生成
        editText=new EditText(this);
        editText.setText("",EditText.BufferType.NORMAL);
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP,WC));
        layout.addView(editText);
        mInputText = editText;
        
      //ボタンの生成
       layout.addView(makeButton("音声認識","recog"));
       layout.addView(makeButton("ツイートする","activity_tweet"));      
    }

    //ツイートプログラム
    private void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    mTwitter.updateStatus(params[0]);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            //ツイート結果
            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    showToast("ツイートが完了しました。");
                    finish();
                } else {
                    showToast("ツイートに失敗しました。");
                }
            }
        };
        task.execute(mInputText.getText().toString());
    }
    //結果をToastで表示
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    
    //アクティビティ起動時に呼ばれる
    /*public void onClick(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }*/
    
	//ボタンの生成
    private Button makeButton(String text,String tag) {
        Button button=new Button(this);
        button.setText(text);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        return button;
    }

    //ボタンクリック時に呼ばれる
    public void onClick(View v) {
        try {
        	String tag = v.getTag().toString();
        	if(tag.equals("activity_tweet")){ // ツイートが押されたときの処理
                tweet();
                
        	}
        	else if(tag.equals("recog")){ // 音声認識押されたときの処理
                //音声認識の実行
                Intent intent=new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "端末のマイクに向かって話かけてください");
                startActivityForResult(intent,REQUEST_CODE);
        	}
        } catch (ActivityNotFoundException e) {
        }        
    }    
    
    //アクティビティ終了時に呼ばれる
    @Override
    protected void onActivityResult(int requestCode,
        int resultCode,Intent data) {
        //音声認識結果の取得
        if (requestCode==0 && resultCode==RESULT_OK) {
            ArrayList<String> results=
                data.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS);
            int i = 0;
        	 editText.setText(results.get(i));
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}
