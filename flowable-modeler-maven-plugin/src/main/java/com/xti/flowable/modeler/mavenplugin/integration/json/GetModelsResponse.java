package com.xti.flowable.modeler.mavenplugin.integration.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GetModelsResponse {
	
	private List<GetModelsResponseItem> data;

	public int getLongestKeyLength ( int minimum ) {
		int longest = minimum;
		for ( GetModelsResponseItem item: data ) {
			if ( item.getKey().length() > longest ) {
				longest = item.getKey().length();
			}
		}
		return longest;
	}
	public int getLongestNameLength ( int minimum ) {
		int longest = minimum;
		for ( GetModelsResponseItem item: data ) {
			if ( item.getName().length() > longest ) {
				longest = item.getName().length();
			}
		}
		return longest;
	}
	public List<GetModelsResponseItem> getData() {
		return data;
	}
	public void setData(List<GetModelsResponseItem> data) {
		this.data = data;
	}
	
}