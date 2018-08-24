package com.example.design3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Socket socket;
	BufferedReader socket_in;
	PrintWriter socket_out;
	EditText Text_In;
	Button button;
	Button button1;
	TextView Text_Out;
	String dataWrite="";
	String dataRead="";
	String Telephone="";
	int type,trial=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Window win = getWindow();  // 윈도우 객체 참조
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 좌,우 화면 전환시 전체 화면 보기로 설정.
		
		try {
			socket = new Socket("223.195.14.138", 5555);  // 장소나 와이파이 지역 전환시 항상 체크!!
			socket_out = new PrintWriter(socket.getOutputStream(), true);
			socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		} catch (IOException e) {
			e.printStackTrace();

		}
		Text_In = (EditText) findViewById(R.id.text_in);
		button = (Button) findViewById(R.id.button);
		button1 = (Button) findViewById(R.id.button1);
		Text_Out = (TextView) findViewById(R.id.text_out);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dataWrite = Text_In.getText().toString();
				if(trial!=1)
				{
					type=Integer.parseInt(dataWrite);
					trial++;
				}
				if (dataWrite != null) {
					Log.w("NETWORK", " " + dataWrite + type); 
					socket_out.println(dataWrite);
				}
			}
		});
		
		button1.setOnClickListener(new OnClickListener() {		//'입력완료'버튼을 누르면
			public void onClick(View v) {
				try{
					Intent CallIntent=new Intent(Intent.ACTION_DIAL);	//or ACTION_CALL
					CallIntent.setData(Uri.parse("tel:"+ Telephone));
					startActivity(CallIntent);
					}
				catch(Exception ex)
				{
					Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		Thread worker = new Thread() {
			public void run() {
				try {
					while (!(dataRead.equals(null))) {  /** 없으면 데이터 업데이트 안ㅋ함ㅋ. */
						dataRead = socket_in.readLine();
						Text_Out.post(new Runnable() {
							public void run() {
								Log.w("ANDROID"," " + dataRead);
								switch(type)
								{
								case 2:
								case 5:
									Text_Out.append(dataRead + "\n");
									break;
								case 3:
								case 4:
									Text_Out.append(dataRead);
									break;
								case 6:
									Text_Out.append(dataRead);
									Telephone+=dataRead;
									break;
								}
							}
						});
					}
				} catch (Exception e) {
				}
			}
		};
		worker.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onConfigurationChanged(Configuration newConfig)  // 화면 좌,우 전환시 발생하는 토스트 관련.
	{
		super.onConfigurationChanged(newConfig);
		
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)  // 가로방향 전환
		{
			Toast.makeText(this, "Landscape", Toast.LENGTH_SHORT).show();
		}
		else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
		{
			Toast.makeText(this, "Portrait", Toast.LENGTH_SHORT).show();
		}
	}
}