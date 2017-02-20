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
  * @Description: 这个适配器用于把每个商家的元数据填充到shangjia_tiem.xml的布局里，
  * 然后再把所有装载好的item添加到shangjia_list.xml里面的lisView。
  * 使用方法：把数据通过构造函数传到这个适配器中，在给shangjia_list.xml里的listview调用setAdapter
  * 就会把这个适配器中的数据加载显示出来。
  * 具体看看代码。
  * TODO
 */
public class ShopListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<ShopMeta> data;//用于装载的元数据
//	private AbImageDownloadQueue mAbImageDownloadQueue = null;//这个是用于下载图片的工具下面会用到
	
	public ShopListAdapter(Context context,ArrayList<ShopMeta> md){
		this.context=context;
		this.data=md;
//		this.mAbImageDownloadQueue = AbImageDownloadQueue.getInstance();//初始化图片下载工具后才能用
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
		final ViewHolder dl;//先声明不赋值
		//如果缓存convertView为空，则需要创建View 
        if(convertView == null)
        {
			//装载商家列表项的布局，并把布局中需要设置的控件给拿到，放到DataList
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
		//下面对控件的内容进行修改
        final ShopMeta meta=data.get(position);
        
        //商家缩略图 这里我先用放在drawable里的默认图片测试 
        String imgeURL=meta.getImage();
        //也可以通过本地文件夹加载（如下），或者 通过URL加载网络图片（见放在末尾的代码）
        //dl.iv_shop_img.setImageBitmap(AbFileUtil.getBitmapFormSrc("image/image_loading.png"));
        if(meta.getCategory()=="美食")
        dl.iv_shop_img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_category_0));
        //给有团购商家加上团购的图标
		if(meta.getHasDeal())
		{
			
			dl.iv_deal_thumb.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_group));
			dl.iv_deal_thumb.setVisibility(1);
		}else
		{
			dl.iv_deal_thumb.setVisibility(0);
		}	
		//商家名称
		dl.tv_shangjia_name.setText(meta.getShopname());
		//人均:￥
		dl.tv_price_title.setText("人均:￥");
		//人均的价格
		dl.tv_price.setText(meta.getPrice());
		dl.tv_price.setTextColor(Color.BLACK);
		dl.tv_price.setTextSize(18);
		//商家地址，所处商区
		dl.tv_address.setText(meta.getLoc());
		//商家的类别，东北菜，粤菜等
		dl.tv_category.setText(meta.getCategory());
		//商家离当前的距离
		dl.tv_distance.setText(meta.getRange());
		//评分1-5，多少分 得多少个红星，剩余为白星星
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
		 //如果你的图片是用URL实时下载的，把上面的mAbImageDownloadQueue取消注释
		//用异步下载图片。 首先显示一个正在加载的图片，下载完发生回调再把图片放上去
//		 if(!AbStrUtil.isEmpty(imgeURL)){
//			//设置下载项 
//            AbImageDownloadItem item = new AbImageDownloadItem(); 
//  	      	//设置显示的大小
//  	      	item.width = 120;
//  	      	item.height = 120;
//  	      	//设置为缩放
//  	      	item.type = AbConstant.SCALEIMG;
//  	      	item.imageUrl = imgeURL;
//  	     
//  	      	dl.iv_shop_img.setImageBitmap(AbFileUtil.getBitmapFormSrc("image/image_loading.png"));
//  	      	//下载完成后更新界面
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
	  * @Description: 布局中的元素
	  * TODO
	 */
	static class ViewHolder{
		public TextView tv_shangjia_name,//商家名称
						tv_price_title,//人均
						tv_price,//价格
						tv_category,//类别
						tv_address,//商家地址
						tv_distance;//距离
		
		public ImageView iv_deal_thumb,//团
						 iv_shop_img;//商家图片
		public ArrayList<ImageView> iv_scores=new ArrayList<ImageView>(5) ;//5个评分星星
	}

}
