package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class NodeCrossRef
       extends CrossRef{
    
    private DipNode target = null;

    // setters
    //--------
    
    public NodeCrossRef setTarget(DipNode target){
	this.target=target;
	return this;
    }


    // getters
    //--------

    public DipNode getTarget(){
       	return this.target;
    }
}
