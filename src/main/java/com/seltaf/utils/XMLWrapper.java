package com.seltaf.utils;
import java.util.*;
import javax.xml.bind.annotation.XmlAnyElement;
 
public class XMLWrapper<T> {
 
    private List<T> items;
 
    public XMLWrapper() {
        items = new ArrayList<T>();
    }
 
    public XMLWrapper(List<T> items) {
        this.items = items;
    }
 
    @XmlAnyElement(lax=true)
    public List<T> getItems() {
        return items;
    }
 
}