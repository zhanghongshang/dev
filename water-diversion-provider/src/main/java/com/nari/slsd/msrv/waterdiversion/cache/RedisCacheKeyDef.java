package com.nari.slsd.msrv.waterdiversion.cache;

/**
 * @author reset kalar
 */
public class RedisCacheKeyDef {

    public static class ModelKey {
        public static final String TREE = "water_use_unit_tree:";
        public static final String DEPT = "permission_dept:";
        public static final String USER = "permission_user:";
        public static final String STATION = "wr_diversion_port:";

        public static final String MNG_UNIT_ID = "deptId";
        public static final String MNG_UNIT_NAME = "name";
        public static final String MNG_UNIT_CODE = "code";
        public static final String USER_ID = "userId";
        public static final String USER_NAME = "userName";

        public static final String CONTRAST = "plan_act_contrast";
    }

}
