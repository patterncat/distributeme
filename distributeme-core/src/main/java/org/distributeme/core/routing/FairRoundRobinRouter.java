package org.distributeme.core.routing;

import org.distributeme.core.ClientSideCallContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This router sends each call to another instance. It is useful if you want to cluster a service
 * @author lrosenberg
 */
public class FairRoundRobinRouter implements Router{
	/**
	 * Max mod parameter.
	 */
	private long MAX = 0;

	private int MOD = 0;
	/**
	 * Callcounter. 
	 */
	private AtomicLong callCounter = new AtomicLong(0);
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(FairRoundRobinRouter.class);
	
	@Override
	public void customize(String parameter) {
		try{
			MOD = Integer.parseInt(parameter);
			MAX = 1000L*MOD;
		}catch(NumberFormatException e){
			log.error("Can't set customization parameter "+parameter+", send all traffic to default instance");
		}
	}

	@Override
	public String getServiceIdForCall(ClientSideCallContext callContext) {
		if (MAX==0)
			return callContext.getServiceId();
		long fromCounter = callCounter.incrementAndGet();
		if (fromCounter>=MAX){
			long oldCounter = fromCounter;
			fromCounter = 0;
			callCounter.compareAndSet(oldCounter, 0);
		}
		return callContext.getServiceId()+"_"+(fromCounter%MOD);
	}

}