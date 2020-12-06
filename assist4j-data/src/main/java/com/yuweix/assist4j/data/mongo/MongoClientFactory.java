package com.yuweix.assist4j.data.mongo;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;


/**
 * @author yuwei
 */
public class MongoClientFactory implements FactoryBean<MongoClient> {
	private MongoClient mongo;


	public MongoClientFactory(boolean needAuth, List<ServerAddress> seeds, MongoCredential credential) {
		if (needAuth) {
			mongo = new MongoClient(seeds, credential, new MongoClientOptions.Builder().build());
		} else {
			mongo = new MongoClient(seeds);
		}
	}


	@Override
	public MongoClient getObject() throws Exception {
		return mongo;
	}

	@Override
	public Class<?> getObjectType() {
		return mongo == null ? MongoClient.class : mongo.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
