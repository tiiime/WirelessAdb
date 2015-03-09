package com.github.tiiime.wirelessadb;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "com.github.tiiime.wirelessadb";
    private EditText display = null;
    private TextView port = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View openbtn = findViewById(R.id.open_btn);
        View closebtn = findViewById(R.id.close_btn);
        display = (EditText) findViewById(R.id.display);
        port = (TextView) findViewById(R.id.port);

        openbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = port.getText().toString().trim();
                int _port = 5555;
                try {
                    _port = Integer.valueOf(text);
                } catch (Exception e) {
                }


                String[] cmds = new String[]{"setprop service.adb.tcp.port " + _port,
                        "stop adbd",
                        "start adbd"};
                try {
                    execCmd(cmds);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] cmds = new String[]{"stop adbd"};
                try {
                    execCmd(cmds);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
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

    @NonNull
    private void execCmd(final String[] cmds) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        InputStream in = null;
        DataOutputStream out = null;

        try {
            Process process = runtime.exec("su");
            in = process.getInputStream();
            out = new DataOutputStream(process.getOutputStream());

            for (String cmd : cmds) {
                out.writeBytes(cmd);
                out.writeBytes("\n");
                out.flush();
            }
            out.writeBytes("exit\n");
            out.flush();


            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = "";
            StringBuilder sb = new StringBuilder(line);

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
                Log.v(TAG, line);

            }

            int code = process.waitFor();
            display.setText(new String(sb) + "\n exit code:" + code);

        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
