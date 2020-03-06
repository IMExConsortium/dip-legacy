package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class LinkCrossRef
       extends CrossRef{
    
    private Link target = null;

    // setters
    //--------
    
    public LinkCrossRef setTarget(Link target){
	this.target=target;
	return this;
    }


    // getters
    //--------

    public Link getTarget(){
       	return this.target;
    }
}
