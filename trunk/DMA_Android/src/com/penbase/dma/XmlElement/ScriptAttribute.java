package com.penbase.dma.XmlElement;

public class ScriptAttribute {	
	//Function name
	public static final String FUNCTION_ADD = "add";
	public static final String FUNCTION_ADDCRITERIA = "addCriteria";
	public static final String FUNCTION_ALERT = "alert";
	public static final String FUNCTION_CANCELNRECORD = "cancelNewRecord";
	public static final String FUNCTION_CANCELTRANSACTION = "cancelTransaction";
	public static final String FUNCTION_CLEAR = "clear";
	public static final String FUNCTION_CONFIRM = "confirm";	
    public static final String FUNCTION_ERROR = "error";
    public static final String FUNCTION_GETFIELDVALUE = "getFieldValue";
    public static final String FUNCTION_GETSELECTEDRECORD = "getSelectedRecord";    
    public static final String FUNCTION_NAVIGATE = "navigate";
    public static final String FUNCTION_NEWRECORD = "newRecord";
    public static final String FUNCTION_NOW = "now";        
    public static final String FUNCTION_REFRESH = "refresh";    
    public static final String FUNCTION_SETCURRENTRECORD = "setCurrentRecord";
    public static final String FUNCTION_SETVALUE = "setValue";
    public static final String FUNCTION_STARTNRECORD = "startNewRecord";
    public static final String FUNCTION_STARTTRANSACTION = "startTransaction";
    public static final String FUNCTION_SYNC = "sync";
    
    //Namespace
    public static final String NAMESPACE_COMPONENT_CB = "component.combobox";
    public static final String NAMESPACE_COMPONENT_DV = "component.dataview";
    public static final String NAMESPACE_DATE = "date";
    public static final String NAMESPACE_DB = "database";
    public static final String NAMESPACE_DB_TABLE = "database.table";
    public static final String NAMESPACE_RUNTIME = "runtime";
    public static final String NAMESPACE_USER = "user";
    
    //Parameters name
    public static final String PARAMETER_NAME_CAPTION = "caption";
    public static final String PARAMETER_NAME_DISTINCT = "distinct";
    public static final String PARAMETER_NAME_ELEMENT = "element";
    public static final String PARAMETER_NAME_FACELESS = "faceless";
    public static final String PARAMETER_NAME_FIELDS = "fields";
    public static final String PARAMETER_NAME_LINK = "link";		//link the last condition 
    public static final String PARAMETER_NAME_TEXT = "text";    
    public static final String PARAMETER_NAME_VALUE = "value";
    public static final String PARAMETER_NAME_VALUES = "values";
    

    //Parameters type
    public static final String PARAMETER_TYPE_BOOLEAN = "boolean";
    public static final String PARAMETER_TYPE_OBJECT = "object";
    
    
    //Common values
    public static final String COMPONENT = "component";    
    public static final String FIELD = "field";
    public static final String FILTER = "filter";
    public static final String FORM = "form";
    public static final String LIST = "list";
    public static final String OPERATOR = "operator"; 
    public static final String ORDER = "order";
    public static final String RECORD = "record";
    public static final String STRING = "string";
    public static final String TABLE = "table";
  
    
    //Constants
    public static final String CONST_FALSE = "FALSE";
    public static final String CONST_NULL = "NULL";
    public static final String CONST_TRUE = "TRUE";
    
    //Operators
    public static final int AND = 29;
    public static final int EQUALS = 20;    
    public static final int GREATERTHAN = 22;
    public static final int GREATERTHANOREQUALS = 23;
    public static final int LESSTHAN = 24;
    public static final int LESSTHANOREQUALS = 25;
    public static final int NOTEQUALS = 21;
    public static final int OR = 30;
    public static final int STRINGSTARTWITH = 26;
    public static final int STRINGENDWITH = 27;
    public static final int STRINGCONTAINS = 28;
    
    

}