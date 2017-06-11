package offloadingAlgos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import simulation.MobileDevice;
import simulation.WirelessStation;

public class OffloadingAlgos {

	// matrix contains -1: not initialzied; 0: offloading; 1: offload
	private static List<Integer> matrix; 
	
	static{
		matrix = Collections.synchronizedList(new ArrayList<Integer>());
	}
	
	public void offloadingDecisionByMEC(MobileDevice device, int port) {
		//		HashMap<String,Integer> stationInfo = getWirelessStationInfo(content);
		int bestPort = calculateOverheadForMEC(port, device);
		device.setTargetPort(bestPort);
		setOffloadingWeight(device);
	}

	public void offloadingDecisionByDynamic(MobileDevice device) {
		Random rand = new Random();
		if( device.getId() >= matrix.size() ) {
			for( int i = matrix.size(); i <= device.getId(); ++i) {
				matrix.add(-1);
			}
		}
		if( matrix.get(device.getId()) == -1) {
			matrix.set(device.getId(), rand.nextInt(2));
		}
		if( matrix.get(device.getId()) == 0 && !isBeneficial(device) ) {
			device.setOffloadWeight(0);
		}
	}

	public boolean isBeneficial(MobileDevice device) {
		Hashtable<Integer,Integer> connectionInfo = WirelessStation.getConnectionInfo();
		double localTimeOverhead = device.getCompletelyLocalySpan();
		double localEnergyOverhead = localTimeOverhead/2;
		double minCloudOverhead = Double.MAX_VALUE;
		int transferTimeSpan = -1;
		int bestPort = -1;
		for( int port : WirelessStation.getPorts()) {
			int lossFactor = connectionInfo.get(port) * 2;
			double curOverhead = 6 
					+  ( (double) device.getDataSize() / (double)WirelessStation.getBandWidth()) * lossFactor
					+  ( (double) device.getDataSize() / (double)WirelessStation.getBandWidth()) / 3;
			if( curOverhead < minCloudOverhead) {
				minCloudOverhead = curOverhead;
				transferTimeSpan = device.getDataSize() / WirelessStation.getBandWidth() * lossFactor;
				bestPort = port;
			}
		}
		if( minCloudOverhead < (localTimeOverhead + localEnergyOverhead)) {
			device.setTargetPort(bestPort);
			device.setCloudTimeSpan(6);
			device.setTransferTimeSpan(transferTimeSpan ) ;
			device.setOffloadWeight(1);
			return true;
		}
		return false;
	} 
	
	/***
	 * 
	 * @param connectionInfo
	 * @param port
	 * @param device
	 * @return
	 */
	public int calculateOverheadForMEC(int port, MobileDevice device) {
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
