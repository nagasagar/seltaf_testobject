package com.seltaf.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.stream.*;

import org.apache.log4j.Logger;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.seltaf.core.Filter;
import com.seltaf.core.SeltafContextManager;
import com.seltaf.util.internal.entity.TestEntity;
import com.seltaf.utils.XMLWrapper;



public class XmlObjectDataHelper {
	private static Logger logger = Logger.getLogger(XmlObjectDataHelper.class);
	
	/**
     * Create Entity Objects based on data in spreadsheet.
     *
     * <p/>This method is only for Data Provider. Because it also filer the data based on the
     * dpTagsInclude/dpTagsExclude which is defined in testng configuration file
     *
     * @param   clazz
     * @param   entityClazzMap
     * @param   filename
     * @param   filter
     *
     * @return
     *
     * @throws  Exception
     */
    public static Iterator<Object[]> getEntitiesFromxml(final LinkedHashMap<String, Class<?>> entityClazzMap,String testname, final String filename)
        throws Exception {
    	entityClazzMap.put("XMLWrapper", XMLWrapper.class);
    	 Class<?>[] classes = entityClazzMap.values().toArray(new Class[0]);
    	 JAXBContext jc = JAXBContext.newInstance(classes);
        Iterator<Object[]> dataIterator = getDataFromxml(jc,testname,filename, null, true);

        List<Object[]> list = parseEntityData(dataIterator, classes);

        return list.iterator();
    }
	
	/**
     * Create Entity Objects based on data in spreadsheet.
     *
     * <p/>This method is only for Data Provider. Because it also filer the data based on the
     * dpTagsInclude/dpTagsExclude which is defined in testng configuration file
     *
     * @param   clazz
     * @param   entityClazzMap
     * @param   filename
     * @param   filter
     *
     * @return
     *
     * @throws  Exception
     */
    public static Iterator<Object[]> getEntitiesFromxml(final LinkedHashMap<String, Class<?>> entityClazzMap,String testname, final String filename, final Filter filter)
        throws Exception {
    	if(filter!=null && !filter.getName().toLowerCase().contains("testentity"))
    	{
    		throw new Exception("for XMLObjectDatahelper filtering is currently supported only for testentity parameters");
    	}
    	entityClazzMap.put("XMLWrapper", XMLWrapper.class);
    	 Class<?>[] classes = entityClazzMap.values().toArray(new Class[0]);
    	 JAXBContext jc = JAXBContext.newInstance(classes);
        Iterator<Object[]> dataIterator = getDataFromxml(jc,testname,filename, filter, true);

        List<Object[]> list = parseEntityData(dataIterator, classes);

        if(list.size()==0)
        {
        	logger.info("None of the data matches filter criteria");
        }
        return list.iterator();
        
    }
    
    /**
     * Reads data from spreadsheet. If sheetName and sheetNumber both are supplied the sheetName takes precedence. Put
     * the excel sheet in the same folder as the test case and specify clazz as <code>this.getClass()</code> .
     */
    public static synchronized Iterator<Object[]> getEntitiesFromxml(final String dataobjectspackagename, final String testname,final String filename,final Filter filter) throws Exception {
    	if(filter!=null && !filter.getName().toLowerCase().contains("testentity"))
    	{
    		throw new Exception("for XMLObjectDatahelper filtering is currently supported only for testentity parameters");
    	}
    	 ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
		 Set<ClassPath.ClassInfo> topLevelClasses =
	                classPath.getTopLevelClasses(dataobjectspackagename);
		 int toSize=topLevelClasses.size();
		 Class<?>[] classes=new Class[toSize+1];
		 int i=0;
		 for (ClassInfo clz :topLevelClasses) {
			 classes[i]=clz.load();
			 i++;
		 }
		 classes[i] = XMLWrapper.class;
		 JAXBContext jc = JAXBContext.newInstance(classes);
         Iterator<Object[]> dataIterator = getDataFromxml(jc,testname, filename, filter, true);

         List<Object[]> list = parseEntityData(dataIterator, classes);

         return list.iterator();
    }
    
    /**
     * Reads data from spreadsheet. If sheetName and sheetNumber both are supplied the sheetName takes precedence. Put
     * the excel sheet in the same folder as the test case and specify clazz as <code>this.getClass()</code> .
     */
    public static synchronized Iterator<Object[]> getEntitiesFromxml(final String dataobjectspackagename, final String testname,final String filename) throws Exception {
    	
    	 ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
		 Set<ClassPath.ClassInfo> topLevelClasses =
	                classPath.getTopLevelClasses(dataobjectspackagename);
		 int toSize=topLevelClasses.size();
		 Class<?>[] classes=new Class[toSize+2];
		 int i=0;
		 for (ClassInfo clz :topLevelClasses) {
			 classes[i]=clz.load();
			 i++;
		 }
		 classes[i] = XMLWrapper.class;
		 classes[i+1] = TestEntity.class;
		 JAXBContext jc = JAXBContext.newInstance(classes);
         Iterator<Object[]> dataIterator = getDataFromxml(jc,testname, filename, null, true);

         List<Object[]> list = parseEntityData(dataIterator, classes);

         return list.iterator();
    }
    
    public static synchronized Iterator<Object[]> getDataFromxml(JAXBContext jc,final String testname, final String filename,
            final Filter filter,final boolean supportDPFilter) throws Exception {

    	
    	if(filter!=null && !filter.getName().toLowerCase().contains("testentity"))
    	{
    		throw new Exception("for XMLObjectDatahelper filtering is currently supported only for testentity parameters");
    	}
        System.gc();

        // CSVHelper handle CSV Files
        if (filename.toLowerCase().endsWith(".xml")) {
            return readDataFromxml(jc,testname, filename, filter, supportDPFilter);
        } else {
            throw new Exception("illegal file format, XmlDataHelper is suuposed to be used woth xml files");
        }
    }
    
    public static synchronized Iterator<Object[]> readDataFromxml(JAXBContext jc,final String testname, final String filename,
            Filter filter,final boolean supportDPFilter) throws Exception {
    	if(filter!=null && !filter.getName().toLowerCase().contains("testentity"))
    	{
    		throw new Exception("for XMLObjectDatahelper filtering is currently supported only for testentity parameters");
    	}
    	List<Object[]> testdata = new ArrayList<Object[]>();
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		XMLInputFactory xif = XMLInputFactory.newInstance();
        StreamSource xml = new StreamSource(filename);
        XMLStreamReader xsr = xif.createXMLStreamReader(xml);
        while(xsr.hasNext()) {
            if(xsr.isStartElement() && testname.equals(xsr.getLocalName())) {
            	xsr.nextTag();
                if(xsr.getLocalName().equals("dataset")) {
                	while(xsr.isStartElement()&&xsr.hasNext())
                	{
                		XMLWrapper<?> wrapper = (XMLWrapper<?>) unmarshaller.unmarshal(xsr,XMLWrapper.class).getValue();
                		Object[] x = wrapper.getItems().toArray();
                		// To support include tags and exclude tags
                        
                		if(supportDPFilter)
                		{
                			Filter dpFilter = XmlObjectDataHelper.getDPFilter();

                            if (dpFilter != null) {
                                if (filter == null) {
                                    filter = dpFilter;
                                } else {
                                    filter = Filter.and(filter, dpFilter);
                                }
                            }
                		}
                		boolean filterflag = isfiltered(x,filter,supportDPFilter);
                		if(filterflag){
                			testdata.add(x);
                		}
                		xsr.next();
                	}
                   
                }
                else
                {
                	throw(new Exception("testdata.xml not formatted datasets should be immediate children of test node whose name is passeed to the getTestData() method"));
                }
            }
            xsr.next();
         }
        
		
        return testdata.iterator();
    
    }

    public static synchronized boolean isfiltered(Object[] x,Filter f, boolean supportDPFilter) {
    	boolean flag = false;
    	for(Object o:x) {
        	if(o.getClass()==TestEntity.class)
        		{
        		TestEntity t = (TestEntity) o;
        			Map<String, Object> testentitymap  = t.toMap();
        			if (supportDPFilter) {
                        SpreadSheetHelper.formatDPTags(testentitymap);
                    }
        			if(f.match(testentitymap))
        				flag=true;
        		}
        	}
            
       
		return flag;
	}

	private static List<Object[]> parseEntityData(final Iterator<Object[]> dataIterator,final Class<?>[] classes) throws Exception {
        List<Object[]> list = new ArrayList<Object[]>();

        while (dataIterator.hasNext()) {

            Object[] rowDataArray = dataIterator.next();

            List<Object> rowData = new ArrayList<Object>();
            
            if (classes != null) {
                for(Object data:rowDataArray) {
                	for(Class<?> clazz : classes) 
                	{
                		if(data.getClass()==clazz)
                		{
                			rowData.add(data);
                		}
                	}
                    
                }
            }

            list.add(rowData.toArray(new Object[] {rowData.size()}));
        }

        return list;
    }
	
	 protected static Filter getDPFilter() {
	        String includedTags = SeltafContextManager.getGlobalContext().getDPTagsInclude();
	        String excludedTags = SeltafContextManager.getGlobalContext().getDPTagsExclude();

	        Filter dpFilter = null;
	        if (includedTags != null && includedTags.trim().length() > 0) {
	            String[] includeTagsArray = includedTags.split(",");
	            for (int idx = 0; includeTagsArray.length > 0 && idx < includeTagsArray.length; idx++) {
	                if (dpFilter == null) {
	                    dpFilter = Filter.containsIgnoreCase(TestEntity.TEST_DP_TAGS,
	                            "[" + includeTagsArray[0].trim() + "]");
	                } else {
	                    dpFilter = Filter.or(dpFilter,
	                            Filter.containsIgnoreCase(TestEntity.TEST_DP_TAGS,
	                                "[" + includeTagsArray[idx].trim() + "]"));
	                }
	            }
	        }

	        if (excludedTags != null && excludedTags.trim().length() > 0) {
	            String[] excludeTagsArray = excludedTags.split(",");
	            for (int idx = 0; excludeTagsArray.length > 0 && idx < excludeTagsArray.length; idx++) {
	                if (dpFilter == null) {
	                    dpFilter = Filter.not(Filter.containsIgnoreCase(TestEntity.TEST_DP_TAGS,
	                                "[" + excludeTagsArray[idx].trim() + "]"));
	                } else {
	                    dpFilter = Filter.and(dpFilter,
	                            Filter.not(
	                                Filter.containsIgnoreCase(TestEntity.TEST_DP_TAGS,
	                                    "[" + excludeTagsArray[idx].trim() + "]")));
	                }
	            }
	        }

	        return dpFilter;
	    }

}
