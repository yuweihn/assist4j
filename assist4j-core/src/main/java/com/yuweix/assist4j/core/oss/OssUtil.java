package com.yuweix.assist4j.core.oss;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.yuweix.assist4j.core.io.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * OSS文件工具类
 * @author yuwei
 */
public class OssUtil {
	private static final Logger log = LoggerFactory.getLogger(OssUtil.class);
	private String endpoint;
	private String accessKey;
	private String accessSecret;
	private String bucketName;

	private OSSClient ossClient = null;
	private boolean ossClientWithBucketLockInit = false;
	private final ReentrantLock ossClientLock = new ReentrantLock();
	private final ReentrantLock ossClientWithBucketLock = new ReentrantLock();


	public OssUtil(String endpoint, String accessKey, String accessSecret, String bucketName) {
		this.endpoint = endpoint;
		this.accessKey = accessKey;
		this.accessSecret = accessSecret;
		this.bucketName = bucketName;
	}

	private OSSClient getOSSClient() {
		if (ossClient == null) {
			ossClientLock.lock();
			try {
				if (ossClient == null) {
					ossClient = new OSSClient(this.endpoint, this.accessKey, this.accessSecret);
				}
			} finally {
				ossClientLock.unlock();
			}
		}

		return ossClient;
	}

	private OSSClient getOSSClientWithBucket() {
		OSSClient ossClient = getOSSClient();
		if (!ossClientWithBucketLockInit) {
			ossClientWithBucketLock.lock();
			try {
				if (!ossClientWithBucketLockInit) {
					if (!ossClient.doesBucketExist(bucketName)) {
						ossClient.createBucket(bucketName);
					}
					ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
					ossClientWithBucketLockInit = true;
				}
			} finally {
				ossClientWithBucketLock.unlock();
			}
		}

		return ossClient;
	}

	/**
	 * 删除一个Bucket和其中的文件
	 */
	public void deleteBucket() {
		if (!getOSSClient().doesBucketExist(bucketName)) {
			return;
		}

		synchronized (this) {
			List<String> keyList = queryBucketKeyList();
			if (keyList != null && keyList.size() > 0) {
				for (String key: keyList) {
					//先删除bucket下的文件
					getOSSClient().deleteObject(bucketName, key);
				}
			}
			getOSSClient().deleteBucket(bucketName);
			ossClientWithBucketLockInit = false;
		}
	}

	/**
	 * 查询Bucket下所有的key
	 */
	public List<String> queryBucketKeyList() {
		List<String> list = new ArrayList<String>();
		if (!getOSSClient().doesBucketExist(bucketName)) {
			return list;
		}

		ObjectListing objectListing = getOSSClient().listObjects(bucketName);
		List<OSSObjectSummary> summaryList = objectListing.getObjectSummaries();
		if (summaryList == null || summaryList.size() <= 0) {
			return list;
		}

		for (OSSObjectSummary summary: summaryList) {
			list.add(summary.getKey());
		}
		return list;
	}

	/**
	 * 上传文件至OSS
	 * @param content
	 * @param key                 如：prd/readme.txt
	 */
	public String uploadFile(byte[] content, String key) {
		log.info("OSS upload file: key[{}]", key);
		String protocol = getProtocol(endpoint);
		ByteArrayInputStream bis = new ByteArrayInputStream(content);

		ObjectMetadata objectMeta = new ObjectMetadata();
		objectMeta.setContentLength(bis.available());
		getOSSClientWithBucket().putObject(bucketName, key, bis, objectMeta);
		String url = protocol + bucketName + "." + endpoint.substring(protocol.length()) + "/" + key;
		log.info("URL: {}", url);
		try {
			bis.close();
		} catch (IOException e) {
			log.error("", e);
		}
		return url;
	}
	private String getProtocol(String endpoint) {
		if (endpoint != null && endpoint.startsWith("https://")) {
			return "https://";
		} else {
			return "http://";
		}
	}

	/**
	 * 从OSS下载文件
	 * @param key
	 */
	public byte[] downloadFile(String key) {
		OSSObject ossObject = getOSSClientWithBucket().getObject(new GetObjectRequest(bucketName, key));
		InputStream objectContent;
		if (ossObject == null || (objectContent = ossObject.getObjectContent()) == null) {
			return null;
		}

		return StreamUtil.read(objectContent);
	}

	/**
	 * 生成一个用HTTP GET方法访问OSSObject的URL
	 * @param key
	 */
	public URL getDownloadURL(String key) {
		return getOSSClientWithBucket().generatePresignedUrl(bucketName, key, new Date());
	}

	/**
	 * 从OSS删除文件
	 * @param key
	 */
	public void deleteFile(String key) {
		getOSSClientWithBucket().deleteObject(bucketName, key);
	}
}
