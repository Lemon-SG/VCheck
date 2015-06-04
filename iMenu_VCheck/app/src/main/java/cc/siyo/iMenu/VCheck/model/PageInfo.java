package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * 页数实体
 * 
 * @author Created by ShangGuan on 15-3-19
 */
public class PageInfo extends BaseModel<PageInfo>{

	/** 总数 */
	public String total;
	/** 每页数量 */
	public String count;
	/** 1-有更多/0-没有更多*/
	public String more;

	@Override
	public PageInfo parse(JSONObject jsonObject) {
		if (jsonObject != null && jsonObject.length() > 0) {
			total = jsonObject.optString("total");
			count = jsonObject.optString("count");
			more = jsonObject.optString("more");
			return this;
		}
		return null;
	}

}
