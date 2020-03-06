package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class CountsFull{
    
    private Map<String,Integer> details= new HashMap<String,Integer>();
    
    // setters
    //--------
    
    public CountsFull setDetail(Map<String,Integer> details){
	this.details=details;
	return this;
    }

    // getters
    //--------
    
    public Map<String,Integer> getDetail(){
       	return this.details;
    }
}
