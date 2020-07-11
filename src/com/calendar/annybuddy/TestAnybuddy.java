package com.calendar.annybuddy;

import java.util.Date ;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestAnybuddy {
	public static final String FREE = "free";
	static String centerId;
	public static void main(String[] args) throws Exception {
		JSONArray slots = new JSONArray();
		try {
			System.setProperty("webdriver.gecko.driver", "./lib/geckodriver.exe");
	        FirefoxDriver driver = new FirefoxDriver();
	        String url="https://www.eversports.de/widget/w/tb4wwe";
	        driver.get(url);
	        centerId=url.substring(url.lastIndexOf("/")+1,url.length());
	        //attendre la fin de chargement de DOM 
	        Thread.sleep(2500);
	        
	        List<WebElement> trsCourt = driver.findElements(By.xpath(".//tr[@class='court']"));
	        for (WebElement trCourt : trsCourt) 
	        { 
	        	 List<WebElement> tdsCourt = trCourt.findElements(By.tagName("td"));
	        	for(int i=1;i<tdsCourt.size();i++) {
	        		slots.put(getSlot(tdsCourt.get(i)));
	        		
	        	}
		   
	        }
	        
	    } catch (NoClassDefFoundError ex) {
	        System.out.println("error: " + ex.getStackTrace());
	    }
	     System.out.println(slots);
	     fileOutPut(slots);

	}
	
	/**
	 * creation d'un objet contient les données d'un slot
	 * @param slotParams webElement contient les information d'un td
	 * return un objet json
	 */
	
	private static JSONObject  getSlot(WebElement slotParams){
		JSONObject   slot = new JSONObject  ();
		try {
		    slot.put("centerId",centerId);
			slot.put("facilityId",slotParams.getAttribute("data-court"));
			slot.put("isAvailable",statusCourt(slotParams.getAttribute("data-state")));
			slot.put("startTime",getDate(slotParams.getAttribute("data-date"),slotParams.getAttribute("data-start")));
			slot.put("endTime",getDate(slotParams.getAttribute("data-date"),slotParams.getAttribute("data-end"))); 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return slot ;
	}
	
	/**
	 * return status de court
	 * @param state type string le data recuperer de td
	 * return Boolean 
	 */
	private static Boolean statusCourt(String state) {
		return state.equals(FREE);
	}
	
	/**
	 * return String aprés l ajout de :
	 * @param string de contient 4 chiffre de type hhmm
	 * return string sous form hh:mm 
	 */
	
	private static String addChar(String str) {
	    return str.substring(0, 2) + ":" + str.substring(2);
	}
	
	/**
	 * return String aprés la concatenation
	 * @param string sous forme AAAA-MM-DD
	 * @param string sous forme hhmm
	 * return string sous form AAAA-MM-DDThh:mm 
	 */
	
	private static String getDate(String date,String time) {
		return date+"T"+addChar(time);
	}
	
	/**
	 * void
	 * @param jsonArray jsoon array contient tous les slots
	 */
	private static void fileOutPut(JSONArray slotsOutput) {
		PrintWriter writer;
		try {
			writer = new PrintWriter("slots.json", "UTF-8");
			writer.println(slotsOutput);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

}
