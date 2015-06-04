package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 
 * @author Created by ShangGuan on 15-03-04.
 *
 * @param <T>
 */
public abstract class BaseModel<T> implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract T parse(JSONObject jsonObject);
}
