package com.holstine.viewer;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.serial.Serial;

 
public class SerialIngester extends PApplet {

	private static SerialIngester instance = new SerialIngester();
	Serial myPort; // The serial port
	List<IngestVectorObserver> observers = new ArrayList<IngestVectorObserver>();
	String myString ="";
	static final int LF = 10; // Linefeed in ASCII
	
	
	private SerialIngester(){
		for(String s: Serial.list()) {
			System.out.println(s);
		}
		myPort = new Serial(this, "com6", 9600);
		myPort.clear();
		// Throw out the first reading, in case we started reading
		// in the middle of a string from the sender.
		myString = myPort.readStringUntil(LF);
		myPort.write("rate:90\n");
		
		IngestRunner runner = new IngestRunner();
		Thread t = new Thread(runner);
		t.start();
	}
	
	public static SerialIngester getInstance(){
		return SerialIngester.instance;
	}
	
	public void addObserver(IngestVectorObserver observer){
		this.observers.add(observer);
	}
	
	public void notifyObservers(float[] vector ){
		for(IngestVectorObserver observer:observers){
			observer.newVector(  vector );
			
		}
	}
	
	float[] ingestOneVector(){
		while (myPort.available() > 0) {
			myString = myPort.readStringUntil(LF); 
			if (myString != null) {

				String[] cutupString = myString.split(",");
				if (cutupString.length == 4) {
					String d = cutupString[0];
					String x = cutupString[1];
					String y = cutupString[2];
					String z = cutupString[3];
						
					float[] point = new float[3];
					float dist =   Float.parseFloat(d);
					point[0]= Float.parseFloat(x)*dist;
					point[1]= Float.parseFloat(y)*dist;
					point[2]= Float.parseFloat(z)*dist; 
					System.out.printf(
							"  %12.2f  %12.2f  %12.2f\n", 
						 	point[0],point[1],point[2]) ;
					
					return point;
				}
			}
		}
		return null;
		
		
	}
	
	
	private boolean validateVector(float[] vector) {
		if(vector == null) return false;
		if(Math.abs(vector[0]) <5 ) return false;
		if(Math.abs(vector[1]) < 5 ) return false;
		return true;
	}
	
	class IngestRunner implements Runnable{

		@Override
		public void run() {
			while(true){
				
				float[]  vector =ingestOneVector();
				boolean keep = validateVector(vector);
				if(keep) notifyObservers(vector);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		
		
	}
	
	
	interface IngestVectorObserver {
		void newVector(float[] vector);
	}
}