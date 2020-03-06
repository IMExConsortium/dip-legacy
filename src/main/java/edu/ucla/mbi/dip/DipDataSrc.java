package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class DipDataSrc
    extends DataSrc{

    private String authors = "";    // authors (last ii, last ii,... format)
    private String title   = "";    // title

    private String jname   = "";    // journal name 

    private String volume  = "";    // volume

    private String issue   = "";    // issue

    private String pages   = "";    // pages (start-stop format)
    private int year       = 0;    // year

    private String abst    = "";    // abstract

    private String nlmid   = "";    // NLMID journal ID

    private Set<Evidence> evid = null;

    // setters
    //--------

    public DataSrc setEvidence( Set<Evidence> evidence ) {
        this.evid = evidence;
        return this;
    }
    
    public DataSrc setAuthors( String authors ){
	this.authors = authors;
	return this;
    }

    public DataSrc setTitle(String title){
	this.title=title;
	return this;
    }

    public DataSrc setJname(String jname){
	this.jname=jname;
	return this;
    }

    public DataSrc setVolume(String volume){
	this.volume=volume;
	return this;
    }

    public DataSrc setIssue(String issue){
	this.issue=issue;
	return this;
    }
    
    public DataSrc setPages(String pages){
	this.pages=pages;
	return this;
    }

    public DataSrc setYear(int year){
	this.year=year;
	return this;
    }

    public DataSrc setAbstract(String abst){
	this.abst=abst;
	return this;
    }

    public DataSrc setNlmid(String nlmid){
	this.nlmid=nlmid;
	return this;
    }

    // getters
    //--------

    public Set<Evidence> getEvidence() {
        return this.evid;
    }

    public String getAuthors(){
       	return this.authors;
    }

    public String getTitle(){
       	return this.title;
    }

    public String getJname(){
       	return this.jname;
    }

    public String getVolume(){
       	return this.volume;
    }

    public String getIssue(){
       	return this.issue;
    }

    public String getPages(){
       	return this.pages;
    }

    public int getYear(){
       	return this.year;
    }

    public String getAbstract(){
       	return this.abst;
    }

    public String getNlmid(){
       	return this.nlmid;
    }
    
}
