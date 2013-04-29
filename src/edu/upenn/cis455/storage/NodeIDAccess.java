package edu.upenn.cis455.storage;

import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class NodeIDAccess {
	private EntityStore store;
	private PrimaryIndex<String, NodeID> nodeIndex;
	private Environment environment;

	public NodeIDAccess(Environment env){
		environment = env;
		StoreConfig storeConfig = new StoreConfig();
		storeConfig.setAllowCreate(true);
		store = new EntityStore(env, "NodeIDStore", storeConfig);
		nodeIndex = store.getPrimaryIndex(String.class, NodeID.class); 
	}

	private void putNodeID(NodeID id) {
		nodeIndex.put(id);
		environment.sync();
	}

	public NodeID getNodeID(String uniqueID) {
		return nodeIndex.get(uniqueID);
	}
	
	public String getID (String uniqueID) {
		NodeID temp = getNodeID(uniqueID);
		if(temp!=null)
			return temp.Id;
		else
			return null;
	}

	public void putID(String identifier, String id) {
		NodeID nodeID = new NodeID(identifier, id);
		putNodeID(nodeID);
	}
	
	public void deleteNodeID(String uniqueID) {
		nodeIndex.delete(uniqueID);
		environment.sync();
	}

	public void closeStore(){
		store.close();
	}
}
