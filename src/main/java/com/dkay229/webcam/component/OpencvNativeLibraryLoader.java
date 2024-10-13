package com.dkay229.webcam.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class OpencvNativeLibraryLoader {
    private static final Logger logger = LoggerFactory.getLogger(OpencvNativeLibraryLoader.class);
    public static final String WIN_X64 = "C:\\Program Files\\opencv\\opencv-4.9.0-windows\\opencv\\build\\java\\x64\\opencv_java490.dll";
    public static final String WIN_X86 = "C:\\Program Files\\opencv\\opencv-4.9.0-windows\\opencv\\build\\java\\x86\\opencv_java490.dll";
    public static final String osArch = System.getProperty("os.arch");
    public static final String OPENCVS_NATIVE_LIBRARY = System.getProperty("opencv.native.library");
    private final ResourceLoader resourceLoader;

    @Autowired
    public OpencvNativeLibraryLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.loadOPenCvNativeLibrary();
    }

    public void loadOPenCvNativeLibrary() {
        String nativeLibrary;
        try {
            if (OPENCVS_NATIVE_LIBRARY != null&& !OPENCVS_NATIVE_LIBRARY.isEmpty()) {
                logger.info("Loading OpenCV native library defined by opencv.native.library: " + OPENCVS_NATIVE_LIBRARY);
                nativeLibrary = OPENCVS_NATIVE_LIBRARY;
            } else
            if (osArch.equals("amd64") || osArch.equals("x86_64")) {
                nativeLibrary=OpencvNativeLibraryLoader.WIN_X64;
            } else if (osArch.equals("x86")) {
                nativeLibrary=OpencvNativeLibraryLoader.WIN_X86;
            } else {
                logger.error("Unsupported architecture: " + osArch);
                throw new UnsupportedOperationException("Unsupported architecture: " + osArch);
            }
            logger.info("Loading OpenCV native library: " + nativeLibrary);
            System.load(nativeLibrary);
        } catch (Exception e) {
            logger.error("Error loading OpenCV native library", e);
        }
    }
}