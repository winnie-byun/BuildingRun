package com.example.samsung.team_a;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class loginActivity extends AppCompatActivity{
    EditText edtID, edtPass;
    Button btnLogin,btnFind,btnNewId;

    public static String STuser_id="";
    public static String STuser_pass="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtID = (EditText) findViewById(R.id.edtId);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnLogin=(Button) findViewById(R.id.btnLogin);


    } //onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void loginButtonClick(View v) {
        /*String userIdValue = edtID.getText().toString();
        String userPassValue = edtPass.getText().toString();
        String type ="login";

        loginBW loginbw = new loginBW(this);
        loginbw.execute(type,userIdValue,userPassValue);
*/
        Intent i = new Intent(loginActivity.this, MainActivity.class);
        loginActivity.this.startActivity(i);

    }

    public void newIDButtonClick(View v) {
        edtID.setText("");
        edtPass.setText("");
        Intent i = new Intent(loginActivity.this, newIdActivity.class);
        loginActivity.this.startActivity(i);
    }
    public void find_pwButtonClick(View v) {

        Intent i = new Intent(loginActivity.this, Find_pwActivity.class);
        loginActivity.this.startActivity(i);
    }

}

/*class loginBW extends AsyncTask<String, Void, String> {
    String type = "";
    Context context;
    AlertDialog alertdialog;
    public static String isitlogin="";

    loginBW(Context etx){
        context =etx;
    }

    public loginBW() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        type=params[0];
        String login_url="http://"+FirstActivity.connectIP+"/login.php";
        if(type.equals("login")){
            try {
                String user_id = params[1];
                String user_pass = params[2];
                loginActivity.STuser_id = user_id;
                loginActivity.STuser_pass = user_pass;

                URL url=new URL(login_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));
                String post_data = URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")
                        +"&"+URLEncoder.encode("user_pass","UTF-8")+"="+URLEncoder.encode(user_pass,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputstream.close();
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String result="";
                String line="";

                while((line=bufferedReader.readLine())!=null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub

        alertdialog= new AlertDialog.Builder(context).create();
        alertdialog.setTitle("메세지");
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if(type.equals("login"))
        {
            if(result.substring(1,2).equals("1"))
            {
                alertdialog.setMessage("로그인에 성공하셨습니다.");
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            }
            else
            {
                alertdialog.setMessage("아이디와 비밀번호를 확인하세요.");
            }
            alertdialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }
}*/