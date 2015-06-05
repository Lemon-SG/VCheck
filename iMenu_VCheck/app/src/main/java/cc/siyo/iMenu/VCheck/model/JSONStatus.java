package cc.siyo.iMenu.VCheck.model;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * 返回数据状态实体
 * Created by Lemon on 15-03-04.
 */
@SuppressWarnings("rawtypes")
public class JSONStatus extends BaseModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 成功标石 1-成功，0-失败*/
	public String succeed;
	/** 错误标识码*/
	public String error_code;
	/** 错误提示详情*/
	public String error_desc;
	/** 请求是否成功*/
	public Boolean isSuccess = false;
	/** 数据JSON*/
	public JSONObject data;
	/** 页数*/
	public PageInfo pageInfo;
	
	@Override
	public JSONStatus parse(JSONObject jsonObject) {
		JSONObject status = jsonObject.optJSONObject("status");
		succeed = status.optString("succeed");
		error_code = status.optString("error_code");
		error_desc = status.optString("error_desc");
		if(Integer.parseInt(succeed) == 1){
			isSuccess = true;
		}
		if(Integer.parseInt(succeed) == 0){
			isSuccess = false;
		}
		data = jsonObject.optJSONObject("data");
		pageInfo = new PageInfo().parse(jsonObject.optJSONObject("paginated"));
		return this;
	}
}
