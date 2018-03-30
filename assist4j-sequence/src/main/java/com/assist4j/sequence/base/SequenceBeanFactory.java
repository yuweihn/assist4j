package com.assist4j.sequence.base;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import com.assist4j.sequence.dao.SequenceDao;
import com.assist4j.sequence.exception.SequenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/**
 * @author yuwei
 */
public class SequenceBeanFactory implements BeanFactoryPostProcessor, BeanPostProcessor {
	private static final Logger log = LoggerFactory.getLogger(SequenceBeanFactory.class);
	private static final String DEFAULT_FIELD_SEQUENCE_DAO = "sequenceDao";
	private static final String DEFAULT_FIELD_SEQ_NAME = "name";
	private static final String DEFAULT_METHOD_INIT = "init";
	private static final String DEFAULT_METHOD_DESTROY = "destroy";

	private Class<? extends Sequence> sequenceClass;
	private List<Property> constructorArgList;
	private List<Property> propertyList;
	private String fieldSeqName;
	private String sequenceBeanHolderBeanName;
	private String initMethod;
	private String destroyMethod;


	private DefaultListableBeanFactory beanFactory;
	private static volatile AtomicBoolean done = new AtomicBoolean(false);



	public SequenceBeanFactory(Class<? extends Sequence> sequenceClass, String sequenceBeanHolderBeanName) {
		this(sequenceClass.getName(), sequenceBeanHolderBeanName);
	}
	public SequenceBeanFactory(String sequenceClassName, String sequenceBeanHolderBeanName) {
		this(sequenceClassName, null, null, null, sequenceBeanHolderBeanName);
	}
	public SequenceBeanFactory(String sequenceClassName, List<Property> propertyList
			, String fieldSeqName, String sequenceBeanHolderBeanName) {
		this(sequenceClassName, null, propertyList, fieldSeqName, sequenceBeanHolderBeanName);
	}
	public SequenceBeanFactory(String sequenceClassName, List<Property> constructorArgList, List<Property> propertyList
			, String fieldSeqName, String sequenceBeanHolderBeanName) {
		this(sequenceClassName, constructorArgList, propertyList, fieldSeqName, sequenceBeanHolderBeanName, null, null);
	}
	/**
	 * @param sequenceClassName                       准备实例化的Sequence实现类
	 * @param constructorArgList                      Sequence实现类的构造函数参数序列
	 * @param propertyList                            Sequence实现类的属性列表
	 * @param fieldSeqName                            Sequence实现类的seqName属性名
	 * @param sequenceBeanHolderBeanName              准备实例化的bean列表的持有者
	 * @param initMethod
	 * @param destroyMethod
	 */
	@SuppressWarnings("unchecked")
	public SequenceBeanFactory(String sequenceClassName, List<Property> constructorArgList, List<Property> propertyList
			, String fieldSeqName, String sequenceBeanHolderBeanName
			, String initMethod, String destroyMethod) {
		Assert.notNull(sequenceBeanHolderBeanName, "[sequenceBeanHolderBeanName] is required.");
		try {
			Class<?> clz = Class.forName(sequenceClassName);
			if(!Sequence.class.isAssignableFrom(clz)) {
				throw new SequenceException("[sequenceClassName] must be a subclass of " + Sequence.class.getName() + ".");
			}
			this.sequenceClass = (Class<? extends Sequence>) clz;
		} catch (ClassNotFoundException e) {
			throw new SequenceException(e);
		}

		this.constructorArgList = constructorArgList;
		this.propertyList = propertyList;
		if(StringUtils.isEmpty(fieldSeqName)) {
			this.fieldSeqName = DEFAULT_FIELD_SEQ_NAME;
		} else {
			this.fieldSeqName = fieldSeqName;
		}
		this.sequenceBeanHolderBeanName = sequenceBeanHolderBeanName;
		this.initMethod = initMethod;
		this.destroyMethod = destroyMethod;

		checkSequenceClass();
		checkInitMethod();
		checkDestoryMethod();
	}

	/**
	 * 检查sequenceClass，必须含有fieldSeqName指定的属性名和propertyList中的所有属性名。
	 */
	private void checkSequenceClass() {
		try {
			Field seqNameField = this.sequenceClass.getDeclaredField(this.fieldSeqName);
			if(seqNameField == null) {
				throw new NoSuchFieldException(this.fieldSeqName);
			}
			if(!CollectionUtils.isEmpty(this.propertyList)) {
				for(Property prop: this.propertyList) {
					Field field = this.sequenceClass.getDeclaredField(prop.getPropertyName());
					if(field == null) {
						throw new NoSuchFieldException(prop.getPropertyName());
					}
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new SequenceException(e);
		}
	}

	/**
	 * 如果未设置initMethod，将sequenceClass类中的init作为默认的initMethod
	 */
	private void checkInitMethod() {
		if(this.initMethod != null && !"".equals(this.initMethod)) {
			return;
		}
		try {
			Method defaultInitMethod = this.sequenceClass.getMethod(DEFAULT_METHOD_INIT);
			if(defaultInitMethod != null) {
				this.initMethod = defaultInitMethod.getName();
			}
		} catch (NoSuchMethodException | SecurityException e) {
			log.warn(e.toString());
		}
	}
	/**
	 * 如果未设置destroyMethod，将sequenceClass类中的destroy作为默认的destroyMethod
	 */
	private void checkDestoryMethod() {
		if(this.destroyMethod != null && !"".equals(this.destroyMethod)) {
			return;
		}
		try {
			Method defaultDestroyMethod = this.sequenceClass.getMethod(DEFAULT_METHOD_DESTROY);
			if(defaultDestroyMethod != null) {
				this.destroyMethod = defaultDestroyMethod.getName();
			}
		} catch (NoSuchMethodException | SecurityException e) {
			log.warn(e.toString());
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = (DefaultListableBeanFactory)beanFactory;
	}
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(done.get()) {
			return bean;
		}

		if(done.compareAndSet(false, true) == false) {
			return bean;
		}

		if(beanName.equals(sequenceBeanHolderBeanName)) {
			return bean;
		}

		registerBeans();
		return bean;
	}

	private void registerBeans() {
		SequenceBeanHolder sequenceBeanHolder = beanFactory.getBean(sequenceBeanHolderBeanName, SequenceBeanHolder.class);
		Map<String, String> beanSeqMap = sequenceBeanHolder.getBeanSeqNameMap();
		if(CollectionUtils.isEmpty(beanSeqMap)) {
			return;
		}

		checkPropertyList();

		Iterator<Entry<String, String>> entryItr = beanSeqMap.entrySet().iterator();
		while(entryItr.hasNext()) {
			Entry<String, String> entry = entryItr.next();
			String beanName = entry.getKey();
			String seqName = entry.getValue();

			if(beanName == null || "".equals(beanName.trim())) {
				continue;
			}
			beanName = beanName.trim();

			if(seqName == null || "".equals(seqName.trim())) {
				seqName = beanName.trim();
			} else {
				seqName = seqName.trim();
			}

			List<Property> propList = new ArrayList<Property>();
			if(!CollectionUtils.isEmpty(this.propertyList)) {
				propList.addAll(this.propertyList);
			}
			propList.add(new Property(fieldSeqName, seqName, Property.TYPE_VALUE));
			registerBean(beanName, this.constructorArgList, propList);
		}
	}
	private void registerBean(String beanName, List<Property> constArgList, List<Property> propList) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(sequenceClass);
		if(!CollectionUtils.isEmpty(constArgList)) {
			for(Property constructorArg: constArgList) {
				if(Property.TYPE_VALUE == constructorArg.getType()) {
					builder.addConstructorArgValue(constructorArg.getValue());
				} else if(Property.TYPE_REFERENCE == constructorArg.getType()) {
					builder.addConstructorArgReference(constructorArg.getValue().toString());
				} else {
					throw new RuntimeException("Error parameter [type] in constructorArgList!");
				}
			}
		}

		if(!CollectionUtils.isEmpty(propList)) {
			for(Property prop: propList) {
				if(Property.TYPE_VALUE == prop.getType()) {
					builder.addPropertyValue(prop.getPropertyName(), prop.getValue());
				} else if(Property.TYPE_REFERENCE == prop.getType()) {
					builder.addPropertyReference(prop.getPropertyName(), prop.getValue().toString());
				} else {
					throw new RuntimeException("Error parameter [type] in propertyList!");
				}
			}
		}
		if(!StringUtils.isEmpty(initMethod)) {
			builder.setInitMethodName(initMethod);
		}
		if(!StringUtils.isEmpty(destroyMethod)) {
			builder.setDestroyMethodName(destroyMethod);
		}
		beanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}

	/**
	 * this.propertyList中必须有一个SequenceDao类型的属性
	 */
	private void checkPropertyList() {
		if(this.propertyList == null) {
			this.propertyList = new ArrayList<Property>();
		}

		for(Property property: this.propertyList) {
			try {
				Field field = this.sequenceClass.getDeclaredField(property.getPropertyName());
				if(SequenceDao.class.isAssignableFrom(field.getType())) {
					return;
				}
			} catch (Exception e) {
			}
		}

		try {
			Field defaultSequenceDaoField = this.sequenceClass.getDeclaredField(DEFAULT_FIELD_SEQUENCE_DAO);
			if(defaultSequenceDaoField == null) {
				throw new NoSuchFieldException(DEFAULT_FIELD_SEQUENCE_DAO);
			}
		} catch (Exception e) {
			throw new SequenceException(e);
		}

		String[] beanNamesForSequenceDao = beanFactory.getBeanNamesForType(SequenceDao.class);
		if(beanNamesForSequenceDao == null || beanNamesForSequenceDao.length != 1) {
			throw new SequenceException("Error happens while inject field SequenceDao.");
		}

		this.propertyList.add(new Property(DEFAULT_FIELD_SEQUENCE_DAO, beanNamesForSequenceDao[0], Property.TYPE_REFERENCE));
	}

	public static class Property {
		private String propertyName;
		private Object value;
		private byte type;

		public static final byte TYPE_VALUE = 0;
		public static final byte TYPE_REFERENCE = 1;

		public Property(String propertyName, Object value, byte type) {
			this.propertyName = propertyName;
			this.value = value;
			this.type = type;
		}
		public String getPropertyName() {
			return propertyName;
		}
		public Object getValue() {
			return value;
		}
		public byte getType() {
			return type;
		}
	}
}
