package network.tcp.messages;

import java.util.ArrayList;
import java.util.List;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Message that is send to an entity (client/server).
 * 
 * @author Michael
 *
 */
@Serializable
public class QueryMessage extends AbstractMessage {
	/** The Method to be called */
	private Method method;
	/** Parameters of the method */
	private List<ByteString> parameters;

	public QueryMessage() {
		this.parameters = new ArrayList<>();
	}

	public QueryMessage(Method method) {
		this.method = method;
		this.parameters = new ArrayList<>();
	}

	public QueryMessage(Method method, ByteString parameter) {
		this.method = method;
		this.parameters = new ArrayList<>();
		this.parameters.add(parameter);
	}

	public QueryMessage(Method method, List<ByteString> parameters) {
		this.method = method;
		this.parameters = parameters;
	}

	public List<ByteString> getParameters() {
		return parameters;
	}

	public void setParameters(List<ByteString> parameters) {
		this.parameters = parameters;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * Returns the weight of this message in bytes. Allowed are not more than
	 * about 32000 bytes.
	 * 
	 * @return
	 */
	public long getBytes() {
		long bytes = 0;
		for (ByteString b : parameters)
			bytes = bytes + b.copyAsBytes().length;
		return bytes;
	}

	/**
	 * Only prints on the console.
	 * 
	 * @param sender
	 */
	public void logSendMessage(String sender) {
		System.out.println("[" + sender + "] Sending message: " + this.getMethod() + " (" + this.getBytes() + " bytes)");
	}
}
