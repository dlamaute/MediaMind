package com.MediaMind;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.applet.*;
import java.applet.*;
import java.net.*;

public class songAnalytics {
	
	/* Matches mood input from emotiv to song by using valence and arousal values from musicover.
	 */
		static String[] Names;
		static double exc, ltexc;
		
		public static double[] getExcites(){
			double[] exciteValues = {exc, ltexc};
			return exciteValues;
		}

		private static final int OPTIONS = 3;


		private void musicMatcher() {
			//	Get an arraylist of song names from spotify.
			ArrayList<String> songNames = spotifySongs();
			int numSongs = songNames.size();
			//	Get quantitative data in format [valence1, valence2, ..., valenceN   ;  arousal1, arousal2, ..., arousalN]
			double[][] quantValues = musicoveryRate(songNames);
			double[] valence = new double[numSongs];
			double[] arousal = new double[numSongs];
			for (int i=0;i<numSongs;i++) {
				valence[i] = quantValues[0][i];
				arousal[i] = quantValues[1][i];
			}
			//	Get the emotiv data on [excitement, long term excitement]
			double[] exciteValues = getExcites();
			double excitement = exciteValues[0];
			double excitementLT = exciteValues[1];
			//	[First choice, Second choice, Third choice]
			int[] songID = songMatch(valence, arousal, excitement, excitementLT);
			String[] Names = new String[OPTIONS];
			for (int i=0;i<OPTIONS;i++) {
				Names[i] = songNames.get(songID[i]);
			}		


			//	************
			//	Integrate with Pebble by passing in Names
			//	Return songToPlay
			//	The Pebble must return filename
			//	************

			//		//	Play the song specified by the songname.
			//		playSong(songToPlay);
			//		boolean shouldSkip = skipDet();
			//		//	*********REMEMBER TO CAP SKIPS*********
			//		//	**** what is it skipping to
			//		//	If input boolean is true, skip the song.
			//		skipSong(shouldSkip);
		}


		private ArrayList<String> spotifySongs() {
			Random rand = new Random();
			String[] songarray = {"Stairway to Heaven","Walking on Sunshine","Somewhere Only We Know",
					"Instant Crush","Born to Die","Stuck Inside of Mobile With the Memphis Blues Again","Mr. Blue Sky",
					"Oxygene, Pt. 2","Take a Walk","Take Me to Church","Heroes","Telescope","Creep","Purple Haze",
					"Blue Monday","I'm in Love Again","We Used to Wait","Real Situation"};
			ArrayList<String> songs = new ArrayList<String>();
			for (int i=0;i<songarray.length;i++) {
				songs.add(songarray[i]);
			}
			while(songs.size() > 8) songs.remove(rand.nextInt(songs.size()-1));
			return songs;
		}


		private int[] songMatch(double[] valence, double[] arousal, double excitement, double excitementLT) {
			int numSongs = valence.length;
			double distance = 2;
			int[] songID = new int[numSongs];
			for(int i=0;i<OPTIONS;i++) songID[i]=0;
			double[] difIDs = getDifs(valence, arousal, excitement, excitementLT, distance, numSongs);
			songID = getIDs(songID, valence, arousal, excitement, excitementLT, distance, difIDs, numSongs);
			return songID;
		}


		private double[] getDifs(double[] valence, double[] arousal, double excitement, 
				double excitementLT, double distance, int numSongs) {
			double[] difIDs = new double[numSongs];
			for (int i=0;i<numSongs;i++) {
				double LTdif = Math.abs(valence[i]-excitementLT);
				double exDif = Math.abs(arousal[i]-excitement);
				double difSq = Math.pow(LTdif,2)+Math.pow(exDif,2);
				difIDs[i] = difSq;
			}
			return difIDs;
		}



		private int[] getIDs(int[] songID, double[] valence, double[] arousal, double excitement, 
				double excitementLT, double distance, double[] difIDs, int numSongs) {
			for (int i=0;i<OPTIONS;i++) {

				double smallest = difIDs[0];
				int index = 0;
				for(int j=1;i<numSongs;i++) {
					if (difIDs[j] < smallest) {
						smallest = difIDs[j];
						index = j;
					}
				}
				songID[i] = index;
				difIDs[index] = 2;
			}
			return songID;
		}


		protected static void playSong(String clipName) {
			try {
				AudioClip clip = Applet.newAudioClip(new URL(clipName));
				clip.play();
			} catch (MalformedURLException murle) {
				System.out.println(murle);
			}
		}


		private double [][] musicoveryRate(ArrayList<String> songNames){
			int numSongs = songNames.size();
			double[][] quantValues = new double[2][numSongs];
			double valence = 0;
			double arousal = 0;
			for(int i = 0; i < numSongs; i++){
				String input = songNames.get(i);
				if(input == "Stairway to Heaven"){
					valence = .3;
					arousal = .8;
				}
				if(input == "Walking on Sunshine"){
					valence = .8;
					arousal = .6;
				}
				if(input == "Somewhere Only We Know"){
					valence = .4;
					arousal = .6;
				}
				if(input == "Instant Crush"){
					valence = .9;
					arousal = .5;
				}
				if(input == "Born to Die"){
					valence = .5;
					arousal = .5;
				}
				if(input == "Stuck Inside of Mobile With the Memphis Blues Again"){
					valence = .2;
					arousal = .4;
				}
				if(input == "Mr. Blue Sky"){
					valence = .6;
					arousal = .8;
				}
				if(input == "Oxygene, Pt. 2"){
					valence = .6;
					arousal = .7;
				}
				if(input == "Take a Walk"){
					valence = .5;
					arousal = .8;
				}
				if(input == "Take Me to Church"){
					valence = .5;
					arousal = .2;
				}
				if(input == "Heroes"){
					valence = .1;
					arousal = .9;
				}
				if(input == "Telescope"){
					valence = .2;
					arousal = .8;
				}
				if(input == "Creep"){
					valence = .1;
					arousal = .8;
				}
				if(input == "Purple Haze"){
					valence = .7;
					arousal = .6;
				}
				if(input == "Blue Monday"){
					valence = .9;
					arousal = .5;
				}
				if(input == "I'm in Love Again"){
					valence = .9;
					arousal = .1;
				}
				if(input == "We Used to Wait"){
					valence = .6;
					arousal = .6;
				}
				if(input == "Real Situation"){
					valence = .3;
					arousal = .8;
				}
				quantValues[0][i] = valence;
				quantValues[1][i] = arousal;
			} 
			return quantValues;
		}

		private String[] musicovery(ArrayList<String> songNames){
			int numSongs = songNames.size();
			String[] subtitles = new String[numSongs];
			String subtitle = "";
			for(int i = 0; i < numSongs; i++){
				String input = songNames.get(i);
				if(input == "Stairway to Heaven"){
					subtitle = "Led Zeppelin";
				}
				if(input == "Walking on Sunshine"){
					subtitle = "Katrina and the Waves";
				}
				if(input == "Somewhere Only We Know"){
					subtitle = "Keane";
				}
				if(input == "Instant Crush"){
					subtitle = "Daft Punk";
				}
				if(input == "Born to Die"){
					subtitle = "Lana Del Rey";
				}
				if(input == "Stuck Inside of Mobile With the Memphis Blues Again"){
					subtitle = "Bob Dylan";
				}
				if(input == "Mr. Blue Sky"){
					subtitle = "Electric Light Orchestra";
				}
				if(input == "Oxygene, Pt. 2"){
					subtitle = "Jean Michel Jarre";
				}
				if(input == "Take a Walk"){
					subtitle = "Passion Pit";
				}
				if(input == "Take Me to Church"){
					subtitle = "Hozier";
				}
				if(input == "Heroes"){
					subtitle = "David Bowie";
				}
				if(input == "Telescope"){
					subtitle = "Cage The Elephant";
				}
				if(input == "Creep"){
					subtitle = "Radiohead";
				}
				if(input == "Purple Haze"){
					subtitle = "The Jimi Hendrix Experience";
				}
				if(input == "Blue Monday"){
					subtitle = "New Order";
				}
				if(input == "I'm in Love Again"){
					subtitle = "Tony Bennett";
				}
				if(input == "We Used to Wait"){
					subtitle = "Arcade Fire";
				}
				if(input == "Real Situation"){
					subtitle = "Bob Marley";
				}

				subtitles[i] = subtitle;
			} 
			return subtitles;
		}

		// put on cap on skipping in higher level
		//	private boolean skipDet() {
		//		int totalTime = 15000;
		//		int numChecks = 30;
		//		long pauseTime = totalTime/numChecks;
		//		//	Check Frustration values from emotiv
		//		double frus0 = checkFrustration();
		//		double threshold = 0.3;
		//
		//		for (int i=0;i<numChecks;i++) {
		//			//	Check Frustration values from emotiv
		//			double frustration = checkFrustration();
		//			double delta = frustration-frus0;
		//			if (delta>threshold) return true;
		//			Thread.sleep(pauseTime);
		//		}
		//		return false;
		//	}


}
