package com.yuweix.assist4j.data.mongo;


import com.mongodb.MongoCredential;
import org.springframework.beans.factory.FactoryBean;


/**
 * @author yuwei
 */
public class MongoCredentialFactory implements FactoryBean<MongoCredential> {
	private MongoCredential mongoCredential;



	public MongoCredentialFactory(String userName, String password, String authDbName) {
		mongoCredential = MongoCredential.createScramSha1Credential(userName, authDbName, password.toCharArray());
	}


	@Override
	public MongoCredential getObject() throws Exception {
		return mongoCredential;
	}

	@Override
	public Class<? extends MongoCredential> getObjectType() {
		return mongoCredential == null ? MongoCredential.class : mongoCredential.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
