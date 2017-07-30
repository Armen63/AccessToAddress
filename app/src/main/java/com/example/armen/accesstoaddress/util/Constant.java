package com.example.armen.accesstoaddress.util;


public class Constant {

    public class API {

        public static final String HOST = "https://raw.githubusercontent.com/Armen63/armenasatryan.github.io";
        public static final String URL_LIST = HOST + "/master/url%20list/urls.json";
        public static final String ACCESS_EXIST = "https://github.com/Armen63/armenasatryan.github.io/blob/master/url%20list/access_exist.png?raw=true";
        public static final String NO_ACCESS = "https://raw.githubusercontent.com/Armen63/armenasatryan.github.io/master/url%20list/no_access.png";
        public static final String LOADING = "https://raw.githubusercontent.com/Armen63/armenasatryan.github.io/master/url%20list/loading.gif";
    }

    public class Extra {
        public static final String URL = "URL";
        public static final String POST_ENTITY = "POST_ENTITY";
        public static final String REQUEST_TYPE = "REQUEST_TYPE";
    }

    public class RequestType {
        public static final int URL_LIST = 1;
        public static final int URL_ITEM = 2;
    }

    public class RequestMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String PUT = "PUT";
        public static final String HEAD = "HEAD";
    }


    public class Symbol {
        public static final String SPACE = " ";
    }

    public class Boolean {
        public static final String TRUE = "true";
        public static final String FALSE = "false";
    }

    public class Util {
        public static final String UTF_8 = "UTF-8";
    }

}
