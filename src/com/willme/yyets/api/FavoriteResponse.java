package com.willme.yyets.api;

import java.util.ArrayList;

import com.willme.yyets.entities.YYResource;

public class FavoriteResponse {

	private ArrayList<YYResource> favoriteList;
	private int totalPage;
	private int currentPage;

	public ArrayList<YYResource> getFavoriteList() {
		return favoriteList;
	}

	public void setFavoriteList(ArrayList<YYResource> favoriteList) {
		this.favoriteList = favoriteList;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

}
