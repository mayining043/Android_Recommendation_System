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
  * @Description:�����չʾ�̼��б��Activity.Ҫװ�ص���shangjia_list.xml����
  * TODO
 */
public class Listshop extends Activity {
	
	private ShopListAdapter adapter;
	private ArrayList<ShopMeta> itemList;//Ҫ���ĸ����̼ҵ�����
	private ListView lv_shangjia;//������ʾ�̼��б���
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
	//�ƶ�������
	private MyContext.GetMessageBinder mybinder;
	private ServiceConnection connection =new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mybinder=(MyContext.GetMessageBinder)service;
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO �Զ����ɵķ������
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
		//�ƶ�������
		Intent s=new Intent(this,MyContext.class);
		bindService(s,connection,BIND_AUTO_CREATE);
		context= (ImageView)findViewById(R.id.i1);
		context.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				//������ʾ��������Ϣ
				AlertDialog.Builder f=new AlertDialog.Builder(Listshop.this);
				f.setTitle("��������Ϣ");
				f.setMessage(mybinder.toString());
				f.setCancelable(true);
				f.show();
			}
		});
		setListView();
	}
	
	//ÿ��Ҫ����list�͵������setListView,׷�����ݵ�iteamList��������������adapter
	public void setListView(){
		itemList = new ArrayList<ShopMeta>();
		DownloadPlace thread=new DownloadPlace();
		thread.execute();
	}
	/**
	 * @ClassName: DownloadPlace(�ڲ���)
	 * @Description:�̳�AsyncTask��̨�첽��Ϣ��,���Ⱥ�̨��ȡ��ַ�������������ݲ�չʾ
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
				//��һ������ʼ��λ����Ϣ
				publishProgress(1);
				Thread.sleep(500);
				locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
				List<String>providerList=locationManager.getProviders(true);
		
				//�ڶ�������ȡ�ֻ���λ
				publishProgress(21);
				Thread.sleep(500);
				if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
					provider=LocationManager.NETWORK_PROVIDER;
				}
				else if(providerList.contains(LocationManager.GPS_PROVIDER)){
					provider=LocationManager.GPS_PROVIDER;
				}
				else{
					publishProgress(991);//�ֻ��޷���λ�����ʹ�����991
					Thread.sleep(500);
					return false;
				}
				publishProgress(22);//�ֻ������˶�λ
				Thread.sleep(500);
				
				publishProgress(23);//���Ի�ȡ��λ��Ϣ
				location=locationManager.getLastKnownLocation(provider);
				Thread.sleep(500);
				if(location==null){
					publishProgress(992);//��ȡ��λʧ�ܣ��������992
					return false;
				}
				//��ȡ��λ�ɹ��������û�ģ��
				publishProgress(24);//�����û�ģ��
				Thread.sleep(1000);
				
				
				//����������ȡ����
				publishProgress(31);
				poi=poi+"&location="+location.getLatitude()+","+location.getLongitude();
				Thread.sleep(500);
				
				//����json
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
							publishProgress(994);//json����ʧ��
							return false;
						}
					}
				}catch (ClientProtocolException e) {
					publishProgress(993);//��ȡ�б�ʧ�ܣ��������993
					return false;
					
				} catch (IOException e) {
					publishProgress(993);//��ȡ�б�ʧ�ܣ��������993
					return false;
					
				}
				//���
				publishProgress(200);
				Thread.sleep(1000);		
				return true;	
			} catch (Exception e) {
				// TODO �Զ����ɵ� catch ��
				
				return false;
			}
		}
		@Override
		protected void onProgressUpdate(Integer... value){
			Integer[] status=value;
			switch(status[0]){
			case 1:
				load_txt.setText("���ڳ�ʼ������...");
				break;
			case 21:
				load_txt.setText("���ڼ���ֻ��Ƿ���Զ�λ...");
				break;
			case 22:
				load_txt.setText("��⵽�ֻ���λ������");
				break;
			case 23:
				load_txt.setText("���Ի�ȡ�ֻ���λ...");
				break;
			case 24:
				load_txt.setText("���ڽ����û�ģ��...");
				mybinder.run(location);
				break;
			case 31:
				load_txt.setText("������ȡ����...");
				break;
			case 32:
				load_txt.setText("���ڷ�������...");
				break;
			case 991:
				load_txt.setText("��⵽�ֻ�δ������λ���ܣ�");
				Toast.makeText(Listshop.this,"�����ֻ������п�����λ��������ԣ�",Toast.LENGTH_SHORT).show();
				break;
			case 992:
				load_txt.setText("��ȡ��λ��Ϣʧ�ܣ�");
				Toast.makeText(Listshop.this,"��ȡ����GPS�źţ����ƶ��������ط����ԣ�",Toast.LENGTH_SHORT).show();
				break;
			case 993:
				load_txt.setText("��ȡ�б�������Ϣʧ�ܣ�");
				Toast.makeText(Listshop.this,"http����ʧ�ܣ������ֻ��Ƿ�����������",Toast.LENGTH_SHORT).show();
				break;
			case 994:
				load_txt.setText("json����ʧ��");
				Log.w("json", ans);
				break;
			case 200:
				load_txt.setText("��ɣ���");
				load_bar.setVisibility(View.GONE);
				break;
			default:
				load_txt.setText("���ڽ�������������");
			}
			
		}
		@Override
		protected void onPostExecute(Boolean result){
			if(result){
				list.setVisibility(View.VISIBLE);
				addr.setVisibility(View.GONE);
				load_txt.setText("���ڳ�ʼ������...");
				Toast.makeText(Listshop.this,"��ȡ�б�ɹ���",Toast.LENGTH_SHORT).show();
				adapter = new ShopListAdapter(Listshop.this,itemList);
				lv_shangjia.setAdapter(adapter);
				locationManager.requestLocationUpdates(provider,5000,50,locationlistener);
			}
			else{
				load_txt.setVisibility(View.VISIBLE);
				load_bar.setVisibility(View.GONE);
				load_txt.setText("��ȡ�б�ʧ�ܣ�������");
			}
		}
		/**
		 * ���ڽ���json���������
		 * @param ans
		 * @return
		 */
		protected Boolean parseJSONWithJSONObject(){
			try {
				JSONObject jsonObject = new JSONObject(ans);
				JSONArray a = jsonObject.getJSONArray("results");  
                
				for(int i=0;i<a.length();i++){
					String category="��ʳ";
					String image_url="";
					String l="��ַδ֪";
					String Price="�۸�δ֪";
					String distance="����δ֪";
					String name="�̼�δ֪";
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
						if(!name.contains("�̼�δ֪"))
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
	 * @Description:���ڼ���û��ĵ�����Ϣ�Ƿ����ı�
	 * @author mayining
	 */
	private LocationListener locationlistener=new LocationListener(){
		@Override
		public void onLocationChanged(Location location) {
			location=locationManager.getLastKnownLocation(provider);
			mybinder.SetLocation(location);
			// TODO �Զ����ɵķ������
			AlertDialog.Builder f=new AlertDialog.Builder(Listshop.this);
			f.setTitle("λ�÷����ı�");
			f.setMessage("�Ƿ����»�ȡ�Ƽ�?");
			f.setCancelable(true);
			f.setPositiveButton("��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setListView();
				}
			});
			f.setNegativeButton("��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			f.show();
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO �Զ����ɵķ������
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO �Զ����ɵķ������
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO �Զ����ɵķ������
		}
	};
	
	
	@Override
	public void onDestroy(){
		locationManager.removeUpdates(locationlistener);
		unbindService(connection);
	}
	/**
	 * @Name: add_new_item
	 * @Description:����װ���µ�item��list��
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
