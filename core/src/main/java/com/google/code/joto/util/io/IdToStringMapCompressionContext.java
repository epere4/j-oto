package com.google.code.joto.util.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * helper class for encoding/decoding String in a contextual compressed way
 */
public class IdToStringMapCompressionContext {
	
	private int idGenerator = 1;
	private ArrayList<String> idToValue = new ArrayList<String>();
	private Map<String,Integer> valueToId = new HashMap<String,Integer>();

	//-------------------------------------------------------------------------

	public IdToStringMapCompressionContext() {
		idToValue.add(null); // 0=null, real values start at index 1
	}

	//-------------------------------------------------------------------------

	public void encode(String value, DataOutputStream out) throws IOException {
		if (value == null) {
			out.writeInt(0);
		} else {
			Integer id = valueToId.get(value);
			if (id != null) {
				out.writeInt(id.intValue());
			} else {
				// generate a new unique id... encode as "-id,value"
				// even if the decoder knows the eventIdGenerator value,
				// it is "better" to use a marker for newly generated value
				// benefit: the context can re-read encoded values, by itself or others
				// even it would be no more a "newly" generated id/value
				int newid = idGenerator++;
				id = Integer.valueOf(newid);
				idToValue.add(value);
				valueToId.put(value, id);
				
				out.writeInt(-newid); // with negative sign!
				out.writeUTF(value);
			}
		}
	}

	public String decode(DataInputStream in) throws IOException {
		String res; 
		int id = in.readInt();
		if (id == 0) {
			res = null;
		} else if (id > 0) {
			// "(+)id" : existing value
			res = idToValue.get(id);
		} else { // id < 0
			// "-id,value"
			id = -id;
			res = in.readUTF();
			// register newly id (not needed when re-reading from context)
			if (idToValue.size() >= id && idToValue.get(id) != null) {
				idGenerator = id; //??
				if (idToValue.size() < id) {
					idToValue.ensureCapacity(id);
					for (int i = idToValue.size(); i < id; i++) {
						idToValue.add(null);
					}
				}
				idToValue.set(id, res);
				valueToId.put(res, Integer.valueOf(id));
			} // else re-read value.. 
		}
		return res;
	}
	
}