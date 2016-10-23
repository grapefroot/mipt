package ru.phystech.java2.presentation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConfigClass.class);
        StoreableShell sh = applicationContext.getBean(StoreableShell.class);
        sh.runShell(args);
    }
}
