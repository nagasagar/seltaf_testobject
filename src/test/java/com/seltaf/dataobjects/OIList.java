package com.seltaf.dataobjects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="OIList")
public class OIList {
	private String name;
	private List<Item> items=new ArrayList<Item>();
	
	@XmlElement 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name="item")
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	public String toString() {
        return ("List: [ Name: " + name +", Items:["+items+"]"+ " ]");
    }

}
