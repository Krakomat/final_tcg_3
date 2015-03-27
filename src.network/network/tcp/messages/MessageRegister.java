package network.tcp.messages;

import com.jme3.network.serializing.Serializer;

public class MessageRegister {
	
	public static void registerSerializables(){
		Serializer.registerClass(QueryMessage.class);
		Serializer.registerClass(RespondMessage.class);
		Serializer.registerClass(ByteString.class);
	}
}
