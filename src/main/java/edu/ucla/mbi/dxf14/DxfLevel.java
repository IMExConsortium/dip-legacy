package edu.ucla.mbi.dxf14;

/*  #===================================================================
    # $Id:: DxfLevel.java 2616 2012-08-07 05:17:42Z lukasz             $
    # Version: $Rev:: 2616                                             $
    #===================================================================
    #     DxfLevel - hierarchy of the levelof detail provided within
    #                Dxf files
    #      
    #     NOTES:
    #
    #===================================================================
*/

public class DxfLevel
    implements Comparable{

    private final String name;
    private static int nextOrdinal=0;
    private final int ordinal=nextOrdinal++;
    
    private DxfLevel(String name) {this.name=name;}
    
    public String toString(){return name;}
    public int compareTo(Object o){
	return ordinal - ((DxfLevel)o).ordinal;
    }
    
    public static final DxfLevel STUB = new DxfLevel("stub");
    public static final DxfLevel SHRT = new DxfLevel("short");
    public static final DxfLevel BASE = new DxfLevel("base");
    public static final DxfLevel FULL = new DxfLevel("full");
    public static final DxfLevel DEEP = new DxfLevel("deep"); 
    public static final DxfLevel MIT  = new DxfLevel("mit"); 

    public static DxfLevel fromString(String str){

	if(str.toLowerCase().equals("stub"))  return DxfLevel.STUB;
	if(str.toLowerCase().equals("short")) return DxfLevel.SHRT;
	if(str.toLowerCase().equals("base"))  return DxfLevel.BASE;
	if(str.toLowerCase().equals("full"))  return DxfLevel.FULL;
	if(str.toLowerCase().equals("deep"))  return DxfLevel.DEEP;
	if(str.toLowerCase().equals("mit"))   return DxfLevel.MIT;
	return DxfLevel.STUB;  // NOTE: throw exception 
    }

}
