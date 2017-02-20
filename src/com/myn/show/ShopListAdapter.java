package com.myn.show;

import java.util.ArrayList;
import java.util.HashMap;

import com.ab.global.AbConstant;
import com.ab.net.AbImageDownloadCallback;
import com.ab.net.AbImageDownloadItem;
import com.ab.net.AbImageDownloadQueue;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.example.shoolist.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/***
 * 
  * @ClassName: ShopListAdapter
  * @Description: ������������ڰ�ÿ���̼ҵ�Ԫ������䵽shangjia_tiem.xml�Ĳ����
  * Ȼ���ٰ�����װ�غõ�item��ӵ�shangjia_list.xml�����lisView��
  * ʹ�÷�����������ͨ�����캯����������������У��ڸ�shangjia_list.xml���listview����setAdapter
  * �ͻ������������е����ݼ�����ʾ������
  * ���忴�����롣
  * TODO
 */
public class ShopListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<ShopMeta> data;//����װ�ص�Ԫ����
//	private AbImageDownloadQueue mAbImageDownloadQueue = null;//�������������ͼƬ�Ĺ���������õ�
	
	public ShopListAdapter(Context context,ArrayList<ShopMeta> md){
		this.context=context;
		this.data=md;
//		this.mAbImageDownloadQueue = AbImageDownloadQueue.getInstance();//��ʼ��ͼƬ���ع��ߺ������
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder dl;//����������ֵ
		//�������convertViewΪ�գ�����Ҫ����View 
        if(convertView == null)
        {
			//װ���̼��б���Ĳ��֣����Ѳ�������Ҫ���õĿؼ����õ����ŵ�DataList
			convertView = LayoutInflater.from(context).inflate(R.layout.shangjia_item, null);
			dl=new ViewHolder();
			dl.iv_shop_img = (ImageView) convertView.findViewById(R.id.iv_shop_img);
			dl.iv_deal_thumb = (ImageView) convertView.findViewById(R.id.iv_deal_thumb);
			dl.tv_shangjia_name = (TextView) convertView.findViewById(R.id.tv_shangjia_name);
			dl.tv_price_title = (TextView) convertView.findViewById(R.id.tv_price_title);
			dl.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			dl.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
			dl.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
			dl.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
			dl.iv_scores.add((ImageView) convertView.findViewById(R.id.iv_score1));
			dl.iv_scores.add((ImageView) convertView.findViewById(R.id.iv_score2));
			dl.iv_scores.add((ImageView) convertView.findViewById(R.id.iv_score3));
			dl.iv_scores.add((ImageView) convertView.findViewById(R.id.iv_score4));
			dl.iv_scores.add((ImageView) convertView.findViewById(R.id.iv_score5));
			convertView.setTag(dl);
        }else
        {
        	dl=(ViewHolder)convertView.getTag();
        }
		//����Կؼ������ݽ����޸�
        final ShopMeta meta=data.get(position);
        
        //�̼�����ͼ ���������÷���drawable���Ĭ��ͼƬ���� 
        String imgeURL=meta.getImage();
        //Ҳ����ͨ�������ļ��м��أ����£������� ͨ��URL��������ͼƬ��������ĩβ�Ĵ��룩
        //dl.iv_shop_img.setImageBitmap(AbFileUtil.getBitmapFormSrc("image/image_loading.png"));
        if(meta.getCategory()=="��ʳ")
        dl.iv_shop_img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_category_0));
        //�����Ź��̼Ҽ����Ź���ͼ��
		if(meta.getHasDeal())
		{
			
			dl.iv_deal_thumb.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_group));
			dl.iv_deal_thumb.setVisibility(1);
		}else
		{
			dl.iv_deal_thumb.setVisibility(0);
		}	
		//�̼�����
		dl.tv_shangjia_name.setText(meta.getShopname());
		//�˾�:��
		dl.tv_price_title.setText("�˾�:��");
		//�˾��ļ۸�
		dl.tv_price.setText(meta.getPrice());
		dl.tv_price.setTextColor(Color.BLACK);
		dl.tv_price.setTextSize(18);
		//�̼ҵ�ַ����������
		dl.tv_address.setText(meta.getLoc());
		//�̼ҵ���𣬶����ˣ����˵�
		dl.tv_category.setText(meta.getCategory());
		//�̼��뵱ǰ�ľ���
		dl.tv_distance.setText(meta.getRange());
		//����1-5�����ٷ� �ö��ٸ����ǣ�ʣ��Ϊ������
		int score=meta.getScore();
		for(int i=0;i<score;i++)
		{
			dl.iv_scores.get(i).setImageDrawable(
					context.getResources().getDrawable(R.drawable.ic_rating_star_small_on));
		}
		for(int i=score;i<5;i++)
		{
			dl.iv_scores.get(i).setImageDrawable(
					context.getResources().getDrawable(R.drawable.ic_rating_star_small_off));
		}
		 //������ͼƬ����URLʵʱ���صģ��������mAbImageDownloadQueueȡ��ע��
		//���첽����ͼƬ�� ������ʾһ�����ڼ��ص�ͼƬ�������귢���ص��ٰ�ͼƬ����ȥ
//		 if(!AbStrUtil.isEmpty(imgeURL)){
//			//���������� 
//            AbImageDownloadItem item = new AbImageDownloadItem(); 
//  	      	//������ʾ�Ĵ�С
//  	      	item.width = 120;
//  	      	item.height = 120;
//  	      	//����Ϊ����
//  	      	item.type = AbConstant.SCALEIMG;
//  	      	item.imageUrl = imgeURL;
//  	     
//  	      	dl.iv_shop_img.setImageBitmap(AbFileUtil.getBitmapFormSrc("image/image_loading.png"));
//  	      	//������ɺ���½���
//  	      	item.callback = new AbImageDownloadCallback() { 
//  	            @Override 
//  	            public void update(Bitmap bitmap, String imageUrl) { 
//  	            	if(bitmap!=null){
//  	            		dl.iv_shop_img.setImageBitmap(bitmap); 
//  	            	}else{
//  	            		dl.iv_shop_img.setImageBitmap(AbFileUtil.getBitmapFormSrc("image/image_error.png"));
//  	            	}
//  	            } 
//  	      	}; 
//  	      	mAbImageDownloadQueue.download(item); 
//        }else{
//        	dl.iv_shop_img.setImageBitmap(AbFileUtil.getBitmapFormSrc("image/image_no.png"));
//        }
		return convertView;
	}
	
	/**
	 * 
	  * @ClassName: DataList
	  * @Description: �����е�Ԫ��
	  * TODO
	 */
	static class ViewHolder{
		public TextView tv_shangjia_name,//�̼�����
						tv_price_title,//�˾�
						tv_price,//�۸�
						tv_category,//���
						tv_address,//�̼ҵ�ַ
						tv_distance;//����
		
		public ImageView iv_deal_thumb,//��
						 iv_shop_img;//�̼�ͼƬ
		public ArrayList<ImageView> iv_scores=new ArrayList<ImageView>(5) ;//5����������
	}

}
