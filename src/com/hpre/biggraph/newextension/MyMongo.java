package com.hpre.biggraph.newextension;

public class MyMongo extends BaseMongoDAL{

	public MyMongo(String databaseName, 
			String collectionName) {
		super(MongoConfigure.host, databaseName, MongoConfigure.port, collectionName, MongoConfigure.name, MongoConfigure.pass);
	}

}
