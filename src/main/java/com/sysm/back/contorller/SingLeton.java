package com.sysm.back.contorller;

public class SingLeton {
    private static SingLeton singLeton = null;

    private SingLeton() {

    }

    //适用于单线程
    public static SingLeton getInstance() {
        if (singLeton == null) {
            singLeton = new SingLeton();
        }
        return singLeton;
    }

    //适用于多少线程
    public static synchronized SingLeton getSingLeton() {
        if (singLeton == null) {
            singLeton = new SingLeton();
        }
        return singLeton;
    }

    //双重检索
    public static SingLeton getInstance1() {
        if (singLeton == null) {
            synchronized (SingLeton.class) {
                if (singLeton == null) {
                    singLeton = new SingLeton();
                }
            }
        }
        return singLeton;
    }

}
