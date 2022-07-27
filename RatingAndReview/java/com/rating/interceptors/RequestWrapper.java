package com.rating.interceptors;

import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public final class RequestWrapper extends HttpServletRequestWrapper {


	public RequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}

	/**public String[] getParameterValues(String parameter) {

		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = cleanXSS(values[i]);
		}
		
		return encodedValues;
	}

	public String getParameter(String parameter) {
		
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		return cleanXSS(value);
	}*/

	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value == null)
			return null;
		return cleanXSS(value);

	}
    public boolean getValidationStatus(HttpServletRequest servletRequest)
    {
	  boolean status=false;
	  Enumeration<String> values = servletRequest.getParameterNames();
      while(values.hasMoreElements())
      {
   	   String value=values.nextElement();
       if(stripXSSAndSql(servletRequest.getParameter(value)))
       {    	   
     	status=true; 
     	break;
       }
      } 
     
      return status;
  }
	/**private String cleanXSS(String value) {
		// You'll need to remove the spaces from the html entities below
		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		value = value.replaceAll("'", "& #39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']","\"\"");
		value = value.replaceAll("script", "");
		return value;
	}*/
	 private String cleanXSS(String value) {
	        if (value != null) {
	            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
	            // avoid encoded attacks.
	            // value = ESAPI.encoder().canonicalize(value);
	 
	            // Avoid null characters
	        	value = value.replaceAll("<", "").replaceAll(">", "");
	    		value = value.replaceAll("\\(", "").replaceAll("\\)", "");
	    		value = value.replaceAll("'", "");
	    		value = value.replaceAll("script", "");
	    		value = value.replaceAll("eval", "");
	    		value = value.replaceAll("<", "").replaceAll(">", "");
	    		value = value.replaceAll("\\(", "").replaceAll("\\)", "");
	            // Avoid anything between script tags
	            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            // Avoid anything in a src='...' type of expression
	            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            // Remove any lonesome </script> tag
	            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            // Remove any lonesome <script ...> tag
	            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            // Avoid eval(...) expressions
	            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            // Avoid expression(...) expressions
	            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            // Avoid javascript:... expressions
	            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            // Avoid vbscript:... expressions
	            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
	            value = scriptPattern.matcher(value).replaceAll("");
	 
	            // Avoid onload= expressions
	            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            value = scriptPattern.matcher(value).replaceAll("");
	        }
	        return value;
	    }
	 public boolean stripXSSAndSql(String value) 
	    {
	    	boolean flag = false;
	    	System.out.println("-------------------------------------"+value);
	        if (value != null) {
	        	 // Avoid anything between script tags
	            Pattern scriptPattern = Pattern.compile(
	                    "<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }
	            // Avoid anything in a
	            // src="http://www.yihaomen.com/article/java/..." type of
	            // e-xpression
	            scriptPattern = Pattern.compile("src[\r\n| | ]*=[\r\n| | ]*[\\\"|\\\'](.*?)[\\\"|\\\']",
	                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }
	            // Remove any lonesome </script> tag
	            scriptPattern = Pattern.compile("</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }
	            // Remove any lonesome <script ...> tag
	            scriptPattern = Pattern.compile("<[\r\n| | ]*script(.*?)>",
	                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }
	            // Avoid eval(...) expressions
	            scriptPattern = Pattern.compile("eval\\((.*?)\\)",
	                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }
	            // Avoid e-xpression(...) expressions
	            scriptPattern = Pattern.compile("e-xpression\\((.*?)\\)",
	                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }
	            // Avoid javascript:... expressions
	            scriptPattern = Pattern.compile("javascript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }
	            // Avoid vbscript:... expressions
	            scriptPattern = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }
	            // Avoid onload= expressions
	            scriptPattern = Pattern.compile("onload(.*?)=",
	                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	            flag = scriptPattern.matcher(value).find();
	            if (flag) {
	                return flag;
	            }        
	        }
	        return flag;

	    }	

}
