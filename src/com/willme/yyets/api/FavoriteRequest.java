package com.willme.yyets.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;

import com.willme.yyets.entities.YYResource;

public class FavoriteRequest {

	public static ArrayList<YYResource> getFavoriteListMoblie() {
		ArrayList<YYResource> favorites = null;
		String content = getContentByJsoup(Constants.FAVORITE_MOBILE_URL);
		if(content != null){
		    Document doc=Jsoup.parse(content);
		    Elements favlist=doc.getElementsByClass("fav-list");
		    if(favlist.size()>0){
		    	Elements linkStrs=favlist.get(0).getElementsByTag("li"); 
		    	favorites = new ArrayList<YYResource>();
		    	for(Element linkStr:linkStrs){
		    		YYResource resource = new YYResource();
		    		resource.setUpdateTime(linkStr.select("p b").text());
		    		String name = linkStr.select("a:not(.f2)").get(0).text();
		    		name = name.replaceAll("【.*】", "");
		    		resource.setName(name);
		    		
		    		String desc = linkStr.select("p:not(.f0)").get(0).text();
		    		desc = desc.replace("说明：", "");
		    		resource.setUpdateInfo(desc);
		    		
		    		String url = linkStr.getElementsByClass("f2").get(0).attr("href");
		    		Uri uri=Uri.parse(url);
		    		resource.setId(uri.getQueryParameter("itemid"));
		    		resource.setChannel(uri.getQueryParameter("channel"));
		    		favorites.add(resource);
		        }
		    }
		}
		return favorites;
	}
	
	public static FavoriteResponse getFavoriteList(int page){
		FavoriteResponse response = null;
		Uri.Builder uriBuilder = Uri.parse(Constants.FAVORITE_URL).buildUpon();
		uriBuilder.appendQueryParameter("page", String.valueOf(page));
		String content = getContentByJsoup(uriBuilder.build().toString());
		if(content != null){
			response = new FavoriteResponse();
		    Document doc=Jsoup.parse(content);
		    Elements pages = doc.select(".pages");
		    int cur = 0;
		    int total = 0;
		    if(pages.size() > 0){
		    	Element p = pages.get(0);
		    	Elements curs = p.getElementsByClass("cur");
		    	if(curs.size() > 0){
		    		cur = Integer.valueOf(curs.get(0).text());
		    	}
		    	Elements as = p.select("a");
		    	for(Element a:as){
		    		String t = a.text();
		    		if(t.startsWith("...")){
		    			t = t.substring(3, t.length());
		    			total = Integer.valueOf(t);
		    		}
		    	}
		    }
		    if(cur == 0){
		    	cur = page;
		    }
		    if(total == 0){
		    	total = page;
		    }
		    response.setCurrentPage(cur);
		    response.setTotalPage(total);
		    
		    Elements favlist=doc.getElementsByClass("u_d_list");
		    if(favlist.size()>0){
		    	Elements linkStrs=favlist.get(0).getElementsByTag("li"); 
		    	ArrayList<YYResource> favorites = new ArrayList<YYResource>();
		    	for(Element linkStr:linkStrs){
		    		YYResource resource = new YYResource();
		    		String time = linkStr.getElementsByClass("resource_time").get(0).attr("time");
		    		resource.setUpdateTime(Long.valueOf(time)*1000);
		    		String name = linkStr.select("strong").get(0).text();
		    		name = name.replaceAll("【.*】", "");
		    		resource.setName(name);
		    		
		    		String desc = linkStr.select(".desc").get(0).text();
		    		desc = desc.replace("说明：", "");
		    		resource.setUpdateInfo(desc);
		    		
		    		Element el = linkStr.getElementsByClass("u_bnts").get(0);
		    		resource.setId(el.attr("rid"));
		    		resource.setChannel(el.attr("channel"));
		    		String imgUrl = linkStr.select("img").get(0).attr("src");
		    		resource.setImgUrl(imgUrl);
		    		favorites.add(resource);
		        }
		    	response.setFavoriteList(favorites);
		    }
		}
		return response;
	}
	
	public static String getContentByJsoup(String url) {
		String content = null;
		try {
			Document doc;
			doc = Jsoup.connect(url)
					.cookies(getCookies())
					.timeout(50000)
					.get();
			content = doc.toString();
		} catch (IOException e) {}
		return content;
	}
	
	public static HashMap<String,String>getCookies(){
		HashMap<String, String> cookies = new HashMap<String,String>();
		List<Cookie> cookieList = YYeTsRestClient.getCookieStore().getCookies();
		for(Cookie c: cookieList){
			cookies.put(c.getName(), c.getValue());
		}
		return cookies;
	}

}
