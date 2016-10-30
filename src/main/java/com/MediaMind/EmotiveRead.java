package com.MediaMind;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;


public class EmotiveRead {
	
	static OSCReceiver rec = null;
	static DatagramChannel dch = null;
	static String[] emotions;

	public static String[] readEmotive() {
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
	                      emotions[15] = (String)msg.getArg(1);
	                      emotions[16] = (String)msg.getArg(2);
	                      emotions[14] = (String)msg.getArg(3);
	                      emotions[17] = (String)msg.getArg(4);
	                      emotions[18] = (String)msg.getArg(5);
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
		return emotions;

	}

}
