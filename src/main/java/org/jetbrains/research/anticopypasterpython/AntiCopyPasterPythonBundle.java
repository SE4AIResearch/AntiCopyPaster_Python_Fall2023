package org.jetbrains.research.anticopypasterpython;

import com.intellij.AbstractBundle;
import com.intellij.reference.SoftReference;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

public final class AntiCopyPasterPythonBundle {
    private static final String BUNDLE = "AntiCopyPasterPythonBundle";
    private static Reference<ResourceBundle> INSTANCE;

    private AntiCopyPasterPythonBundle() {
    }

    // Provide key to retrieve corresponding text from properties file
    public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = SoftReference.dereference(INSTANCE);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            INSTANCE = new SoftReference<>(bundle);
        }
        return bundle;
    }
}