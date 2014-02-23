package com.munch.exchange.services.internal.fred;

import java.util.LinkedList;
import java.util.List;

import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.EconomicDataCategory;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class FredSeriesCategory {
	
	
	static final String FRED_SERIES_CATEGORIES_HTTP_PATH = "http://api.stlouisfed.org/fred/series/categories";
	static final String FRED_CATEGORY_HTTP_PATH = "http://api.stlouisfed.org/fred/category";
	
	
	private String symbol;
	
	public FredSeriesCategory(String symbol) {
		super();
		this.symbol = symbol;
	}
	
	public FredSeriesCategory(EconomicData economicData) {
		super();
		this.symbol = economicData.getId();
	}
	
	private String createCategoriesUrl(){
		String url=FredApi.URL+"series/categories?";
		url+="series_id="+this.symbol+"&";
		url+="api_key="+IServiceKey.API_KEY+"&";
		url+="file_type=json";
		return url;
	}
	
	private String createCategoryUrl(String category_id){
		String url=FredApi.URL+"category?";
		url+="category_id="+category_id+"&";
		url+="api_key="+IServiceKey.API_KEY+"&";
		url+="file_type=json";
		return url;
	}
	
	public JSONArray getJSONCategories(){
		JSONObject obj= FredApi.getJSONObject(this.createCategoriesUrl());
		return obj.getJSONArray("categories");
	}
	
	public JSONObject getJSONCategory(String category_id){
		JSONObject obj= FredApi.getJSONObject(this.createCategoryUrl(category_id));
		JSONArray  array=obj.getJSONArray("categories");
		for(int i=0;i<array.length();i++){
			if(array.get(i) instanceof JSONObject){
				JSONObject j_o=(JSONObject) array.get(i);
				return j_o;
			}
			
		}
		return null;
	}
	
	
	private EconomicDataCategory JSonObjectToEconomicDataCategory(JSONObject cat){
		EconomicDataCategory n_cat=new EconomicDataCategory();
		n_cat.setId(String.valueOf(cat.getInt("id")));
		n_cat.setName(cat.getString("name"));
		n_cat.setParentId(String.valueOf(cat.getInt("parent_id")));
		return n_cat;
		
	}
	
	public List<EconomicDataCategory> getCategories(){
		LinkedList<EconomicDataCategory> cats=new LinkedList<EconomicDataCategory>();
		
		JSONArray catr=this.getJSONCategories();
		if(catr==null)return cats;
		
		for(int i=0;i<catr.length();i++){
			if(catr.get(i) instanceof JSONObject){
				EconomicDataCategory cc=JSonObjectToEconomicDataCategory((JSONObject)catr.get(i) );
				cats.add(cc);
				searchParentCategories(cc);
			}
		}
		
		return cats;
		
	}
	
	
	private void searchParentCategories(EconomicDataCategory cat){
		if(!cat.getParentId().isEmpty() && Long.parseLong(cat.getParentId())>0){
			JSONObject parent=this.getJSONCategory(cat.getParentId());
			if(parent==null)return;
			EconomicDataCategory p=JSonObjectToEconomicDataCategory(parent);
			System.out.println("Parent Cat: "+p);
			cat.setParent(p);
			this.searchParentCategories(p);
		}
	}
	
	/*
	private Category getCategory(String id){
		 QueryBuilder builder = new QueryBuilder (
		            FredContext.INSTANCE.getRestTemplate(),
		            FRED_CATEGORY_HTTP_PATH);
		 Categories cats= builder
		            .setApiKey(IServiceKey.API_KEY)
		            .setCategoryId(Long.parseLong(id))
		            .doGet (Categories.class);
		 
		 if(cats==null)return null;
		 
		 List<Category> categoryList = cats.getCategoryList();

		 if(categoryList==null || categoryList.isEmpty())return null;
		
		 
		 return categoryList.get(0);
	}
	
	private Categories getResult() {
		if(categories==null){
			 QueryBuilder builder = new QueryBuilder (
			            FredContext.INSTANCE.getRestTemplate(),
			            FRED_SERIES_CATEGORIES_HTTP_PATH);

			 categories = builder
			            .setApiKey(IServiceKey.API_KEY)
			            .setSeriesId(this.symbol)
			           // .setRealtimeStart(realtimeStart)
			           // .setRealtimeEnd(realtimeEnd)
			            .doGet (Categories.class);
		}
		
		return categories;
	}
	*/
	
	public static void main(String[] args) {
		//CPIAUCSL
		//FredSeries s=new FredSeries("GNPCA");
		FredSeriesCategory s=new FredSeriesCategory("CPIAUCSL");
		for(EconomicDataCategory cat:s.getCategories())
			System.out.println(cat);
		
		//System.out.println(s.getEconomicData());
		
		//TODO Implement Caterogy search!
		
	}

}
