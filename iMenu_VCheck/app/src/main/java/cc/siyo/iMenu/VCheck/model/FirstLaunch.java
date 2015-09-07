package cc.siyo.iMenu.VCheck.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Lemon on 2015/9/1 10:46.
 * Desc:
 */
@Table(name = "launch")
public class FirstLaunch {

    @Id(column = "id")
    public int id;
    public boolean isFirstLaunch;

    public FirstLaunch() {
    }

    public FirstLaunch(int id, boolean isFirstLaunch) {
        this.id = id;
        this.isFirstLaunch = isFirstLaunch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFirstLaunch() {
        return isFirstLaunch;
    }

    public void setIsFirstLaunch(boolean isFirstLaunch) {
        this.isFirstLaunch = isFirstLaunch;
    }
}
