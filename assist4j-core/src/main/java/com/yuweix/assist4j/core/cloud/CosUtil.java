package com.yuweix.assist4j.core.cloud;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.yuweix.assist4j.core.io.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 腾讯云COS文件工具类
 * @author yuwei
 */
public class CosUtil {
	private static final Logger log = LoggerFactory.getLogger(CosUtil.class);
	private String secretId;
	private String secretKey;
	private String region;
	private String endpoint;
	private String bucketName;

	private COSClient cosClient = null;
	private boolean cosClientWithBucketLockInit = false;
	private final ReentrantLock cosClientLock = new ReentrantLock();
	private final ReentrantLock cosClientWithBucketLock = new ReentrantLock();


	public CosUtil(String secretId, String secretKey, String region, String bucketName) {
		this(secretId, secretKey, region, null, bucketName);
	}
	public CosUtil(String secretId, String secretKey, String region, String protocol, String bucketName) {
		this.secretId = secretId;
		this.secretKey = secretKey;
		this.region = region;
		if (protocol == null || "".equals(protocol)) {
			protocol = "http";
		}
		this.endpoint = protocol + "://" + bucketName + ".cos." + region + ".myqcloud.com";
		this.bucketName = bucketName;
	}

	private COSClient getCosClient() {
		if (cosClient == null) {
			cosClientLock.lock();
			try {
				if (cosClient == null) {
					COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
					ClientConfig clientConfig = new ClientConfig(new Region(region));
					cosClient = new COSClient(cred, clientConfig);
				}
			} finally {
				cosClientLock.unlock();
			}
		}

		return cosClient;
	}

	private COSClient getCosClientWithBucket() {
		COSClient cosClient = getCosClient();
		if (!cosClientWithBucketLockInit) {
			cosClientWithBucketLock.lock();
			try {
				if (!cosClientWithBucketLockInit) {
					if (!cosClient.doesBucketExist(bucketName)) {
						cosClient.createBucket(bucketName);
					}
					cosClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
					cosClientWithBucketLockInit = true;
				}
			} finally {
				cosClientWithBucketLock.unlock();
			}
		}

		return cosClient;
	}

	/**
	 * 删除一个Bucket和其中的文件
	 */
	public void deleteBucket() {
		if (!getCosClient().doesBucketExist(bucketName)) {
			return;
		}

		synchronized (this) {
			List<String> keyList = queryBucketKeyList();
			if (keyList != null && keyList.size() > 0) {
				for (String key: keyList) {
					//先删除bucket下的文件
					getCosClient().deleteObject(bucketName, key);
				}
			}
			getCosClient().deleteBucket(bucketName);
			cosClientWithBucketLockInit = false;
		}
	}

	/**
	 * 查询Bucket下所有的key
	 */
	public List<String> queryBucketKeyList() {
		List<String> list = new ArrayList<String>();
		if (!getCosClient().doesBucketExist(bucketName)) {
			return list;
		}

		ObjectListing objectListing = getCosClient().listObjects(bucketName);
		List<COSObjectSummary> summaryList = objectListing.getObjectSummaries();
		if (summaryList == null || summaryList.size() <= 0) {
			return list;
		}

		for (COSObjectSummary summary: summaryList) {
			list.add(summary.getKey());
		}
		return list;
	}

	/**
	 * 上传文件至COS
	 * @param content
	 * @param key                 如：prd/readme.txt
	 */
	public String uploadFile(byte[] content, String key) {
		log.info("COS upload file: key[{}]", key);
		ByteArrayInputStream bis = new ByteArrayInputStream(content);
		ObjectMetadata objectMeta = new ObjectMetadata();
		objectMeta.setContentLength(bis.available());
		getCosClientWithBucket().putObject(bucketName, key, bis, objectMeta);
		String url = endpoint + "/" + key;
		log.info("URL: {}", url);
		try {
			bis.close();
		} catch (IOException e) {
			log.error("", e);
		}
		return url;
	}

	/**
	 * 从COS下载文件
	 * @param key
	 */
	public byte[] downloadFile(String key) {
		COSObject cosObject = getCosClientWithBucket().getObject(new GetObjectRequest(bucketName, key));
		InputStream objectContent;
		if (cosObject == null || (objectContent = cosObject.getObjectContent()) == null) {
			return null;
		}

		return StreamUtil.read(objectContent);
	}

	/**
	 * 生成一个用HTTP GET方法访问COSObject的URL
	 * @param key
	 */
	public URL getDownloadURL(String key) {
		return getCosClientWithBucket().generatePresignedUrl(bucketName, key, new Date());
	}

	/**
	 * 从COS删除文件
	 * @param key
	 */
	public void deleteFile(String key) {
		getCosClientWithBucket().deleteObject(bucketName, key);
	}
}


