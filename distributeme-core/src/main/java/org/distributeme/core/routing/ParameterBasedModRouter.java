package org.distributeme.core.routing;

import net.anotheria.util.StringUtils;
import org.distributeme.core.ClientSideCallContext;

import java.util.List;

/**
 * This router is based on numeric parameters and their mod value.
 *
 * @author lrosenberg.
 * @version $Id: $Id
 */
public class ParameterBasedModRouter extends AbstractRouter implements Router{

	/**
	 * Mod based for routing.
	 */
	private int mod;
	/**
	 * Position of the parameter to use in mod.
	 */
	private int parameterPosition;
	
	/** {@inheritDoc} */
	@Override
	public String getServiceIdForCall(ClientSideCallContext callContext) {
		List<?> parameters = callContext.getParameters();
		if (parameters.size()<parameterPosition)
			throw new AssertionError("Inproperly configured router, parameter count is less than expected - actual: "+parameters.size()+", expected: "+parameterPosition);
		
		Object parameter = parameters.get(parameterPosition);
		long parameterValue = getModableValue(parameter);
		return callContext.getServiceId() + "_"+(parameterValue % mod);
	}

	/**
	 * Returns parameter value converted to Long actually.
	 * For further <a>mod</a> operation.
	 *
	 * @param o  object to convert
	 * @return long value
	 */
	protected long getModableValue(Object o){
		if (o instanceof Integer)
			return ((Integer)o).longValue();
		if (o instanceof Long)
			return (Long) o;
		if (o instanceof Boolean)
			return (Boolean) o ? 2 : 1;
		if (o==null)
			throw new AssertionError("Null object is not supported");
		throw new AssertionError("Object "+o+" of type "+o.getClass()+" is not supported");
		
	}

	/** {@inheritDoc} */
	@Override
	public void customize(String parameter) {
		try{
			String[] t = StringUtils.tokenize(parameter, ',');
			mod = Integer.parseInt(t[0]);
			parameterPosition = Integer.parseInt(t[1]);
		}catch(NumberFormatException e){
			throw new AssertionError("Customization parameter does not consist of two comma-separated numbers "+parameter);
		}catch(NullPointerException e){
			throw new AssertionError("Customization parameter is obviously null "+parameter);
		}catch(ArrayIndexOutOfBoundsException e){
			throw new AssertionError("Customization parameter does not consist of two comma-separated numbers "+parameter);
		}
	}

	/**
	 * <p>Getter for the field <code>mod</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMod() {
		return mod;
	}

	/**
	 * <p>Getter for the field <code>parameterPosition</code>.</p>
	 *
	 * @return a int.
	 */
	public int getParameterPosition() {
		return parameterPosition;
	}

}
