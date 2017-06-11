package offloadingAlgos;

import java.util.HashMap;
import java.util.Hashtable;

import simulation.MobileDevice;
import simulation.WirelessStation;

public class OffloadingAlgos {

	private final String channelNum = "channelNum",
			bandWidth = "bandWidth",
			lossFactor = "lossFactor";

	public void offloadingDecisionByMEC(MobileDevice device, int port) {
		//		HashMap<String,Integer> stationInfo = getWirelessStationInfo(content);
		Hashtable<Integer,Integer> connectionInfo = WirelessStation.getConnectionInfo();
		int bestPort = calculateOverhead(connectionInfo, port, device);
		device.setTargetPort(bestPort);
		setOffloadingWeight(device);
	}

	public void offloadDecisionByDynamic(MobileDevice[] devices) {
		StringBuilder decisions = new StringBuilder();
	}

	public  HashMap<String,Integer> getWirelessStationInfo(String content) {
		HashMap<String,Integer> res = new HashMap<>();
		String[] info = content.split(" ");
		for( String str : info) {
			if( str.startsWith(channelNum)) 
				res.put(channelNum, Integer.parseInt(str.split(":")[1]));
			else if( str.startsWith(bandWidth)) 
				res.put(bandWidth, Integer.parseInt(str.split(":")[1]));
			else if( str.startsWith(lossFactor)) 
				res.put(lossFactor, Integer.parseInt(str.split(":")[1]));
		}

		return res;
	}



	/***
	 * 
	 * @param connectionInfo
	 * @param port
	 * @param device
	 * @return
	 */
	public int calculateOverhead(Hashtable<Integer,Integer> connectionInfo, int port, MobileDevice device) {
		int currentChannelConnectionNum = WirelessStation.getConnectionInfo().get(port);
		int lossFactor = WirelessStation.getLossFactor();
		int bandWidth = WirelessStation.getBandWidth();
		double currentOverHead = calculateOverhead(lossFactor, currentChannelConnectionNum, bandWidth);
		double conditionalOverhead = calculateOverhead(lossFactor,currentChannelConnectionNum - 1, bandWidth);
		double[]  connections= new double[WirelessStation.getPorts().length - 1];
		double[]  lossFactors = new double[connections.length];
		double[]  overheads = new double[connections.length];
		int[] ports = new int[overheads.length];
		int index = 0;
		for( int otherPort : WirelessStation.getPorts() ) {
			if( otherPort == port) continue;
//			overheads[index] = WirelessStation.getConnectionInfo().get(otherPort);
			connections[index] = WirelessStation.getConnectionInfo().get(otherPort);
			lossFactors[index] = Math.pow(WirelessStation.getLossFactor(), connections[index]);
			ports[index] = otherPort;
			currentOverHead += calculateOverhead(lossFactor, connections[index],bandWidth);
			index++;
		}
		index = 0;
		for( int i = 0; i < overheads.length; ++i) {
			overheads[i] = conditionalOverhead + calculateOverhead(lossFactor, connections[i]+1, bandWidth);
		}
		double minOverhead = currentOverHead;
		int bestPort = port;
//		System.out.println("initialOverhead:   "+currentOverHead);
		for( int i = 0; i < overheads.length; ++i) {
			if( minOverhead > overheads[i]) {
				minOverhead = overheads[i];
				bestPort = ports[i];
			}
		}
		return bestPort;
	}

	public double calculateOverhead(double lossFactor, double connectionNum, double bandWidth) {
		return  (Math.pow((double)lossFactor, (double)connectionNum) * (double)connectionNum) / (double)bandWidth;
	}
	
	public void setOffloadingWeight(MobileDevice device) {

		if( device.getBattery() < 20)
			device.setOffloadWeight(0.9f);
		else {
			device.setOffloadWeight( 100 - device.getBattery());
		}

		if( device.getBattery() > 80)
			device.setOffloadWeight(0.5f);
		else if( device.getBattery() > 60 && device.getBattery() <= 80) 
			device.setOffloadWeight(0.6f);
		else if( device.getBattery() > 40 && device.getBattery() <= 60)
			device.setOffloadWeight(0.7f);
		else if( device.getBattery() > 20 && device.getBattery() <= 40) 
			device.setOffloadWeight(0.8f);
		else 
			device.setOffloadWeight(0.9f);
	}




}
