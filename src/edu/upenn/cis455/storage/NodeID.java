package edu.upenn.cis455.storage;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class NodeID {
	
	public NodeID(String identifier, String Id){
		this.identifier = identifier;
		this.Id = Id;
	}

	public NodeID(){
	}

	@PrimaryKey
	public String identifier;
	public String Id;

}
