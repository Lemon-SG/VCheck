package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * 默认配置文件格式
 * 
 * @author Created by ShangGuan on 15-3-10
 */
@SuppressWarnings("rawtypes")
public class ClientConfig extends BaseModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 键KEY */
	public String key;
	/** 值VALUE */
	public String value;
	/** 数据DATA */
	public String data;

	@Override
	public ClientConfig parse(JSONObject jsonObject) {
		if (jsonObject != null && jsonObject.length() > 0) {
			key = jsonObject.optString("key");
			value = jsonObject.optString("value");
			data = jsonObject.optString("data");
		}
		return this;
	}

}
