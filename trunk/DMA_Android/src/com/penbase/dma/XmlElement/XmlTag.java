package com.penbase.dma.XmlElement;

public class XmlTag {
	//Root
    public static final String TAG_ROOT = "x35";
    public static final String TAG_APP = "a";
    
    //Login
    public static final String TAG_LOGIN_ID = "id";
    public static final String TAG_LOGIN_VER = "ver";
    public static final String TAG_LOGIN_BLD = "bld";
    public static final String TAG_LOGIN_SUB = "sub";
    public static final String TAG_LOGIN_DBID = "dbid";
    public static final String TAG_LOGIN_TIT = "tit";
    
    //Design
    public static final String TAG_DESIGN_S = "s";
    public static final String TAG_DESIGN_S_G = "g";
    public static final String TAG_DESIGN_S_G_SM = "sm";
    public static final String TAG_DESIGN_S_G_FID = "fid";
    public static final String TAG_DESIGN_S_MM = "mm";
    public static final String TAG_DESIGN_S_MM_E = "e";             

    public static final String TAG_DESIGN_F = "f";
    public static final String TAG_DESIGN_F_T = "t";
    public static final String TAG_DESIGN_F_BC = "bc";
    public static final String TAG_DESIGN_F_DGT = "dgt";
    public static final String TAG_DESIGN_F_FS = "fs";
    public static final String TAG_DESIGN_F_H = "h";
    public static final String TAG_DESIGN_F_ID = "id";
    public static final String TAG_DESIGN_F_M = "m";                
    public static final String TAG_DESIGN_F_W = "w";
    
    //Component elements
    public static final String TAG_COMPONENT_BARCODECOMPONENT = "bac";
    public static final String TAG_COMPONENT_BUTTON = "b";
    public static final String TAG_COMPONENT_CHECKBOX = "ch";
    public static final String TAG_COMPONENT_CHECKBOX_CHECKED = "v";
    public static final String TAG_COMPONENT_COMBOBOX = "cb";
    public static final String TAG_COMPONENT_COMBOBOX_ITEM = "icb";
    public static final String TAG_COMPONENT_COMBOBOX_ITEM_VALUE = "v";
    public static final String TAG_COMPONENT_COMBOBOX_LABELTABLE = "lst";
    public static final String TAG_COMPONENT_COMBOBOX_LABELFIELD = "lsf";
    public static final String TAG_COMPONENT_COMBOBOX_VALUETABLE = "vst";
    public static final String TAG_COMPONENT_COMBOBOX_VALUEFIELD = "vsf";
    public static final String TAG_COMPONENT_DATAVIEW = "dv";
    public static final String TAG_COMPONENT_DATAVIEW_COLUMN = "dvc";
    public static final String TAG_COMPONENT_DATAVIEW_COLUMN_HEADER = "hdr";
    public static final String TAG_COMPONENT_DATAVIEW_COLUMN_CALC = "calc";
    public static final String TAG_COMPONENT_DATAVIEW_PAGESIZE = "dvps";   
    public static final String TAG_COMPONENT_DATEFIELD = "df";
    public static final String TAG_COMPONENT_GAUGE = "g";
    public static final String TAG_COMPONENT_IMAGE = "i";   
    public static final String TAG_COMPONENT_LABEL = "l";
    public static final String TAG_COMPONENT_MENUBAR = "mb";
    public static final String TAG_COMPONENT_NAVIBAR = "nb";
    public static final String TAG_COMPONENT_NUMBERBOX = "nbox";
    public static final String TAG_COMPONENT_PICTUREBOX = "pb";
    public static final String TAG_COMPONENT_RADIOBUTTON = "rb";
    public static final String TAG_COMPONENT_TEXTFIELD = "t";
    public static final String TAG_COMPONENT_TEXTFIELD_MULTI = "ml";
    public static final String TAG_COMPONENT_TEXTFIELD_EDIT = "ed";
    public static final String TAG_COMPONENT_TEXTZONE = "tz";
    public static final String TAG_COMPONENT_TIMEFIELD = "tf";                      
    public static final String TAG_COMPONENT_VIDEOBOX = "vd";               
    
    //Common component attributes
    public static final String TAG_COMPONENT_COMMON_ALIGN = "a";
    public static final String TAG_COMPONENT_COMMON_BACKGROUND = "bg";
    public static final String TAG_COMPONENT_COMMON_FONTSIZE = "fs";
    public static final String TAG_COMPONENT_COMMON_FONTTYPE = "ft";                
    public static final String TAG_COMPONENT_COMMON_ID = "id";
    public static final String TAG_COMPONENT_COMMON_LABEL = "l";
    public static final String TAG_COMPONENT_COMMON_LCOORDX = "x2";
    public static final String TAG_COMPONENT_COMMON_LCOORDY = "y2";
    public static final String TAG_COMPONENT_COMMON_LHEIGHT = "h2";
    public static final String TAG_COMPONENT_COMMON_LWIDTH = "w2";
    public static final String TAG_COMPONENT_COMMON_PCOORDX = "x";
    public static final String TAG_COMPONENT_COMMON_PCOORDY = "y";
    public static final String TAG_COMPONENT_COMMON_PHEIGHT = "h";
    public static final String TAG_COMPONENT_COMMON_PWIDTH = "w";
    public static final String TAG_COMPONENT_COMMON_TABLEID = "tid";
    public static final String TAG_COMPONENT_COMMON_FIELDID = "fid";

    //Database element
    public static final String TAG_DB = "db";
    public static final String TAG_DB_ID = "id";
    
    //Table
    public static final String TAG_TABLE = "t";
    public static final String TAG_TABLE_ID = "id";
    public static final String TAG_TABLE_NAME = "n";        
    public static final String TAG_TABLE_SYNC = "sy";
    
    //Field
    public static final String TAG_FIELD = "f";
    public static final String TAG_FIELD_FORIEIGNTABLE = "ft";
    public static final String TAG_FIELD_FORIEIGNFIELD = "ff";
    public static final String TAG_FIELD_ID = "id";
    public static final String TAG_FIELD_NAME = "n";
    public static final String TAG_FIELD_PK = "pk";
    public static final String TAG_FIELD_PK_AUTO = "auto";
    public static final String TAG_FIELD_TYPE = "ty";
    public static final String TAG_FIELD_SIZE = "sz";
    public static final String TAG_FIELD_SYNC = "sy";
    
    //Resources     
    public static final String TAG_RESOURCES_RL = "rl";
    public static final String TAG_RESOURCES_RL_ID = "id";
    public static final String TAG_RESOURCES_R = "r";
    public static final String TAG_RESOURCES_R_ID = "id";
    public static final String TAG_RESOURCES_R_HASHCODE = "h";
    public static final String TAG_RESOURCES_R_EXT = "ext";

    //Events
    public static final String TAG_EVENT_ONCLICK = "onc";
    public static final String TAG_EVENT_ONLOAD = "onl";
    public static final String TAG_EVENT_ONCHANGE = "ong";
    
    //Behaviors
    public static final String TAG_SCRIPT = "s";
    public static final String TAG_SCRIPT_FUNCTION = "f";   
    public static final String TAG_SCRIPT_CALL = "c";
    public static final String TAG_SCRIPT_CALL_FUNCTION = "f";
    public static final String TAG_SCRIPT_CALL_NAMESPACE = "ns";
    public static final String TAG_SCRIPT_PARAMETER = "p";  
    public static final String TAG_SCRIPT_ELEMENT = "elt";
    public static final String TAG_SCRIPT_ELEMENT_ID = "id";
    public static final String TAG_SCRIPT_KEYWOED = "kw";
    public static final String TAG_SCRIPT_SET = "set";
    public static final String TAG_SCRIPT_VAR = "v";
    public static final String TAG_SCRIPT_OPERATOR = "o";
    public static final String TAG_SCRIPT_RIGHT = "r";
    public static final String TAG_SCRIPT_LEFT = "l";
    public static final String TAG_SCRIPT_IF = "i";
    public static final String TAG_SCRIPT_THEN = "t";
    public static final String TAG_SCRIPT_CONDITIONS = "cds";
    public static final String TAG_SCRIPT_CONDITION = "cd";
    
    //Common behavior attributes
    public static final String TAG_SCRIPT_NAME = "n";
    public static final String TAG_SCRIPT_TYPE = "t";
    
    
    public static final String IMPORT = "import";
    public static final String EXPORT = "export";
    public static final String IMPORT_EXPORT = "import_export";
    
    public static final String FALSE = "false";
    public static final String TRUE = "true";

}