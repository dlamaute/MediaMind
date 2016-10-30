package com.MediaMind;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.DatagramChannel;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.microsoft.windowsazure.core.utils.KeyStoreType;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.management.*;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.management.configuration.ManagementConfiguration;

import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.MediaMind.songAnalytics;

public class Main extends songAnalytics{
	
	static String uri = "http://mindmedia.cloudapp.net/";
	static String subscriptionId = "600b94ff-3c30-4523-97ee-4acf98ad13b3";
	static String keyStoreLocation = "/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Commands/release.keystore";
	static String keyStorePassword = "@ardVark";
	
	static OSCReceiver rec = null;
	static DatagramChannel dch = null;
	static String[] emotions = {".47", ".12", "0", "0", "0"};
	
	static double exc, ltexc;
	
	private String str, res;
	
	public static double[] getExcites(){
		double[] exciteValues = {exc, ltexc};
		return exciteValues;
	}
	
	public static void main(String[] args) 
		throws IOException, URISyntaxException, ServiceException,
		ParserConfigurationException, SAXException {
		
		Configuration config = ManagementConfiguration.configure(
				new URI(uri),
					subscriptionId,
					keyStoreLocation,
					keyStorePassword,
					KeyStoreType.jks
				);
		
		try{
			InetAddress emotivIP = InetAddress.getByName("127.0.0.1"); 
			final SocketAddress addr = new InetSocketAddress( emotivIP, 7400 );
	        final Object notify = new Object();
	        System.out.println("I'm trying to connect?");
	        dch = DatagramChannel.open();
	        dch.socket().bind( null );    // assigns an automatic local socket address
	        rec = OSCReceiver.newUsing( dch );
	        rec.addOSCListener( new OSCListener() {
	              public void messageReceived( OSCMessage msg, SocketAddress sender, long time )
	              {
	                  if( msg.getName().equals( "status.reply" )) {
	                      System.out.println( "scsynth is running. contains " +
	                          msg.getArg( 14 ) + " unit generators, " +
	                          msg.getArg( 15 ) + " synths, " +
	                          msg.getArg( 16 ) + " groups, " +
	                          msg.getArg( 17 ) + " synth defs.\n" +
	                          "CPU load is " + msg.getArg( 18 ) + "% (average) / " +
	                          msg.getArg( 6 ) + "% (peak)" );
	                      synchronized( notify ) {
	                          notify.notifyAll();
	                      }
	                      emotions[0] = (String)msg.getArg(15);
	                      emotions[1] = (String)msg.getArg(16);
	                      emotions[2] = (String)msg.getArg(14);
	                      emotions[3] = (String)msg.getArg(17);
	                      emotions[4] = (String)msg.getArg(18);
	                  }
	              } 
	          });
	        rec.startListening();
	        synchronized( notify ) {
	              notify.wait( 5000 );
	        }
		}
	        catch( InterruptedException e1 ) {}
	        catch( IOException e2 ) {
	            System.err.println( e2.getLocalizedMessage() );
	        }
	        finally {
	            if( rec != null ) {
	                rec.dispose();
	            } else if( dch != null ) {
	                try {
	                    dch.close();
	                }
	                catch( IOException e4 ) {};
	            }
	        }

		}
{
		//give double: excitement, double: long term excitement
		
		exc = Double.parseDouble(emotions[0]);
		ltexc = Double.parseDouble(emotions[1]);
		
		//retrieve string[]
		String[] mediaChoices = songAnalytics.Names;
		
		//make array become string
		String choices = "";
		for (String s: mediaChoices){
			choices += s+"%";
		}
		
		JSONObject request = new JSONObject();
        try { request.put("GET", choices); }
        catch (JSONException e6){ e6.printStackTrace(); }
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(uri + "SendMedia");
        
		//then, given the 3 songs in array of Strings of song names (& artists?), send String of song titles and artists separated by percentage signs
		//TV show then tv show ep: 3
		//Movies: 3
		
		//send as JSON to pebble
		
		try {
            StringEntity se = new StringEntity(request.toString());
            //se.setContentType("application/json;charset=UTF-8");
            post.setEntity(se);
            String thing1 = EntityUtils.toString(post.getEntity());
            HttpResponse response = client.execute(post);
            String thing = EntityUtils.toString(response.getEntity());
            JSONObject jObj = new JSONObject(thing);
            str = jObj.getString("result");
        } catch (UnsupportedEncodingException e8) {
            e8.printStackTrace();
        } catch (ClientProtocolException e9) {
            e9.printStackTrace();
        } catch (IOException e10) {
            e10.printStackTrace();
        } catch (JSONException e11){
            e11.printStackTrace();
        }
		
		int medChoice = Integer.parseInt(str);
    
      //then need listener for JSON (of int): [1-9], where each corresponds to the position of the song assigned in the array.
      //call method that plays relevant song
		
		String finChoice = mediaChoices[medChoice + 1];
		
        JSONObject request2 = new JSONObject();
        try { request2.put("GET", finChoice); }
        catch (JSONException e7){ e7.printStackTrace(); }
        HttpClient client2 = new DefaultHttpClient();
        HttpPost post2 = new HttpPost(uri + "GetChoice");
        
        try {
            StringEntity se = new StringEntity(request.toString());
            //se.setContentType("application/json;charset=UTF-8");
            post.setEntity(se);
            String thing1 = EntityUtils.toString(post.getEntity());
            HttpResponse response = client.execute(post);
        } catch (UnsupportedEncodingException e12) {
            e12.printStackTrace();
        } catch (ClientProtocolException e13) {
            e13.printStackTrace();
        } catch (IOException e14) {
            e14.printStackTrace();
        } catch (JSONException e15){
            e15.printStackTrace();
        }
        
        String filename = finChoice + ".au";
		songAnalytics.playSong(filename);

	}
		
}

