package com.myn.show;
/**
 * 
  * @ClassName: ShopMeta
  * TODO
 */
public class ShopMeta {

	private String loc;//�̼ҵ�ַ
	private String image;//�̼�ͼƬ URL
	private String range;//����
	private String category;//���
	private String shopname;//�̼�����
	private String price;//�۸�
	private boolean hasDeal;//�Ƿ����Ź�������Ҫ��ʾһ���Ź�ͼ��
	private int score;//����
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	public boolean getHasDeal() {
		return hasDeal;
	}
	public void setHasDeal(boolean hasDeal) {
		this.hasDeal = hasDeal;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}


//	@Override
//	public String toString() {
//		return "ShopMeta [merchsid=" + merchsid + ", loc=" + loc + ", image="
//				+ image + ", range=" + range + ", category=" + category
//				+ ", shorttitle=" + shorttitle + ", describe=" + describe
//				+ ", price=" + price + ", value=" + value + ", bought="
//				+ bought + ", grade=" + grade + ", gradecount=" + gradecount
//				+ ", shoplevel=" + shoplevel + ", ischooseseat=" + ischooseseat
//				+ ", shopname=" + shopname + ", isholiday=" + isholiday
//				+ ", hosttype=" + hosttype + ", issubscribe=" + issubscribe
//				+ ", date=" + date + ", city=" + city + "]";
//	}

	public ShopMeta(){}
	
	public ShopMeta(String loc,String image,String range,int score,
			String category,String shopname,String price,boolean hasDeal) {
		super();
//		this.merchsid = merchsid;
		this.loc = loc;
		this.image = image;
		this.range = range;
		this.category = category;
		this.shopname = shopname;
		this.price = price;
		this.hasDeal=hasDeal;//�Ƿ����Ź�������Ҫ��ʾһ���Ź�ͼ��
		this.score=score;
	}
	
	
}
