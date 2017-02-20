package com.myn.show;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
/**
 * 该类获取用户上下文信息
 * @author mayin
 *
 */
public class MyContext extends Service{
	protected Location location;
	protected String address;
	protected String position;
	protected String weather;
	protected Date date;
	protected String dateString;
	private GetMessageBinder mBinder=new GetMessageBinder();
	private String ak="http://api.map.baidu.com/geocoder/v2/?&output=json&pois=0&ak=PzDmzShcUmYPfliuA5OAXeNlWQR8iwxo";
	private String w="http://api.map.baidu.com/telematics/v3/weather?coord_type=wgs84&output=json&ak=rl2vAoVWcMD4OkSgsIMQDvmb7FSTcgz2";
	class GetMessageBinder extends Binder{
		@Override
		public String toString(){
			String ans;
			ans="时间："+dateString+"\r\n地点："+address+"\r\n经纬："+
					location.getLatitude()+","+location.getLongitude()+
					"\r\n天气："+weather+"\r\nEnd";
			return ans;
		}
		public void run(Location l){
			SetLocation(l);
			new Thread (new Runnable(){
				@Override
				public void run() {
					while(true){
						try {
							while(location==null)
								Thread.sleep(1000);
							Thread.sleep(5000);
						} catch (Exception e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						//获取时间
						date = new Date();
					    long times = date.getTime();
					    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    dateString = formatter.format(date);
						//获取地址
					    get_address();
					    //获取天气
					    get_weather();
					}
				}
				
			}).start();
		}
		
		public void SetLocation(Location l){
			location=l;
		}
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	@Override
	public void onCreate(){
		super.onCreate();
	}
	@Override 
	public int onStartCommand(Intent intent,int flags,int startId){
		return super.onStartCommand(intent, flags, startId);
		
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	
	
	protected Boolean parseJSONWithJSONObject_place(String ans){
		try {
			JSONObject jsonObject = new JSONObject(ans);
			JSONObject a =(JSONObject) jsonObject.get("result");
			address=a.getString("formatted_address");
		} catch (Exception e) {
			address=e.toString();
			return false;
		}
		return true;
	}
	
	protected Boolean parseJSONWithJSONObject_weather(String ans){
		try {
			JSONObject jsonObject = new JSONObject(ans);
			JSONArray b =jsonObject.getJSONArray("results");
			JSONObject a=b.getJSONObject(0);
			String pm=a.getString("pm25");
			b = (JSONArray) a.get("weather_data");
			a=b.getJSONObject(0);
			String sweather=a.getString("weather");
			String temperature=a.getString("temperature");
			String wind=a.getString("wind");
			weather=sweather+"\r\npm2.5:  "+pm+"\r\n风力:  "+wind
					+"\r\n温度:  "+temperature;
		} catch (Exception e) {
			weather=e.toString();
			return false;
		}
		return true;
	}

	public void get_address(){
		try {
			ak=ak+"&location="+location.getLatitude()+","+location.getLongitude();
			HttpClient client=new DefaultHttpClient();
			HttpGet httpGet=new HttpGet(ak);
			HttpResponse httpresponse;
			httpresponse = client.execute(httpGet);
			if(httpresponse.getStatusLine().getStatusCode()==200){
				HttpEntity entity=httpresponse.getEntity();
				String ans=EntityUtils.toString(entity,"utf-8");
				parseJSONWithJSONObject_place(ans);
			}
		}catch (Exception e) {
			address=e.toString();
		} 
	}
	
	public void get_weather(){
		try {
			w=w+"&location="+location.getLongitude()+","+location.getLatitude();
			HttpClient client=new DefaultHttpClient();
			HttpGet httpGet=new HttpGet(w);
			HttpResponse httpresponse;
			httpresponse = client.execute(httpGet);
			if(httpresponse.getStatusLine().getStatusCode()==200){
				HttpEntity entity=httpresponse.getEntity();
				String ans=EntityUtils.toString(entity,"utf-8");
				parseJSONWithJSONObject_weather(ans);
			}
		}catch (Exception e) {
			weather=e.toString();
		} 
	}
}
