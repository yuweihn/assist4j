package com.yuweix.assist4j.web.multipart;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


/**
 * @author yuwei
 */
public class NotEmptyMultipartResolver extends CommonsMultipartResolver {

	public NotEmptyMultipartResolver() {
		super();
	}

	/**
	 * 刨去size为0的文件
	 * 注意：fileItem.isFormField()为true的数据是普通的表单类型，不是文件类型。
	 */
	@Override
	protected MultipartParsingResult parseFileItems(List<FileItem> fileItems, String encoding) {
		if (fileItems == null || fileItems.size() <= 0) {
			/**
			 * do nothing.
			 */
		} else {
			List<FileItem> tempList = new ArrayList<FileItem>();

			for (FileItem fileItem: fileItems) {
				if (fileItem == null || fileItem.isFormField() || fileItem.getSize() > 0) {
					tempList.add(fileItem);
				}
			}

			fileItems.clear();
			fileItems.addAll(tempList);
		}
		return super.parseFileItems(fileItems, encoding);
	}
}
