package network.tcp.messages;

import java.util.ArrayList;
import java.util.List;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Message that is send to an entity (client/server) as an answer to a query message.
 * 
 * @author Michael
 *
 */
@Serializable
public class RespondMessage extends AbstractMessage {
	/** The Method to be called */
	private Method method;
	/** Parameters of the method */
	private List<ByteString> parameters;

	public RespondMessage() {
		this.parameters = new ArrayList<>();
	}

	public RespondMessage(Method method) {
		this.method = method;
		this.parameters = new ArrayList<>();
	}

	public RespondMessage(Method method, ByteString parameter) {
		this.method = method;
		this.parameters = new ArrayList<>();
		this.parameters.add(parameter);
	}

	public RespondMessage(Method method, List<ByteString> parameters) {
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
	 * Returns the weight of this message in bytes. Allowed are not more than about 32000 bytes.
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
	 * @param receiver
	 */
	public void logSendMessage(String receiver) {
		System.out.println("[" + receiver + "] Received response message: " + this.getMethod() + " (" + this.getBytes() + " bytes)");
	}
}
