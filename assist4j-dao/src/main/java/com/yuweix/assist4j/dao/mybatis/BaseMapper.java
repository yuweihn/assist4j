package com.yuweix.assist4j.dao.mybatis;


import java.io.Serializable;


/**
 * @author yuwei
 */
public interface BaseMapper<T extends Serializable, PK extends Serializable> 
			extends InsertMapper<T, PK>, UpdateMapper<T, PK>, DeleteMapper<T, PK>, SelectMapper<T, PK> {
	
}

