package cc.siyo.iMenu.VCheck.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import org.json.JSONObject;

@Table(name = "region_list")
public class Region extends BaseModel<Region> {

    private static final String TAG = "Region";
    @Id(column = "region_id")
    public String region_id;
    public String parent_id;
    public String sort_order;
    public String region_name;
    public String aliases_name;
    public String level;
    public String is_open;

    @Override
    public Region parse(JSONObject jsonObject) {
        if (jsonObject != null) {
            region_id = jsonObject.optString("region_id");
            parent_id = jsonObject.optString("parent_id");
            sort_order = jsonObject.optString("sort_order");
            region_name = jsonObject.optString("region_name");
            aliases_name = jsonObject.optString("aliases_name");
            level = jsonObject.optString("level");
            is_open = jsonObject.optString("is_open");
        }
        return this;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public String getAliases_name() {
        return aliases_name;
    }

    public void setAliases_name(String aliases_name) {
        this.aliases_name = aliases_name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getIs_open() {
        return is_open;
    }

    public void setIs_open(String is_open) {
        this.is_open = is_open;
    }
}
