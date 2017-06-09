package offloadingAlgos;

import java.util.HashMap;

import simulation.MobileDevice;
import simulation.WirelessStation;

public class OffloadingAlgos {
	
	private final String channelNum = "channelNum",
							    bandWidth = "bandWidth",
							    lossFactor = "lossFactor";
	
	public void offloadingDecision(MobileDevice device, int port) {
//				HashMap<String,Integer> stationInfo = getWirelessStationInfo(content);
				HashMap<Integer,Integer> connectionInfo = WirelessStation.getConnectionInfo();
				int bestPort = calculateOverhead(connectionInfo, port, device);
				device.setTargetPort(bestPort);
				setOffloadingWeight(device);
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
	
	public int calculateOverhead(HashMap<Integer,Integer> connectionInfo, int port, MobileDevice device) {
		int currentChannelConnectionNum = connectionInfo.get(port);
		double currentOverHead = (WirelessStation.getLossFactor() * currentChannelConnectionNum ) / WirelessStation.getBandWidth();
		double conditionalOverhead = (WirelessStation.getLossFactor() * ( currentChannelConnectionNum - 1) ) / WirelessStation.getBandWidth();
		double[] overheads = new double[WirelessStation.getPorts().length - 1];
		int[] ports = new int[overheads.length];
		int index = 0;
		for( int otherPort : WirelessStation.getPorts() ) {
			if( otherPort == port) continue;
			double curOverhead = (WirelessStation.getLossFactor() * WirelessStation.getConnectionInfo().get(otherPort) ) / WirelessStation.getBandWidth();
			currentOverHead += curOverhead;
			double potentialOverhead = (WirelessStation.getLossFactor() * ( WirelessStation.getConnectionInfo().get(otherPort) + 1) ) / WirelessStation.getBandWidth();
			for( int i : WirelessStation.getPorts() ) {
				if( i == otherPort || i == port) continue;
				potentialOverhead += (WirelessStation.getLossFactor() * WirelessStation.getConnectionInfo().get(i) ) / WirelessStation.getBandWidth();
			}
			overheads[index] = conditionalOverhead + potentialOverhead;
			ports[index] = otherPort;
			index++;
		}
		double minOverhead = currentOverHead;
		int bestPort = port;
		System.out.println("initialOverhead:   "+currentOverHead);
		for( int i = 0; i < overheads.length; ++i) {
			System.out.println("Overhead:   "+i+overheads[i]);
			if( minOverhead > overheads[i]) {
				minOverhead = overheads[i];
				bestPort = ports[i];
			}
		}
		return bestPort;
	}
	
	public void setOffloadingWeight(MobileDevice device) {
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
