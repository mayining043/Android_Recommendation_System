package com.myn.show;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.shoolist.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
  * @ClassName: Listshop
  * @Description:这个是展示商家列表的Activity.要装载的是shangjia_list.xml布局
  * TODO
 */
public class Listshop extends Activity {
	
	private ShopListAdapter adapter;
	private ArrayList<ShopMeta> itemList;//要填充的各个商家的数据
	private ListView lv_shangjia;//用于显示商家列表布局
	private RelativeLayout addr;
	private ImageView context;
	private LinearLayout list;
	private TextView load_txt;
	private ProgressBar load_bar;
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private String poi="http://api.map.baidu.com/place/v2/search?scope=2&query=%E7%BE%8E%E9%A3%9F&radius=2000&output=json&page_size=40&ak=PzDmzShcUmYPfliuA5OAXeNlWQR8iwxo";
	private String ans;
	private String Err;
	//移动上下文
	private MyContext.GetMessageBinder mybinder;
	private ServiceConnection connection =new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mybinder=(MyContext.GetMessageBinder)service;
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO 自动生成的方法存根
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shangjia_list);
		lv_shangjia = (ListView) findViewById(R.id.lv_shangjia);
		addr= (RelativeLayout)findViewById(R.id.ll_refresh_addr);
		list= (LinearLayout)findViewById(R.id.ll_shangjialist);
		load_txt= (TextView)findViewById(R.id.load_text);
		load_bar= (ProgressBar)findViewById(R.id.load_img);
		//移动上下文
		Intent s=new Intent(this,MyContext.class);
		bindService(s,connection,BIND_AUTO_CREATE);
		context= (ImageView)findViewById(R.id.i1);
		context.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				//用于显示上下文信息
				AlertDialog.Builder f=new AlertDialog.Builder(Listshop.this);
				f.setTitle("上下文信息");
				f.setMessage(mybinder.toString());
				f.setCancelable(true);
				f.show();
			}
		});
		setListView();
	}
	
	//每次要更新list就调用这个setListView,追加数据到iteamList，里面再设置下adapter
	public void setListView(){
		itemList = new ArrayList<ShopMeta>();
		DownloadPlace thread=new DownloadPlace();
		thread.execute();
	}
	/**
	 * @ClassName: DownloadPlace(内部类)
	 * @Description:继承AsyncTask后台异步信息类,首先后台获取地址，联网更新数据并展示
	 * @author mayining
	 *
	 */
	public class DownloadPlace extends AsyncTask<Void,Integer,Boolean>{
		@Override
		protected void onPreExecute(){
			list.setVisibility(View.GONE);
			addr.setVisibility(View.VISIBLE);
			
		}
		@Override
		protected Boolean doInBackground(Void... params){
			try {
				Thread.sleep(1500);
				//第一步，初始化位置信息
				publishProgress(1);
				Thread.sleep(500);
				locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
				List<String>providerList=locationManager.getProviders(true);
		
				//第二步，获取手机定位
				publishProgress(21);
				Thread.sleep(500);
				if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
					provider=LocationManager.NETWORK_PROVIDER;
				}
				else if(providerList.contains(LocationManager.GPS_PROVIDER)){
					provider=LocationManager.GPS_PROVIDER;
				}
				else{
					publishProgress(991);//手机无法定位，发送错误码991
					Thread.sleep(500);
					return false;
				}
				publishProgress(22);//手机开启了定位
				Thread.sleep(500);
				
				publishProgress(23);//尝试获取定位信息
				location=locationManager.getLastKnownLocation(provider);
				Thread.sleep(500);
				if(location==null){
					publishProgress(992);//获取定位失败，错误代码992
					return false;
				}
				//获取定位成功，建立用户模型
				publishProgress(24);//建立用户模型
				Thread.sleep(1000);
				
				
				//第三步，拉取数据
				publishProgress(31);
				poi=poi+"&location="+location.getLatitude()+","+location.getLongitude();
				Thread.sleep(500);
				
				//分析json
				try {
					HttpClient client=new DefaultHttpClient();
					HttpGet httpGet=new HttpGet(poi);
					HttpResponse httpresponse;
					publishProgress(32);
					Log.i("shoolist", poi);
					httpresponse = client.execute(httpGet);
					
					if(httpresponse.getStatusLine().getStatusCode()==200){
						HttpEntity entity=httpresponse.getEntity();
						ans=EntityUtils.toString(entity,"utf-8");
						if(!parseJSONWithJSONObject()){
							publishProgress(994);//json解析失败
							return false;
						}
					}
				}catch (ClientProtocolException e) {
					publishProgress(993);//获取列表失败，错误代码993
					return false;
					
				} catch (IOException e) {
					publishProgress(993);//获取列表失败，错误代码993
					return false;
					
				}
				//完成
				publishProgress(200);
				Thread.sleep(1000);		
				return true;	
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				
				return false;
			}
		}
		@Override
		protected void onProgressUpdate(Integer... value){
			Integer[] status=value;
			switch(status[0]){
			case 1:
				load_txt.setText("正在初始化操作...");
				break;
			case 21:
				load_txt.setText("正在检测手机是否可以定位...");
				break;
			case 22:
				load_txt.setText("检测到手机定位正常！");
				break;
			case 23:
				load_txt.setText("尝试获取手机定位...");
				break;
			case 24:
				load_txt.setText("正在建立用户模型...");
				mybinder.run(location);
				break;
			case 31:
				load_txt.setText("正在拉取数据...");
				break;
			case 32:
				load_txt.setText("正在分析数据...");
				break;
			case 991:
				load_txt.setText("检测到手机未开启定位功能！");
				Toast.makeText(Listshop.this,"请在手机设置中开启定位服务后重试！",Toast.LENGTH_SHORT).show();
				break;
			case 992:
				load_txt.setText("获取定位信息失败！");
				Toast.makeText(Listshop.this,"获取不到GPS信号，请移动到开阔地方重试！",Toast.LENGTH_SHORT).show();
				break;
			case 993:
				load_txt.setText("获取列表数据信息失败！");
				Toast.makeText(Listshop.this,"http访问失败，请检查手机是否联网后重试",Toast.LENGTH_SHORT).show();
				break;
			case 994:
				load_txt.setText("json解析失败");
				Log.w("json", ans);
				break;
			case 200:
				load_txt.setText("完成！！");
				load_bar.setVisibility(View.GONE);
				break;
			default:
				load_txt.setText("正在进行其他操作！");
			}
			
		}
		@Override
		protected void onPostExecute(Boolean result){
			if(result){
				list.setVisibility(View.VISIBLE);
				addr.setVisibility(View.GONE);
				load_txt.setText("正在初始化操作...");
				Toast.makeText(Listshop.this,"获取列表成功！",Toast.LENGTH_SHORT).show();
				adapter = new ShopListAdapter(Listshop.this,itemList);
				lv_shangjia.setAdapter(adapter);
				locationManager.requestLocationUpdates(provider,5000,50,locationlistener);
			}
			else{
				load_txt.setVisibility(View.VISIBLE);
				load_bar.setVisibility(View.GONE);
				load_txt.setText("获取列表失败，请重试");
			}
		}
		/**
		 * 用于解析json并填充数据
		 * @param ans
		 * @return
		 */
		protected Boolean parseJSONWithJSONObject(){
			try {
				JSONObject jsonObject = new JSONObject(ans);
				JSONArray a = jsonObject.getJSONArray("results");  
                
				for(int i=0;i<a.length();i++){
					String category="美食";
					String image_url="";
					String l="地址未知";
					String Price="价格未知";
					String distance="距离未知";
					String name="商家未知";
					Boolean hasDeal=true;
					Integer score=0;
					try{
						JSONObject o=a.getJSONObject(i);
						JSONObject dd = (JSONObject) o.get("detail_info");
						score=(int)Double.parseDouble(dd.getString("overall_rating"));
						l=o.getString("address");
						Price=dd.getString("price");
						distance="2KM";
						name=o.getString("name");
					}
					catch(Exception e){
						Err=e.toString();
					}
					finally{
						if(!name.contains("商家未知"))
						add_new_item(category,image_url,l,
								Price,distance,name,hasDeal,score);
					}
				}
			} catch (Exception e) {
				Err=e.toString();
				return false;
			}
			return true;
		}
	}
	/**
	 * @Name: LocationListener
	 * @Description:用于检测用户的地理信息是否发生改变
	 * @author mayining
	 */
	private LocationListener locationlistener=new LocationListener(){
		@Override
		public void onLocationChanged(Location location) {
			location=locationManager.getLastKnownLocation(provider);
			mybinder.SetLocation(location);
			// TODO 自动生成的方法存根
			AlertDialog.Builder f=new AlertDialog.Builder(Listshop.this);
			f.setTitle("位置发生改变");
			f.setMessage("是否重新获取推荐?");
			f.setCancelable(true);
			f.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setListView();
				}
			});
			f.setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			f.show();
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO 自动生成的方法存根
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO 自动生成的方法存根
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO 自动生成的方法存根
		}
	};
	
	
	@Override
	public void onDestroy(){
		locationManager.removeUpdates(locationlistener);
		unbindService(connection);
	}
	/**
	 * @Name: add_new_item
	 * @Description:用于装载新的item到list中
	 * @author mayining
	 */
	protected void add_new_item(String category, String image_url,String l,
			String Price,String distance,String name,Boolean hasDeal,Integer score){
		ShopMeta item = new ShopMeta();
		item.setCategory(category);
		item.setImage(image_url);
		item.setLoc(l);
		item.setPrice(String.valueOf(Price));
		item.setRange(distance);
		item.setShopname(name);
		item.setHasDeal(hasDeal);
		item.setScore(score);
		itemList.add(item);
	}
}
