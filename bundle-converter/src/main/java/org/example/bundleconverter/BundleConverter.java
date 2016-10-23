package org.example.bundleconverter;


import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ListResourceBundle;
import java.util.NoSuchElementException;
import java.util.Properties;


public class BundleConverter {

    private File sourceFile;

    public BundleConverter(File classFile) {
        if (classFile == null || !classFile.exists()) {
            throw new NoSuchElementException("Wrong file path");
        }

        sourceFile = classFile;
    }

    private String getClassFilePath() {
        int index = sourceFile.getName().lastIndexOf(".");
        return String.format("%s.%s", this.getClass().getPackage().getName(),sourceFile.getName().substring(0, index));
    }

    public void convert() throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFile);
        compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
        URI pathToDirectory = sourceFile.getParentFile().toURI();

        ClassLoader loader = new URLClassLoader(new URL[]{pathToDirectory.toURL()});
        Object clazz = loader.loadClass(getClassFilePath()).newInstance();
        if (!(clazz instanceof ListResourceBundle)) {
            throw new ClassCastException("Class isn't an instance of ListResourceBundle");
        }
        Properties converted = new Properties();
        ListResourceBundle loaded = (ListResourceBundle) clazz;
        for (String key : loaded.keySet()) {
            converted.setProperty(key, (String) loaded.handleGetObject(key));
        }
        File f = new File(sourceFile.getParentFile(), String.format("%s_converted.properties", sourceFile.getName()));
        FileOutputStream stream = new FileOutputStream(f);
        converted.store(stream, "this file was converted from java bundle");
        stream.close();

    }
}
